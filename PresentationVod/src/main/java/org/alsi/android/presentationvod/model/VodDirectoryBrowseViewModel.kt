package org.alsi.android.presentationvod.model

import androidx.core.math.MathUtils.clamp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.domain.vod.interactor.VodBrowseCursorGetUseCase
import org.alsi.android.domain.vod.interactor.VodBrowseCursorObserveUseCase
import org.alsi.android.domain.vod.interactor.VodDirectoryUseCase
import org.alsi.android.domain.vod.interactor.VodListingPageUseCase
import org.alsi.android.domain.vod.model.guide.directory.*
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import org.alsi.android.domain.vod.model.guide.listing.VodListingWindow
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodBrowseCursorMoveUseCase
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class VodDirectoryBrowseViewModel @Inject constructor (
    private val directoryUseCase: VodDirectoryUseCase,
    private val browseCursorGetUseCase: VodBrowseCursorGetUseCase,
    private val browseCursorMoveUseCase: VodBrowseCursorMoveUseCase,
    browseCursorObserveUseCase: VodBrowseCursorObserveUseCase,
    private val listingPageUseCase: VodListingPageUseCase,
    private val presentationManager: PresentationManager
) : ViewModel() {

    private val liveDirectory: MutableLiveData<Resource<VodDirectoryBrowseLiveData>> = MutableLiveData()

    private var directory: VodDirectory? = null
    private var position: VodDirectoryPosition? = null

    private var isOnResume: Boolean = true

    val currentPresentation: StreamingServicePresentation? get()
    = presentationManager.provideContext()?.presentation

    val tvPresentations: List<StreamingServicePresentation> get()
    = presentationManager.providePresentations(StreamingServiceKind.TV, skipCurrent = false)

    val vodPresentations: List<StreamingServicePresentation> get()
    = presentationManager.providePresentations(StreamingServiceKind.VOD, skipCurrent = true)

    private val listingRequestsRegistry: MutableSet<Int> = mutableSetOf()

    init {
        browseCursorObserveUseCase.execute(BrowseCursorSubscriber())

        liveDirectory.postValue(Resource.loading())
        directoryUseCase.execute(DirectorySubscriber())
    }

    fun getLiveDirectory(): LiveData<Resource<VodDirectoryBrowseLiveData>> = liveDirectory

    fun dispose() {
        directoryUseCase.dispose()
    }

    fun onSectionSelected(section: VodSection) {
        directory?: return
        liveDirectory.postValue(Resource.loading())
        browseCursorMoveUseCase.execute(BrowseCursorMoveSubscriber(),
            VodBrowseCursorMoveUseCase.Params(section))
    }

    fun onUnitSelected(unit: VodUnit) {
        directory?: return
        if (null == unit.window) {
            // load unit's listing only when it's empty
            liveDirectory.postValue(Resource.loading())
            browseCursorMoveUseCase.execute(
                BrowseCursorMoveSubscriber(),
                VodBrowseCursorMoveUseCase.Params(directory!!.sectionById[unit.sectionId], unit)
            )
        }
    }

    fun onUnitSelected(selectedUnitIndex: Int) {
        directory?: return
        position?.sectionIndex?: return
        val section = directory!!.sections[position!!.sectionIndex]
        val unit = section.units[selectedUnitIndex]
        if (null == unit.window) {
            // load unit's listing only when it's empty
            liveDirectory.postValue(Resource.loading())
            browseCursorMoveUseCase.execute(
                BrowseCursorMoveSubscriber(),
                VodBrowseCursorMoveUseCase.Params(section, unit)
            )
        }
    }

    fun onListingItemSelected(item: VodListingItem, itemIndex: Int) {
        // preconditions
        directory?: return
        val section = directory!!.sectionById[item.sectionId]?: return
        val unit = section.unitById[item.unitId]?: return
        liveDirectory.postValue(Resource.loading())
        browseCursorMoveUseCase.execute(BrowseCursorMoveSubscriber(),
            VodBrowseCursorMoveUseCase.Params(section, unit, item, itemIndex))
    }

    fun onListingItemAction(item: VodListingItem, itemIndex: Int, navigate: () -> Unit) {
        // preconditions
        directory?: return
        val section = directory!!.sectionById[item.sectionId]?: return
        val unit = section.unitById[item.unitId]?: return
        // ensure browse cursor in a correct position before navigating to the details fragment
        liveDirectory.postValue(Resource.loading())
        browseCursorMoveUseCase.execute(BrowseCursorMoveOnActionSubscriber(navigate),
            VodBrowseCursorMoveUseCase.Params(section, unit, item, itemIndex))
    }

    fun onResume() {
        isOnResume = true
        browseCursorGetUseCase.execute(GetBrowseCursorSubscriber())
    }

    private inner class DirectorySubscriber: DisposableSingleObserver<VodDirectory>() {
        override fun onSuccess(directory: VodDirectory) {
            this@VodDirectoryBrowseViewModel.directory = directory
            browseCursorGetUseCase.execute(GetBrowseCursorSubscriber())
        }
        override fun onError(e: Throwable) {
            liveDirectory.postValue(Resource.error(e))
        }
    }

    private inner class GetBrowseCursorSubscriber: DisposableSingleObserver<VodBrowseCursor>() {
        override fun onSuccess(t: VodBrowseCursor) = handleBrowseCursorUpdate(t)
        override fun onError(e: Throwable) = liveDirectory.postValue(Resource.error(e))
    }

    private inner class BrowseCursorSubscriber: DisposableObserver<VodBrowseCursor>() {
        override fun onNext(t: VodBrowseCursor)  = handleBrowseCursorUpdate(t)
        override fun onError(e: Throwable) = liveDirectory.postValue(Resource.error(e))
        override fun onComplete() { /* not applicable */ }
    }

    fun handleBrowseCursorUpdate(t: VodBrowseCursor) {
        directory?: return

        // evaluate current browsing position
        val section: VodSection?
        val unit: VodUnit?
        val itemPosition: Int?
        if (null == t.section) {
            section = if (directory?.sections != null && directory!!.sections.isNotEmpty())
                directory?.sections?.elementAt(0) else return
            unit = if (section?.units != null && section.units.isNotEmpty())
                section.units[0] else return
            itemPosition = 0
        }
        else {
            section = t.section?: return
            unit = t.unit?: return
            itemPosition = t.itemPosition?: 0
        }

        // set position
        val sectionIndex = directory?.sectionPositionById?.getValue(section.id)?: return
        val unitIndex = section.unitPositionById[unit.id]?: return

        position = VodDirectoryPosition(sectionIndex, unitIndex, itemPosition)

        // request initial directory display in case the cursor stores preloaded directory part
        if (isOnResume && unit.window != null) {
            isOnResume = false
            val sectionsUpdate: MutableList<VodSection> = directory!!.sections.toMutableList()
            sectionsUpdate[sectionIndex] = section
            directory = VodDirectory(sectionsUpdate, t.timeStamp)
            liveDirectory.postValue(Resource.success(VodDirectoryBrowseLiveData(
                directory!!, position!!, VodDirectoryUpdateScope(sectionIndex))))
            return
        }

        // proceed to load initial or subsequently requested parts of the directory
        var executedUseCasesCounter = 0

        // get current page of current unit if it's not loaded yet
        unit.window?: executedUseCasesCounter++
        if (unit.window != null) {
            if (unit.window!!.items.size - itemPosition < 10) {
                // load next page
                requestListingPage(section.id, unit.id, unit.window!!.items.size)
            }
        }
        else {
            // load first page
            requestListingPage(section.id, unit.id, itemPosition)
        }

        // get initial pages of some not loaded yet NEXT units
        if (unitIndex < section.units.size - 1) {
            val afterRangeEnd = clamp(unitIndex + VISIBLE_WINDOW_HALF,
                unitIndex + 1, section.units.size - 1)
            for (i in unitIndex + 1 .. afterRangeEnd) {
                val iterationUnit = section.units[i]
                iterationUnit.window?: executedUseCasesCounter++
                iterationUnit.window?: requestListingPage(section.id, iterationUnit.id, 0)
            }
        }

        // get initial pages of some not loaded yet PREVIOUS units
        if (unitIndex >= 0) {
            val beforeRangeStart = clamp(unitIndex - VISIBLE_WINDOW_HALF,
                0, unitIndex - 1)
            for (i in unitIndex - 1 downTo beforeRangeStart) {
                val iterationUnit = section.units[i]
                iterationUnit.window?: executedUseCasesCounter++
                iterationUnit.window?: requestListingPage(section.id, iterationUnit.id, 0)
            }
        }

        if (executedUseCasesCounter == 0)
            liveDirectory.postValue(Resource.success(VodDirectoryBrowseLiveData(
                directory!!, position!!, VodDirectoryUpdateScope.empty()))) // to remove not needed progress indication
    }

    private fun requestListingPage(sectionId: Long, unitId: Long, start: Int) {
        // check if the request is already done and skip it if it is
        val requestKey = listingPageRequestKey(sectionId, unitId, start)
        if (listingRequestsRegistry.contains(listingPageRequestKey(sectionId, unitId, start)))
            return
        // register request to avoid repetition, which is possible due to item selection
        // events are faster then the requests execution
        listingRequestsRegistry.add(requestKey)
        // execute request
        listingPageUseCase.execute(ListingPageSubscriber(),
            VodListingPageUseCase.Params(sectionId, unitId, 0, DEFAULT_LISTING_PAGE_SIZE))
    }

    private fun listingPageRequestKey(sectionId: Long, unitId: Long, start: Int)
    = listOf(sectionId, unitId, start).hashCode()


    inner class BrowseCursorMoveSubscriber : DisposableSingleObserver<VodBrowseCursor>() {
        override fun onSuccess(t: VodBrowseCursor) {} // silently accept it, actual job done by BrowseCursorSubscriber
        override fun onError(e: Throwable) = liveDirectory.postValue(Resource.error(e))
    }

    inner class BrowseCursorMoveOnActionSubscriber(private val navigate: () -> Unit)
        : DisposableSingleObserver<VodBrowseCursor>() {
        override fun onSuccess(t: VodBrowseCursor) {
            navigate()
        }
        override fun onError(e: Throwable) {
            liveDirectory.postValue(Resource.error(e))
        }
    }

    private inner class ListingPageSubscriber: DisposableSingleObserver<VodListingPage>() {
        override fun onSuccess(page: VodListingPage) {
            if (null == directory || null == position || page.items.isEmpty()) {
                liveDirectory.postValue(Resource.success())
                return
            }
            val unit = directory?.sectionById?.getValue(page.sectionId)?.unitById?.getValue(page.unitId)
            if (null == unit) {
                liveDirectory.postValue(Resource.success())
                return
            }
            // window update
            if (null == unit.window) {
                unit.window = VodListingWindow(page)
            }
            else {
                unit.window?.add(page)
            }
            // update scope
            val sectionIndex = directory?.sectionPositionById?.getValue(page.sectionId)
            val unitIndex = directory?.sectionById?.getValue(page.sectionId)?.unitPositionById?.getValue(unit.id)
            val updateScope = if (sectionIndex != null && unitIndex != null)
                VodDirectoryUpdateScope(sectionIndex, unitIndex) else null
            // live data post
            liveDirectory.postValue(Resource.success(VodDirectoryBrowseLiveData(
                directory!!, position!!, updateScope)))
        }
        override fun onError(e: Throwable) {
            liveDirectory.postValue(Resource.error(e))
        }
    }

    companion object {
        const val DEFAULT_LISTING_PAGE_SIZE = 20
        const val VISIBLE_WINDOW_HALF = 4
    }
}
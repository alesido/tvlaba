package org.alsi.android.presentationvod.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.vod.interactor.VodBrowseCursorObserveUseCase
import org.alsi.android.domain.vod.interactor.VodItemUseCase
import org.alsi.android.domain.vod.interactor.VodListingPageUseCase
import org.alsi.android.domain.vod.interactor.VodNewPlaybackUseCase
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodBrowseCursorMoveUseCase
import org.alsi.android.domain.vod.model.session.VodBrowsePage
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class VodDigestViewModel @Inject constructor (

    private val browseCursorObserveUseCase: VodBrowseCursorObserveUseCase,
    private val browseCursorMoveUseCase: VodBrowseCursorMoveUseCase,
    private val newPlaybackUseCase: VodNewPlaybackUseCase,
    private val listingPageUseCase: VodListingPageUseCase,
    private val vodItemUseCase: VodItemUseCase,

    ) : ViewModel() {

    private val liveData: MutableLiveData<Resource<VodDigestLiveData>> = MutableLiveData()
    private val snapshot = VodDigestLiveData()

    val currentListingPosition: Int get() = snapshot.cursor?.itemPosition?: 0

    init {
       liveData.postValue(Resource.loading())
       browseCursorObserveUseCase.execute(VodBrowseCursorSubscriber())
    }

    //region View Model Interface

    fun getLiveData(): LiveData<Resource<VodDigestLiveData>> = liveData

    fun onBackNavigation() {
        val params = snapshot.cursor?.let {
                VodBrowseCursorMoveUseCase.Params(page = VodBrowsePage.ITEM, reuse = true)
        }?: return
        browseCursorMoveUseCase.execute(VodBrowseCursorMoveSubscriber(), params)
    }

    fun dispose() {
        browseCursorObserveUseCase.dispose()
        browseCursorMoveUseCase.dispose()
        listingPageUseCase.dispose()
        vodItemUseCase.dispose()
    }

    fun onListingItemSelected(item: VodListingItem, itemPosition: Int) {
        snapshot.cursor?.unit?.window?.items?: return
        val unit = snapshot.cursor?.unit
        val size = unit!!.window!!.items.size
        if (size - itemPosition < 10 && unit.sectionId != null) {
            // load next listing page
            listingPageUseCase.execute(ListingPageSubscriber(),
                VodListingPageUseCase.Params(unit.sectionId!!, unit.id, size,
                    VodDirectoryBrowseViewModel.DEFAULT_LISTING_PAGE_SIZE
                ))
        }
    }

    fun onListingItemAction(item: VodListingItem, itemPosition: Int) {
        snapshot.cursor?: return
        liveData.postValue(Resource.loading())
        with(snapshot.cursor!!) {
            browseCursorMoveUseCase.execute(VodBrowseCursorMoveSubscriber(),
                VodBrowseCursorMoveUseCase.Params(
                    section, unit, item, itemPosition,
                    page = VodBrowsePage.ITEM
                ))
        }
    }

    fun onPlaybackAction(item: VodListingItem, seriesId: Long? = null, navigate: () -> Unit) {
        newPlaybackUseCase.execute(NewPlaybackSubscriber(navigate),
            VodNewPlaybackUseCase.Params(item, seriesId))
    }

    //endregion
    //region Use Case Subscribers

    inner class VodBrowseCursorSubscriber : DisposableObserver<VodBrowseCursor>() {
        override fun onNext(t: VodBrowseCursor) {
            if (null == t.section || null == t.unit || null == t.item) return
            snapshot.cursor = t
            vodItemUseCase.execute(VodItemSubscriber(), VodItemUseCase.Params(t.item!!.id))
        }
        override fun onError(e: Throwable)  = liveData.postValue(Resource.error(e))
        override fun onComplete() { /* not applicable */ }
    }

    inner class VodItemSubscriber : DisposableSingleObserver<VodListingItem>() {
        override fun onSuccess(t: VodListingItem) {
            snapshot.cursor?: return
            // listing item entity may belong to several units and section, so while it stored
            // it does not reference section and unit - set them here for the fragment operation
            snapshot.details = t.copy(
                sectionId = snapshot.cursor?.section?.id,
                unitId = snapshot.cursor?.unit?.id)
            snapshot.updateScope = VodDigestUpdateScope.DIGEST
            liveData.postValue(Resource.success(snapshot))
        }
        override fun onError(e: Throwable)  = liveData.postValue(Resource.error(e))
    }

    inner class VodBrowseCursorMoveSubscriber : DisposableSingleObserver<VodBrowseCursor>() {
        override fun onSuccess(t: VodBrowseCursor) {
            // moving cursor emits cursor change event which is received in the cursor subscriber
        }
        override fun onError(e: Throwable)  = liveData.postValue(Resource.error(e))
    }

    private inner class NewPlaybackSubscriber (val navigate: () -> Unit)
        : DisposableSingleObserver<VodPlayback>() {
        override fun onSuccess(t: VodPlayback) = navigate()
        override fun onError(e: Throwable)  = liveData.postValue(Resource.error(e))
    }

    private inner class ListingPageSubscriber: DisposableSingleObserver<VodListingPage>() {
        override fun onSuccess(page: VodListingPage) {
            if (page.items.isEmpty()) {
                liveData.postValue(Resource.success()) // stop progress indication, no update
                return
            }
            snapshot.cursor?.unit?.let {
                it.window?.add(page)
                snapshot.updateScope = VodDigestUpdateScope.LISTING
                liveData.postValue(Resource.success(snapshot))

            } ?: liveData.postValue(Resource.success())
        }
        override fun onError(e: Throwable)  = liveData.postValue(Resource.error(e))
    }

    //endregion
}
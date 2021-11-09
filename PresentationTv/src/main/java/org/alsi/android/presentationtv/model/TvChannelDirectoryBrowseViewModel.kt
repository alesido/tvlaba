package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.domain.tv.interactor.guide.*
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowsePage
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import javax.inject.Inject

/** View model to allow user browse channel categories contents in, particularly, Leanback's
 *  media content browser layout.
 *
 */
open class TvChannelDirectoryBrowseViewModel @Inject constructor(
        directoryObservationUseCase: TvChannelDirectoryObservationUseCase,
        private val directoryViewUpdateUseCase: TvChannelDirectoryViewUpdateUseCase,
        private val newPlaybackUseCase: TvNewPlaybackUseCase,
        private val browseCursorGetUseCase: TvBrowseCursorGetUseCase,
        private val browseCursorMoveUseCase: TvBrowseCursorMoveUseCase,
        private val presentationManager: PresentationManager

) : ViewModel() {

    private val liveDirectory: MutableLiveData<Resource<TvChannelDirectoryBrowseLiveData>> = MutableLiveData()

    private var directory: TvChannelDirectory? = null

    val currentPresentation: StreamingServicePresentation? get()
    = presentationManager.provideContext()?.presentation

    val tvPresentations: List<StreamingServicePresentation> get()
    = presentationManager.providePresentations(StreamingServiceKind.TV, skipCurrent = true)

    val vodPresentations: List<StreamingServicePresentation> get()
    = presentationManager.providePresentations(StreamingServiceKind.VOD, skipCurrent = false)

    init {
        liveDirectory.postValue(Resource.loading())
        directoryObservationUseCase.execute(ChannelDirectorySubscriber())
    }

    fun getLiveDirectory(): LiveData<Resource<TvChannelDirectoryBrowseLiveData>> = liveDirectory

    // region Interface

    /**
     * @param listingIndex Index of channel or menu items listing
     * @param itemPosition Position of a channel or menu item in a listing
     * @param item TV channel
     */
    @Suppress("UNUSED_PARAMETER")
    fun onListingItemSelected(listingIndex: Int, itemPosition: Int, item: Any) {
        directory?: return
        if (item is TvChannel && listingIndex > 0
            && listingIndex < directory!!.categories.size - 1) {
            val category = directory!!.categories[listingIndex]
            browseCursorMoveUseCase.execute(BrowseCursorMoveSubscriber(),
                TvBrowseCursorMoveUseCase.Params(
                    category = category,
                    channel = item,
                    page = TvBrowsePage.CHANNELS))
        }
        else {
            browseCursorMoveUseCase.execute(
                BrowseCursorMoveSubscriber(),
                TvBrowseCursorMoveUseCase.Params(page = TvBrowsePage.CHANNELS, reuse = false)
            )
        }
    }

    fun onChannelAction(channel: TvChannel, navigate: () -> Unit) {
        // ensure browse cursor in a correct position before navigating to the details fragment
        val category = directory?.categories?.first { it.id == channel.categoryId }?: return
        browseCursorMoveUseCase.execute(BrowseCursorMoveOnActionSubscriber(navigate),
                TvBrowseCursorMoveUseCase.Params(
                        category = category,
                        channel = channel,
                        page = TvBrowsePage.CHANNELS))
    }

    fun onPause() {
        directoryViewUpdateUseCase.execute(DirectoryViewUpdateSubscriber(),
            TvChannelDirectoryViewUpdateUseCase.Params(TvChannelListWindow.empty(),
                cancelUpdate = true))
    }

    fun onResume() {
// This is not necessary to have correct "page" in Browse Cursor for the sake of
// "onChannelSelected" called back by the RecyclerView upon redraw
//
//        browseCursorMoveUseCase.execute(BrowseCursorMoveSubscriber(),
//            TvBrowseCursorMoveUseCase.Params(
//                page = TvBrowsePage.CHANNELS,
//                reuse = true
//            ))
        browseCursorGetUseCase.execute(BrowseCursorSubscriber())
    }

    // endregion
    // region Dispose

    fun dispose() {
//        directoryObservationUseCase.dispose()
//        directoryViewUpdateUseCase.dispose()
//        newPlaybackUseCase.dispose()
//        browseCursorGetUseCase.dispose()
//        browseCursorMoveUseCase.dispose()
    }

    fun onItemsVisibilityChange(visibleChannelDirectoryItems: TvChannelListWindow) {
        if (visibleChannelDirectoryItems.ids.isEmpty()) return
        directoryViewUpdateUseCase.execute(DirectoryViewUpdateSubscriber(),
                TvChannelDirectoryViewUpdateUseCase.Params(visibleChannelDirectoryItems))
    }

    // endregion
    // region Subscribers

    private inner class ChannelDirectorySubscriber: DisposableObserver<TvChannelDirectory>() {
        override fun onNext(directory: TvChannelDirectory) {
            this@TvChannelDirectoryBrowseViewModel.directory = directory
            browseCursorGetUseCase.execute(BrowseCursorSubscriber())
        }
        override fun onError(e: Throwable) {
            liveDirectory.postValue(Resource.error(e))
        }
        override fun onComplete() { /* not applicable */ }
    }

    inner class DirectoryViewUpdateSubscriber: DisposableCompletableObserver() {
        override fun onComplete() {
            //
        }
        override fun onError(e: Throwable) {
            liveDirectory.postValue(Resource.error(e))
        }
    }

    inner class BrowseCursorSubscriber : DisposableSingleObserver<TvBrowseCursor>() {
        override fun onSuccess(cursor: TvBrowseCursor) {
            val dir = this@TvChannelDirectoryBrowseViewModel.directory?: return
            if (null == cursor.category || null == cursor.channel) {
                liveDirectory.postValue(Resource(ResourceState.SUCCESS,
                        TvChannelDirectoryBrowseLiveData(dir), null))
                return
            }
            val position = TvChannelDirectoryPosition(
                    categoryIndex = dir.categoryIndex(cursor.category!!),
                    channelIndex = (dir.channelIndex(cursor.channel!!)?:-1)
            )
            liveDirectory.postValue(Resource(ResourceState.SUCCESS,
                    TvChannelDirectoryBrowseLiveData(dir, position), null))
        }
        override fun onError(e: Throwable) {
            liveDirectory.postValue(Resource.error(e))
        }
    }

    inner class BrowseCursorMoveSubscriber : DisposableSingleObserver<TvBrowseCursor>() {
        override fun onSuccess(t: TvBrowseCursor) {
            // silently accept it's OK
        }
        override fun onError(e: Throwable) {
            liveDirectory.postValue(Resource.error(e))
        }
    }

    inner class BrowseCursorMoveOnActionSubscriber(private val navigate: () -> Unit)
        : DisposableSingleObserver<TvBrowseCursor>() {
        override fun onSuccess(t: TvBrowseCursor) {
            val channel =  t.channel?: return
            newPlaybackUseCase.execute(NewPlaybackSubscriber(navigate),
                    TvNewPlaybackUseCase.Params(channel.categoryId, channel))
        }
        override fun onError(e: Throwable) {
            liveDirectory.postValue(Resource.error(e))
        }
    }


    inner class NewPlaybackSubscriber(private val navigate: () -> Unit)
        : DisposableSingleObserver<TvPlayback>() {
        override fun onSuccess(t: TvPlayback) {
            navigate()
        }
        override fun onError(e: Throwable) {
            liveDirectory.postValue(Resource.error(e))
        }
    }

    // endregion
}
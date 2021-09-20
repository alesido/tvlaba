package org.alsi.android.datavod.repository

import io.reactivex.Single
import org.alsi.android.datavod.store.VodDirectoryLocalStore
import org.alsi.android.datavod.store.VodDirectoryRemoteStore
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import org.alsi.android.domain.vod.repository.VodRepository
import java.util.concurrent.TimeUnit

/**
 *  This is to hold code which is common to all VOD repositories. Especially that dealing
 *  with local and remote store at the same time.
 */
abstract class VodDataRepository(

    streamingServiceId: Long,
    private val remote: VodDirectoryRemoteStore,
    private val local: VodDirectoryLocalStore

    ): VodRepository(streamingServiceId) {

    override fun getDirectory(): Single<VodDirectory> =
        local.getDirectory().flatMap { localDirectory ->
            if (isDirectoryExpired(localDirectory))
                remote.getDirectory().flatMap { remoteDirectory ->
                    local.putDirectory(remoteDirectory).toSingle { remoteDirectory }
            } else Single.just(localDirectory)
        }

    override fun getListingPage(
        sectionId: Long,
        unitId: Long,
        start: Int,
        length: Int
    ): Single<VodListingPage> =
        local.getListingPage(sectionId, unitId, start).flatMap { localPage ->
            if (isPageExpired(localPage))
                remote.getListingPage(sectionId, unitId, start, length).flatMap { remotePage ->
                    local.putListingPage(remotePage).toSingle { remotePage }
                }
            else
                Single.just(localPage)
        }

    override fun getPromoPage(): Single<VodListingPage>  =
        local.getPromoPage().flatMap { localPage ->
            if (isPromoPageExpired(localPage))
                remote.getPromoPage().flatMap { remotePage ->
                    local.putPromoPage(remotePage).toSingle { remotePage}
                }
            else
                Single.just(localPage)
        }

    override fun search(
        titleSubstring: String,
        sectionId: Long?,
        unitId: Long?,
        start: Int,
        length: Int
    ): Single<VodListingPage> =
        local.getSearchResultPage(titleSubstring, sectionId, unitId, start, length).flatMap { localPage ->
            if (isSearchResultPageExpired(localPage))
                remote.search(titleSubstring, sectionId, unitId, start, length).flatMap { remotePage ->
                    local.putSearchResultPage(remotePage, titleSubstring).toSingle { remotePage }
                }
            else
                Single.just(localPage)
        }

    override fun getListingItem(vodItemId: Long): Single<VodListingItem> =
        local.getListingItem(vodItemId).flatMap { localItem ->
            if (isLocalItemExpired(localItem))
                remote.getListingItem(vodItemId).flatMap { remoteItem ->
                    local.putListingItem(remoteItem).toSingle { remoteItem }
                }
            else
                Single.just(localItem)
        }

    override fun getSingleVideoStream(vodItemId: Long): Single<VideoStream> =
        local.getSingleVideoStream(vodItemId).flatMap { localStream ->
            if (isStreamDataExpired(localStream))
                remote.getSingleVideoStream(vodItemId).flatMap { remoteStream ->
                    local.putSingleVideoStream(vodItemId, remoteStream).toSingle { remoteStream }
                }
            else
                Single.just(localStream)
        }

    override fun getSeriesVideoStream(seriesId: Long): Single<VideoStream> =
        local.getSeriesVideoStream(seriesId).flatMap { localStream ->
            if (isStreamDataExpired(localStream))
                remote.getSeriesVideoStream(seriesId).flatMap { remoteStream ->
                    local.putSingleVideoStream(seriesId, remoteStream).toSingle { remoteStream }
                }
            else
                Single.just(localStream)
        }



    open fun isDirectoryExpired(directory: VodDirectory) =
        directory.isEmpty() || null == directory.timeStamp ||
                now() - (directory.timeStamp?: 0L) > EXPIRATION_DIRECTORY_MILLIS


    open fun isPageExpired(page: VodListingPage) =
        null == page.timeStamp || now() - (page.timeStamp?: 0L) > EXPIRATION_PAGE_MILLIS

    open fun isPromoPageExpired(page: VodListingPage) =
        null == page.timeStamp || now() - (page.timeStamp?: 0L) > EXPIRATION_PAGE_MILLIS

    open fun isSearchResultPageExpired(page: VodListingPage) =
        null == page.timeStamp || now() - (page.timeStamp?: 0L) > EXPIRATION_PAGE_MILLIS

    open fun isLocalItemExpired(item: VodListingItem) =
        null == item.timeStamp || now() - (item.timeStamp?: 0L) > EXPIRATION_ITEM_MILLIS

    open fun isStreamDataExpired(stream: VideoStream) =
        null == stream.timeStamp || now() - (stream.timeStamp?: 0L) > EXPIRATION_STREAM_MILLIS


    /** this is to support now time mocking
     */
    fun now(): Long = System.currentTimeMillis()

    companion object {
         val EXPIRATION_DIRECTORY_MILLIS = TimeUnit.HOURS.toMillis(0)
         val EXPIRATION_PAGE_MILLIS = TimeUnit.HOURS.toMillis(0)
         val EXPIRATION_ITEM_MILLIS = TimeUnit.HOURS.toMillis(0)
         val EXPIRATION_STREAM_MILLIS = TimeUnit.HOURS.toMillis(0)
    }
}
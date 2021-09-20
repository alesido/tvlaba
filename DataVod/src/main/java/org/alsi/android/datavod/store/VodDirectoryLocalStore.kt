package org.alsi.android.datavod.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage

interface VodDirectoryLocalStore {

    /** Connect store interface for user's individual storage section.
     */
    fun switchUser(userLoginName: String)

    fun putDirectory(directory: VodDirectory): Completable
    fun getDirectory(): Single<VodDirectory>

    fun putListingPage(page: VodListingPage): Completable
    fun getListingPage(sectionId: Long, unitId: Long, start: Int): Single<VodListingPage>

    fun putPromoPage(promoPage: VodListingPage): Completable
    fun getPromoPage(): Single<VodListingPage>

    fun putSearchResultPage(page: VodListingPage, titleSubstring: String): Completable

    fun getSearchResultPage(titleSubstring: String,
        sectionId: Long?, unitId: Long?, page: Int, count: Int): Single<VodListingPage>

    fun putListingItem(item: VodListingItem): Completable
    fun getListingItem(vodItemId: Long): Single<VodListingItem>

    fun putSingleVideoStream(vodItemId: Long, stream: VideoStream): Completable
    fun getSingleVideoStream(vodItemId: Long): Single<VideoStream>

    fun putSeriesVideoStream(seriesId: Long, stream: VideoStream): Completable
    fun getSeriesVideoStream(seriesId: Long): Single<VideoStream>
}
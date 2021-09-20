package org.alsi.android.datavod.store

import io.reactivex.Single
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage

/**
 *  The idea behind store that it provides access to stored objects. So, there are methods
 *  to access objects separately or in a structure like directory
 */
interface VodDirectoryRemoteStore {

    fun getDirectory(): Single<VodDirectory>
    fun getSections(): Single<List<VodSection>>
    fun getUnits(sectionId: Long): Single<List<VodUnit>>
    fun getListingPage(sectionId: Long, unitId: Long, start: Int, length: Int): Single<VodListingPage>
    fun getPromoPage(): Single<VodListingPage>
    fun search(titleSubstring: String, sectionId: Long?, unitId: Long?, start: Int, length: Int): Single<VodListingPage>
    fun getListingItem(vodItemId: Long): Single<VodListingItem>
    fun getSingleVideoStream(vodItemId: Long): Single<VideoStream>
    fun getSeriesVideoStream(seriesId: Long): Single<VideoStream>
}
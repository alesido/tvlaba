package org.alsi.android.domain.vod.repository

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.repository.DirectoryRepository
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import javax.inject.Inject

abstract class VodRepository (streamingServiceId: Long) : DirectoryRepository(streamingServiceId) {

    abstract fun getDirectory(): Single<VodDirectory>

    /** Get a page, i.e. limited subset, of a unit's (subsection's) VOD listing.
     *
     *  NOTE The returned page item contains a subset of data sufficient to display it in a list.
     */
    abstract fun getListingPage(
        sectionId: Long,
        unitId: Long,
        start: Int,
        length: Int
    ): Single<VodListingPage>

    /** Get the promotional listing, all items at once. Or, an empty one if not supported&
     *
     *  NOTE This method seems odd because promotional units can be used instead (special
     *  unit type). Actually, it was used once, for Premier, when there was a special UI
     *  requested to display promo in the accordion UI
     */
    abstract fun getPromoPage(): Single<VodListingPage>

    /** Find VOD items which titles contain given substring. If ID of unit not given,
     *  then search in all units of the section. If ID of section  is not set too,
     *  provide global search.
     */
    abstract fun search(
        titleSubstring: String,
        sectionId: Long?,
        unitId: Long?,
        start: Int,
        length: Int
    ): Single<VodListingPage>

    /** Get detailed data on a VOD listing item, i.e. item content variant for the VOD digest.
     *
     * NOTE There are more details then in the listing item variant, still it rather won't have
     * the video stream data. The reason is that life time of video stream data is different from
     * that of the VOD item.
     */
    abstract fun getListingItem(vodItemId: Long): Single<VodListingItem>

    /** Get video stream data for a VOD item having single video, not series.
     * @param vodItemId ID of VOD Listing Item having single video
     */
    abstract fun getSingleVideoStream(vodItemId: Long): Single<VideoStream>

    /** Get video stream data for a series.
     * @param seriesId ID of a series.
     */
    abstract fun getSeriesVideoStream(seriesId: Long): Single<VideoStream>

    /** Reload language dependent parts of the directory to local store if supported
     */
    abstract override fun onLanguageChange(): Completable
}
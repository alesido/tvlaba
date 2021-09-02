package org.alsi.android.moidom.store.vod

import io.reactivex.Single
import org.alsi.android.datavod.store.VodDirectoryRemoteStore
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import org.alsi.android.moidom.mapper.VodDirectorySourceDataMapper
import org.alsi.android.moidom.mapper.VodItemSourceMapper
import org.alsi.android.moidom.mapper.VodListingPageMapper
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.EXTRA_GENRE_BEST_ID
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.EXTRA_GENRE_LAST_ID
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.QUERY_PARAM_VOD_LISTING_TYPE_BEST
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.QUERY_PARAM_VOD_LISTING_TYPE_LAST
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.UNKNOWN_UNIT_ID
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.VOD_SECTION_ID
import java.net.URI
import javax.inject.Inject

class VodDirectoryRemoteStoreMoiDom @Inject constructor(

    private val remoteService: RestServiceMoidom,
    private val remoteSession: RemoteSessionRepositoryMoidom

): VodDirectoryRemoteStore {

    private val directorySourceMapper = VodDirectorySourceDataMapper()
    private val pageSourceMapper = VodListingPageMapper()
    private val itemSourceMapper = VodItemSourceMapper()

    override fun getDirectory(): Single<VodDirectory> = remoteSession.getSessionId()
        .flatMap { remoteService.getVodGenres(it) }
        .map { directorySourceMapper.mapFromSource(it) }


    override fun getSections(): Single<List<VodSection>> = remoteSession.getSessionId()
        .flatMap { remoteService.getVodGenres(it) }
        .map { directorySourceMapper.mapFromSource(it).sections }

    override fun getUnits(sectionId: Long): Single<List<VodUnit>> = remoteSession.getSessionId()
        .flatMap { sid -> remoteService.getVodGenres(sid) }
        .map { directorySourceMapper.mapFromSource(it).sectionById[sectionId]?.units?: listOf() }

    override fun getListingPage(
        sectionId: Long,
        unitId: Long,
        page: Int,
        count: Int
    ): Single<VodListingPage> {
        var vodListingType: String? = null
        var requestParamGenreId: Long? = null
        when (unitId) {
            EXTRA_GENRE_BEST_ID -> vodListingType = QUERY_PARAM_VOD_LISTING_TYPE_BEST
            EXTRA_GENRE_LAST_ID -> vodListingType = QUERY_PARAM_VOD_LISTING_TYPE_LAST
            else -> requestParamGenreId = unitId
        }
        return remoteSession.getSessionId()
            .flatMap {
                remoteService.getGenreVodList(it, vodListingType, requestParamGenreId, page, count)
            }
            .map {
                pageSourceMapper.mapFromSource(sectionId, unitId, page, count, it)
            }
    }

    override fun getPromoPage(): Single<VodListingPage> = Single.just(VodListingPage.empty())

    override fun search(
        titleSubstring: String,
        sectionId: Long?,
        unitId: Long?,
        page: Int,
        count: Int
    ): Single<VodListingPage> = remoteSession.getSessionId()
        .flatMap { remoteService.searchGenreVodList(
            it, RestServiceMoidom.QUERY_PARAM_VOD_LISTING_TYPE_TEXT, sectionId, titleSubstring) }
        .map { pageSourceMapper.mapFromSource(VOD_SECTION_ID, UNKNOWN_UNIT_ID, page, count, it) }

    override fun getListingItem(vodItemId: Long): Single<VodListingItem> = remoteSession.getSessionId()
        .flatMap { remoteService.getVodInfo(it, vodItemId) }
        .map { itemSourceMapper.mapFromSource(it) }

    override fun getSingleVideoStream(vodItemId: Long): Single<VideoStream> = remoteSession.getSessionId()
        .flatMap { remoteService.getVodStreamUrl(
            sid = it,
            videoId = vodItemId)
        }
        .map { VideoStream(URI.create(it.url), VideoStreamKind.RECORD) }

    override fun getSeriesVideoStream(seriesId: Long): Single<VideoStream> = remoteSession.getSessionId()
        .flatMap { remoteService.getVodStreamUrl(
            sid = it,
            videoId = seriesId)
        }
        .map { VideoStream(URI.create(it.url), VideoStreamKind.RECORD) }
}
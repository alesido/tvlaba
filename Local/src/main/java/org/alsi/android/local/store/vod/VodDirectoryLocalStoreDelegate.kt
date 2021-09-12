package org.alsi.android.local.store.vod

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.alsi.android.datavod.store.VodDirectoryLocalStore
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import org.alsi.android.local.mapper.vod.*
import org.alsi.android.local.model.user.UserAccountSubject
import org.alsi.android.local.model.vod.*

class VodDirectoryLocalStoreDelegate(
    private val serviceBoxStore: BoxStore,
    accountSubject: UserAccountSubject
): VodDirectoryLocalStore {

    private var userLoginName: String = "guest"

    private val directoryBox: Box<VodDirectoryEntity> = serviceBoxStore.boxFor()
    private val sectionsBox: Box<VodSectionEntity> = serviceBoxStore.boxFor()
    private val unitsBox: Box<VodUnitEntity> = serviceBoxStore.boxFor()

    private val pageBox: Box<VodListingPageEntity> = serviceBoxStore.boxFor()
    private val itemBox: Box<VodListingItemEntity> = serviceBoxStore.boxFor()

    private val videoSingleBox: Box<VodVideoSingleStreamEntity> = serviceBoxStore.boxFor()
    private val videoSeriesBox: Box<VodVideoSeriesStreamEntity> = serviceBoxStore.boxFor()

    private val directoryMapper = VodDirectoryEntityMapper()
    private val sectionMapper = VodSectionEntityMapper()
    private val unitMapper = VodUnitEntityMapper()

    private val pageMapperWriter = VodListingPageEntityMapperWriter()
    private val itemMapperWriter = VodListingItemEntityMapperWriter()

    private val disposables = CompositeDisposable()

    init {
        val s = accountSubject.subscribe {
            switchUser(it.loginName)
        }
        s?.let { disposables.add(it) }
    }

    override fun switchUser(userLoginName: String) {
        this.userLoginName = userLoginName
    }

    /**
     * @see "https://docs.objectbox.io/relations#updating-tomany"
     */
    override fun putDirectory(directory: VodDirectory) = Completable.fromRunnable {

        val sectionEntities: MutableList<VodSectionEntity> = mutableListOf()
        directory.sections.forEach { section ->
            // Rule#2: put targets of ToMany relation to store before adding them to the ToMany
            // property of the owner entity, of course, only if they have @Id(assignable = true):
            val unitEntities = section.units.map { unitMapper.mapToEntity(it) }
            unitsBox.put(unitEntities)

            // Rule#1: If the owning, source Object uses @Id(assignable = true) attach its Box
            // before modifying its ToMany:
            val sectionEntity = VodSectionEntity(section.id, section.title)
            sectionsBox.attach(sectionEntity)
            sectionEntity.units.addAll(unitEntities)
            sectionEntities.add(sectionEntity)
        }
        // Rule#2 applied:
        sectionsBox.put(sectionEntities)

        // Rule#1 applied:
        val directoryEntity = VodDirectoryEntity(
            VodDirectoryEntity.SINGLE_RECORD_DIRECTORY_ID, System.currentTimeMillis())
        directoryBox.attach(directoryEntity)
        directoryEntity.sections.clear()
        directoryEntity.sections.addAll(sectionEntities)

        directoryBox.put(directoryEntity)
    }

    override fun getDirectory(): Single<VodDirectory> = Single.fromCallable {
        directoryMapper.mapFromEntity(
            directoryBox.get(VodDirectoryEntity.SINGLE_RECORD_DIRECTORY_ID)
        )
    }

    override fun putListingPage(page: VodListingPage) = Completable.fromRunnable {
        pageMapperWriter.putEntity(serviceBoxStore, page)
    }

    override fun getListingPage(
        sectionId: Long,
        unitId: Long,
        page: Int,
        count: Int
    ): Single<VodListingPage> = Single.fromCallable {
        val found = pageBox.query {
            equal(VodListingPageEntity_.sectionId, sectionId)
            equal(VodListingPageEntity_.unitId, unitId)
            equal(VodListingPageEntity_.pageNumber, page.toLong())
            equal(VodListingPageEntity_.count, count.toLong())
        }.findUnique()
        found?.let {pageMapperWriter.mapFromEntity(it) }
    }

    override fun putPromoPage(promoPage: VodListingPage): Completable {
        TODO("Not yet implemented")
    }

    override fun getPromoPage(): Single<VodListingPage> {
        TODO("Not yet implemented")
    }

    override fun putSearchResultPage(page: VodListingPage, titleSubstring: String): Completable {
        TODO("Not yet implemented")
    }

    override fun getSearchResultPage(
        titleSubstring: String,
        sectionId: Long?,
        unitId: Long?,
        page: Int,
        count: Int
    ): Single<VodListingPage> {
        TODO("Not yet implemented")
    }

    override fun putListingItem(item: VodListingItem) = Completable.fromRunnable {
        itemMapperWriter.putEntity(serviceBoxStore, item, true)
    }

    override fun getListingItem(vodItemId: Long): Single<VodListingItem> = Single.fromCallable {
        itemMapperWriter.mapFromEntity(itemBox.get(vodItemId))
    }

    override fun putSingleVideoStream(vodItemId: Long, stream: VideoStream) = Completable.fromRunnable {
        videoSingleBox.put(VodVideoSingleStreamEntity(vodItemId = vodItemId, streamUri = stream.uri,
            subtitlesUri = stream.subtitles, timeStamp = System.currentTimeMillis()))
    }

    override fun getSingleVideoStream(vodItemId: Long): Single<VideoStream> = Single.fromCallable {
        val entity = videoSingleBox.query {
            equal(VodVideoSingleStreamEntity_.vodItemId, vodItemId)
        }.findUnique()
        entity?.let {
            VideoStream(it.streamUri, VideoStreamKind.RECORD, it.subtitlesUri, it.timeStamp)
        } ?: VideoStream.empty()
    }

    override fun putSeriesVideoStream(seriesId: Long, stream: VideoStream) = Completable.fromRunnable {
        videoSeriesBox.put(VodVideoSeriesStreamEntity(seriesId = seriesId, streamUri = stream.uri,
            subtitlesUri = stream.subtitles, timeStamp = System.currentTimeMillis()))
    }

    override fun getSeriesVideoStream(seriesId: Long) = Single.fromCallable {
        val entity = videoSeriesBox.query {
            equal(VodVideoSeriesStreamEntity_.seriesId, seriesId)
        }.findUnique()
        entity?.let {
            VideoStream(it.streamUri, VideoStreamKind.RECORD, it.subtitlesUri, it.timeStamp)
        } ?: VideoStream.empty()
    }
}
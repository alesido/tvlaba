package org.alsi.android.local.mapper.vod

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem.Video
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import org.alsi.android.local.model.vod.*
import java.net.URI

/**
 * @Id(assignable = true) handling rules:
 *
 * Rule#1: If the owning, source Object uses @Id(assignable = true) attach its Box before
 * modifying its ToMany property
 *
 * Rule#2: Put targets of ToMany relation to store before adding them to the ToMany property of
 * the owner entity, of course, only if they have @Id(assignable = true)
 */
class VodListingPageEntityMapperWriter: EntityMapper<VodListingPageEntity, VodListingPage> {
    private val itemMapperWriter = VodListingItemEntityMapperWriter()
    override fun mapFromEntity(entity: VodListingPageEntity) = with(entity) { VodListingPage (
        sectionId?: -1L, unitId?: -1L,
        total, pageNumber, count,
        items.map { itemMapperWriter.mapFromEntity(it) },
        timeStamp
    )}
    override fun mapToEntity(domain: VodListingPage): VodListingPageEntity = with(domain) {
        val entity = VodListingPageEntity (
            id = 0L,
            if (sectionId > 0) sectionId else null,
            if (unitId > 0) unitId else null,
            total, pageNumber, count, System.currentTimeMillis()
        )
        entity.items.addAll(items.map { itemMapperWriter.mapToEntity(it) })
        entity
    }
    fun putEntity(store: BoxStore, domain: VodListingPage): VodListingPageEntity = with(domain) {
        val entity = VodListingPageEntity (
            id = 0L,
            if (sectionId > 0) sectionId else null,
            if (unitId > 0) unitId else null,
            total, pageNumber, count, System.currentTimeMillis()
        )
        val itemEntities = items.map { itemMapperWriter.putEntity(store, it) }
        store.boxFor(VodListingItemEntity::class.java).put(itemEntities) // Rule#2
        entity.items.clear() // precaution
        entity.items.addAll(itemEntities)
        store.boxFor(VodListingPageEntity::class.java).put(entity)
        entity
    }
}

class VodListingItemEntityMapperWriter: EntityMapper<VodListingItemEntity, VodListingItem> {
    private val singleMapper = VideoSingleEntityMapperWriter()
    private val serialMapper = VideoSerialEntityMapperWriter()
    private val postersMapper = VodPostersMapper()
    private val attributesMapper = VodAttributesMapper()
    override fun mapFromEntity(entity: VodListingItemEntity) = with(entity) {
        VodListingItem(id, sectionId, unitId, title, description,
            when {
                videoSingle.target != null -> singleMapper.mapFromEntity(videoSingle.target)
                videoSerial.target != null -> serialMapper.mapFromEntity(videoSerial.target)
                else -> null
            },
            postersMapper.mapFromEntity(posters),
            attributesMapper.mapFromEntity(attributes.target),
            timeStamp
        )
    }
    override fun mapToEntity(domain: VodListingItem) = with(domain) {
        val entity = VodListingItemEntity (id, sectionId, unitId, title, description,
            System.currentTimeMillis())
        video.let {
            if (video is Video.Single)
                entity.videoSingle.target = singleMapper.mapToEntity(video as Video.Single)
            else
                entity.videoSerial.target = serialMapper.mapToEntity(video as Video.Serial)
        }
        posters?.let { entity.posters.addAll(postersMapper.mapToEntity(it)) }
        attributes?.let { entity.attributes.target = attributesMapper.mapToEntity(it) }
        entity
    }
    fun putEntity(store: BoxStore, domain: VodListingItem, writeEntity: Boolean = false): VodListingItemEntity  = with(domain) {
        val itemBox: Box<VodListingItemEntity> = store.boxFor()
        val entity = VodListingItemEntity (id, sectionId, unitId, title, description,
            System.currentTimeMillis())
        itemBox.attach(entity) // Rule#1
        video.let {
            if (video is Video.Single)
                entity.videoSingle.target = singleMapper.putEntity(store, video as Video.Single) // Rule#2
            else
                entity.videoSerial.target = serialMapper.putEntity(store, video as Video.Serial) // Rule#2
        }
        // entities of posters have no assigned IDs, so they'll be put automatically
        posters?.let { entity.posters.addAll(postersMapper.mapToEntity(it)) }
        attributes?.let {
            // Rule#2 indirect: genres of attributes have assigned IDs
            entity.attributes.target = attributesMapper.mapToEntity(it)
        }
        // Rule#2 May br delegated to a caller in order of optimization by putting a list of listing item entities at once
        if (writeEntity) itemBox.put(entity)
        entity
    }
}

class VideoSingleEntityMapperWriter: EntityMapper<VideoSingleEntity, Video.Single> {
    private val trackMapper = VodTrackEntityMapper()
    override fun mapFromEntity(entity: VideoSingleEntity) = with(entity) {
        Video.Single(id, title, description, uri, tracks.map { trackMapper.mapFromEntity(it) }, durationMillis)
    }
    override fun mapToEntity(domain: Video.Single) = with(domain) {
        val entity = VideoSingleEntity(id, title, description, uri, durationMillis)
        tracks?.let { list -> entity.tracks.addAll( list.map { trackMapper.mapToEntity(it) })}
        entity
    }
    fun putEntity(store: BoxStore, domain: Video.Single): VideoSingleEntity = with(domain) {
        val singleBox: Box<VideoSingleEntity> = store.boxFor()
        val entity = VideoSingleEntity(id, title, description, uri, durationMillis)
        singleBox.attach(entity)
        tracks?.let { list ->
            // entities of tracks have no assigned ID, so they do not put here
            entity.tracks.addAll( list.map { trackMapper.mapToEntity(it) })
        }
        // Rule#2 for OneToOne relation
        singleBox.put(entity) // Rule#2
        entity
    }
}

class VideoSerialEntityMapperWriter: EntityMapper<VideoSerialEntity, Video.Serial> {
    private val seriesMapper = VideoSeriesEntityMapperWriter()
    override fun mapFromEntity(entity: VideoSerialEntity) = with(entity) {
            Video.Serial(id, title, description, series.map { seriesMapper.mapFromEntity(it) })
        }
    override fun mapToEntity(domain: Video.Serial) = with(domain) {
        val entity = VideoSerialEntity(id, title, description)
        entity.series.addAll(series.map { seriesMapper.mapToEntity(it) })
        entity
    }
    fun putEntity(store: BoxStore, domain: Video.Serial): VideoSerialEntity = with(domain) {
        val serialBox: Box<VideoSerialEntity> = store.boxFor()
        val entity = VideoSerialEntity(id, title, description)
        serialBox.attach(entity) // Rule#1
        val seriesEntities = series.map { seriesMapper.putEntity(store, it) }
        store.boxFor(VideoSeriesEntity::class.java).put(seriesEntities) // Rule#2
        entity.series.clear() // precaution
        entity.series.addAll(seriesEntities)
        serialBox.put(entity) // Rule#2 for OneToOne relation
        entity
    }
}

class VideoSeriesEntityMapperWriter: EntityMapper<VideoSeriesEntity, Video.Series> {
    private val trackMapper = VodTrackEntityMapper()
    override fun mapFromEntity(entity: VideoSeriesEntity) = with(entity) {
        Video.Series(id, title, season, episode, description, uri,
            tracks.map { trackMapper.mapFromEntity(it) },
            durationMillis?: 0L)
    }
    override fun mapToEntity(domain: Video.Series) = with(domain) {
        val entity = VideoSeriesEntity(id, season, episode, title, description, uri, durationMillis)
        tracks?.let { list ->
            entity.tracks.addAll( list.map { trackMapper.mapToEntity(it) })
        }
        entity
    }
    fun putEntity(store: BoxStore, domain: Video.Series): VideoSeriesEntity = with(domain) {
        val entity = VideoSeriesEntity(id, season, episode, title, description, uri, durationMillis)
        store.boxFor(VideoSeriesEntity::class.java).attach(entity) // Rule#1 applicable even if there is no ToMany relation with targets with assigned IDs
        tracks?.let { list ->
            // entities of tracks have no assigned ID, so they do not put here
            entity.tracks.addAll( list.map { trackMapper.mapToEntity(it) })
        }
        // Rule#2 delegated to mapper-writer for serial entity to optimize by putting all series video entities at once
        entity
    }
}

class VodTrackEntityMapper: EntityMapper<VodTrackEntity, VodListingItem.Track> {
    override fun mapFromEntity(entity: VodTrackEntity) = with(entity) {
        when (type) {
            VodTrackEntity.TRACK_VIDEO -> VodListingItem.Track.Video(id, languageCode, title)
            VodTrackEntity.TRACK_AUDIO -> VodListingItem.Track.Audio(id, languageCode, title)
            VodTrackEntity.TRACK_SUBTITLES -> VodListingItem.Track.Subtitles(id, languageCode, title)
            else -> VodListingItem.Track.Audio(id, languageCode, title)
        }
    }
    override fun mapToEntity(domain: VodListingItem.Track) = with(domain) {
        VodTrackEntity(id,
            when (domain) {
                is VodListingItem.Track.Video -> VodTrackEntity.TRACK_VIDEO
                is VodListingItem.Track.Audio -> VodTrackEntity.TRACK_AUDIO
                is VodListingItem.Track.Subtitles -> VodTrackEntity.TRACK_SUBTITLES
                else -> VodTrackEntity.TRACK_SUBTITLES
            },
            languageCode, title)
    }
}

class VodPostersMapper: EntityMapper<List<VodPosterEntity>, VodListingItem.Posters> {
    override fun mapFromEntity(entity: List<VodPosterEntity>): VodListingItem.Posters  {
        val gallery: MutableList<URI> = mutableListOf()
        for (i in 1 until entity.size) entity[i].uri?.let { gallery.add(it) }
        return VodListingItem.Posters (entity[0].uri, gallery)
    }
    override fun mapToEntity(domain: VodListingItem.Posters) = with(domain) {
        val list: MutableList<VodPosterEntity> = mutableListOf()
        poster?.let { list.add(VodPosterEntity(uri = it)) }
        gallery?.let {posters -> posters.map { list.add(VodPosterEntity(uri = it)) }}
        list
    }
}

class VodAttributesMapper: EntityMapper<VodAttributesEntity, VodListingItem.Attributes> {
    private val genreMapper = VodGenreEntityMapper()
    private val creditMapper = VodCreditEntityMapper()
    override fun mapFromEntity(entity: VodAttributesEntity) = with(entity)  {
        VodListingItem.Attributes(
            durationMillis,
            genres.map { genreMapper.mapFromEntity(it) },
            credits.map { creditMapper.mapFromEntity(it) },
            year, country,
            quality, ageLimit, kinopoiskRate, imdbRate
        )
    }
    override fun mapToEntity(domain: VodListingItem.Attributes) = with(domain)  {
        val entity = VodAttributesEntity(
            0L, durationMillis,
            year, country,
            quality, ageLimit, kinopoiskRate, imdbRate
        )
        genres?.let { list -> entity.genres.addAll( list.map { genreMapper.mapToEntity(it) }) }
        credits?.let { list -> entity.credits.addAll( list.map { creditMapper.mapToEntity(it) }) }
        entity
    }
}

class VodGenreEntityMapper: EntityMapper<VodGenreEntity, VodListingItem.Genre> {
    override fun mapFromEntity(entity: VodGenreEntity)
    = VodListingItem.Genre (entity.genreId, entity.title)
    override fun mapToEntity(domain: VodListingItem.Genre)
    = VodGenreEntity (0L, domain.genreId, domain.title)
}

class VodCreditEntityMapper: EntityMapper<VodCreditEntity, VodListingItem.Credit> {
    override fun mapFromEntity(entity: VodCreditEntity)
    = VodListingItem.Credit (entity.name, entity.role)
    override fun mapToEntity(domain: VodListingItem.Credit)
    = VodCreditEntity (0L, domain.name, domain.role)
}
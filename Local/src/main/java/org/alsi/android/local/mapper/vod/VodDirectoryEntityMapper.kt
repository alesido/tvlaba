package org.alsi.android.local.mapper.vod

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.local.model.vod.VodDirectoryEntity
import org.alsi.android.local.model.vod.VodSectionEntity
import org.alsi.android.local.model.vod.VodUnitEntity

class VodDirectoryEntityMapper: EntityMapper<VodDirectoryEntity, VodDirectory> {

    private val sectionMapper = VodSectionEntityMapper()

    override fun mapFromEntity(entity: VodDirectoryEntity) = VodDirectory(
        sections = entity.sections.map { sectionMapper.mapFromEntity(it) },
        timeStamp = entity.timeStamp
    )

    override fun mapToEntity(domain: VodDirectory): VodDirectoryEntity {
        val entity = VodDirectoryEntity(
            VodDirectoryEntity.SINGLE_RECORD_DIRECTORY_ID,
            System.currentTimeMillis())
        entity.sections.addAll( domain.sections.map { sectionMapper.mapToEntity(it) })
        return entity
    }
}

class VodSectionEntityMapper: EntityMapper<VodSectionEntity, VodSection> {

    private val unitMapper = VodUnitEntityMapper()

    override fun mapFromEntity(entity: VodSectionEntity) = with(entity) { VodSection(
            id, title, units.map { unitMapper.mapFromEntity(it) }
        )}


    override fun mapToEntity(domain: VodSection): VodSectionEntity {
        val entity = VodSectionEntity(domain.id, domain.title)
        entity.units.addAll( domain.units.map { unitMapper.mapToEntity(it) })
        return entity
    }
}

class VodUnitEntityMapper: EntityMapper<VodUnitEntity, VodUnit> {

    override fun mapFromEntity(entity: VodUnitEntity): VodUnit
    = with(entity) { VodUnit(id, entity.section.targetId, title, total) }

    override fun mapToEntity(domain: VodUnit): VodUnitEntity
    = with(domain) { VodUnitEntity(id, title, total) }
}
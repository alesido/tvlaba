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
        sections = entity.sections.sortedBy{ it.ordinal }.map{ sectionMapper.mapFromEntity(it) },
        timeStamp = entity.timeStamp
    )

    override fun mapToEntity(domain: VodDirectory): VodDirectoryEntity {
        val entity = VodDirectoryEntity(
            VodDirectoryEntity.SINGLE_RECORD_DIRECTORY_ID,
            System.currentTimeMillis())
        entity.sections.addAll( domain.sections.mapIndexed { index, section -> sectionMapper.mapToEntity(section, index) })
        return entity
    }
}

class VodSectionEntityMapper: EntityMapper<VodSectionEntity, VodSection> {

    private val unitMapper = VodUnitEntityMapper()

    override fun mapFromEntity(entity: VodSectionEntity) = with(entity) { VodSection(
        id, title, units.sortedBy{ it.ordinal }.map { unitMapper.mapFromEntity(it) },
        isSectionSubstitute
    )}

    fun mapToEntity(domain: VodSection, index: Int): VodSectionEntity {
        val entity = VodSectionEntity(domain.id, domain.title, index, domain.isSectionSubstitute)
        entity.units.addAll( domain.units.mapIndexed { unitIndex, unit ->
            unitMapper.mapToEntity(unit, unitIndex)
        })
        return entity
    }

    override fun mapToEntity(domain: VodSection): VodSectionEntity {
        TODO("Not applicable, see another variant")
    }
}

class VodUnitEntityMapper: EntityMapper<VodUnitEntity, VodUnit> {

    override fun mapFromEntity(entity: VodUnitEntity): VodUnit
    = with(entity) { VodUnit(id, entity.section.targetId, title, total) }

    fun mapToEntity(domain: VodUnit, index: Int): VodUnitEntity
    = with(domain) { VodUnitEntity(id, title, total, index) }

    override fun mapToEntity(domain: VodUnit): VodUnitEntity {
        TODO("Not applicable, see another variant")
    }
}
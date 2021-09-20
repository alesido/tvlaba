package org.alsi.android.moidom.mapper

import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.moidom.model.vod.VodGenresResponse
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.remote.mapper.SourceDataMapper

class VodDirectorySourceDataMapper: SourceDataMapper<VodGenresResponse, VodDirectory> {

    override fun mapFromSource(source: VodGenresResponse): VodDirectory {

        val dstUnits = source.genres.map { rec -> VodUnit(
            id = rec.id.toLong() + RestServiceMoidom.EXTRA_GENRE_ID_OFFSET,
            title = rec.name,
            total = rec.count
        )}.toMutableList()

        // TODO Domain level asset for special genre titles, the same scheme as for ExceptionMessages
        dstUnits.add(0, VodUnit(id = RestServiceMoidom.EXTRA_GENRE_BEST_ID, title = "BEST"))
        dstUnits.add(1, VodUnit(id = RestServiceMoidom.EXTRA_GENRE_LAST_ID, title = "LAST"))

        return VodDirectory(listOf(
            VodSection(
                RestServiceMoidom.VOD_SECTION_SUBSTITUTE_ID,
                units = dstUnits,
                isSectionSubstitute = true)
        ))
    }
}
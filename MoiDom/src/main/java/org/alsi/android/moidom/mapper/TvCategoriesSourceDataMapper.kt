package org.alsi.android.moidom.mapper

import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.moidom.model.tv.GetTvGroupResponse
import org.alsi.android.remote.mapper.SourceDataMapper

class TvCategoriesSourceDataMapper: SourceDataMapper<GetTvGroupResponse, List<TvChannelCategory>> {

    override fun mapFromSource(source: GetTvGroupResponse): List<TvChannelCategory> {
        val categories: MutableList<TvChannelCategory> = mutableListOf()
        val iconPathMapper = TvCategoryIconPathMapper(source)
        var groupOrdinal = 0
        source.groups.forEach {
            categories.add(TvChannelCategory(
                    id = it.id.toLong(),
                    ordinal = groupOrdinal++,
                    title = it.name,
                    logo = iconPathMapper.fromPath(it.icon_path)))
        }
        return categories
    }
}
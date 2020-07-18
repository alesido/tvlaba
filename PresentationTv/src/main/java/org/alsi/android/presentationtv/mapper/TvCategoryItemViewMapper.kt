package org.alsi.android.presentationtv.mapper

import android.net.Uri
import org.alsi.android.presentation.mapper.Mapper
import org.alsi.android.domain.implementation.model.IconType
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.presentation.mapper.getDrawableIdentifierByName
import org.alsi.android.presentationtv.model.TvCategoryItemViewModel

open class TvCategoryItemViewMapper: Mapper<TvCategoryItemViewModel, TvChannelCategory> {

    override fun mapToView(type: TvChannelCategory): TvCategoryItemViewModel {
        return when(type.logo?.kind) {
            IconType.LOCAL_VECTOR -> TvCategoryItemViewModel(type.id, type.title,
                    getDrawableIdentifierByName(type.logo?.reference?:"unknown")?: 0)
            IconType.LOCAL_RASTER -> TvCategoryItemViewModel(type.id, type.title,
                    getDrawableIdentifierByName(type.logo?.reference?:"unknown")?: 0)
            IconType.REMOTE_RASTER -> TvCategoryItemViewModel(type.id, type.title,
                    Uri.parse(type.logo?.reference))
            else -> TvCategoryItemViewModel(type.id, type.title)
        }
    }
}
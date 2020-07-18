package org.alsi.android.tvlaba.mobile.tv.categories

import android.net.Uri
import androidx.annotation.DrawableRes
import org.alsi.android.presentationtv.model.TvCategoryItemViewModel
import org.alsi.android.tvlaba.mobile.mapper.ViewMapper
import javax.inject.Inject

/**
 * This seemingly odd class is necessary because #TvCategoryItemViewModel cannot be used directly
 * as it defined in a different module, - smart cast is impossible.
 */
class TvCategoryItem(val id: Long, val title: String, @DrawableRes val logoDrawableRes: Int?, val logoRasterUri: Uri?)

class TvCategoryItemViewMapper @Inject constructor(): ViewMapper<TvCategoryItemViewModel, TvCategoryItem> {
    override fun mapToView(presentation: TvCategoryItemViewModel): TvCategoryItem {
        return TvCategoryItem(presentation.id, presentation.title, presentation.logoDrawableRes, presentation.logoRasterUri)
    }
}
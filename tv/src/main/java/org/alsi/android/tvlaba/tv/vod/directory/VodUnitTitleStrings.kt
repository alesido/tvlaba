package org.alsi.android.tvlaba.tv.vod.directory

import android.content.Context
import androidx.annotation.StringRes
import org.alsi.android.domain.vod.model.guide.directory.VodUnitTitles
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.framework.validateContext
import javax.inject.Inject

class VodUnitTitleStrings @Inject constructor(private var context: Context): VodUnitTitles {

    private val appContext = context

    override fun changeContext(replacementContext: Any) {
        if (replacementContext !is Context) return
        context = validateContext(replacementContext, appContext)
    }

    override fun last(): String = s(R.string.title_vod_unit_last)

    override fun best(): String = s(R.string.title_vod_unit_best)

    fun s(@StringRes id: Int)
            = validateContext(context, appContext).getString(id)
}
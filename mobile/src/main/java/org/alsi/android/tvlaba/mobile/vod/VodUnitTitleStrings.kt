package org.alsi.android.tvlaba.mobile.vod

import android.content.Context
import org.alsi.android.domain.vod.model.guide.directory.VodUnitTitles
import javax.inject.Inject

class VodUnitTitleStrings @Inject constructor(private var context: Context): VodUnitTitles {

    private val appContext = context

    override fun changeContext(replacementContext: Any) {
        if (replacementContext !is Context) return
        TODO("Not yet implemented")
    }

    override fun last(): String {
        TODO("Not yet implemented")
    }

    override fun best(): String {
        TODO("Not yet implemented")
    }
}
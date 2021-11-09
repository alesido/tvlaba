package org.alsi.android.domain.tv.model.guide

import org.alsi.android.domain.implementation.model.TypedIconReference

class TvChannelCategory(
    val id: Long,
    val ordinal: Int? = null,
    val title: String,
    val logo: TypedIconReference? = null
) {
    fun isEmpty() = id == -1L

    companion object {
        fun empty() = TvChannelCategory(-1L, title = "")
    }
}



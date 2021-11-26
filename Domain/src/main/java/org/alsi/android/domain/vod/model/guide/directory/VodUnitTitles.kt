package org.alsi.android.domain.vod.model.guide.directory

interface VodUnitTitles {

    fun changeContext(replacementContext: Any)

    fun last(): String
    fun best(): String
}
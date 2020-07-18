package org.alsi.android.tvlaba.tv.mapper

interface ViewMapper<in P, out V> {

    fun mapToView(presentation: P): V

}
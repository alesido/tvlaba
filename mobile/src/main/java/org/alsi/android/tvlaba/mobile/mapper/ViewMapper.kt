package org.alsi.android.tvlaba.mobile.mapper

interface ViewMapper<in P, out V> {

    fun mapToView(presentation: P): V

}
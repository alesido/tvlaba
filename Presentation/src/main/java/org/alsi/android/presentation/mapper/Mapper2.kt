package org.alsi.android.presentation.mapper

interface Mapper2<out V, in D1, D2> {

    fun mapToView(type: D1, subtype: D2): V
}
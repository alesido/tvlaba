package org.alsi.android.remote.mapper

/**
 * Created on 7/30/18.
 */
interface SourceDataMapper<S, D> {
    fun mapFromSource(source: S): D
}
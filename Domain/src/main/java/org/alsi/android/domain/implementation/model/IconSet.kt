package org.alsi.android.domain.implementation.model

/**
 * Created on 7/6/18.
 */
enum class IconType {
    LOCAL_VECTOR, // vector drawable found by a conventional reference name
    LOCAL_RASTER, // raster drawable found by a conventional reference name
    REMOTE_RASTER // raster drawable represented by a raster file URI
}

open class IconSet(val kind : IconType, val set : Map<Long, String>)
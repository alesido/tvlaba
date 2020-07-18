package org.alsi.android.domain.implementation.model

enum class IconType {
    UNKNOWN,
    LOCAL_VECTOR, // vector drawable found by a conventional reference name
    LOCAL_RASTER, // raster drawable found by a conventional reference name
    REMOTE_RASTER // raster drawable represented by a raster file URI
}

class TypedIconReference(val kind : IconType, val reference: String)

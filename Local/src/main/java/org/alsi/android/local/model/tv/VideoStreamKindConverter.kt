package org.alsi.android.local.model.tv

import io.objectbox.converter.PropertyConverter
import org.alsi.android.domain.streaming.model.VideoStreamKind

class VideoStreamKindConverter: PropertyConverter<VideoStreamKind, Int> {

    override fun convertToDatabaseValue(kind: VideoStreamKind): Int = kind.ordinal

    override fun convertToEntityProperty(databaseValue: Int): VideoStreamKind =
            if (databaseValue < VideoStreamKind.values().size)
                VideoStreamKind.values()[databaseValue] else VideoStreamKind.UNKNOWN
}
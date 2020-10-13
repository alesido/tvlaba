package org.alsi.android.local.model.settings

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import io.objectbox.relation.ToOne
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlFunction

@Entity
data class RemoteControlKeyEntity (
        @Id var id: Long,

        @Convert(converter = RcFunctionPropertyConverter::class, dbType = Long::class)
        val function: RcFunctionProperty,

        val keyCode: Int) {

    lateinit var device: ToOne<DeviceModelOptionEntity>

    constructor() : this(0L, RcFunctionProperty.UNKNOWN, -1)
}

enum class RcFunctionProperty(val id: Long, val reference: RemoteControlFunction) {
    UNKNOWN(0L, RemoteControlFunction.UNKNOWN),
    TV_MODE(1L, RemoteControlFunction.TV_MODE),
    VOD_MODE(2L, RemoteControlFunction.VOD_MODE),
    ASPECT_RATIO(3L, RemoteControlFunction.ASPECT_RATIO),
    TRACK_SELECT(4L, RemoteControlFunction.TRACK_SELECT),
    REWIND_LEFT(5L, RemoteControlFunction.REWIND_LEFT),
    REWIND_RIGHT(6L, RemoteControlFunction.REWIND_RIGHT),
    REWIND_LEFT_FASTER(7L, RemoteControlFunction.REWIND_LEFT_FASTER),
    REWIND_RIGHT_FASTER(8L, RemoteControlFunction.REWIND_RIGHT_FASTER),
    PREVIOUS_DAY(9L, RemoteControlFunction.PREVIOUS_DAY),
    NEXT_DAY(10L, RemoteControlFunction.NEXT_DAY),
    PREVIOUS_PROGRAM(11L, RemoteControlFunction.PREVIOUS_PROGRAM),
    NEXT_PROGRAM(12L, RemoteControlFunction.NEXT_PROGRAM),
    ADD_FAVORITE_CHANNEL(13L, RemoteControlFunction.ADD_FAVORITE_CHANNEL),
    DELETE_FAVORITE_CHANNEL(14L, RemoteControlFunction.DELETE_FAVORITE_CHANNEL);
    companion object {
        val valueById = values().map { it.id to it }.toMap()
        val valueByReference = values().map { it.reference to it }.toMap()
    }
}

class RcFunctionPropertyConverter: PropertyConverter<RcFunctionProperty, Long> {
    override fun convertToDatabaseValue(entityProperty: RcFunctionProperty?): Long {
        return entityProperty?.id?: RcFunctionProperty.UNKNOWN.id
    }

    override fun convertToEntityProperty(databaseValue: Long?): RcFunctionProperty {
        return databaseValue?. let { RcFunctionProperty.valueById[it]}?: RcFunctionProperty.UNKNOWN
    }
}
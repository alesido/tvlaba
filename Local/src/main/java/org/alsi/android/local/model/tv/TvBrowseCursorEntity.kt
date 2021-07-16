package org.alsi.android.local.model.tv

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import org.alsi.android.domain.tv.model.session.TvBrowsePage
import org.alsi.android.local.framework.objectbox.LocalDateConverter
import org.joda.time.LocalDate

@Entity
data class TvBrowseCursorEntity(

        @Id var id: Long = 0L,

        var userLoginName: String = "guest",

        var categoryId: Long = 0L,
        var channelId: Long = 0L,

        @Convert(converter = LocalDateConverter::class, dbType = Long::class)
        var scheduleDate: LocalDate,
        var programId: Long = 0L,

        @Convert(converter = TvBrowsePageConverter::class, dbType = Long::class)
        var page: TvBrowsePageProperty? = null,

        var timeStamp: Long = 0L,
)

enum class TvBrowsePageProperty(val id: Long, val value: TvBrowsePage) {
        UNKNOWN(0L, TvBrowsePage.UNKNOWN),
        CATEGORIES(1L, TvBrowsePage.CATEGORIES),
        CHANNELS(2L, TvBrowsePage.CHANNELS),
        SCHEDULE(3L, TvBrowsePage.SCHEDULE),
        PROGRAM(4L, TvBrowsePage.PROGRAM),
        PLAYBACK(5L, TvBrowsePage.PLAYBACK);
        companion object {
                val valueById = values().map { it.id to it }.toMap()
                val valueByType = values().map { it.value to it }.toMap()
        }
}

class TvBrowsePageConverter: PropertyConverter<TvBrowsePageProperty, Long> {
        override fun convertToDatabaseValue(entityProperty: TvBrowsePageProperty?): Long = entityProperty?.id?: IconTypeProperty.UNKNOWN.id
        override fun convertToEntityProperty(databaseValue: Long?): TvBrowsePageProperty = TvBrowsePageProperty.valueById[databaseValue]?: TvBrowsePageProperty.UNKNOWN
}

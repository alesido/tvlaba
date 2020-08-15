package org.alsi.android.moidom.model.tv

import android.text.format.DateUtils
import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError
import org.joda.time.format.DateTimeFormat


data class EpgResponse(

        val epg: List<Epg>?,

        val icons: List<Icon>,

        val tz_sec: Int,
        val ts_sec: Int,
        val with_ts: Int,

        override val error: RequestError?,
        override val servertime: Int

): BaseResponse() {

    data class Epg(

        val title: String? = null,
        val name: String? = null,
        val title_id: String? = null,
        val sub_title: String? = null,
        val sub_title_id: String? = null,

        val desc: String? = null,
        val desc_full: String? = null,

        val season: Int? = null,
        val series: Int? = null,

        val ut_start: Any? = null,
        val ut_stop: Any? = null,

        val icon_main: String? = null,
        val icons: List<String>? = null,

        val local_start: String? = null,
        val local_stop: String? = null,
        val local_range: String? = null,

        val lang: String? = null,

        val category: List<Category>? = null,
        val country: List<Country>? = null,
        val year: String? = null, // year, years range, etc.
        val pg: Int = 0, // age group

        val credits: List<Credit>? = null,
        val awards: List<Any>? = null,
        val production: List<Production>? = null,

        val rt_kinopoisk: Float? = null,
        val rt_imdb: Float? = null,

        val is_current: Any? = null,
        val is_live: Any? = null,
        val has_archive: Any? = null
    ) {
        // region Fixed Awards

        val fixedAwards: List<Award> get() {
            val output: MutableList<Award> = mutableListOf()
            awards?: return output
            var i = 0
            val s = awards.size
            while (i < s) {
                val award = awards[i]
                if (award is Award) {
                    output.add(award)
                } else if (award is String) {
                    output.add(Award(i.toString(), award, null))
                }
                i++
            }
            return output
        }

        // endregion
        // region Fixed Booleans

        val fixedIsCurrent: Boolean get() = fixIntOrBoolean(is_current?: false)
        val fixedIsLive: Boolean get() = fixIntOrBoolean(is_live?: false)
        val fixedHasArchive: Boolean get() = fixIntOrBoolean(has_archive?: false)

        private fun fixIntOrBoolean(b: Any): Boolean {
            if (b is Boolean) return b
            return if (b is Int) b > 0 else false
        }

        // endregion
        // region Fixed Time Value

        val fixedUtStart: Long get () = timePrintToTimestampSeconds(ut_start)
        val fixedUtStop: Long get () = timePrintToTimestampSeconds(ut_stop)

        private val startEndDateTimeFormatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss zzz")
        private fun timePrintToTimestampSeconds(t: Any?): Long {
            t?: return 0L
            if (t is Long) return t
            if (t is Double) return t.toLong()
            return if (t is String) {
                try {
                    startEndDateTimeFormatter.parseMillis(t) / DateUtils.SECOND_IN_MILLIS
                } catch (x: Exception) {
                    0L
                }
            } else 0L
        }

        // endregion
    }

    data class Category(
        val id: String? = null,
        val name: String? = null
    )

    data class Country(
        val id: String? = null,
        val name: String? = null
    )

    data class Credit(
        val id: String? = null,
        val type: String? = null,
        val name: String? = null,
        val foto: List<String>? = null
    )

    data class Award(
        val id: String? = null,
        val name: String? = null,
        val year: String? = null
    )

    data class Production(
        val id: String? = null,
        val name: String? = null
    )
}
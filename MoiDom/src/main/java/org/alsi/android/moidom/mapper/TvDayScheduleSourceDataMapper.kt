package org.alsi.android.moidom.mapper

import org.alsi.android.domain.tv.interactor.guide.TvProgramCredit
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.moidom.model.tv.EpgResponse
import org.alsi.android.remote.mapper.SourceDataMapper
import org.joda.time.LocalDate
import java.util.concurrent.TimeUnit

class TvDayScheduleSourceDataMapper {

    fun mapFromSource(

            channelId: Long,
            date: LocalDate,
            source: EpgResponse

    ) : TvDaySchedule {

        val posterMapper = TvProgramPosterIconPathMapper(source)

        val programs = source.epg!!.map {

            TvProgramIssue(

                    channelId = channelId,
                    time = TvProgramTimeInterval(
                            startUnixTimeMillis = TimeUnit.SECONDS.toMillis(it.fixedUtStart),
                            endUnixTimeMillis = TimeUnit.SECONDS.toMillis(it.fixedUtStop)),
                    title = it.title,
                    description = it.desc_full,
                    hasArchive = it.fixedHasArchive,

                    mainPosterUri = posterMapper.fixUri(it.icon_main),
                    allPosterUris = it.icons?.map { uriString -> posterMapper.fixUri(uriString) },

                    season = it.season,
                    series = it.series,

                    releaseDates = it.year,
                    languageCode = it.lang,
                    ageGroup = it.pg,

                    categoryNames = it.category?.map { c -> c.name?:"" },
                    countryNames = it.country?.map { c -> c.name?:"" },
                    credits = it.credits?.map { c ->
                        TvProgramCredit(
                                name = it.name,
                                role = when(c.type) {
                                    "actor" -> CreditRole.ACTOR
                                    "writer" -> CreditRole.WRITER
                                    "composer" -> CreditRole.COMPOSER
                                    "producer" -> CreditRole.PRODUCER
                                    "director" -> CreditRole.DIRECTOR
                                    else -> CreditRole.ACTOR
                                },
                                photoUris = c.foto?.map { uriString ->
                                    posterMapper.fixUri(uriString) }
                        )
                    },
                    awards = it.fixedAwards.map { a -> a.name }.joinToString (", "),
                    production = it.production?.map { p -> p.name }?.joinToString (", "),
                    rateKinopoisk = it.rt_kinopoisk,
                    rateImdb = it.rt_imdb
            )
        }

        return TvDaySchedule(channelId, date, items = programs)
    }
}
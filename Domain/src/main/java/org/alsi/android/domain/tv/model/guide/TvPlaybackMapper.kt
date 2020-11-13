package org.alsi.android.domain.tv.model.guide

import org.alsi.android.domain.streaming.model.VideoStream
import java.net.URI

class TvPlaybackMapper {

    fun from(channel: TvChannel, stream: VideoStream) : TvPlayback {
        with (channel.live) {
            return TvPlayback(
                    channelId = channel.id,
                    programId = time?.startUnixTimeMillis?:0L,
                    stream = stream,
                    time = time,
                    title = title,
                    description = description,

                    channelNumber = channel.number,
                    channelTitle = channel.title,
                    channelLogoUri = channel.logoUri
            )
        }
    }

    fun from(channel: TvChannel, program: TvProgramIssue, stream: VideoStream) : TvPlayback {
        with (program) {
            return TvPlayback (
                    channelId = channelId,
                    programId = programId,
                    stream = stream,
                    time = time,
                    title = title,
                    description = description,

                    mainPosterUri = mainPosterUri,
                    allPosterUris = allPosterUris,

                    season = season,
                    series = series,

                    releaseDates = releaseDates,
                    languageCode = languageCode,
                    ageGroup = ageGroup,

                    categoryNames = categoryNames,
                    countryNames = countryNames,
                    credits = credits,

                    awards = awards,
                    production = production,
                    rateKinopoisk = rateKinopoisk,
                    rateImdb = rateImdb,

                    channelNumber = channel.number,
                    channelTitle = channel.title,
                    channelLogoUri = channel.logoUri
            )
        }
    }
}
package org.alsi.android.domain.tv.model.guide

import org.alsi.android.domain.streaming.model.VideoStream

class TvPlaybackMapper {

    /**
     *  Map live stream playback for a TV channel.
     */
    fun from(channel: TvChannel, stream: VideoStream) : TvPlayback {
        with (channel.live) {
            return TvPlayback(
                    channelId = channel.id,
                    programId = time?.startUnixTimeMillis,
                    stream = stream,
                    time = time,
                    title = title,
                    description = description,

                    isLive = true,
                    isLiveRecord = false,
                    isUnderParentControl = channel.features.isPasswordProtected,

                    channelNumber = channel.number,
                    channelTitle = channel.title,
                    channelLogoUri = channel.logoUri
            )
        }
    }

    /**
     *  Map archive/record stream of a program or a live record of current program on a channel
     */
    fun from(channel: TvChannel, program: TvProgramIssue, stream: VideoStream) : TvPlayback {
        with (program) {
            return TvPlayback (
                    channelId = channelId,
                    programId = programId,
                    stream = stream,
                    time = time,
                    title = title,
                    description = description,

                    isLive = false,
                    isLiveRecord =
                        evaluateTvProgramDisposition(program.time) == TvProgramDisposition.LIVE,

                    isUnderParentControl = channel.features.isPasswordProtected,

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
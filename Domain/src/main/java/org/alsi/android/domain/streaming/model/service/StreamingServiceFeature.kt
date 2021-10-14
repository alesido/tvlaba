package org.alsi.android.domain.streaming.model.service

enum class StreamingServiceFeature {

    // app level, TV

    TV_ARCHIVE,
    TV_TIME_SHIFT,
    TV_PROGRAM_SEARCH,

    // app level, VOD

    VOD,
    EXTRA_VOD,
    VOD_SEARCH,
    VOD_HIERARCHY, // i.e. category-genre

    // server level

    SEEK_WITH_RESTART,
    BITRATE_OPTIONS,
    API_BASED_AUDIO_TRACK_SELECTION, // audio track can be selected only through server API (not with stream data)
    API_BASED_CAPTION_TRACK_SELECTION, // subtitles track can be selected only through server API (not via stream data)

    MAY_DISABLE_SOME_SUBTITLE_TRACKS
}
package org.alsi.android.presentationtv.model

import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio

sealed class TvPlaybackPreferenceChangeEvent

data class TvPlaybackAspectRatioChanged(val newAspectRatio: VideoAspectRatio)
    : TvPlaybackPreferenceChangeEvent()

data class TvPlaybackAudioTrackLanguageChanged(val audioTrackLanguage: LanguageOption)
    : TvPlaybackPreferenceChangeEvent()

data class TvPlaybackSubtitlesLanguageChanged(val audioTrackLanguage: LanguageOption)
    : TvPlaybackPreferenceChangeEvent()

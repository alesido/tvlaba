package org.alsi.android.presentationtv.model

import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio

sealed class PlaybackPreferenceChangeEvent

data class PlaybackAspectRatioChanged(val newAspectRatio: VideoAspectRatio)
    : PlaybackPreferenceChangeEvent()

data class PlaybackAudioTrackLanguageChanged(val audioTrackLanguage: LanguageOption)
    : PlaybackPreferenceChangeEvent()

data class PlaybackSubtitlesLanguageChanged(val audioTrackLanguage: LanguageOption)
    : PlaybackPreferenceChangeEvent()

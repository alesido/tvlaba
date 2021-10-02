package org.alsi.android.presentationvod.model

import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio

sealed class VodPlaybackPreferenceChangeEvent

data class VodPlaybackAspectRatioChanged(val newAspectRatio: VideoAspectRatio)
    : VodPlaybackPreferenceChangeEvent()

data class VodPlaybackAudioTrackLanguageChanged(val audioTrackLanguage: LanguageOption)
    : VodPlaybackPreferenceChangeEvent()

data class VodPlaybackSubtitlesLanguageChanged(val audioTrackLanguage: LanguageOption)
    : VodPlaybackPreferenceChangeEvent()

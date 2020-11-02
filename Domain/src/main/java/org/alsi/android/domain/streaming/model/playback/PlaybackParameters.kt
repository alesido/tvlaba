package org.alsi.android.domain.streaming.model.playback

import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio

/**
 * Reserved for saving and restoring of the playback parameters for channels.
 */
class PlaybackParameters (

        val aspectRatio: VideoAspectRatio = VideoAspectRatio.ASPECT_FILL_SCREEN,

        var subtitlesLanguage: LanguageOption? = null,

        var audioTrackLanguage: LanguageOption? = null
)
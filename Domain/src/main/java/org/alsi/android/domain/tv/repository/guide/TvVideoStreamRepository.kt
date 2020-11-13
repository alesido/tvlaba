package org.alsi.android.domain.tv.repository.guide

import io.reactivex.Single
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue

abstract class TvVideoStreamRepository {

  /** Get set of data required to playback a live (channel) program
   */
  abstract fun getVideoStream(channel: TvChannel, accessCode: String?): Single<VideoStream?>

  /** Get set of data required to playback an archive (recorded) program
   */
  abstract fun getVideoStream(program: TvProgramIssue, accessCode: String?): Single<VideoStream?>

  /** Get set of data required to playback a live or an archive program
   */
  fun getVideoStream(channel: TvChannel?, program: TvProgramIssue?, accessCode: String?): Single<VideoStream?> =
          if (program != null)
            getVideoStream(program, accessCode)
          else
            getVideoStream(channel!!, accessCode)
}
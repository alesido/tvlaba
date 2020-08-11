package org.alsi.android.domain.tv.repository.guide

import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import java.net.URI

abstract class TvVideoStreamRepository {

  /** Get set of data required to playback a live (channel) program
   */
  abstract fun getVideoStreamUri(channel: TvChannel, accessCode: String?): Single<URI?>

  /** Get set of data required to playback an archive (recorded) program
   */
  abstract fun getVideoStreamUri(program: TvProgramIssue, accessCode: String?): Single<URI?>

  /** Get set of data required to playback a live or an archive program
   */
  fun getVideoStreamUri(channel: TvChannel?, program: TvProgramIssue?, accessCode: String?): Single<URI?> =
          if (program != null)
            getVideoStreamUri(program, accessCode)
          else
            getVideoStreamUri(channel!!, accessCode)
}
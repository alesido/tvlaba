package org.alsi.android.domain.tv.model.guide

import java.net.URI

/** TV program issue. A particular broadcasting of a show, movie, event, etc.
 * on a certain TV channel at a certain date and time.<br/><br/>
 *
 * @param channelId ID of a TV channel on which the program issued.
 * @param programId ID of a program, not a program issue per
 */
class TvProgramIssue(val channelId: Long, var programId: Long? = null)
{
    /**
     *  Program time interval, start and end time. N/A for channels w/o EPG.
     */
    var time: TvProgramTimeInterval? = null

    /**
     * Title of the program item. N/A for channels w/o EPG.
     */
    var title: String? = null

    /** It's possible that a channel w/o EPG has no live program title available and something
     *  like "NO EPG" may be provided to indicate this
     */
    var isTitleAvailable: Boolean = true

    /**
     * Description of the program. Optional and may be N/A for channels w/o EPG.
     */
    var description: String? = null

    /** Disposition: LIVE, RECORD, etc. ...
     */
    var disposition: TvProgramDisposition? = null

    /** Video stream URI
     */
    var videoStreamUri: URI? = null
}

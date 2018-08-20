package org.alsi.android.domain.tv.model.guide

/** TV program issue. A particular broadcasting of a show, movie, event, etc.
 * on a certain TV channel at a certain date and time.<br/><br/>
 *
 * @param channelId ID of a TV channel on which the program issued.
 * @param programId ID of a program, not a program issue per ce, used to identify video stream(?)
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

    /**
     * Description of the program. Optional and may be N/A for channels w/o EPG.
     */
    var description: String? = null

    /** Disposition: LIVE, RECORD, etc. ...
     */
    var disposition: TvProgramDisposition? = null
}

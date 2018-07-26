package org.alsi.android.domain.tv.model.guide

import java.net.URI

/** TV program issue. A particular broadcasting of a show, movie, event, etc.
 * on a certain TV channel at a certain date and time.<br/><br/>
 *
 * @param id ID of a program issue, not a program per se (?)
 * @param channelId ID of a TV channel on which the program issued.
 * @param videoStreamUri URI of the live or recorded video stream for the program issue.
 */
class TvProgramIssue(val id: Long, val channelId: Long, val videoStreamUri: URI)
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
}

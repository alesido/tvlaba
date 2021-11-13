package org.alsi.android.domain.tv.model.guide

/** Live TV program issue. A particular broadcasting of a show, movie, event, etc.
 * on a certain TV channel at a certain date and time.<br/><br/>
 */
class TvProgramLive(

        /**
         *  Program time interval, start and end time. N/A for channels w/o EPG.
         */
        var time: TvProgramTimeInterval? = null,

        /**
         * Title of the program item. N/A for channels w/o EPG.
         */
        var title: String? = null,

        /**
         * Description of the program. Optional and may be N/A for channels w/o EPG.
         */
        var description: String? = null
) {
        fun isEmpty() = null == time && null == title

        companion object {
                fun empty() = TvProgramLive()
        }
}

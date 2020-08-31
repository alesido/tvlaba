package org.alsi.android.domain.tv.model.guide

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat.longDate
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *  Day schedule of TV programs.
 */
@Suppress("MemberVisibilityCanBePrivate")
class TvDaySchedule(
        val channelId: Long,
        val date: LocalDate,
        val timeShiftHours: Int = 0,
        val items: List<TvProgramIssue>
) {
    /** ... to find program by ID.
     */
    private val idMap = mutableMapOf<Long, TvProgramIssue>()

    /** ... to find program position by ID.
     */
    private val positionMap= mutableMapOf<Long, Int>()

    /** Timeline is a navigable list of the schedule time points where one program possibly ends
     *  and another possibly starts. Introduced to search program by time. See also timeMap.
     */
    private val timeLine: NavigableSet<Long> = TreeSet()

    /** Sorted list of program nodes, i.e. points of time at which one program possibly ends
     * and another possibly starts, at list one of them is defined. Introduced to search
     * program by time. See also timeLine.
     */
    private val timeMap = mutableMapOf<Long, TimeNode>()

    /** Schedule section structure.
     */
    val sections: MutableList<TimeSection> = mutableListOf()

    /**
     */
    init {
        if (items.isNotEmpty()) {
            items[0].time?.startUnixTimeMillis?.let { s0 ->
                timeLine.add(s0)
                timeMap[s0] = TimeNode(starting = items[0])
                for ((i, item) in items.withIndex()) {
                    item.programId?: continue
                    item.time?: continue
                    idMap[item.programId!!] = item
                    positionMap[item.programId!!] = i
                    val t: TvProgramTimeInterval = item.time!!
                    val s: Long = t.startUnixTimeMillis
                    val e: Long = t.endUnixTimeMillis
                    timeMap[e] = TimeNode(ending = item)
                    timeLine.add(e)
                    val timeNode = timeMap[s]
                    if (timeNode != null) timeNode.starting = item
                }
            }

            with (sections) {
                add(TimeSection(0,6,  date))
                add(TimeSection(6,12, date))
                add(TimeSection(12,18, date))
                add(TimeSection(18,24, date))
            }

            items.forEach { program ->
                sections.forEach { section ->
                    program.time?.let {
                        if (section.contains(program.time!!.startUnixTimeMillis)) section.items.add(program)
                    }
                }
            }
        }
    }

    val longDateString: String get() = date.toString(longDate())

    val live: TvProgramIssue? get() {
        val nowMillis = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(timeShiftHours.toLong())
        val candidateProgramStart = timeLine.floor(nowMillis)?: return null
        val candidateProgram = timeMap[candidateProgramStart]?.starting?: return null
        candidateProgram.time?: return null
        if (candidateProgram.time!!.endUnixTimeMillis < nowMillis) return null
        return candidateProgram
    }

    val programAtMiddle: TvProgramIssue? get() {
        val midDayMillis = date.toDateTimeAtStartOfDay().millis + TimeUnit.HOURS.toMillis(12)
        val candidateProgramStart = timeLine.floor(midDayMillis)?: return null
        val candidateProgram = timeMap[candidateProgramStart]?.starting?: return null
        candidateProgram.time?: return null
        if (candidateProgram.time!!.endUnixTimeMillis < midDayMillis) return null
        return candidateProgram
    }

    fun programAtTime(issueDateTime: LocalDateTime): TvProgramIssue? {
        val programStart = timeLine.floor(issueDateTime.toDateTime().millis) ?: return null
        return timeMap[programStart]!!.starting!!
    }

    fun positionOf(programIssue: TvProgramIssue): Int? = positionMap[programIssue.programId]
    fun positionOf(playback: TvPlayback): Int? = positionMap[playback.programId]

    fun contains(programIssue: TvProgramIssue) = positionMap[programIssue.programId] != null
    fun contains(playback: TvPlayback) = positionMap[playback.programId] != null

    fun programForPlayback(playback: TvPlayback): TvProgramIssue? {
        val position = positionMap[playback.programId]
        return position?.let { items[position] }
    }

    val middlePosition: Int? get() {
        return programAtMiddle?.let { positionMap[it.programId] }
    }

    /** Time based section of the schedule.
     */
    class TimeSection(val startHour: Int, val endHour: Int, day: LocalDate,
                      val title: String = "$startHour:00 - $endHour:00",
                      val items: MutableList<TvProgramIssue> = mutableListOf()) {

        var range: LongRange

        init {
            val startMillis = if (startHour == 0) day.minusDays(1).toDateTimeAtStartOfDay().millis
            else day.toDateTimeAtStartOfDay().millis + TimeUnit.HOURS.toMillis(startHour.toLong())

            val endMillis = if (endHour == 24) day.plusDays(1).toDateTimeAtStartOfDay().millis
            else day.toDateTimeAtStartOfDay().millis + TimeUnit.HOURS.toMillis(endHour.toLong())

            range = LongRange(startMillis, endMillis)
        }

        fun contains(testTimeMillis: Long) = range.contains(testTimeMillis)
    }


    /** Time node, i.e. point of time at which one program possibly ends
     *  and another possibly starts, at list one of them is defined.
     */
    private class TimeNode (
            var ending: TvProgramIssue? = null,
            var starting: TvProgramIssue? = null
    )
}
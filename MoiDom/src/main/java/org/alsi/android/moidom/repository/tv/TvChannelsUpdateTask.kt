package org.alsi.android.moidom.repository.tv

import android.text.format.DateUtils
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import org.alsi.android.domain.tv.model.guide.TvChannelListWindow
import org.alsi.android.domain.tv.model.guide.TvChannels
import org.alsi.android.domain.tv.model.guide.TvChannelsChange
import org.alsi.android.framework.Now
import org.alsi.android.framework.formatMillis
import org.alsi.android.moidom.repository.tv.TvChannelsUpdateTaskState.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TvChannelsUpdateTask(

        val window: TvChannelListWindow,
        private val now: Now,
        private val timeline: List<Long>
) {
    var subscription: Disposable? = null

    private var targetMillis: Long = 0L

    private var state: TvChannelsUpdateTaskState = ZERO

    private var totalDefects = 0

    private var lastError: Throwable? = null

    private val timeFormatter = DateTimeFormat.forPattern("HH:mm:ss.SSS")

    val zipper = BiFunction<TvChannels, TvChannels, TvChannelsChange> { remotes, locals ->
        val remotesMap = remotes.map { it.id to it }.toMap()
        val localsMap = locals.map { it.id to it }.toMap()
        return@BiFunction TvChannelsChange(
                create = remotes.filter { null == localsMap[it.id] },
                update = remotes.filter {
                    val localTime = localsMap[it.id]?.live?.time
                    val remoteTime = it.live.time
                    remoteTime?.isNotSet == false
                            && remoteTime.endUnixTimeMillis > targetMillis
                            && localTime?.endUnixTimeMillis != remoteTime.endUnixTimeMillis
                },
                delete = locals.filter { null == remotesMap[it.id] },
                defect = remotes.filter {
                    val remoteTime = it.live.time
                    remoteTime?.isNotSet == false
                            && remoteTime.endUnixTimeMillis <= targetMillis
                }
        )
    }

    /** Schedule update delay depending on:
     * - position of current time in the update timeline
     * - and presence of update defects.
     */
    fun schedule2(): Flowable<Long> = Flowable.just(this).flatMap { task ->

        // there are defects (not updated items), - repeat update request to remove
        if (task.totalDefects > 0) {
            onStepScheduled(target = task.targetMillis, delay = DELAY_DEFECT_FIX_MILLS)
            return@flatMap Flowable.timer(DELAY_DEFECT_FIX_MILLS, TimeUnit.MILLISECONDS)
        }

        // testable now time
        val nowMillis = now.millis()

        if (timeline.isEmpty()) {
            // no channel has data on current program now and possible they're appear
            // later, - lazily poll server in intervals
            onStepScheduled(target = nowMillis + DELAY_UPDATE_POLLING_MILLIS, delay = DELAY_UPDATE_POLLING_MILLIS)
            return@flatMap Flowable.timer(DELAY_UPDATE_POLLING_MILLIS, TimeUnit.MILLISECONDS)
        }

        // next scheduled update time
        val nextTargetMillis = timeline.find {
            it > nowMillis - DELAY_PROACTIVE_UPDATE_MILLIS
        }?: nowMillis // just to avoid null correctly = somewhere behind the timeline end

        if (targetMillis == 0L) {
            // starting update sequence
            if (nextTargetMillis > timeline[0]) {
                // there are outdated items to update immediately
                onStepScheduled(
                        target = nextTargetMillis,
                        delay = DELAY_PROACTIVE_UPDATE_MILLIS // just to avoid series of requests w/o delays
                )
                return@flatMap Flowable.timer(DELAY_PROACTIVE_UPDATE_MILLIS, TimeUnit.MILLISECONDS)
            }
        }

        // find a delay to the next update request (there are possible intermediate requests
        // scheduled to fix defects and they may override scheduled updates)
        var updateDelay = nextTargetMillis - nowMillis - DELAY_PROACTIVE_UPDATE_MILLIS
        if (updateDelay < 0) updateDelay = DELAY_PROACTIVE_UPDATE_MILLIS

        // continue with the delay
        onStepScheduled(target = nextTargetMillis, delay = updateDelay)
        return@flatMap Flowable.timer(updateDelay, TimeUnit.MILLISECONDS)
    }

    /** Start update task, i.e. series of updates by schedule by subscribing to repeating
     * update step flowable.
     */
    fun startWith(repeatingUpdateStepFlowable: Flowable<TvChannelsChange>) {
        state = WAITING
        subscription = repeatingUpdateStepFlowable.subscribe({ change ->
            if (change.defect.isEmpty() && targetMillis >= timeline.last()) onComplete()
        }, { Timber.w(it, "Error getting TV channels directory") })
    }

    fun onError(error: Throwable?) {
        lastError = error
        state = ERROR
        Timber.e(error, "ERROR @ %s for target %s",
                now.time().toString(timeFormatter),
                DateTime(targetMillis).toString(timeFormatter))
    }

    private fun onComplete() {
        state = COMPLETED
        if (subscription?.isDisposed == false) subscription!!.dispose()
        Timber.d("COMPLETED @ %s for target %s",
                now.time().toString(timeFormatter),
                DateTime(targetMillis).toString(timeFormatter))

    }

    private fun cancel() {
        state = CANCELLED
        if (subscription?.isDisposed == false) subscription!!.dispose()
    }

    val isCancelled get() = state == CANCELLED

    // region Trace & State

    fun onDuplicate(window: TvChannelListWindow) {
        Timber.d("ALREADY scheduled for %s, task was %s %s, skipping ...",
                window, state.name, subscriptionStatusReport())
    }

    fun onCancelled(window: TvChannelListWindow) {
        Timber.d("CANCELLED target was: %s, state was: %s %s, now: %s",
                DateTime(targetMillis).toString(timeFormatter), state.name,
                subscriptionStatusReport(), now.time().toString(timeFormatter))
        Timber.d("NEXT %s", window)
        cancel()
    }

    private fun onStepScheduled(target: Long, delay: Long) {
        targetMillis = target
        val start = now.millis() + delay
        Timber.d("SCHEDULED target: %s, start: %s, now: %s, delay: %s",
                DateTime(target).toString(timeFormatter), DateTime(start).toString(timeFormatter),
                now.time().toString(timeFormatter), formatMillis(delay))
    }

    fun onStepStarted() {
        state = RUNNING
        Timber.d("STARTED @ %s for target %s",
                now.time().toString(timeFormatter),
                DateTime(targetMillis).toString(timeFormatter))
    }

    fun onChangeReceived(change: TvChannelsChange) {
        totalDefects = change.defect.size
        Timber.d("UPDATE %s", change.toString())
    }

    // endregion

    private fun subscriptionStatusReport() = if (subscription?.isDisposed == true)
        " (DISPOSED!)" else { if (subscription != null) "(ALIVE)" else "(NOT SUBSCRIBED)" }

    companion object {

        /** Anticipated time to request channel data update to get response in time.
         **/
        const val DELAY_PROACTIVE_UPDATE_MILLIS = 500L

        /** Delay to repeat update request expecting that the anticipated update
         * became available on the server
         **/
        const val DELAY_DEFECT_FIX_MILLS = 15 * DateUtils.SECOND_IN_MILLIS

        /** Delay for "lazy" polling for updates
         */
        const val DELAY_UPDATE_POLLING_MILLIS = 5 * DateUtils.MINUTE_IN_MILLIS


        fun empty() = TvChannelsUpdateTask(TvChannelListWindow.empty(), Now(), listOf())
    }
}

enum class TvChannelsUpdateTaskState {
    ZERO, WAITING, RUNNING, ERROR, COMPLETED, CANCELLED
}


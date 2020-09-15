package org.alsi.android.moidom.repository.tv

import android.text.format.DateUtils
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datatv.repository.TvChannelDataRepository
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.framework.RxUtils
import org.alsi.android.framework.formatMillis
import org.alsi.android.moidom.model.LoginEvent
import org.alsi.android.moidom.repository.tv.TvChannelDataExpiration.Companion.DELAY_NEXT_PROGRAM_UPDATE_ANTICIPATION_MILLIS
import org.alsi.android.moidom.repository.tv.TvChannelDataExpiration.Companion.DELAY_UPDATE_POLLING
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.max

class TvChannelDataRepositoryMoidom @Inject constructor(): TvChannelDataRepository() {

    val tag = TvChannelDataRepositoryMoidom::class.java.simpleName

    /** As soon as the login subject property gets value from dependency injection (on a late init),
     * subscription created and it initializes local store delegate for just logged in user.
     * The dependency injection is attached to the setter method here.
     */
    @set:Inject
    var loginSubject: PublishSubject<LoginEvent>? = null
        set(value) {
            field = value
            val s = value?.subscribe {
                local.switchUser(it.account.loginName)
            }
            s?.let { disposables.add(it) }
        }

    /** Directory subject made BehaviourSubject as it returns the last result immediately on subscription.
     */
    private val directorySubject: BehaviorSubject<TvChannelDirectory> = BehaviorSubject.create()

    /** Channel list subject intentionally made BehaviourSubject because it returns the last
     * result immediately on subscription.
     */
    private val channelsSubject: BehaviorSubject<List<TvChannel>> = BehaviorSubject.create()

    var expiration = TvChannelDataExpiration()

    private val timeFormatter = DateTimeFormat.forPattern("HH:mm:ss.SSS")

    // region API Override

    /** TV channel directory was introduced to support APIs that receive both categories and
     * channels in one response, though there is no presentations now to access them both
     * at once. Possibly it's an overhead to have directory.
     *
     * TV channel directory is made observable to allow subsequent presentation updates due to
     * requested (required) update of current (live) channel programs.
     *
     * This method provides cache with expiration functionality.
     */
    override fun getDirectory(): Observable<TvChannelDirectory> {
        val s = local.getDirectory().flatMap { directory ->
            if (expiration.directoryExpired(directory)) {
                expiration.checkInDirectory()
                remote.getDirectory().flatMap {
                    local.putDirectory(it).toSingle {it}
                }
            }
            else {
                Single.just(directory)
            }
        }.subscribe( { directory ->
            directorySubject.onNext(directory)
        }, {
            Log.e(TvChannelDataRepositoryMoidom::class.simpleName, it.toString())
        })
        disposables.add(s)
        return directorySubject
    }

    /** Read directory from local store and share with subscribers if there are any (avoiding overhead
     * of reading local store for nobody)
     */
    private fun sendUpdateToDirectorySubscribers(change: TvChannelsChange) {
        if (directorySubject.hasObservers()) {
            val s = local.getDirectory().subscribe({ directory ->
                directory.change = change
                directorySubject.onNext(directory)
            }, {
                Log.w(TvChannelDataRepositoryMoidom::class.java.simpleName,
                        "Error reading TV channels directory from the local store", it)
            })
            disposables.add(s)
        }
    }

    /** This returns categories list observable to support scenario when categories and channels
     * received with one response and it's correct to notify on categories update. This isn't
     * applicable to Moi Dom service.
     *
     * This method provides cache with expiration functionality.
     */
    override fun getCategories(): Observable<List<TvChannelCategory>> {
        return local.getCategories().flatMap { categories ->
            if (expiration.categoriesExpired(categories)) {
                expiration.checkInCategories()
                remote.getCategories()
            }
            else Single.just(categories)
        }.toObservable()
    }

    /**
     * TV channels list is made observable to allow subsequent presentation updates due to
     * requested (required) update of current (live) channel programs.
     *
     * This method provides cache with expiration functionality.
     */
    override fun getChannels(categoryId: Long): Observable<List<TvChannel>> {
        val s = local.getChannels(categoryId).flatMap { channels ->
            if (expiration.channelsExpired(channels)) {
                expiration.checkInChannels()
                remote.getChannels()
            }
            else Single.just(channels)
        }.subscribe { channels -> channelsSubject.onNext(channels.filter { it.categoryId == categoryId })}
        disposables.add(s)
        return channelsSubject
    }

    // endregion
    // region Update Scheduling

    private var currentUpdateTask: TvChannelsUpdateTask? = null

    private val updateZipper = BiFunction<TvChannels, TvChannels, TvChannelsChange> {
        remotes, locals ->
        val remotesMap = remotes.map { it.id to it }.toMap()
        val localsMap = locals.map { it.id to it }.toMap()
        return@BiFunction TvChannelsChange(
                create = remotes.filter { null == localsMap[it.id] },
                update = remotes.filter { localsMap[it.id]?.live?.time?.isCurrent == false },
                delete = locals.filter { null == remotesMap[it.id] },
                defect = remotes.filter { it.live.time?.isCurrent == false && it.live.time?.isNotSet == false}
        )
    }

    private fun updateDelay(updateTargetTimeMillis: Long): Long {
        val updateStartMillis = updateTargetTimeMillis - DELAY_NEXT_PROGRAM_UPDATE_ANTICIPATION_MILLIS
        val nowMillis = expiration.getCurrentTimeMillis()
        val updateDelayMillis = updateStartMillis - nowMillis

        Log.d(tag, String.format("target: %s, start: %s, now: %s, delay: %s",
                DateTime(updateTargetTimeMillis).toString(timeFormatter),
                DateTime(updateStartMillis).toString(timeFormatter),
                DateTime(nowMillis).toString(timeFormatter),
                formatMillis(updateDelayMillis.toInt())
        ))

        return updateDelayMillis
    }

    /** Schedule channels update to have an actual display.
     *
     * * Cancel current task on a next incoming task if it's different.
     * * Repeat update request until there are all channels are up to date.
     */
    override fun scheduleChannelsUpdate(window: TvChannelListWindow) {

        val updateTargetTimeMillis = local.getChannelWindowExpirationMillis(window.ids)?: return
        currentUpdateTask?.let { task ->
            if (task.targetMillis == updateTargetTimeMillis && !task.isCancelled) {
                // skip update requested as it's the same as the undergoing
                logTaskSkipped(updateTargetTimeMillis)
                return
            }
            // current simple solution: cancel task in any state cause it is no more actual
            logTaskCancelled(updateTargetTimeMillis)
            task.cancel()
        }

        // schedule the launch with delay and repetition to remove update defects
        val updateSubscription = Flowable.interval(max(0L, updateDelay(updateTargetTimeMillis)),
                DELAY_UPDATE_POLLING, TimeUnit.MILLISECONDS)

        // request changes and map response to CRUD categories
        .flatMap {
            logTaskStared()
            currentUpdateTask?.setRunning()
            Flowable.zip(
                    remote.getChannels().toFlowable(),
                    local.getChannels().toFlowable(),
                    updateZipper)

        // apply changes to the local store
        }.flatMap { change ->
            logChange(change)
            local.updateChannels(change).toSingle { change } .toFlowable()
        }

        // notify on changes
        .flatMap { change ->
            channelsSubject.onNext(change.update)
            sendUpdateToDirectorySubscribers(change)
            Flowable.just(change)
        }

        // make exponential backoff in case of error
        .retryWhen { error ->
            RxUtils.exponentialBackoff(error, 1, 5) {
                currentUpdateTask?.setFailed(it)
                true
            }
        }

        // take while there are defects
        .takeUntil { change ->
            change.defect.isEmpty()
        }

        .subscribe({ change ->
            if (change.defect.isEmpty()) currentUpdateTask?.setCompleted()
        }, {
            Log.w(TvChannelDataRepositoryMoidom::class.java.simpleName,
                "Error getting TV channels directory", it)
        })

        currentUpdateTask = TvChannelsUpdateTask(updateTargetTimeMillis, updateSubscription)
        currentUpdateTask!!.setWaiting()
        disposables.add(currentUpdateTask!!.subscription)
    }

    private fun logTaskSkipped(newTaskTargetTime: Long) {
        currentUpdateTask?.let { task ->
            Log.d(tag, String.format("ALREADY scheduled for %s, task was %s %s, skipping ...",
                    DateTime(newTaskTargetTime).toString(timeFormatter), task.state.name,
                    if (task.subscription.isDisposed) "(DISPOSED!)" else "(ALIVE)"))
        }
    }

    private fun logTaskCancelled(newTaskTargetTime: Long) {
        currentUpdateTask?.let { task ->
            Log.d(tag, String.format("CANCELLED not actual %s %s @ %s against next @%s", task.state.name,
                    if (task.subscription.isDisposed) "(DISPOSED!)" else "(ALIVE)",
                    DateTime(task.targetMillis).toString(timeFormatter),
                    DateTime(newTaskTargetTime).toString(timeFormatter)
            ))
        }
    }

    private fun logTaskStared() {
        currentUpdateTask?: return
        Log.d(tag, String.format("STARTED @ %s for target %s",
                DateTime().toString(timeFormatter),
                DateTime(currentUpdateTask?.targetMillis).toString(timeFormatter)
        ))
    }

    private fun logChange(change: TvChannelsChange) {
        Log.d(tag, String.format("UPDATE %s", change.toString()))
    }

    // endregion
}

// region Expiration
@Suppress("MemberVisibilityCanBePrivate")
class TvChannelDataExpiration {

    private var lastCategoriesUpdateMillis = 0L
    private var lastChannelsUpdateMillis = 0L


    fun checkInDirectory() {
        lastCategoriesUpdateMillis = getCurrentTimeMillis()
        lastChannelsUpdateMillis = lastCategoriesUpdateMillis
    }

    fun checkInCategories() {
        lastCategoriesUpdateMillis = getCurrentTimeMillis()
    }

    fun checkInChannels() {
        lastChannelsUpdateMillis = getCurrentTimeMillis()
    }

    fun directoryExpired(directory: TvChannelDirectory)
            = categoriesExpired(directory.categories) || channelsExpired(directory.channels)

    fun categoriesExpired(categories: List<TvChannelCategory>): Boolean
            = categories.isEmpty() || lastCategoriesUpdateMillis == 0L
            || System.currentTimeMillis() - lastCategoriesUpdateMillis > EXPIRATION_CATEGORIES_MILLIS

    fun channelsExpired(channels: List<TvChannel>): Boolean
            = channels.isEmpty() || lastChannelsUpdateMillis == 0L
            || System.currentTimeMillis() - lastChannelsUpdateMillis > EXPIRATION_CHANNELS_MILLIS
            || channelProgramDataExpired(channels)

    fun channelProgramDataExpired(channels: List<TvChannel>): Boolean {
        val nowMillis = System.currentTimeMillis()
        for (channel in channels) {
            channel.live.time?.let { if (it.endUnixTimeMillis < nowMillis) return true }
        }
        return false
    }

    /** Should avoid using System.currentTimeMillis() 'cause static methods cannot be mocked easily,
     *  particularly Mockito does not allow this. PowerMock can be used for that, but it
     *  have to be really necessary.
     *
     *  Made public to mock in tests.
     */
    fun getCurrentTimeMillis(): Long {
        return DateTime.now().millis
    }

    companion object {
        const val EXPIRATION_CATEGORIES_MILLIS = DateUtils.MINUTE_IN_MILLIS * 60
        const val EXPIRATION_CHANNELS_MILLIS = DateUtils.MINUTE_IN_MILLIS * 30

        /** Anticipated time to request channel data update to get response in time. */
        const val DELAY_NEXT_PROGRAM_UPDATE_ANTICIPATION_MILLIS = 500

        /** Delay to repeat update request expecting that the data became available on the server */
        const val DELAY_UPDATE_POLLING = 15 * DateUtils.SECOND_IN_MILLIS
    }
}

// endregion

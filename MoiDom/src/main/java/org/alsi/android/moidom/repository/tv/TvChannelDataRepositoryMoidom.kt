package org.alsi.android.moidom.repository.tv

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.alsi.android.datatv.repository.TvChannelDataRepository
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.framework.Now
import org.alsi.android.framework.RxUtils
import org.alsi.android.moidom.repository.SettingsRepositoryMoidom
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvChannelDataRepositoryMoidom @Inject constructor(
    private val settingsRepository: SettingsRepositoryMoidom,
    private val defaults: StreamingServiceDefaults
): TvChannelDataRepository() {

// Saved for example
//
//    /** As soon as the login subject property gets value from dependency injection (on a late init),
//     * subscription created and it initializes local store delegate for just logged in user.
//     * The dependency injection is attached to the setter method here.
//     */
//    @set:Inject
//    var loginSubject: PublishSubject<LoginEvent>? = null
//        set(value) {
//            field = value
//            val s = value?.subscribe {
//                local.switchUser(it.account.loginName)
//            }
//            s?.let { disposables.add(it) }
//        }

    /** Directory subject made BehaviourSubject as it returns the last result immediately on subscription.
     */
    private val directorySubject: BehaviorSubject<TvChannelDirectory> = BehaviorSubject.create()

    /** Channel list subject intentionally made BehaviourSubject because it returns the last
     * result immediately on subscription.
     */
    private val channelsSubject: BehaviorSubject<List<TvChannel>> = BehaviorSubject.create()

    var expiration = TvChannelDataExpiration()

    /** Mock'able current date-time
     */
    var now = Now()

    // region API Override

    /** TV channel directory was introduced to support APIs that receive both categories and
     * channels in one response, though there is no presentations now to access them both
     * at once. Possibly it's an overhead to have directory.
     *
     * TV channel directory is made observable to allow subsequent presentation updates due to
     * requested (required) update of current (live) channel programs.
     */
    override fun observeDirectory(): Observable<TvChannelDirectory> {
        return directorySubject
    }

    override fun getDirectory(): Single<TvChannelDirectory> {
        return local.getDirectory().flatMap { localDir ->
            if (expiration.directoryExpired(localDir)) {
                remote.getDirectory().flatMap { remoteDir ->
                    local.putDirectory(remoteDir).toSingle {
                        expiration.checkInDirectory()
                        remoteDir
                    }
                }
            }
            else {
                // check if language and time shift settings changed since last local
                // directory copy update
                val settings = settingsRepository.lastValues()
                val languageDifferent = settings.language?.code != null
                        && settings.language?.code != localDir.language
                val timeShiftDifferent = settings.timeShiftSettingHours != null
                        && settings.timeShiftSettingHours != localDir.timeShift

                @Suppress("CascadeIf")
                if (languageDifferent) {
                    remote.getCategories().map { categoriesUpdate ->
                        if (timeShiftDifferent) {
                            val diff = localDir.timeShift - settings.timeShiftSettingHours!!
                            localDir.channels.forEach { it.live.time = it.live.time?.shift(diff) }
                        }
                        localDir.copy(
                            categories = categoriesUpdate,
                            language = settings.language!!.code,
                            timeShift = settings.timeShiftSettingHours?: 0
                        )
                    }.flatMap { updatedDir ->
                        local.putDirectory(updatedDir).toSingle { updatedDir }
                    }
                }
                else if (timeShiftDifferent) {
                    val diff = localDir.timeShift - settings.timeShiftSettingHours!!
                    localDir.channels.forEach { it.live.time = it.live.time?.shift(diff) }
                    val updatedDir = localDir.copy(timeShift = settings.timeShiftSettingHours!!)
                    local.putDirectory(updatedDir).toSingle { updatedDir }
                }
                else {
                    Single.just(localDir)
                }
            }
        }
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
                Timber.w(it,"Error reading TV channels directory from the local store")
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

    override fun onLanguageChange(): Completable {
        return Single.zip(local.getDirectory(), remote.getCategories())
        { localDir, categoriesUpdate ->
            localDir.copy(
                categoriesUpdate,
                language = settingsRepository.lastValues().language?.code
                ?: defaults.getDefaultLanguageCode()
            )
        }.flatMap { updatedDir ->
            local.putDirectory(updatedDir).doOnComplete {
                directorySubject.onNext(updatedDir)
            }.toSingle { updatedDir }
        }.ignoreElement()
    }

    override fun onTimeShiftChange(): Completable {
        val targetShift = settingsRepository.lastValues().timeShiftSettingHours
            ?: return Completable.complete()
        return local.getDirectory().map { localDir ->
            val diff = localDir.timeShift - targetShift
            localDir.channels.forEach { it.live.time = it.live.time?.shift(diff) }
            val updatedDir = localDir.copy(timeShift = targetShift)
            local.putDirectory(updatedDir)
            directorySubject.onNext(updatedDir)
        }.ignoreElement()
    }

    override fun authorizeContentAccess(password: String, channelId: Long?): Completable {
        return if (channelId != null) {
            local.findChannelById(channelId).flatMap {
                if (it.features.isPasswordProtected) settingsRepository
                    .changeParentalControlPassword(password, password).toSingle { true }
                else Single.just(true)
            }.ignoreElement()
        }
        else {
            // NOTE This API hasn't a method to authorize content access, a temporary hack used
            settingsRepository.changeParentalControlPassword(password, password)
        }
    }

    // endregion
    // region Update Scheduling

    private var updateTask = TvChannelsUpdateTask.empty()

    /** Schedule channels update to have an actual display.
     *
     * * Cancel current task on a next incoming task if it's different.
     * * Repeat update request until there are all channels are up to date.
     */
    override fun scheduleChannelsUpdate(window: TvChannelListWindow, cancelUpdate: Boolean) {

        // check if the task is a duplicate
        if (updateTask.window.ids == window.ids && !updateTask.isCancelled) {
            // skip update requested as it's the same as the undergoing
            updateTask.onDuplicate(window)
            return
        }

        // simple solution: cancel current task despite it's state - it isn't actual any more
        updateTask.onCancelled(window)
        if (cancelUpdate)
            return

        // create the new task
        updateTask = TvChannelsUpdateTask(window, now,
                local.getChannelWindowUpdateSchedule(window.ids))

        // request changes and sort out the response to the categories
        val updateFlowable = updateTask.schedule2().flatMap {

            updateTask.onStepStarted()
            Flowable.zip(
                    remote.getChannels().toFlowable(),
                    local.getChannels().toFlowable(),
                    updateTask.zipper)
        }

        // apply changes to the local store
        .flatMap { change ->
            updateTask.onChangeReceived(change)
            local.updateChannels(change)
            Flowable.just(change)
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
                updateTask.onError(it)
                true
            }
        }

        // go schedule next step or complete
        .repeat()

        //
        updateTask.startWith(updateFlowable)
        updateTask.subscription?.let { disposables.add(it) }
    }

    // endregion
}


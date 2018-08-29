package org.alsi.android.moidom.repository.tv

import android.text.format.DateUtils
import android.util.Log
import io.objectbox.BoxStore
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datatv.repository.TvChannelDataRepository
import org.alsi.android.datatv.store.TvChannelRemoteStore
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannelListWindow
import org.alsi.android.local.store.tv.TvChannelLocalStoreDelegate
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.model.LoginEvent
import org.alsi.android.moidom.store.tv.TvChannelRemoteStoreMoidom
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class TvChannelDataRepositoryMoidom: TvChannelDataRepository() {

    /** Moidom service box store is injected as a dependency because it shared with
     * other service repositories.
     */
    @field:[Inject Named("${Moidom.TAG}.${StreamingService.TV}")]
    lateinit var moidomServiceBoxStore: BoxStore

    /** As soon as the login subject property gets value from dependency injection (on a late init),
     * subscription created and it initializes local store delegate for just logged in user.
     * The dependency injection is attached to the setter method here.
     */
    @set:Inject
    var loginSubject: PublishSubject<LoginEvent>? = null
        set(value) {
            field = value
            value?.subscribe {
                local = TvChannelLocalStoreDelegate(moidomServiceBoxStore, it.account.loginName)
            }
        }

    /** Remote store property is overridden just to assign more specific type to it.
     */
    override var remote: TvChannelRemoteStore = TvChannelRemoteStoreMoidom()

    /** Directory subject made BehaviourSubject as it returns the last result immediately on subscription.
     */
    private val directorySubject: BehaviorSubject<TvChannelDirectory> = BehaviorSubject.create()

    /** Channel list subject intentionally made BehaviourSubject because it returns the last
     * result immediately on subscription.
     */
    private val channelsSubject: BehaviorSubject<List<TvChannel>> = BehaviorSubject.create()

    private var channelsUpdate: Disposable? = null

    var expiration = TvChannelDataExpiration()

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
        local.getDirectory().flatMap { directory ->
            if (expiration.directoryExpired(directory)) {
                expiration.checkInDirectory()
                remote.getDirectory()
            }
            else Single.just(directory)
        }.subscribe { directory -> directorySubject.onNext(directory) }
        return directorySubject
    }

    /** Read directory from local store and share with subscribers if there are any (avoiding overhead
     * of reading local store for nobody)
     */
    private fun sendUpdateToDirectorySubscribers() {
        if (directorySubject.hasObservers()) {
            local.getDirectory().subscribe({
                directory -> directorySubject.onNext(directory) }, {
                Log.w(TvChannelDataRepositoryMoidom::class.java.simpleName,
                        "Error reading TV channels directory from the local store", it)
            })
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
        local.getChannels(categoryId).flatMap { channels ->
            if (expiration.channelsExpired(channels)) {
                expiration.checkInChannels()
                remote.getChannels()
            }
            else Single.just(channels)
        }.subscribe { channels -> channelsSubject.onNext(channels.filter { it.categoryId == categoryId })}
        return channelsSubject
    }

    // endregion
    // region Update Schedule

    /**
     * TODO Add retryWhen with custom functional object as an argument.
     * See "https://stackoverflow.com/questions/22066481/rxjava-can-i-use-retry-but-with-delay"
     */
    override fun scheduleChannelsUpdate(window: TvChannelListWindow) {
        val expirationTimeMillis = local.getChannelWindowExpirationMillis(window.ids)?: return
        val expirationDelayMillis = expirationTimeMillis - expiration.getCurrentTimeMillis()
        channelsUpdate?.dispose()
        channelsUpdate = Single.timer(if (expirationDelayMillis < 0) expirationDelayMillis else 0L, TimeUnit.MILLISECONDS)
                .flatMap { remote.getChannels() }
                .map { channels -> channels.filter { it.id in window.ids }}
                .flatMap { channels -> local.putChannels(channels).toSingle { channels }}
                .subscribe({ channels ->
                    channelsSubject.onNext(channels)
                    sendUpdateToDirectorySubscribers()
                }, {
                    Log.w(TvChannelDataRepositoryMoidom::class.java.simpleName,
                        "Error getting TV channels directory", it)
                })
    }

    // endregion
    // region Expiration
}

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
    }
}


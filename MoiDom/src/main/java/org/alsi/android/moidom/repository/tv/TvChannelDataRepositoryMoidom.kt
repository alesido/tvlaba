package org.alsi.android.moidom.repository.tv

import android.text.format.DateUtils
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import org.alsi.android.datatv.repository.TvChannelDataRepository
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannelListWindow
import org.alsi.android.local.store.tv.TvChannelLocalStoreDelegate
import org.alsi.android.moidom.store.tv.TvChannelRemoteStoreMoidom
import java.util.concurrent.TimeUnit

class TvChannelDataRepositoryMoidom (

        moidomTvServiceId: Long)

    : TvChannelDataRepository( TvChannelRemoteStoreMoidom(),
        TvChannelLocalStoreDelegate(moidomTvServiceId)) {

    private val directorySubject: BehaviorSubject<TvChannelDirectory> = BehaviorSubject.create()
    private val categoriesSubject: BehaviorSubject<List<TvChannelCategory>> = BehaviorSubject.create()
    private val channelsSubject: BehaviorSubject<List<TvChannel>> = BehaviorSubject.create()

    private var channelsUpdate: Disposable? = null

    /** TODO Store last update times in internal Moidom database
     */
    private var lastCategoriesUpdateMillis = 0L
    private var lastChannelsUpdateMillis = 0L

    // region API Override

    override fun getDirectory(): Observable<TvChannelDirectory> {
        local.getDirectory().flatMap { directory ->
            if (directoryExpired(directory)) {
                lastCategoriesUpdateMillis = System.currentTimeMillis()
                lastChannelsUpdateMillis = lastCategoriesUpdateMillis
                remote.getDirectory()
            }
            else Single.just(directory)
        }.subscribe { directory -> directorySubject.onNext(directory) }
        return directorySubject
    }

    override fun getCategories(): Observable<List<TvChannelCategory>> {
        local.getCategories().flatMap { categories ->
            if (categoriesExpired(categories)) {
                lastCategoriesUpdateMillis = System.currentTimeMillis()
                remote.getCategories()
            }
            else Single.just(categories)
        } .subscribe { categories -> categoriesSubject.onNext(categories) }
        return categoriesSubject
    }

    override fun getChannels(categoryId: Long): Observable<List<TvChannel>> {
        local.getChannels(categoryId).flatMap { channels ->
            if (channelsExpired(channels)) {
                lastChannelsUpdateMillis = System.currentTimeMillis()
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
        val expirationDelayMillis = expirationTimeMillis - System.currentTimeMillis()
        channelsUpdate?.dispose()
        channelsUpdate = Single.timer(if (expirationDelayMillis < 0) expirationDelayMillis else 0L, TimeUnit.MILLISECONDS)
                .flatMap { remote.getChannels() }
                .map { channels -> channels.filter { it.id in window.ids }}
                .flatMap { channels -> local.putChannels(channels).toSingle { channels }}
                .subscribe({ channels -> channelsSubject.onNext(channels) },
                        { Log.w(TvChannelDataRepositoryMoidom::class.java.simpleName,
                                "Error getting TV channels directory", it)})
    }

    // endregion
    // region Expiration

    private fun directoryExpired(directory: TvChannelDirectory)
            = categoriesExpired(directory.categories) || channelsExpired(directory.channels)

    private fun categoriesExpired(categories: List<TvChannelCategory>): Boolean
            = categories.isEmpty() || lastCategoriesUpdateMillis == 0L
            || System.currentTimeMillis() - lastCategoriesUpdateMillis > EXPIRATION_CATEGORIES_MILLIS

    private fun channelsExpired(channels: List<TvChannel>): Boolean
            = channels.isEmpty() || lastChannelsUpdateMillis == 0L
            || System.currentTimeMillis() - lastChannelsUpdateMillis > EXPIRATION_CHANNELS_MILLIS
            || channelProgramDataExpired(channels)

    private fun channelProgramDataExpired(channels: List<TvChannel>): Boolean {
        val nowMillis = System.currentTimeMillis()
        for (channel in channels) {
            channel.live?.time?.let { if (it.endUnixTimeMillis < nowMillis) return true }
        }
        return false
    }

    // endregion

    companion object {
        const val EXPIRATION_CATEGORIES_MILLIS = DateUtils.MINUTE_IN_MILLIS * 60
        const val EXPIRATION_CHANNELS_MILLIS = DateUtils.MINUTE_IN_MILLIS * 30
    }
}


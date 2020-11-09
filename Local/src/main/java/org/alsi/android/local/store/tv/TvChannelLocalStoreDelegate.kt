package org.alsi.android.local.store.tv

import android.content.Context
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.datatv.store.TvChannelLocalStore
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannelsChange
import org.alsi.android.local.mapper.tv.TvCategoryEntityMapper
import org.alsi.android.local.mapper.tv.TvChannelEntityMapper
import org.alsi.android.local.model.tv.*
import javax.inject.Inject

/** Delegate for local TV channels store belonging to a service.
 *
 * It is decided, in favor of access speed, to have separate store files for services.
 *
 * NOTE Depending on service subscription user may have different sets categories and channels.
 */
class TvChannelLocalStoreDelegate(

        serviceBoxStore: BoxStore,
        private var userLoginName: String = "guest")

    : TvChannelLocalStore {

    @Inject
    lateinit var context: Context

    private val categoryBox: Box<TvChannelCategoryEntity> = serviceBoxStore.boxFor()
    private val channelBox: Box<TvChannelEntity> = serviceBoxStore.boxFor()
    private val indexBox: Box<TvChannelIndexEntity> = serviceBoxStore.boxFor()
    private val favoriteChannelBox: Box<TvFavoriteChannelEntity> = serviceBoxStore.boxFor()

    private val categoryMapper = TvCategoryEntityMapper()
    private val channelMapper = TvChannelEntityMapper()

    override fun switchUser(userLoginName: String) {
        this.userLoginName = userLoginName
    }

    // region Directory

    override fun putDirectory(directory: TvChannelDirectory): Completable {
        return Completable.fromRunnable {
            categoryBox.put(directory.categories.map { categoryMapper.mapToEntity(it) })
            channelBox.put(directory.channels.map { channelMapper.mapToEntity(it) })
            val indexItems: MutableList<TvChannelIndexEntity> = mutableListOf()
            directory.index.forEach {node ->
                node.value.forEach { channel ->
                    indexItems.add(TvChannelIndexEntity(
                            categoryId = node.key,
                            channelId = channel.id
                    ))
                }
            }
            indexBox.removeAll()
            indexBox.put(indexItems)
        }
    }

    override fun getDirectory(): Single<TvChannelDirectory> = Single.fromCallable {
        val categories = categoryBox.all
                .map { category -> categoryMapper.mapFromEntity(category) }
                .sortedBy { it.ordinal }
        val channels = channelBox.all.map { channel -> channelMapper.mapFromEntity(channel) }
        val channelsById: Map<Long, TvChannel> = channels.map {it.id to it}.toMap()
        val index : MutableMap<Long, MutableList<TvChannel>> = mutableMapOf()
        indexBox.all.forEach { entry ->
            if (! index.contains(entry.categoryId)) {
                index[entry.categoryId] = mutableListOf()
            }
            channelsById[entry.channelId]?.let { index[entry.categoryId]?.add(it) }
        }
        TvChannelDirectory(categories, channels, index)
    }

    // endregion
    // region Categories

    override fun putCategories(categories: List<TvChannelCategory>): Completable {
        return Completable.fromRunnable {
            categoryBox.put(categories.map { categoryMapper.mapToEntity(it) })
        }
    }

    override fun getCategories(): Single<List<TvChannelCategory>> = Single.fromCallable {
        categoryBox.all.map { category -> categoryMapper.mapFromEntity(category) }
    }

    override fun findCategoryById(categoryId: Long): Single<TvChannelCategory?> = Single.fromCallable {
        categoryMapper.mapFromEntity(categoryBox.get(categoryId))
    }

    // endregion
    // region Channels

    override fun putChannels(channels: List<TvChannel>): Completable = Completable.fromRunnable {
        channelBox.put(channels.map { channelMapper.mapToEntity(it) })
    }

    override fun updateChannels(change: TvChannelsChange) {
        if (change.create.isNotEmpty()) {
            channelBox.put(change.create.map { channelMapper.mapToEntity(it) })
        }
        if (change.update.isNotEmpty()) {
            channelBox.put(change.update.map { channelMapper.mapToEntity(it) })
        }
        if (change.delete.isNotEmpty()) {
            channelBox.removeByIds(change.delete.map { it.id })
        }
    }

    override fun removeChannels(channels: List<TvChannel>): Completable = Completable.fromRunnable {
        channelBox.removeByIds(channels.map { it.id })
    }

    override fun getChannels(): Single<List<TvChannel>> = Single.fromCallable {
        channelBox.all.map { channel ->  channelMapper.mapFromEntity(channel) }
    }

    override fun getChannels(categoryId: Long): Single<List<TvChannel>> = Single.fromCallable {
        channelBox.query { equal(TvChannelEntity_.categoryId, categoryId) }.find()
                .map { channel -> channelMapper.mapFromEntity(channel) }
    }

    override fun findChannelByNumber(channelNumber: Int): Single<TvChannel?> = Single.fromCallable {
        val found = channelBox.query { equal(TvChannelEntity_.number, channelNumber.toLong()) }.findUnique()
        found?.let { channelMapper.mapFromEntity(it) }
    }

    /** Find earliest update time to keep the list part actual. I.e., to have all live program
     * times and titles correct (actual).
     */
    override fun getChannelWindowExpirationMillis(channelIds: List<Long>): Long? {
        if (channelIds.isEmpty()) return null
        var earliestEndMillis: Long? = null
        for (channelId in channelIds) {
            val endMillis = channelBox.get(channelId)?.live?.target?.endMillis ?: continue
            if (null == earliestEndMillis || earliestEndMillis > endMillis) {
                earliestEndMillis = endMillis
            }
        }
        return earliestEndMillis
    }

    /** Collect time points when it's time to request update
     */
    override fun getChannelWindowUpdateSchedule(channelIds: List<Long>) = channelIds.mapNotNull {
            channelBox.get(it)?.live?.target?.endMillis }.distinct().sorted()

    // endregion
    // region Favorites

    override fun addChannelToFavorites(channelId: Long): Completable {
        return Completable.fromRunnable {
            findFavoriteChannel(channelId)?:
            favoriteChannelBox.put(TvFavoriteChannelEntity(0L, channelId, userLoginName)) }
    }

    override fun removeChannelFromFavorites(channelId: Long): Completable {
        return Completable.fromRunnable {
            findFavoriteChannel(channelId)?.let { favoriteChannelBox.remove(it.id) }
        }
    }

    override fun isChannelFavorite(channelId: Long): Single<Boolean> {
        return Single.fromCallable { findFavoriteChannel(channelId) != null }
    }

    override fun toggleChannelFromFavorites(channelId: Long): Completable {
        return Completable.fromRunnable {
            findFavoriteChannel(channelId)?.let {
                favoriteChannelBox.remove(it.id)
            }?: favoriteChannelBox.put(TvFavoriteChannelEntity(0L, channelId, userLoginName))
        }
    }

    override fun getFavoriteChannels(): Single<List<TvChannel>> = Single.fromCallable {
        val favoriteIds: List<Long> = favoriteChannelBox.query {
            equal(TvFavoriteChannelEntity_.userLoginName, userLoginName)
        }.find().map { it.channelId }
        val favoriteChannels = channelBox.query().filter { it.id in favoriteIds }.build().find()
        favoriteChannels.map { channelMapper.mapFromEntity(it) }
    }

    private fun findFavoriteChannel(channelId: Long): TvFavoriteChannelEntity? {
        return favoriteChannelBox.query {
            equal(TvFavoriteChannelEntity_.channelId, channelId)
            equal(TvFavoriteChannelEntity_.userLoginName, userLoginName)
        }.findUnique()
    }

    // endregion
}
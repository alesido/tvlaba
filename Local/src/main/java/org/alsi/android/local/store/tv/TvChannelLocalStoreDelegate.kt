package org.alsi.android.local.store.tv

import android.content.Context
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.MyObjectBox
import org.alsi.android.datatv.store.TvChannelLocalStore
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.local.mapper.tv.TvChannelCategoryEntityMapper
import org.alsi.android.local.mapper.tv.TvChannelEntityMapper
import org.alsi.android.local.model.tv.TvChannelCategoryEntity
import org.alsi.android.local.model.tv.TvChannelEntity
import org.alsi.android.local.model.tv.TvChannelEntity_
import org.alsi.android.local.model.tv.TvFavoriteChannelEntity
import org.alsi.android.local.model.user.UserAccountEntity
import javax.inject.Inject

/**
 * NOTE It doesn't make sense to store separate categories-channels for each user account.
 * Though, there are own storage for each services.
 */
class TvChannelLocalStoreDelegate(serviceId: Long): TvChannelLocalStore {

    @Inject lateinit var context: Context
    @Inject lateinit var userAccountObservable: Observable<UserAccountEntity>

    private var userAccountId: Long = 0L

    private val categoryMapper = TvChannelCategoryEntityMapper()
    private val channelMapper = TvChannelEntityMapper()

    private val categoryBox: Box<TvChannelCategoryEntity>
    private val channelBox: Box<TvChannelEntity>
    private lateinit var favoriteChannelBox: Box<TvFavoriteChannelEntity>

    init {
        val boxStore = MyObjectBox.builder()
                .name("$STORE_FILE_NAME_PREFIX.$serviceId")
                .androidContext(context)
                .build()

        categoryBox = boxStore.boxFor()
        channelBox = boxStore.boxFor()

        setFavoriteChannelStore(context, serviceId, userAccountId)

        userAccountObservable.subscribe { userAccountEntity ->
            setFavoriteChannelStore(context, serviceId, userAccountEntity.id)
        }
    }

    private fun setFavoriteChannelStore(context: Context, serviceId: Long, userAccountId: Long) {
        val boxStore = MyObjectBox.builder()
                .name("$STORE_FILE_NAME_PREFIX.$serviceId.$userAccountId")
                .androidContext(context)
                .build()
        favoriteChannelBox = boxStore.boxFor(TvFavoriteChannelEntity::class.java)
    }

    override fun putDirectory(directory: TvChannelDirectory): Completable {
        return Completable.fromRunnable {
            categoryBox.put(directory.categories.map { categoryMapper.mapToEntity(it) })
            channelBox.put(directory.channels.map { channelMapper.mapToEntity(it) })
        }
    }

    override fun getDirectory(): Single<TvChannelDirectory> {
        return Single.create { TvChannelDirectory(
                categoryBox.all.map { category -> categoryMapper.mapFromEntity(category) },
                channelBox.all.map { channel -> channelMapper.mapFromEntity(channel) })
        }
    }

    override fun putCategories(categories: List<TvChannelCategory>): Completable {
        return Completable.fromRunnable {
            categoryBox.put(categories.map { categoryMapper.mapToEntity(it) })
        }
    }

    override fun getCategories(): Single<List<TvChannelCategory>> {
        return Single.create { categoryBox.all.map { category -> categoryMapper.mapFromEntity(category) } }
    }

    override fun findCategoryById(categoryId: Long): Single<TvChannelCategory> {
        return Single.create { categoryBox.get(categoryId) }
    }

    override fun putChannels(channels: List<TvChannel>): Completable {
        return Completable.fromRunnable { channelBox.put(channels.map { channelMapper.mapToEntity(it) }) }
    }

    override fun getChannels(): Single<List<TvChannel>> {
        return Single.create { channelBox.all.map { channel -> channelMapper.mapFromEntity(channel) } }
    }

    override fun getChannels(categoryId: Long): Single<List<TvChannel>> {
        return Single.create { categoryBox.get(categoryId).channels }
    }

    override fun findChannelByNumber(channelNumber: Int): Single<TvChannel?> {
        return Single.create { channelBox.query().equal(TvChannelEntity_.number, channelNumber.toLong()).build().findUnique() }
    }

    /** Find earliest update time to keep the list part actual. I.e., to have all live program
     * times and titles correct (actual).
     */
    override fun getChannelWindowExpirationMillis(channelIds: List<Long>): Long? {
        if (channelIds.isEmpty()) return null
        var earliest: Long = channelBox.get(channelIds[0]).live.target.endMillis?: return null
        channelBox.get(channelIds).forEach {
            val thisEndMillis = it.live.target.endMillis?: return@forEach
            if (thisEndMillis < earliest) earliest = thisEndMillis
        }
        return earliest
    }

    override fun addChannelToFavorites(channelId: Long): Completable {
        return Completable.fromRunnable { favoriteChannelBox.put(TvFavoriteChannelEntity(channelId, userAccountId)) }
    }

    override fun removeChannelFromFavorites(channelId: Long): Completable {
        return Completable.fromRunnable { favoriteChannelBox.remove(channelId) }
    }

    override fun isChannelFavorite(channelId: Long): Single<Boolean> {
        return Single.create { favoriteChannelBox.get(channelId) != null }
    }

    override fun toggleChannelFromFavorites(channelId: Long): Completable {
        return Completable.fromRunnable {
            if (favoriteChannelBox.get(channelId) != null)
                favoriteChannelBox.remove(channelId)
            else
                favoriteChannelBox.put(TvFavoriteChannelEntity(channelId, userAccountId))
        }
    }

    override fun getFavoriteChannels(): Single<List<TvChannel>> {
        return Single.create { _ ->
            val favoriteIds: List<Long> = favoriteChannelBox.all.map { it.tvChannelId }
            val favoriteChannels = channelBox.query().filter { it.id in favoriteIds }.build().find()
            favoriteChannels.map { channelMapper.mapFromEntity(it) }
        }
    }

    companion object {
        val STORE_FILE_NAME_PREFIX = TvChannelLocalStoreDelegate::class.simpleName?:"TvChannelLocalStoreDelegate"
    }
}
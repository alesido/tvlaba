package org.alsi.android.local.store.tv

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.objectbox.relation.ToMany
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.alsi.android.datatv.store.TvChannelLocalStore
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.repository.SettingsRepository
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannelsChange
import org.alsi.android.domain.user.model.SubscriptionPackage
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.mapper.tv.TvCategoryEntityMapper
import org.alsi.android.local.mapper.tv.TvChannelEntityMapper
import org.alsi.android.local.model.tv.*
import org.alsi.android.local.model.user.SubscriptionPackageEntity
import org.alsi.android.local.model.user.SubscriptionPackageEntity_
import org.alsi.android.local.model.user.UserAccountSubject

/** Delegate for local TV channels store belonging to a service.
 *
 * It is decided, in favor of access speed, to have separate store files for services.
 *
 * NOTE Depending on service subscription user may have different sets categories and channels.
 */
class TvChannelLocalStoreDelegate(
    private val serviceId: Long,
    serviceBoxStore: BoxStore,
    accountSubject: UserAccountSubject,
    private val settingsRepository: SettingsRepository,
    private val defaults: StreamingServiceDefaults
) : TvChannelLocalStore {

    private var userLoginName: String = "guest"
    private lateinit var userSubscriptionPackage: SubscriptionPackage

    private val directoryBox: Box<TvChannelDirectoryEntity> = serviceBoxStore.boxFor()
    private val categoryBox: Box<TvChannelCategoryEntity> = serviceBoxStore.boxFor()
    private val channelBox: Box<TvChannelEntity> = serviceBoxStore.boxFor()
    private val subscriptionPackageBox: Box<SubscriptionPackageEntity> = serviceBoxStore.boxFor()
    private val favoriteChannelBox: Box<TvFavoriteChannelEntity> = serviceBoxStore.boxFor()

    private val categoryMapper = TvCategoryEntityMapper()
    private val channelMapper = TvChannelEntityMapper()

    private val disposables = CompositeDisposable()

    init {
        val s = accountSubject.subscribe ({ switchUser(it) }, { /** ignore error */} )
        s?.let { disposables.add(it) }
    }

    override fun switchUser(userAccount: UserAccount) {
        this.userLoginName = userAccount.loginName
        this.userSubscriptionPackage = userAccount.subscriptions
            .first { serviceId == it.serviceId }.subscriptionPackage
    }

    // region Directory

    override fun putDirectory(source: TvChannelDirectory): Completable =
        Completable.fromRunnable {
            // find the TV directory record
            val directoryEntity = attachedDirectory()?.apply {
                // update existing TV directory records
                updateDirectory(this, source)
            // create new directory record
            }?: TvChannelDirectoryEntity(0L, source.language, source.timeShift,
                System.currentTimeMillis()).apply {
                directoryBox.attach(this)
                categories.addAll(source.categories.map { categoryMapper.mapToEntity(it) })
                channels.addAll(source.channels.map { channelMapper.mapToEntity(it) })
                val indexItems: MutableList<TvChannelIndexEntity> = mutableListOf()
                source.index.forEach { node -> node.value.forEach { channel -> indexItems
                    .add(TvChannelIndexEntity(categoryId = node.key, channelId = channel.id)) }}
                index.addAll(indexItems)
                val packageEntity = with(userSubscriptionPackage) {
                    SubscriptionPackageEntity(id, title, termMonths, packets)
                }
                subscriptionPackage.target = packageEntity
                subscriptionPackageBox.put(packageEntity)
            }
            directoryBox.put(directoryEntity)
        }


    override fun getDirectory(): Single<TvChannelDirectory> = Single.fromCallable {
        attachedDirectory()?.run {
            val channels = channels.map { channel -> channelMapper.mapFromEntity(channel) }
            TvChannelDirectory(
                categories = categories.map { categoryMapper.mapFromEntity(it) }
                    .sortedBy { it.ordinal },
                channels = channels,
                index = mapIndexFromEntity(index, channels),
                subscriptionPackage = userSubscriptionPackage,
                language = language?: defaults.getDefaultLanguageCode(),
                timeShift = timeShift?: 0
            )
        } ?: TvChannelDirectory.empty()
    }

    override fun setLanguage(languageCode: String): Completable = Completable.fromRunnable {
        attachedDirectory()?.run {
            directoryBox.put(copy(language = languageCode))
        }
    }

    override fun setTimeShift(hours: Int): Completable = Completable.fromRunnable {
        attachedDirectory()?.run {
            directoryBox.put(copy(timeShift = hours))
        }
    }

    /** This repository works when attached to a directory provided for given subscription package.
     */
    private fun attachedDirectory(): TvChannelDirectoryEntity? {
        return directoryBox.query {
            link(TvChannelDirectoryEntity_.subscriptionPackage).
                equal(SubscriptionPackageEntity_.id, userSubscriptionPackage.id)
            orderDesc(TvChannelDirectoryEntity_.timeStamp)
        }.findFirst()
    }

    private fun mapIndexFromEntity(
        indexEntity: ToMany<TvChannelIndexEntity>,
        channels: List<TvChannel>)
    : Map<Long, List<TvChannel>> = with (indexEntity) {
        val channelsById: Map<Long, TvChannel> = channels.associateBy { it.id }
        this.groupBy { it.categoryId }.mapValues { (_, v) -> v.map { channelsById[it.channelId]!! }}
    }

    private fun updateDirectory(
        target: TvChannelDirectoryEntity,
        source: TvChannelDirectory) {
        updateDirectoryCategories(target.categories, source.categories)
        updateDirectoryChannels(target.channels, source.channels)
        updateDirectoryIndex(target.index, source.index)
        with(target) {
            language = source.language
            timeShift = source.timeShift
            timeStamp = System.currentTimeMillis()
        }
        directoryBox.put(target)
    }

    private fun updateDirectoryCategories(
        target: ToMany<TvChannelCategoryEntity>,
        source: List<TvChannelCategory>
    ) {
        val sourceMap = source.associateBy { it.id }
        val targetMap = target.associateBy { it.externalId }
        // update existing category items
        target.filter { sourceMap[it.externalId] != null }.map {
            it.apply { updateWith(sourceMap[it.externalId]!!) }
        }.let {
            categoryBox.put(it)
        }
        // collect new channel items: source items not found in target collection
        val entitiesToAdd = source.filter { null == targetMap[it.id] }.map {
            categoryMapper.mapToEntity(it)
        }
        // collect items to remove: target collection items not found in the source
        val entitiesToRemove = targetMap.filter { null == sourceMap[it.key] }.map { it.value }
        // remove items collected to remove
        if (entitiesToRemove.isNotEmpty()) {
            target.removeAll(entitiesToRemove)
            categoryBox.remove(entitiesToRemove)
        }
        // add items collected to add
        if (entitiesToAdd.isNotEmpty())
            target.addAll(entitiesToAdd)
    }

    private fun updateDirectoryChannels(target: ToMany<TvChannelEntity>, source: List<TvChannel>) {
        val sourceMap = source.associateBy { it.id }
        val targetMap = target.associateBy { it.externalId }
        // update existing channel items
        target.filter { sourceMap[it.externalId] != null }.map {
            it.apply { updateWith(sourceMap[it.externalId]!!) }
        }.let {
            channelBox.put(it)
        }
        // collect new channel items: source items not found in target collection
        val entitiesToAdd = source.filter { null == targetMap[it.id] }.map {
            channelMapper.mapToEntity(it)
        }
        // collect items to remove: target collection items not found in the source
        val entitiesToRemove = targetMap.filter { null == sourceMap[it.key] }.map { it.value }
        // remove items collected to remove
        if (entitiesToRemove.isNotEmpty()) {
            target.removeAll(entitiesToRemove)
            channelBox.remove(entitiesToRemove)
            // delete removed channels references from favorites
            val matchingFavorites = findFavoriteChannels(entitiesToRemove.map{ it.externalId })
            if (matchingFavorites.isNotEmpty())
                favoriteChannelBox.remove(matchingFavorites)
        }
        // add items collected to add
        if (entitiesToAdd.isNotEmpty())
            target.addAll(entitiesToAdd)
    }

    private fun updateDirectoryIndex(target: ToMany<TvChannelIndexEntity>, source: Map<Long, List<TvChannel>>) {
        // working map of current index
        val targetMap = target.groupBy { it.categoryId }
            .mapValues { (_, v) -> v.associateBy { it.channelId } }
        // index entries to add to the target index
        val entriesToAdd: MutableList<TvChannelIndexEntity> = mutableListOf()
        source.keys.forEach { categoryId ->
            source[categoryId]?.forEach { channel ->
                if (targetMap[categoryId]?.contains(channel.id) != true) {
                    // either a new category or a new channel added
                    entriesToAdd.add(TvChannelIndexEntity(0L, categoryId, channel.id))
                }
            }
        }
        // index entries to remove from the target index
        val entriesToRemove: MutableList<TvChannelIndexEntity> = mutableListOf()
        target.forEach { node ->
            source[node.categoryId]?.find { it.id == node.channelId } ?: run {
                entriesToRemove.add(node)
            }
        }
        // remove collected to remove
        target.removeAll(entriesToRemove)
        // add collected to add
        target.addAll(entriesToAdd)
    }

    // endregion
    // region Categories

    /** Categories inserted when the whole directory inserted. It makes sense sometimes, for example,
     * when switching language, to update only categories in a directory currently attached
     * to this delegate. Adding or removing categories requires the channels and index update
     * too, so use {@link #putDirectory}.
     */
    override fun updateCategories(categories: List<TvChannelCategory>): Completable {
        return Completable.fromRunnable {
            val sourceMap = categories.associateBy { it.id }
            val target = attachedDirectory()?.categories
            target?.forEachIndexed { index, entity ->
                sourceMap[entity.externalId]?.let {
                    val update = categoryMapper.mapToEntity(it)
                    update.id = target[index].id
                    target[index] = update
                }
            }
            if (target?.isNotEmpty() == true)
                categoryBox.put(target)
        }
    }

    override fun getCategories(): Single<List<TvChannelCategory>> = Single.fromCallable {
        attachedDirectory()?.categories?.map { categoryMapper.mapFromEntity(it) }?: listOf()
    }

    override fun findCategoryById(categoryId: Long): Single<TvChannelCategory?> = Single.fromCallable {
        attachedDirectory()?.categories?.find { it.externalId == categoryId }?.let {
            categoryMapper.mapFromEntity(it)
        }?: TvChannelCategory.empty()
    }

    // endregion
    // region Channels

    /** Channels inserted when the whole directory inserted. It makes sense sometimes, for example,
     * when changing time shift, to update only channels in a directory currently attached
     * to this delegate.
     *
     * Adding or removing channels requires the index update and, probably,
     * the categories update too, so use {@link #putDirectory}.
     */
    override fun updateChannels(channels: List<TvChannel>): Completable = Completable.fromRunnable {
        val srcMap = channels.associateBy { it.id }
        val dstEntities = attachedDirectory()?.channels
        dstEntities?.forEachIndexed { index, entity ->
            if (srcMap.containsKey(entity.id))
                if (srcMap.containsKey(entity.externalId)) {
                    val update = channelMapper.mapToEntity(srcMap[entity.externalId]!!)
                    update.id = dstEntities[index].id
                    dstEntities[index] = update
                }
        }
        if (dstEntities?.isNotEmpty() == true)
            channelBox.put(dstEntities)
    }

    /**
     *  It is supposed that the changes are correct (for efficiency).
     */
    override fun updateChannels(change: TvChannelsChange) {
        attachedDirectory()?.let { directoryEntity ->
            if (change.create.isNotEmpty()) {
                directoryEntity.channels.addAll(change.create.map { channelMapper.mapToEntity(it) })
                directoryBox.put(directoryEntity)
            }
            if (change.update.isNotEmpty()) {
                val updateMap = change.update.associateBy { it.id }
                directoryEntity.channels.forEachIndexed { index, item ->
                    if (updateMap.contains(item.externalId)) {
                        directoryEntity.channels[index] = updateMap[item.externalId]!!.let  {
                            val updatedItem = channelMapper.mapToEntity(it)
                            updatedItem.id = item.id
                            updatedItem
                        }
                    }
                }
                channelBox.put(change.update.map { channelMapper.mapToEntity(it) })
            }
            var itemsToDelete: List<TvChannelEntity>? = null
            if (change.delete.isNotEmpty()) {
                val deleteMap = change.delete.associateBy { it.id }
                itemsToDelete = directoryEntity.channels.filter {
                    deleteMap.containsKey(it.externalId) }
                directoryEntity.channels.removeAll(itemsToDelete)
            }
            if (change.create.isNotEmpty() || change.update.isNotEmpty() || change.delete.isNotEmpty()) {
                directoryBox.put(directoryEntity)
                // delete removed channel items from the object box
                itemsToDelete?.let { channelEntities ->
                    channelBox.remove(channelEntities)
                    // delete removed channels from favorites
                    val matchingFavorites = findFavoriteChannels(itemsToDelete.map{ it.externalId })
                    if (matchingFavorites.isNotEmpty())
                        favoriteChannelBox.remove(matchingFavorites)
                }
            }
        }
    }

    override fun getChannels(): Single<List<TvChannel>> = Single.fromCallable {
        attachedDirectory()?.channels?.map { channelMapper.mapFromEntity(it) }?: listOf()
    }

    override fun getChannels(categoryId: Long): Single<List<TvChannel>> = Single.fromCallable {
        attachedDirectory()?.channels?.filter { it.categoryId == categoryId }?.map {
            channelMapper.mapFromEntity(it) }?: listOf()
    }

    override fun findChannelById(channelId: Long): Single<TvChannel?> = Single.fromCallable {
        attachedDirectory()?.channels?.find { it.externalId == channelId }?.let {
            channelMapper.mapFromEntity(it) }?: TvChannel.empty()
    }

    override fun findChannelByNumber(channelNumber: Int): Single<TvChannel?> = Single.fromCallable {
        attachedDirectory()?.channels?.find { it.number == channelNumber }?.let {
            channelMapper.mapFromEntity(it) }?: TvChannel.empty()
    }

    /** Find earliest update time to keep the list part actual. I.e., to have all live program
     * times and titles correct (actual).
     */
    override fun getChannelWindowExpirationMillis(channelIds: List<Long>): Long? {
        if (channelIds.isEmpty()) return null
        var earliestEndMillis: Long? = null
        val set = channelIds.toSet()
        attachedDirectory()?.channels?.filter { set.contains(it.externalId) }?.forEach {
            it.live.target.endMillis?.let { endMillis ->
                if (null == earliestEndMillis || earliestEndMillis!! > endMillis)
                    earliestEndMillis = endMillis
            }
        }
        return earliestEndMillis
    }

    /** Collect time points when it's time to request update
     */
    override fun getChannelWindowUpdateSchedule(channelIds: List<Long>): List<Long> {
        val set = channelIds.toSet()
        return attachedDirectory()?.channels?.filter { entity -> set.contains(entity.externalId) }
            ?.map { entity -> entity.live.target.endMillis!! }?.distinct()?.sorted()?: listOf()
    }

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

    private fun findFavoriteChannels(channelIds: List<Long>): List<TvFavoriteChannelEntity> {
        return favoriteChannelBox.query {
            `in`(TvFavoriteChannelEntity_.channelId, channelIds.toLongArray())
            equal(TvFavoriteChannelEntity_.userLoginName, userLoginName)
        }.find()
    }

    // endregion

    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}
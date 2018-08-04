package org.alsi.android.data.framework.store

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created on 7/8/18.
 */
class DataCacheSingleTypeImpl<E> @Inject constructor(
        private val entitiesCache: DataCacheSingleType<E>)
    : DataCacheSingleType<E> {

    override fun getAll(): Flowable<List<E>> = entitiesCache.getAll()

    override fun getGroup(groupId: Long): Flowable<List<E>> = entitiesCache.getGroup(groupId)

    override fun findById(id: Long): Single<E> = entitiesCache.findById(id)

    override fun save(entities: List<E>, groupId: Long?): Completable {
        return entitiesCache.save(entities, groupId)
                .andThen(entitiesCache.setLastCacheTime(System.currentTimeMillis(), groupId))
    }

    override fun clear(groupId: Long?): Completable {
        return entitiesCache.clear(groupId)
    }

    override fun areEntitiesCached(groupId: Long?): Single<Boolean> = entitiesCache.areEntitiesCached(groupId)

    override fun setLastCacheTime(lastCache: Long, groupId: Long?): Completable = entitiesCache.setLastCacheTime(lastCache, groupId)

    override fun isCacheExpired(groupId: Long?): Single<Boolean> = entitiesCache.isCacheExpired(groupId)
}

class DataCacheSingleTypeExtract<E> @Inject constructor(
        private val entityClass : Class<E>,
        private val entitiesCache: DataCacheMultiType)
    : DataCacheSingleType<E> {

    override fun getAll(): Flowable<List<E>> = entitiesCache.getAll()

    override fun findById(id: Long): Single<E> = entitiesCache.findById(id)

    override fun getGroup(groupId: Long): Flowable<List<E>> = entitiesCache.getGroup(groupId)

    override fun save(entities: List<E>, groupId: Long?): Completable {
        return entitiesCache.save(entities, groupId)
                .andThen(entitiesCache.setLastCacheTime(entityClass, System.currentTimeMillis(), groupId))
    }

    override fun clear(groupId: Long?): Completable = entitiesCache.clear(entityClass, groupId)

    override fun areEntitiesCached(groupId: Long?): Single<Boolean> =
            entitiesCache.areEntitiesCached(entityClass, groupId)

    override fun setLastCacheTime(lastCache: Long, groupId: Long?): Completable =
            entitiesCache.setLastCacheTime(entityClass, lastCache, groupId)

    override fun isCacheExpired(groupId: Long?): Single<Boolean> = entitiesCache.isCacheExpired(entityClass, groupId)
}

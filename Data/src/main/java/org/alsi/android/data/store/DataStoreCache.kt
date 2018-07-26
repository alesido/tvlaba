package org.alsi.android.data.store

import io.reactivex.Completable
import io.reactivex.Single

interface DataCacheSingleType<E> : DataStoreSingleType<E>
{
    fun areEntitiesCached(groupId: Long? = null): Single<Boolean>

    fun setLastCacheTime(lastCache: Long, groupId: Long? = null): Completable

    fun isCacheExpired(groupId: Long? = null): Single<Boolean>
}

interface DataCacheMultiType : DataStoreMultipleType
{
    fun <E> areEntitiesCached(entityClass: Class<E>, groupId: Long? = null): Single<Boolean>

    fun <E> setLastCacheTime(entityClass: Class<E>, lastCache: Long, groupId: Long? = null): Completable

    fun <E> isCacheExpired(entityClass: Class<E>, groupId: Long? = null): Single<Boolean>
}

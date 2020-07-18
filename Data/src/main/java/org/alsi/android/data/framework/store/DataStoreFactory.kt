package org.alsi.android.data.framework.store

import javax.inject.Inject

open class DataStoreFactory<E> @Inject constructor(
        private val cacheDataStore: DataCacheSingleType<E>,
        private val remoteDataStore: DataRemoteSingleType<E>) {

    open fun getDataStore(entitiesCached: Boolean, cacheExpired: Boolean): DataStoreSingleType<E> {
        return if (entitiesCached && !cacheExpired) {
            cacheDataStore
        } else {
            remoteDataStore
        }
    }

    open fun getCacheDataStore(): DataStoreSingleType<E> {
        return cacheDataStore
    }

    open fun getRemoteDataStore(): DataStoreSingleType<E> {
        return remoteDataStore
    }

}

open class DataStoreFactoryGeneric @Inject constructor(
        private val cacheDataStore: DataCacheMultiType,
        private val remoteDataStore: DataRemoteMultiType) {

    open fun getDataStore(entitiesCached: Boolean, cacheExpired: Boolean): DataStoreMultipleType {
        return if (entitiesCached && !cacheExpired) {
            cacheDataStore
        } else {
            remoteDataStore
        }
    }

    open fun getCacheDataStore(): DataStoreMultipleType {
        return cacheDataStore
    }

    open fun getRemoteDataStore(): DataStoreMultipleType {
        return remoteDataStore
    }

}
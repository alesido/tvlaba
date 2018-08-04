package org.alsi.android.data.framework.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.data.framework.store.DataCacheSingleType
import org.alsi.android.data.framework.store.DataRemoteSingleType
import org.alsi.android.data.framework.store.DataStoreFactory
import javax.inject.Inject


class DataRepositoryRemoteWithCache<E,D> @Inject constructor(
        private val mapper: EntityMapper<E,D>,
        private val cache: DataCacheSingleType<E>,
        remote: DataRemoteSingleType<E>) {

    private val factory = DataStoreFactory(cache, remote)


    @Suppress("MemberVisibilityCanBePrivate")
    fun getCacheState(): Observable<Pair<Boolean, Boolean>> {
        return Observable.zip(cache.areEntitiesCached().toObservable(),
                cache.isCacheExpired().toObservable(),
                BiFunction<Boolean, Boolean, Pair<Boolean, Boolean>> { areCached, isExpired ->
                    Pair(areCached, isExpired)
                })
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getCacheState(groupId : Long): Observable<Pair<Boolean, Boolean>> {
        return Observable.zip(cache.areEntitiesCached(groupId).toObservable(),
                cache.isCacheExpired(groupId).toObservable(),
                BiFunction<Boolean, Boolean, Pair<Boolean, Boolean>> { areCached, isExpired ->
                    Pair(areCached, isExpired)
                })
    }

    fun getAll(): Observable<List<D>> {
        return getCacheState()
                .flatMap {
                    factory.getDataStore(it.first, it.second).getAll().toObservable()
                            .distinctUntilChanged()
                }
                .flatMap {
                    cache.save(it).andThen(Observable.just(it))
                }
                .map { list ->
                    list.map {
                        mapper.mapFromEntity(it)
                    }
                }
    }

    fun getGroup(groupId : Long): Observable<List<D>> {
        return getCacheState(groupId)
                .flatMap {
                    factory.getDataStore(it.first, it.second).getGroup(groupId).toObservable()
                            .distinctUntilChanged()
                }
                .flatMap {
                    cache.save(it).andThen(Observable.just(it))
                }
                .map {list -> list.map {mapper.mapFromEntity(it) }
                }
    }

    @Suppress("UNUSED_PARAMETER")
    fun findById(itemId: Long): Single<D> {
        return Single.just(null)
//        return getCacheState()
//                .flatMap {
//                    factory.getDataStore(it.first, it.second).findById(itemId).toObservable()
//                }
//                .map {
//                    Single.just(mapper.mapFromEntity(it))
//                }
    }
}


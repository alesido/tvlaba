package org.alsi.android.data.store

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created on 7/8/18.
 */
interface DataStoreSingleType<E>
{
    fun getAll(): Flowable<List<E>>

    fun getGroup(groupId: Long): Flowable<List<E>>

    fun findById(id: Long) : Single<E>

    fun save(entities: List<E>, groupId: Long? = null): Completable

    fun clear(groupId: Long? = null): Completable
}

interface DataStoreMultipleType
{
    fun <E> getAll(): Flowable<List<E>>

    fun <E> getGroup(groupId: Long): Flowable<List<E>>

    fun <E> findById(id: Long) : Single<E>

    fun <E> save(entities: List<E>, groupId: Long? = null): Completable

    fun <E> clear(entityClass: Class<E>, groupId: Long? = null): Completable
}
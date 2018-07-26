package org.alsi.android.data.store

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class DataRemoteSingleTypeImpl<E> @Inject constructor(
        private val dataStoreRemote: DataRemoteSingleType<E>)
    : DataRemoteSingleType<E> {

    override fun getAll(): Flowable<List<E>> = dataStoreRemote.getAll()

    override fun getGroup(groupId: Long): Flowable<List<E>> = dataStoreRemote.getGroup(groupId)

    override fun findById(id: Long): Single<E> = dataStoreRemote.findById(id)

    override fun save(entities: List<E>, groupId: Long?): Completable = dataStoreRemote.save(entities, groupId)

    override fun clear(groupId: Long?): Completable = dataStoreRemote.clear(groupId)
}

class DataRemoteSingleTypeExtract<E> @Inject constructor(
        private val entityClass : Class<E>,
        private val dataStoreRemote: DataRemoteMultiType)
    : DataRemoteSingleType<E> {

    override fun getAll(): Flowable<List<E>> = dataStoreRemote.getAll()

    override fun getGroup(groupId: Long): Flowable<List<E>> = dataStoreRemote.getGroup(groupId)

    override fun findById(id: Long): Single<E>  = dataStoreRemote.findById(id)

    override fun save(entities: List<E>, groupId: Long?): Completable = dataStoreRemote.save(entities, groupId)

    override fun clear(groupId: Long?): Completable = dataStoreRemote.clear(entityClass, groupId)
}
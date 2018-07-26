package org.alsi.android.data.store

import io.reactivex.Flowable
import io.reactivex.Single

interface DataRemoteSingleType<E> : DataStoreSingleType<E> {

    override fun getAll(): Flowable<List<E>>

    override fun findById(id: Long) : Single<E>
}

interface DataRemoteMultiType : DataStoreMultipleType {

    override fun <E> getAll(): Flowable<List<E>>

    override fun <E> findById(id: Long) : Single<E>
}
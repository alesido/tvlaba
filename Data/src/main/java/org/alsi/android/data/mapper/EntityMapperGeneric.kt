package org.alsi.android.data.mapper

interface EntityMapperGeneric {

    fun <E, D> mapFromEntity(entity: E): D

    fun <E, D> mapToEntity(domain: D): E

}
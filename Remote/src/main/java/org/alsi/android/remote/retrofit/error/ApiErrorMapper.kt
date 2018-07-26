package org.alsi.android.remote.retrofit.error

/**
 * Created on 7/26/18.
 */
interface ApiErrorMapper {
    fun mapCodeToType(code: Int): ApiError
}
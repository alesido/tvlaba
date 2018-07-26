package org.alsi.android.moidom.model.base

import org.alsi.android.remote.retrofit.error.RetrofitExceptionSource

/**
 * Created on 7/25/18.
 */
abstract class BaseResponse : RetrofitExceptionSource {

    abstract val error: RequestError?
    abstract val servertime: Int

    override fun isErrorResponse(): Boolean {
        return error != null
    }

    override fun getApiResponseCode(): Long? {
        return error?.code
    }

    override fun getErrorMessage(): String? {
        return error?.message
    }
}
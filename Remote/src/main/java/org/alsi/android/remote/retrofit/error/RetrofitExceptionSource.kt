package org.alsi.android.remote.retrofit.error

/** Source of HTTP exception which is HTTP response containing error object while the
 *  request was successful, - returned HTTP response code 200
 */
interface RetrofitExceptionSource {

    /** Test whether response object contains an error object.
     */
    fun isErrorResponse(): Boolean

    fun getApiResponseCode(): Long?

    fun getErrorMessage(): String?
}
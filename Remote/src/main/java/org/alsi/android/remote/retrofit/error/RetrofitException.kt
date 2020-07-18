package org.alsi.android.remote.retrofit.error

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

/**
 *  Unified retrofit exception to simplify error
 *  processing end visualization.
 *
 *  @see "https://gist.github.com/yitz-grocerkey/61a66a15a0c22e8ea5149484676618c9#file-rxerrorhandlingcalladapterfactory-kt"
 *  @see "https://bytes.babbel.com/en/articles/2016-03-16-retrofit2-rxjava-error-handling.html"
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class RetrofitException(
        exception: Throwable?,
        private val requestRetrofit: Retrofit,
        private val responseType: Type)

    : RuntimeException( exception?.let { when (it) {
        is HttpException -> it.response()?.message()
        is IOException -> it.localizedMessage
        else -> it.localizedMessage
    }}, exception) {

    var apiResponseCode: Long? = null
    var apiErrorMessage: String? = null

    constructor(call: Call<*>, source: RetrofitExceptionSource, requestRetrofit: Retrofit, responseType: Type)
            : this(null, requestRetrofit, responseType) {
        this.errorKind = Kind.REST_API
        this.apiResponseCode = source.getApiResponseCode()
        this.apiErrorMessage = source.getErrorMessage()
        this.requestUrl = call.request().url
        this.errorData = source
    }

    var errorKind: Kind? = exception?.let {
        if (it is HttpException) {
            if (it.code() == 422) Kind.REST_API else Kind.HTTP
        } else if (it is IOException) Kind.NETWORK
        else Kind.UNEXPECTED
    }

    var requestUrl = exception?.let {
        (it as? HttpException)?.response()?.raw()?.request?.url
    }

    val errorResponse = exception?.let {
        (it as? HttpException)?.response()
    }

    var errorData = exception?.let {
        val errorBody = errorResponse?.errorBody()
        if (it is HttpException && errorBody != null) {
            try {
                val converter : Converter<ResponseBody, Any> =
                        requestRetrofit.responseBodyConverter(responseType, arrayOfNulls<Annotation>(0))
                val result = converter.convert(errorBody)
                (result as RetrofitExceptionSource).let { apiResponseCode = it.getApiResponseCode()
                    apiErrorMessage = it.getErrorMessage() }
                result
        }
            catch (x: IOException) {
                System.out.println(String.format("Retrofit server error deserialization. %s", x.message))
            }
        }
        else null
    }

    enum class Kind {
        /** An [IOException] occurred while communicating to the server.
         */
        NETWORK,

        /**
         * A non-200 HTTP status code was received from the server.
         *
         */
        HTTP,

        /** Server defined (REST API) error when it come originally as 200 code response (which
         * changed to 422 by the special error criteria interceptor).
         */
        REST_API,

        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }
}

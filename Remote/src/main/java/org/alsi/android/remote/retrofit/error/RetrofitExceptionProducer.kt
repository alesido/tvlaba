package org.alsi.android.remote.retrofit.error

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.util.*

class RetrofitExceptionProducer(

    /**
     *  This flag is to add this interceptor into a Retrofit Service only when
     *  testing and avoid it in all the other modes (debugging, release).
     *
     *  This interceptor wont produce test exceptions until its method "start" called.
     *  Still, there will be a little overhead for instrumented tests w/o test exceptions.
     *
     *  This interceptor is assumed to be a Dagger Singleton. The provider method will give
     *  an activated or inactivated instance of this interceptor. See the dagger component
     *  for details.
     *
     *  Retrofit builders have to add this interceptor into the chain only if it's active.
     *
     *  Retrofit based services are internals of remote stores, thus there is no way for a test
     *  to access the Retrofit builders and add the testing interceptor.
     */
    val isActivated: Boolean = true,

    private var testException: Throwable = IOException("Test IO Exception")

): Interceptor {

    enum class RequestMatch(val endpointName: String) {
        LOGIN("/login?"),
        CHANNEL_LIST("/channel_list?"),
        TV_GROUPS("/tv_groups?"),
        TV_PROGRAMS("/tv_programs?"),
        GET_URL("/get_url?");
    }

    enum class Mode {
        EXCEPTION_PRODUCER,
        RESPONSE_REPLACEMENT
    }

    private var mode: Mode = Mode.EXCEPTION_PRODUCER

    private var throwException = false
    private var throwCounter = 0

    private var responseBodyReplacement: ResponseBody? = null

    /** ... to support scenarios in instrumentation tests
     */
    private val exclusionSet: MutableSet<String> = TreeSet()

    /** ... to initialize scenario in instrumentation test
     */
    fun start(throwCounter: Int = 1, aTestException: Throwable? = null) {
        this.mode = Mode.EXCEPTION_PRODUCER
        this.throwCounter = throwCounter
        aTestException?.let { this.testException = aTestException }
        this.throwException = true
    }

    fun start(responseBodyReplacementJsonString: String) {
        this.mode = Mode.RESPONSE_REPLACEMENT
        this.responseBodyReplacement = responseBodyReplacementJsonString
            .toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    fun stop() {
        throwException = false
    }

    fun excludeRequest(vararg matches: RequestMatch) {
        matches.forEach { exclusionSet.add(it.endpointName) }
    }

    fun resetExclusion() {
        exclusionSet.clear()
    }

    //@Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (mode == Mode.EXCEPTION_PRODUCER)
            produceException(chain)
        else
            replaceResponseBody(chain)
    }

    private fun produceException(chain: Interceptor.Chain): Response {
        if (!throwException)
            return chain.proceed(chain.request())

        val endpointName = getRequestEndPoint(chain.request().url.toString())
        if (!exclusionSet.contains(endpointName) && throwCounter-- > 0) {
            println(String.format("### %s thrown for %s", testException.javaClass.name,
                chain.request().url.toString()))
            throw testException
        }
        return chain.proceed(chain.request())
    }

    private fun replaceResponseBody(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response.newBuilder().body(responseBodyReplacement).build()
    }

    private fun getRequestEndPoint(requestUrl: String): String {
        return requestUrl.substring(requestUrl.lastIndexOf("/"), requestUrl.indexOf("?") + 1)
    }
}
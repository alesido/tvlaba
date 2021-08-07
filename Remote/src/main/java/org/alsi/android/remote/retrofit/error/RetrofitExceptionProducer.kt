package org.alsi.android.remote.retrofit.error

import okhttp3.Interceptor
import okhttp3.Response
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

    private var throwException = false
    private var throwCounter = 0

    /** ... to support scenarios in instrumentation tests
     */
    private val exclusionSet: MutableSet<String> = TreeSet()

    /** ... to initialize scenario in instrumentation test
     */
    fun start(throwCounter: Int = 1, aTestException: Throwable? = null) {
        this.throwCounter = throwCounter
        aTestException?.let { this.testException = aTestException }
        this.throwException = true
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

    private fun getRequestEndPoint(requestUrl: String): String {
        return requestUrl.substring(requestUrl.lastIndexOf("/"), requestUrl.indexOf("?") + 1)
    }
}
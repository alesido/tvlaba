package org.alsi.android.moidom.framework

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

class ExceptionTestingInterceptor(
    private val testException: Exception = IOException("Test IO Exception")
): Interceptor {

    enum class RequestMatch(val endpointName: String) {
        LOGIN("/login?"),
        CHANNEL_LIST("/channel_list?"),
        TV_GROUPS("/tv_groups?"),
        TV_PROGRAMS("/tv_programs?"),
        GET_URL("/get_url?");
    }

    private var throwException = true
    private var throwCounter = 1

    /** ... to support scenarios in instrumentation tests
     */
    private val exclusionSet: MutableSet<String> = TreeSet()

    /** ... to initialize scenario in instrumentation test
     */
    fun start(throwCounter: Int = 1) {
        this.throwCounter = throwCounter
        throwException = true
    }

    fun stop() {
        throwException = false
    }

    fun excludeRequest(match: RequestMatch) {
        exclusionSet.add(match.endpointName)
    }

    fun resetExclusion() {
        exclusionSet.clear()
    }

    //@Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val endpointName = getRequestEndPoint(chain.request().url.toString())
        if (!exclusionSet.contains(endpointName) && throwException && throwCounter-- > 0) {
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
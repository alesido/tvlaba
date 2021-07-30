package org.alsi.android.remote.retrofit

import com.google.gson.Gson

import java.util.LinkedHashMap
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.alsi.android.remote.retrofit.error.RetrofitException
import org.alsi.android.remote.retrofit.error.RxErrorHandlingCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/** Retrofit2 service builder/factory
 *
 * @param apiDefinitionClass Required retrofit-annotated API specification.
 * @param baseServiceUrl Required base REST service URL
 */
class RetrofitServiceBuilder<API>
(
    /** Class of retrofit-annotated interface which specifies a REST API.
     */
    private val apiDefinitionClass: Class<API>,

    /** Base URL of the REST API service.
     */
    private val baseServiceUrl: String
)
{
    private var errorPostProcessor: RetrofitErrorPostProcessor? = null

    private var queryParamProvider: QueryParamsProvider? = null
    private var queryPostProcessor: QueryPostProcessor? = null

    private var gson: Gson? = null

    private var isLoggingEnabled = false

    private var customLogger: HttpLoggingInterceptor.Logger? = null

    private val extraQueryParams = LinkedHashMap<String, String>()

    private var isRxErrorHandlingCallAdapterFactoryEnabled: Boolean = false

    /** Interface to define a source of default query parameters changing
     * their value in each request.
     */
    interface QueryParamsProvider {
        val queryParams: List<Pair<String, String>>
    }

    /** Interface to define post processor which can add or modify query parameters
     * taking as input other parameters. Primarily to derive and add query signing
     * parameter.
     */
    interface QueryPostProcessor {
        fun process(request: Request, urlBuilder: HttpUrl.Builder)
    }

    /** Enable logging.
     */
    fun enableLogging(vararg enable: Boolean): RetrofitServiceBuilder<API> {
        this.isLoggingEnabled = enable.isEmpty() || enable[0]
        return this
    }

    /** Set intercepting logger.
     *
     * @param customLogger
     * @return
     */
    fun setLogger(customLogger: HttpLoggingInterceptor.Logger): RetrofitServiceBuilder<API> {
        this.isLoggingEnabled = true
        this.customLogger = customLogger
        return this
    }

    /** Set query parameter appended to each query.
     */
    fun addQueryParameter(paramName: String, paramValue: String): RetrofitServiceBuilder<API> {
        extraQueryParams[paramName] = paramValue
        return this
    }

    /** Set query parameters provider.
     *
     * @param queryParamProvider Source of default query parameters changing their value.
     * @return
     */
    fun setQueryParamsProvider(queryParamProvider: QueryParamsProvider): RetrofitServiceBuilder<API> {
        this.queryParamProvider = queryParamProvider
        return this
    }

    /** Set query post processor.
     *
     * @param queryPostProcessor Query post processor which can add or modify query parameters taking as input other parameters.
     */
    fun setQueryPostProcessor(queryPostProcessor: QueryPostProcessor): RetrofitServiceBuilder<API> {
        this.queryPostProcessor = queryPostProcessor
        return this
    }

    fun enableRxErrorHandlingCallAdapterFactory(
        postErrorProcessor: ((retrofitException: RetrofitException) -> Throwable)? = null
    ): RetrofitServiceBuilder<API> {
        this.isRxErrorHandlingCallAdapterFactoryEnabled = true
        this.errorPostProcessor = postErrorProcessor
        return this
    }

    fun setGson(gson: Gson): RetrofitServiceBuilder<API> {
        this.gson = gson
        return this
    }

    /**
     * @return Configured implementation of an API service.
     */
    fun build(): API
    {
        val clientBuilder = OkHttpClient.Builder()

        if (extraQueryParams.size > 0 || queryParamProvider != null || queryPostProcessor != null)
        {
            val urlParameterInjector = { chain: Interceptor.Chain ->
                var request = chain.request()

                // request modification
                val urlBuilder = request.url.newBuilder()
                for ((key, value) in extraQueryParams) {
                    urlBuilder.addQueryParameter(key, value)
                }
                queryParamProvider?.let {
                    for ((first, second) in it.queryParams) urlBuilder.addQueryParameter(first, second)
                }
                queryPostProcessor?.process(request, urlBuilder)

                request = request.newBuilder().url(urlBuilder.build()).build()

                // request execution
                chain.proceed(request)
            }

            clientBuilder.addInterceptor(urlParameterInjector)
        }

        if (isLoggingEnabled) {
            val loggingInterceptor = if (customLogger != null)
                HttpLoggingInterceptor(customLogger!!)
            else HttpLoggingInterceptor()

            clientBuilder.addInterceptor(loggingInterceptor)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        val retrofit = Retrofit.Builder()
                .baseUrl(baseServiceUrl)
                .client(clientBuilder.build())
                .addCallAdapterFactory(
                    if (isRxErrorHandlingCallAdapterFactoryEnabled)
                        RxErrorHandlingCallAdapterFactory(errorPostProcessor)
                    else
                        RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson?: Gson()))
                .build()

        return retrofit.create(apiDefinitionClass)
    }
}

typealias RetrofitErrorPostProcessor = (retrofitException: RetrofitException) -> Throwable
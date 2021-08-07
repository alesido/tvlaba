package org.alsi.android.moidom.store

import com.google.gson.GsonBuilder
import org.alsi.android.remote.retrofit.RetrofitErrorPostProcessor
import org.alsi.android.remote.retrofit.RetrofitServiceBuilder
import org.alsi.android.remote.retrofit.error.RetrofitExceptionProducer
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap

/**
 * Created on 8/1/18.
 */
object DataServiceFactoryMoidom {

    fun makeRestServiceMoidom(errorPostProcessor: RetrofitErrorPostProcessor, instrumentedTestInterceptor: RetrofitExceptionProducer): RestServiceMoidom {
        val gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()
        val builder =  RetrofitServiceBuilder(RestServiceMoidom::class.java, RestServiceMoidom.SERVICE_URL)
                .enableRxErrorHandlingCallAdapterFactory(errorPostProcessor)
                .setGson(gson).enableLogging()
        if (instrumentedTestInterceptor.isActivated)
            builder.addInterceptor(instrumentedTestInterceptor)
        return builder.build()
    }
}
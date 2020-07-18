package org.alsi.android.moidom.store

import com.google.gson.GsonBuilder
import org.alsi.android.remote.retrofit.RetrofitServiceBuilder
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap

/**
 * Created on 8/1/18.
 */
object DataServiceFactoryMoidom {

    fun makeRestServiceMoidom(): RestServiceMoidom {
        val gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()
        return RetrofitServiceBuilder(RestServiceMoidom::class.java, RestServiceMoidom.SERVICE_URL)
                .enableRxErrorHandlingCallAdapterFactory()
                .setGson(gson).enableLogging().build()
    }
}
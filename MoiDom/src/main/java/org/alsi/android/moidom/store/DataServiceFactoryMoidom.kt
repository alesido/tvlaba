package org.alsi.android.moidom.store

import android.content.Context
import com.google.gson.GsonBuilder
import io.objectbox.BoxStore
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.model.local.user.MyObjectBox
import org.alsi.android.moidom.store.remote.RestServiceMoidom
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

    fun makeInternalStoreService(context: Context): BoxStore {
        return MyObjectBox.builder().name(Moidom.INTERNAL_STORE_NAME).androidContext(context).build()
    }
}
package org.alsi.android.remote.retrofit.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/** A map to indicate which items given by name are enabled, e.g.
 * which streaming services are enabled.
 *
 * Map value 1 means "true", false for other values
 */
open class IntEnablingMap : HashMap<String, Int>()

class JsonDeserializerForIntEnablingMap: JsonDeserializer<IntEnablingMap> {
    override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?)
            : IntEnablingMap {

        val result = IntEnablingMap()
        json?.asJsonArray?.forEach{
            val a = it.asJsonArray
            val name = a[0].asString
            val flag = a[1].asInt
            result[name] = flag
        }
        return result
    }
}


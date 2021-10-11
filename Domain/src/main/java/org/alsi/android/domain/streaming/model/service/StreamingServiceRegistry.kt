package org.alsi.android.domain.streaming.model.service

import java.util.*
import kotlin.collections.ArrayList

class StreamingServiceRegistry(services: List<StreamingService>) : ArrayList<StreamingService>(services) {

    val serviceById: Map<Long, StreamingService> = associateBy({it.id},{it})
    val serviceByTag: Map<String, StreamingService> = associateBy({it.tag},{it})

    /** TODO Make immutable service-by-kind index. Check "https://stackoverflow.com/questions/37931676/how-to-turn-a-mutable-collection-into-an-immutable-one"
     */
    val servicesByKind: MutableMap<StreamingServiceKind, MutableList<StreamingService>> = let { services ->
        val result: EnumMap<StreamingServiceKind, MutableList<StreamingService>> = EnumMap(StreamingServiceKind::class.java)
        services.forEach {
            if (null == result[it.kind]) {
                result[it.kind] = mutableListOf()
            }
            result[it.kind]?.add(it)
        }
        result
    }
}
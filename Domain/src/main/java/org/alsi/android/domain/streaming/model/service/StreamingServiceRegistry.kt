package org.alsi.android.domain.streaming.model.service

class StreamingServiceRegistry : ArrayList<StreamingService>() {

    val serviceById: Map<Long, StreamingService> = associateBy({it.id},{it})
    val serviceByTag: Map<String, StreamingService> = associateBy({it.tag},{it})

    /** TODO Make immutable service-by-kind index. Check "https://stackoverflow.com/questions/37931676/how-to-turn-a-mutable-collection-into-an-immutable-one"
     */
    val servicesByKind: MutableMap<StreamingServiceKind, MutableList<StreamingService>> = let { _ ->
        val result: HashMap<StreamingServiceKind, MutableList<StreamingService>> = HashMap()
        forEach {
            if (null == result[it.kind]) {
                result[it.kind] = mutableListOf()
            }
            result[it.kind]?.add(it)
        }
        result
    }
}
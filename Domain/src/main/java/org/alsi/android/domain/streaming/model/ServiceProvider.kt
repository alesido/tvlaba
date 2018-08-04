package org.alsi.android.domain.streaming.model

/**
 * Created on 7/27/18.
 */
class ServiceProvider(val id: Long, val name: String, private val services: List<StreamingService>) {

    fun serviceByTag(tag: String): StreamingService? {
        services.forEach{ if (it.tag == tag) return it }
        return null
    }
}
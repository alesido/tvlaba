package org.alsi.android.presentation.state

import androidx.annotation.Nullable

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * @see "https://stackoverflow.com/questions/49832787/livedata-prevent-receive-the-last-value-when-start-observing"
 */
class Event<T>(private val content: T, val payload: Any? = null, val error: Throwable? = null) {

    private var hasBeenHandled = false

    @get:Nullable
    val contentIfNotHandled: T?
        get() = if (hasBeenHandled) null else {
            hasBeenHandled = true
            content
        }

    fun hasBeenHandled(): Boolean {
        return hasBeenHandled
    }
}
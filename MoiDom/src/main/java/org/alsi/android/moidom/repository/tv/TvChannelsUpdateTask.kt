package org.alsi.android.moidom.repository.tv

import io.reactivex.disposables.Disposable
import org.alsi.android.moidom.repository.tv.TvChannelsUpdateTaskState.*

class TvChannelsUpdateTask (
        /**
         * Time when a current live ends and the data on a channel live change.
         * This is to verify if the next task is different.
         */
        val targetMillis: Long,

        val subscription: Disposable
) {

    var state: TvChannelsUpdateTaskState = ZERO

    var lastError: Throwable? = null

    fun setRunning() {
        state = RUNNING
    }

    fun setWaiting() {
        state = WAITING
    }

    fun setFailed(error: Throwable?) {
        lastError = error
        state = ERROR
    }

    fun setCompleted() {
        state = COMPLETED
        if (subscription.isDisposed) subscription.dispose()
    }

    fun cancel() {
        state = CANCELLED
        if (subscription.isDisposed) subscription.dispose()
    }

    val isFinished get() = state == COMPLETED
            || state == CANCELLED

    val isCancelled get() = state == CANCELLED
}

enum class TvChannelsUpdateTaskState {
    ZERO, WAITING, RUNNING, ERROR, COMPLETED, CANCELLED
}


package org.alsi.android.framework

import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import kotlin.math.pow

object RxUtils {

    private class ErrorRecoveryStep (val error: Throwable?, val recoveryAttempt: Int)

    /**
     * Exponential backoff. Resubscribes with an exponential delay on each subsequent error.
     *
     * @see "https://blog.danlew.net/2016/01/25/rxjavas-repeatwhen-and-retrywhen-explained/"
     */
    fun exponentialBackoff(

            nextError: Flowable<out Throwable?>,
            initialDelaySeconds: Int,
            attemptsLimit: Int,
            isErrorRecoverable: (Throwable?) -> Boolean

    ): Flowable<*> {

        return nextError.zipWith(Flowable.range(1, attemptsLimit + 1)) {
            // combine an error with a recovery attempt number
            error, attempt -> ErrorRecoveryStep(error, attempt)
        }.flatMap { step ->
            if (step.recoveryAttempt <= attemptsLimit && isErrorRecoverable(step.error)) {
                // retry with delay
                return@flatMap Flowable.timer(
                        initialDelaySeconds.toDouble().pow(step.recoveryAttempt).toLong(),
                        TimeUnit.SECONDS
                )
            }
            // give up retries
            Flowable.error<Throwable?>(step.error)
        }
    }
}

package org.alsi.android.rx

import io.reactivex.Single
import org.junit.Test
import java.util.concurrent.TimeUnit

class RxSnippetTest {

    @Test
    fun repeatWithCondition() {
        var i = 0
        Single.just(i)
        .map {
            i++; println("@repeatWithCondition, map to $i")
        }
        .repeat()
        .takeUntil{
            i == 3
        }
        .subscribe{
            println("@repeatWithCondition, onNext at step $i")
        }
    }

    @Test
    fun repeatWithConditionAndDelay() {
        var i = 0
        val testObserver =
                Single.timer(3, TimeUnit.SECONDS)
                .map {
                    i++; println("@repeatWithConditionAndDelay map to $i")
                }
                .repeat()
                .takeUntil {
                    i == 3
                }
                .test()
        testObserver.await()
    }
}
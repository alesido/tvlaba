package org.alsi.android.local.store.tv

import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.observers.TestObserver
import org.alsi.android.domain.tv.model.session.TvPlayCursor
import org.alsi.android.local.model.MyObjectBox
import org.alsi.android.local.model.user.UserAccountSubject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(JUnit4::class)
class TvPlayCursorLocalStoreDelegateTest {

    private lateinit var boxStore: BoxStore

    private lateinit var storeDelegate: TvPlayCursorLocalStoreDelegate

    @Before
    fun setUp() {
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        boxStore = MyObjectBox.builder().directory(TvChannelLocalStoreDelegateUnitTest.TEST_DATA_DIRECTORY)
            .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
            .build()
        storeDelegate = TvPlayCursorLocalStoreDelegate(boxStore, UserAccountSubject.create())
    }

    @After
    fun tearDown() {
        boxStore.close()
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        boxStore.closeThreadResources()
    }

    @Test
    fun shouldUpdatePlayCursor() {

        val testPutObserver = TestObserver<Unit>()
        val testCursor = TvPlayCursorTestDataFactory.testPlayCursor()
        storeDelegate.putPlayCursor(testCursor)
            .subscribe(testPutObserver)
        onTestObserverTermination(testPutObserver, "shouldUpdatePlayCursor 1")

        val testGetObserver = TestObserver<TvPlayCursor>()
        storeDelegate.getLastPlayCursor().subscribe(testGetObserver)
        onTestObserverTermination(testGetObserver, "shouldUpdatePlayCursor 1")

        val testUpdateObserver = TestObserver<Unit>()
        val testCursorUpdate = TvPlayCursorTestDataFactory.testPlayCursor2()
        storeDelegate.updatePlayCursor(testCursorUpdate.playback)
            .subscribe(testUpdateObserver)
        onTestObserverTermination(testUpdateObserver, "shouldUpdatePlayCursor 2")

        val testGetObserver2 = TestObserver<TvPlayCursor>()
        storeDelegate.getLastPlayCursor().subscribe(testGetObserver2)
        onTestObserverTermination(testGetObserver2, "shouldUpdatePlayCursor 1")

        val result = testGetObserver2.values()[0]
        assertEquals(testCursorUpdate.playback.stream?.kind, result.playback.stream?.kind)
        assertEquals(testCursorUpdate.playback.stream?.uri, result.playback.stream?.uri)
        assertEquals(testCursorUpdate.playback.position, result.seekTime)
    }

    private fun onTestObserverTermination(testObserver: TestObserver<*>, testTag: String) {
        testObserver.awaitTerminalEvent(10, TimeUnit.MILLISECONDS)
        if (testObserver.errorCount() > 0) {
            testObserver.errors()?.forEach { println("TEST: $testTag"); it.printStackTrace() }
            fail()
        }
    }

    companion object {
        val TEST_DATA_DIRECTORY = File("objectbox/test-db")
    }
}
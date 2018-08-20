package org.alsi.android.local.store.tv

import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.observers.TestObserver
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.local.model.MyObjectBox
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
class TvChannelLocalStoreDelegateUnitTest {

    private lateinit var boxStore: BoxStore

    private lateinit var storeDelegate: TvChannelLocalStoreDelegate

            @Before
    fun setUp() {
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        boxStore = MyObjectBox.builder().directory(TEST_DATA_DIRECTORY)
                .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
                .build()
        storeDelegate = TvChannelLocalStoreDelegate(boxStore, "TestUser")
    }

    @Test
    fun shouldStoreCategories() {
        val testObserver = TestObserver<List<TvChannelCategory>>()

        val testCategories = TvChannelTestDataFactory.categories()
        storeDelegate.putCategories(testCategories).subscribe {
            storeDelegate.getCategories().subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldStoreCategories")

        val readCategories = testObserver.values()[0]
        assertEquals(testCategories.size, readCategories.size)
        for (i in 0 until testCategories.size) {
            assertEquals(testCategories[i].id, readCategories[i].id)
            assertEquals(testCategories[i].title, readCategories[i].title)
            assertEquals(testCategories[i].logo.kind, readCategories[i].logo.kind)
            assertEquals(testCategories[i].logo.reference, readCategories[i].logo.reference)
        }
    }

    private fun onTestObserverTermination(testObserver: TestObserver<*>, testTag: String) {
        testObserver.awaitTerminalEvent(1, TimeUnit.SECONDS)
        if (testObserver.errorCount() > 0) {
            testObserver.errors()?.forEach { println("TEST: $testTag"); it.printStackTrace() }
            fail()
        }
    }

    @Test
    fun shouldStoreChannels() {
        val testObserver = TestObserver<List<TvChannel>>()

        val testChannels = TvChannelTestDataFactory.channels()
        storeDelegate.putChannels(testChannels).subscribe {
            storeDelegate.getChannels().subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldStoreChannels")

        val readChannels = testObserver.values()[0]
        assertEquals(testChannels.size, readChannels.size)
        for (i in 0 until testChannels.size) {
            assertEquals(testChannels[i].id, readChannels[i].id)
//            assertEquals(testChannels[i].categoryId, readChannels[i].categoryId)
            assertEquals(testChannels[i].logoUri, readChannels[i].logoUri)
            assertEquals(testChannels[i].number, readChannels[i].number)
            assertEquals(testChannels[i].title, readChannels[i].title)
        }
    }
//
//    @Test
//    fun shouldSearchCategoryById() {
//
//    }
//
//    @Test
//    fun shouldSearchChannelByNumber() {
//
//    }
//
//    @Test
//    fun shouldToggleFavorites() {
//
//    }
//
//    @Test
//    fun shouldSeparateFavoritesOfDifferentUsers() {
//
//    }
//
    @After
    fun tearDown() {
        boxStore.close()
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        boxStore.closeThreadResources()
    }

    companion object {
        val TEST_DATA_DIRECTORY = File("objectbox/test-db")
    }
}
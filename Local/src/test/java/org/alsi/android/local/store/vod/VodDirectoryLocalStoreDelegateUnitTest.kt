package org.alsi.android.local.store.vod

import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.observers.TestObserver
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.local.model.MyObjectBox
import org.alsi.android.local.model.user.UserAccountSubject
import org.alsi.android.local.store.tv.TvChannelLocalStoreDelegateUnitTest
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
class VodDirectoryLocalStoreDelegateUnitTest {

    private lateinit var boxStore: BoxStore

    private lateinit var storeDelegate: VodDirectoryLocalStoreDelegate

    @Before
    fun setUp() {
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        boxStore = MyObjectBox.builder().directory(TEST_DATA_DIRECTORY)
            .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
            .build()
        storeDelegate = VodDirectoryLocalStoreDelegate(boxStore, UserAccountSubject.create())
    }

    @After
    fun tearDown() {
        boxStore.close()
        BoxStore.deleteAllFiles(TvChannelLocalStoreDelegateUnitTest.TEST_DATA_DIRECTORY)
        boxStore.closeThreadResources()
    }

    @Test
    fun shouldStoreDirectory() {
        val testObserver = TestObserver<VodDirectory>()

        val testDirectory = VodDirectoryTestDataFactory.directory()

        storeDelegate.putDirectory(testDirectory).subscribe {
            storeDelegate.getDirectory().subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldStoreDirectory")

        val readDirectory = testObserver.values()[0]
        assertEquals(testDirectory.sections.size, readDirectory.sections.size)
        val readSections = readDirectory.sections
        with(testDirectory) {
            for (i in sections.indices) {
                assertEquals(sections[i].id, readSections[i].id)
                assertEquals(sections[i].title, readSections[i].title)
                assertEquals(sections[i].units.size, readSections[i].units.size, "Different units size in section $i")
                val t = sections[i].units
                val r = readSections[i].units
                for (j in t.indices) {
                    assertEquals(t[j].id, r[j].id)
                    assertEquals(t[j].title, r[j].title)
                    assertEquals(t[j].sectionId, r[j].sectionId)
                    assertEquals(t[j].total, r[j].total)
                }
            }
        }
    }

    @Test
    fun shouldUpdateDirectory() {
        val testObserver = TestObserver<VodDirectory>()
        val testDirectory = VodDirectoryTestDataFactory.directory()
        storeDelegate.putDirectory(testDirectory).subscribe {
            storeDelegate.getDirectory().subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldUpdateDirectory")

        val testObserver2 = TestObserver<VodDirectory>()
        val testDirectory2 = VodDirectoryTestDataFactory.directory2()
        storeDelegate.putDirectory(testDirectory2).subscribe {
            storeDelegate.getDirectory().subscribe(testObserver2)
        }
        onTestObserverTermination(testObserver2, "shouldUpdateDirectory 2")

        val readDirectory2 = testObserver2.values()[0]
        assertEquals(testDirectory2.sections.size, readDirectory2.sections.size)
        val readSections = readDirectory2.sections
        with(testDirectory2) {
            for (i in sections.indices) {
                assertEquals(sections[i].id, readSections[i].id)
                assertEquals(sections[i].title, readSections[i].title)
                assertEquals(sections[i].units.size, readSections[i].units.size, "Different units size in section $i")
                val t = sections[i].units
                val r = readSections[i].units
                for (j in t.indices) {
                    assertEquals(t[j].id, r[j].id)
                    assertEquals(t[j].title, r[j].title)
                    assertEquals(t[j].sectionId, r[j].sectionId)
                    assertEquals(t[j].total, r[j].total)
                }
            }
        }
    }

    private fun onTestObserverTermination(testObserver: TestObserver<*>, testTag: String) {
        testObserver.awaitTerminalEvent(1, TimeUnit.SECONDS)
        if (testObserver.errorCount() > 0) {
            testObserver.errors()?.forEach { println("TEST: $testTag"); it.printStackTrace() }
            fail()
        }
    }

    companion object {
        val TEST_DATA_DIRECTORY = File("objectbox/test-db")
    }
}
package org.alsi.android.local.store.vod

import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.observers.TestObserver
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
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
import kotlin.test.assertNotNull
import kotlin.test.fail

@RunWith(JUnit4::class)
class VodListingLocalStoreDelegateUnitTest {

    private lateinit var boxStore: BoxStore

    private lateinit var storeDelegate: VodDirectoryLocalDelegate

    @Before
    fun setUp() {
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        boxStore = MyObjectBox.builder().directory(TEST_DATA_DIRECTORY)
            .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
            .build()
        storeDelegate = VodDirectoryLocalDelegate(boxStore, UserAccountSubject.create())
    }

    @After
    fun tearDown() {
        boxStore.close()
        BoxStore.deleteAllFiles(TvChannelLocalStoreDelegateUnitTest.TEST_DATA_DIRECTORY)
        boxStore.closeThreadResources()
    }

    @Test
    fun shouldStoreListingPage() {
        val testObserver = TestObserver<VodListingPage>()

        val testPage = VodListingTestDataFactory.listingPage(1)

        storeDelegate.putListingPage(testPage).subscribe {
            with(testPage) {
                storeDelegate.getListingPage(
                    sectionId,
                    unitId,
                    pageNumber?: 1,
                    count?: 0)
            }.subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldStoreListingPage")

        val readPage = testObserver.values()[0]
        assertEquals(testPage.items.size, readPage.items.size)

        // video single assertion
        val singleItem = readPage.items[0]
        with(testPage.items[0]) {

            assertEquals(id, singleItem.id)
            assertEquals(title, singleItem.title)
            assertEquals(description, singleItem.description)
            assertNotNull(singleItem.timeStamp)

            assert(video is VodListingItem.Video.Single)
            assert(singleItem.video is VodListingItem.Video.Single)

            val testVideo = video as VodListingItem.Video.Single
            val readVideo = singleItem.video as VodListingItem.Video.Single
            with (testVideo) {
                assertEquals(id, readVideo.id)
                assertEquals(title, readVideo.title)
                assertEquals(description, readVideo.description)
                assertEquals(uri, readVideo.uri)
                assertEquals(tracks?.size, readVideo.tracks?.size)
                assertEquals(tracks?.elementAt(0)?.languageCode, readVideo.tracks?.elementAt(0)?.languageCode)
                assertEquals(tracks?.elementAt(0)?.title, readVideo.tracks?.elementAt(0)?.title)
                assertEquals(durationMillis, readVideo.durationMillis)
            }

            posters?.let {
                val readPosters = singleItem.posters
                assertEquals(it.poster, readPosters?.poster)
                assertEquals(it.gallery?.size, readPosters?.gallery?.size)
                assertEquals(it.gallery?.elementAt(0), readPosters?.gallery?.elementAt(0))
                assertEquals(it.gallery?.elementAt(2), readPosters?.gallery?.elementAt(2))
            }

            attributes?.let {
                val readAttrs = singleItem.attributes
                assertEquals(it.durationMillis, readAttrs?.durationMillis)
                assertEquals(it.genres?.size, readAttrs?.genres?.size)
                assertEquals(it.genres?.elementAt(0)?.genreId, readAttrs?.genres?.elementAt(0)?.genreId)
                assertEquals(it.genres?.elementAt(0)?.title, readAttrs?.genres?.elementAt(0)?.title)
            }

            // video serial assertion
            val serialItem = readPage.items[3]
            with(testPage.items[3]) {

                assertEquals(id, serialItem.id)
                assertEquals(title, serialItem.title)
                assertEquals(description, serialItem.description)
                assertNotNull(serialItem.timeStamp)

                testPage.items[3].posters?.let {
                    val readPosters = serialItem.posters
                    assertEquals(it.poster, readPosters?.poster)
                    assertEquals(it.gallery?.size, readPosters?.gallery?.size)
                    assertEquals(it.gallery?.elementAt(0), readPosters?.gallery?.elementAt(0))
                    assertEquals(it.gallery?.elementAt(2), readPosters?.gallery?.elementAt(2))
                }

                testPage.items[3].attributes?.let {
                    val readAttrs = serialItem.attributes
                    assertEquals(it.durationMillis, readAttrs?.durationMillis)
                    assertEquals(it.genres?.size, readAttrs?.genres?.size)
                    assertEquals(it.genres?.elementAt(0)?.genreId, readAttrs?.genres?.elementAt(0)?.genreId)
                    assertEquals(it.genres?.elementAt(0)?.title, readAttrs?.genres?.elementAt(0)?.title)
                }

                assert(video is VodListingItem.Video.Serial)
                assert(serialItem.video is VodListingItem.Video.Serial)
                val testSerial = video as VodListingItem.Video.Serial
                val readSerial = serialItem.video as VodListingItem.Video.Serial
                with (testSerial) {
                    assertEquals(id, readSerial.id)
                    assertEquals(title, readSerial.title)
                    assertEquals(description, readSerial.description)

                    assertEquals(series.size, readSerial.series.size)
                    val testSeries = series[0]
                    val readSeries = readSerial.series[0]
                    with(testSeries) {
                        assertEquals(id, readSeries.id)
                        assertEquals(title, readSeries.title)
                        assertEquals(description, readSeries.description)
                        assertEquals(uri, readSeries.uri)
                        assertEquals(tracks?.size, readSeries.tracks?.size)
                        assertEquals(tracks?.elementAt(0)?.languageCode, readSeries.tracks?.elementAt(0)?.languageCode)
                        assertEquals(tracks?.elementAt(0)?.title, readSeries.tracks?.elementAt(0)?.title)
                        assertEquals(durationMillis, readSeries.durationMillis)
                    }
                }
            }
        }
    }

    @Test
    fun shouldStoreSingleVideoItem() {
        val testObserver = TestObserver<VodListingItem>()

        val testItem = VodListingTestDataFactory.videoSingleItem(1)

        storeDelegate.putListingItem(testItem).subscribe {
            (storeDelegate.getListingItem(testItem.id)).subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldStoreSingleVideoItem")

        val readItem = testObserver.values()[0]

        // video single assertion
        with(testItem) {

            assertEquals(id, readItem.id)
            assertEquals(title, readItem.title)
            assertEquals(description, readItem.description)
            assertNotNull(readItem.timeStamp)

            assert(video is VodListingItem.Video.Single)
            assert(readItem.video is VodListingItem.Video.Single)

            val testVideo = video as VodListingItem.Video.Single
            val readVideo = readItem.video as VodListingItem.Video.Single
            with (testVideo) {
                assertEquals(id, readVideo.id)
                assertEquals(title, readVideo.title)
                assertEquals(description, readVideo.description)
                assertEquals(uri, readVideo.uri)
                assertEquals(tracks?.size, readVideo.tracks?.size)
                assertEquals(tracks?.elementAt(0)?.languageCode, readVideo.tracks?.elementAt(0)?.languageCode)
                assertEquals(tracks?.elementAt(0)?.title, readVideo.tracks?.elementAt(0)?.title)
                assertEquals(durationMillis, readVideo.durationMillis)
            }

            posters?.let {
                val readPosters = readItem.posters
                assertEquals(it.poster, readPosters?.poster)
                assertEquals(it.gallery?.size, readPosters?.gallery?.size)
                assertEquals(it.gallery?.elementAt(0), readPosters?.gallery?.elementAt(0))
                assertEquals(it.gallery?.elementAt(2), readPosters?.gallery?.elementAt(2))
            }

            attributes?.let {
                val readAttrs = readItem.attributes
                assertEquals(it.durationMillis, readAttrs?.durationMillis)
                assertEquals(it.genres?.size, readAttrs?.genres?.size)
                assertEquals(it.genres?.elementAt(0)?.genreId, readAttrs?.genres?.elementAt(0)?.genreId)
                assertEquals(it.genres?.elementAt(0)?.title, readAttrs?.genres?.elementAt(0)?.title)
            }
        }
    }

    @Test
    fun shouldStoreSerialVideoItem() {
        val testObserver = TestObserver<VodListingItem>()

        val testItem = VodListingTestDataFactory.videoSerialItem(1)

        storeDelegate.putListingItem(testItem).subscribe {
            (storeDelegate.getListingItem(testItem.id)).subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldStoreSingleVideoItem")

        val serialItem = testObserver.values()[0]

        // video serial assertion
        with(testItem) {

            assertEquals(id, serialItem.id)
            assertEquals(title, serialItem.title)
            assertEquals(description, serialItem.description)
            assertNotNull(serialItem.timeStamp)

            posters?.let {
                val readPosters = serialItem.posters
                assertEquals(it.poster, readPosters?.poster)
                assertEquals(it.gallery?.size, readPosters?.gallery?.size)
                assertEquals(it.gallery?.elementAt(0), readPosters?.gallery?.elementAt(0))
                assertEquals(it.gallery?.elementAt(2), readPosters?.gallery?.elementAt(2))
            }

            attributes?.let {
                val readAttrs = serialItem.attributes
                assertEquals(it.durationMillis, readAttrs?.durationMillis)
                assertEquals(it.genres?.size, readAttrs?.genres?.size)
                assertEquals(it.genres?.elementAt(0)?.genreId, readAttrs?.genres?.elementAt(0)?.genreId)
                assertEquals(it.genres?.elementAt(0)?.title, readAttrs?.genres?.elementAt(0)?.title)
            }

            assert(video is VodListingItem.Video.Serial)
            assert(serialItem.video is VodListingItem.Video.Serial)
            val testSerial = video as VodListingItem.Video.Serial
            val readSerial = serialItem.video as VodListingItem.Video.Serial
            with (testSerial) {
                assertEquals(id, readSerial.id)
                assertEquals(title, readSerial.title)
                assertEquals(description, readSerial.description)

                assertEquals(series.size, readSerial.series.size)
                val testSeries = series[0]
                val readSeries = readSerial.series[0]
                with(testSeries) {
                    assertEquals(id, readSeries.id)
                    assertEquals(title, readSeries.title)
                    assertEquals(description, readSeries.description)
                    assertEquals(uri, readSeries.uri)
                    assertEquals(tracks?.size, readSeries.tracks?.size)
                    assertEquals(tracks?.elementAt(0)?.languageCode, readSeries.tracks?.elementAt(0)?.languageCode)
                    assertEquals(tracks?.elementAt(0)?.title, readSeries.tracks?.elementAt(0)?.title)
                    assertEquals(durationMillis, readSeries.durationMillis)
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
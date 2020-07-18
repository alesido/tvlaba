package org.alsi.android.local.store.tv

import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.observers.TestObserver
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
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
        for (i in testCategories.indices) {
            assertEquals(testCategories[i].id, readCategories[i].id)
            assertEquals(testCategories[i].title, readCategories[i].title)
            assertEquals(testCategories[i].logo?.kind, readCategories[i].logo?.kind)
            assertEquals(testCategories[i].logo?.reference, readCategories[i].logo?.reference)
        }
    }

    @Test
    fun shouldStoreDirectory() {
        val testObserver = TestObserver<TvChannelDirectory>()

        val testCategories = TvChannelTestDataFactory.categories()
        val testChannels = TvChannelTestDataFactory.channels()

        storeDelegate.putDirectory(TvChannelDirectory(testCategories, testChannels, mapOf())).subscribe {
            storeDelegate.getDirectory().subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldStoreCategories")

        val readDirectory = testObserver.values()[0]
        assertEquals(testCategories.size, readDirectory.categories.size)
        for (i in testCategories.indices) {
            assertEquals(testCategories[i].id, readDirectory.categories[i].id)
            assertEquals(testCategories[i].title, readDirectory.categories[i].title)
            assertEquals(testCategories[i].logo?.kind, readDirectory.categories[i].logo?.kind)
            assertEquals(testCategories[i].logo?.reference, readDirectory.categories[i].logo?.reference)
        }
        for (i in testChannels.indices) {
            assertEquals(testChannels[i].id, readDirectory.channels[i].id)
            assertEquals(testChannels[i].categoryId, readDirectory.channels[i].categoryId)
            assertEquals(testChannels[i].logoUri, readDirectory.channels[i].logoUri)
            assertEquals(testChannels[i].number, readDirectory.channels[i].number)
            assertEquals(testChannels[i].title, readDirectory.channels[i].title)
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
        for (i in testChannels.indices) {
            assertEquals(testChannels[i].id, readChannels[i].id)
            assertEquals(testChannels[i].categoryId, readChannels[i].categoryId)
            assertEquals(testChannels[i].logoUri, readChannels[i].logoUri)
            assertEquals(testChannels[i].number, readChannels[i].number)
            assertEquals(testChannels[i].title, readChannels[i].title)
        }
    }

    @Test
    fun shouldSearchCategoryById() {
        val testObserver = TestObserver<TvChannelCategory>()

        val testCategories = TvChannelTestDataFactory.categories()
        storeDelegate.putCategories(testCategories).subscribe {
            storeDelegate.findCategoryById(1L).subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldSearchCategoryById")

        val foundCategory = testObserver.values()[0]
        assertEquals(foundCategory.id, 1L)
    }

    @Test
    fun shouldSearchChannelByNumber() {
        val testObserver = TestObserver<TvChannel>()

        val testChannels = TvChannelTestDataFactory.channels()
        storeDelegate.putChannels(testChannels).subscribe {
            storeDelegate.findChannelByNumber(1).subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldSearchChannelByNumber")

        val foundChannel = testObserver.values()[0]
        assertEquals(foundChannel.number, 1)
    }

    @Test
    fun shouldReturnChannelWindowExpirationMillis() {
        val testChannels = TvChannelTestDataFactory.channels()
        storeDelegate.putChannels(testChannels).subscribe {
            val expirationMillis = storeDelegate.getChannelWindowExpirationMillis(listOf(3,2,1))
            assertEquals(expirationMillis, testChannels[0].live.time!!.endUnixTimeMillis)
        }
    }

    @Test
    fun shouldAddChannelToFavorites() {
        val testObserver = TestObserver<List<TvChannel>>()

        val testChannels = TvChannelTestDataFactory.channels()
        storeDelegate.putChannels(testChannels).subscribe {
            storeDelegate.addChannelToFavorites(1L).subscribe {
                storeDelegate.getFavoriteChannels().subscribe(testObserver)
            }
        }
        onTestObserverTermination(testObserver, "shouldAddChannelToFavorites")

        val favoriteChannels = testObserver.values()[0]
        assertEquals(favoriteChannels.size, 1)
        assertEquals(favoriteChannels[0].id, 1L)
    }

    @Test
    fun shouldRemoveChannelFromFavorites() {
        val testObserver = TestObserver<List<TvChannel>>()

        val testChannels = TvChannelTestDataFactory.channels()
        storeDelegate.putChannels(testChannels).subscribe {
            storeDelegate.addChannelToFavorites(1L).subscribe {
                storeDelegate.addChannelToFavorites(2L).subscribe {
                    storeDelegate.removeChannelFromFavorites(1L).subscribe {
                        storeDelegate.getFavoriteChannels().subscribe(testObserver)
                    }
                }
            }
        }
        onTestObserverTermination(testObserver, "shouldRemoveChannelFromFavorites")

        val favoriteChannels = testObserver.values()[0]
        assertEquals(favoriteChannels.size, 1)
        assertEquals(favoriteChannels[0].id, 2L)
    }

    @Test
    fun shouldToggleChannelFromFavorites() {
        val testObserver = TestObserver<List<TvChannel>>()

        val testChannels = TvChannelTestDataFactory.channels()
        storeDelegate.putChannels(testChannels).subscribe {
            storeDelegate.toggleChannelFromFavorites(3L).subscribe {
                storeDelegate.toggleChannelFromFavorites(4L).subscribe {
                    storeDelegate.toggleChannelFromFavorites(3L).subscribe {
                        storeDelegate.getFavoriteChannels().subscribe(testObserver)
                    }
                }
            }
        }
        onTestObserverTermination(testObserver, "shouldToggleChannelFromFavorites")

        val favoriteChannels = testObserver.values()[0]
        assertEquals(favoriteChannels.size, 1)
        assertEquals(favoriteChannels[0].id, 4L)
    }

    @Test
    fun shouldCheckIfChannelIsFavorite() {
        val testObserver = TestObserver<Boolean>()

        val testChannels = TvChannelTestDataFactory.channels()
        storeDelegate.putChannels(testChannels).subscribe {
            storeDelegate.addChannelToFavorites(3L).subscribe {
                storeDelegate.addChannelToFavorites(4L).subscribe {
                    storeDelegate.toggleChannelFromFavorites(3L).subscribe {
                        storeDelegate.isChannelFavorite(4L).subscribe(testObserver)
                    }
                }
            }
        }
        onTestObserverTermination(testObserver, "shouldCheckIfChannelIsFavorite")

        val isFavorite = testObserver.values()[0]
        assertEquals(isFavorite, true)
    }

    @Test
    fun shouldCheckIfChannelIsNotFavorite() {
        val testObserver = TestObserver<Boolean>()

        val testChannels = TvChannelTestDataFactory.channels()
        storeDelegate.putChannels(testChannels).subscribe {
            storeDelegate.addChannelToFavorites(3L).subscribe {
                storeDelegate.addChannelToFavorites(4L).subscribe {
                    storeDelegate.toggleChannelFromFavorites(3L).subscribe {
                        storeDelegate.isChannelFavorite(3L).subscribe(testObserver)
                    }
                }
            }
        }
        onTestObserverTermination(testObserver, "shouldCheckIfChannelIsNotFavorite")

        val isFavorite = testObserver.values()[0]
        assertEquals(isFavorite, false)
    }

    @Test
    fun shouldSeparateFavoritesOfDifferentUsers() {
        val testObserver = TestObserver<List<TvChannel>>()
        val testChannels = TvChannelTestDataFactory.channels()
        storeDelegate.putChannels(testChannels).subscribe {
            storeDelegate.addChannelToFavorites(1L).subscribe {
                storeDelegate.getFavoriteChannels().subscribe(testObserver)
            }
        }
        onTestObserverTermination(testObserver, "shouldSeparateFavoritesOfDifferentUsers")
        val favoriteChannels = testObserver.values()[0]
        assertEquals(favoriteChannels.size, 1)
        assertEquals(favoriteChannels[0].id, 1L)

        val storeDelegate002 = TvChannelLocalStoreDelegate(boxStore, "TestUser002")
        val testObserver002 = TestObserver<List<TvChannel>>()
        storeDelegate002.addChannelToFavorites(20L).subscribe {
            storeDelegate002.getFavoriteChannels().subscribe(testObserver002)
        }
        onTestObserverTermination(testObserver002, "shouldSeparateFavoritesOfDifferentUsers")
        val favoriteChannels002 = testObserver002.values()[0]
        assertEquals(favoriteChannels002.size, 1)
        assertEquals(favoriteChannels002[0].id, 20L)
    }

    @After
    fun tearDown() {
        boxStore.close()
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        boxStore.closeThreadResources()
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
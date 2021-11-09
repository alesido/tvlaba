package org.alsi.android.local.store.tv

import com.nhaarman.mockitokotlin2.whenever
import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.observers.TestObserver
import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.streaming.repository.SettingsRepository
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannels
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.SubscriptionPackage
import org.alsi.android.domain.user.model.SubscriptionStatus
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.model.MyObjectBox
import org.alsi.android.local.model.user.UserAccountSubject
import org.joda.time.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.fail

@RunWith(JUnit4::class)
class TvChannelLocalStoreDelegateUnitTest {

    @Rule
    @JvmField var mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var boxStore: BoxStore

    @Mock lateinit var settingsRepository: SettingsRepository
    private lateinit var accountSubject: UserAccountSubject

    private lateinit var storeDelegate: TvChannelLocalStoreDelegate

    @Before
    fun setUp() {
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)

        boxStore = MyObjectBox.builder().directory(TEST_DATA_DIRECTORY)
                .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
                .build()

        mockSettingsRepository()

        accountSubject = UserAccountSubject.create()

        storeDelegate = TvChannelLocalStoreDelegate(
            serviceId  = 1L,
            serviceBoxStore = boxStore,
            accountSubject = accountSubject,
            settingsRepository = settingsRepository,
            defaults = StreamingServiceDefaults()
        )

        initUserAccount()
    }

    private fun mockSettingsRepository() {
        whenever(settingsRepository.lastValues()).thenReturn(StreamingServiceSettings(
           language = LanguageOption("en", "English"),
            timeShiftSettingHours = 0
        ))
    }

    private fun initUserAccount() {
        accountSubject.onNext(UserAccount(
            loginName = "LocalStoreTestUser",
            loginPassword = "123",
            subscriptions = listOf(ServiceSubscription(
                serviceId = 1L,
                subscriptionPackage = createTestSubscriptionPackage(),
                expirationDate = LocalDate.parse("2023-12-31"),
                status = SubscriptionStatus.ACTIVE
            )),
        ))
    }

    private fun createTestSubscriptionPackage() = SubscriptionPackage(
        id = 321L, title = "Test Subscription", termMonths = 3,
        packets = listOf("News", "Sports"),
    )

    private fun initUserAccount2() {
        accountSubject.onNext(UserAccount(
            loginName = "LocalStoreTestUser Two",
            loginPassword = "234",
            subscriptions = listOf(ServiceSubscription(
                serviceId = 1L,
                subscriptionPackage = createTestSubscriptionPackage2(),
                expirationDate = LocalDate.parse("2024-12-31"),
                status = SubscriptionStatus.ACTIVE
            )),
        ))
    }

    private fun createTestSubscriptionPackage2() = SubscriptionPackage(
        id = 432L, title = "Test Subscription 2", termMonths = 4,
        packets = listOf("News", "Sports", "Science"),
    )

    /*
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
*/

    @Test
    fun shouldStoreDirectory() {
        val testObserver = TestObserver<TvChannelDirectory>()

        val testCategories = TvChannelTestDataFactory.categories(1, 10)
        val testChannels = TvChannelTestDataFactory.channels(1, 10)
        val testIndex = TvChannelTestDataFactory.categoryChannelIndex(testChannels)

        storeDelegate.putDirectory(TvChannelDirectory(
            testCategories, testChannels, testChannels.groupBy { it.categoryId },
            createTestSubscriptionPackage(), "en", 0
        )).subscribe {
            storeDelegate.getDirectory().subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldStoreDirectory")

        val readDirectory = testObserver.values()[0]
        assertCategoriesEqual(testCategories, readDirectory.categories)
        assertChannelsEqual(testChannels, readDirectory.channels)
        assertIndexesEqual(testIndex, readDirectory.index)
    }

    private fun assertCategoriesEqual(test: List<TvChannelCategory>, read: List<TvChannelCategory>) {
        assertEquals(test.size, read.size)
        for (i in test.indices) {
            assertEquals(test[i].id, read[i].id)
            assertEquals(test[i].title, read[i].title)
            assertEquals(test[i].logo?.kind, read[i].logo?.kind)
            assertEquals(test[i].logo?.reference, read[i].logo?.reference)
        }
    }

    private fun assertChannelsEqual(test: TvChannels, read: TvChannels) {
        assertEquals(test.size, read.size)
        for (i in test.indices) {
            assertEquals(test[i].id, read[i].id)
            assertEquals(test[i].categoryId, read[i].categoryId)
            assertEquals(test[i].logoUri, read[i].logoUri)
            assertEquals(test[i].number, read[i].number)
            assertEquals(test[i].title, read[i].title)
        }
    }

    private fun assertIndexesEqual(test: Map<Long, TvChannels>, read: Map<Long, TvChannels>) {
        for (k in test.keys) {
            assertEquals(test[k]?.size, read[k]!!.size)
            val testCategoryChannels = test[k]!!
            val readCategoryChannels = read[k]!!
            for (i in testCategoryChannels.indices) {
                assertEquals(testCategoryChannels[i].id, readCategoryChannels[i].id)
            }
        }
    }

    @Test
    fun shouldStoreTwoDirectories() {
        val testObserver = TestObserver<TvChannelDirectory>()

        val testCategories = TvChannelTestDataFactory.categories(1, 10)
        val testChannels = TvChannelTestDataFactory.channels(1,10)
        val testIndex = TvChannelTestDataFactory.categoryChannelIndex(testChannels)

        storeDelegate.putDirectory(TvChannelDirectory(
            testCategories, testChannels, testChannels.groupBy { it.categoryId },
            createTestSubscriptionPackage(), "en", 0
        )).subscribe {
            storeDelegate.getDirectory().subscribe(testObserver)
        }
        onTestObserverTermination(testObserver, "shouldStoreTwoDirectories 1")

        val readDirectory = testObserver.values()[0]
        assertCategoriesEqual(testCategories, readDirectory.categories)
        assertChannelsEqual(testChannels, readDirectory.channels)
        assertIndexesEqual(testIndex, readDirectory.index)

        // 2nd directory
        initUserAccount2()

        val testObserver2 = TestObserver<TvChannelDirectory>()

        val testCategories2 = TvChannelTestDataFactory.categories(1, 9)
        val testChannels2 = TvChannelTestDataFactory.channels(1,12)
        val testIndex2 = TvChannelTestDataFactory.categoryChannelIndex(testChannels2)

        storeDelegate.putDirectory(TvChannelDirectory(
            testCategories2, testChannels2, testChannels2.groupBy { it.categoryId },
            createTestSubscriptionPackage2(), "en", 0
        )).subscribe {
            storeDelegate.getDirectory().subscribe(testObserver2)
        }
        onTestObserverTermination(testObserver2, "shouldStoreDirectory 2")

        val readDirectory2 = testObserver2.values()[0]
        assertCategoriesEqual(testCategories2, readDirectory2.categories)
        assertChannelsEqual(testChannels2, readDirectory2.channels)
        assertIndexesEqual(testIndex2, readDirectory2.index)
    }

/*
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

        val storeDelegate002 = TvChannelLocalStoreDelegate(boxStore, UserAccountSubject.create())
        val testObserver002 = TestObserver<List<TvChannel>>()
        storeDelegate002.addChannelToFavorites(20L).subscribe {
            storeDelegate002.getFavoriteChannels().subscribe(testObserver002)
        }
        onTestObserverTermination(testObserver002, "shouldSeparateFavoritesOfDifferentUsers")
        val favoriteChannels002 = testObserver002.values()[0]
        assertEquals(favoriteChannels002.size, 1)
        assertEquals(favoriteChannels002[0].id, 20L)
    }
*/
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
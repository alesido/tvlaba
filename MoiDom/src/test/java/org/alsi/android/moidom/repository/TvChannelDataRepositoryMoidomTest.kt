package org.alsi.android.moidom.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.Single
import org.alsi.android.data.framework.test.readJsonResourceFile
import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.SubscriptionPackage
import org.alsi.android.domain.user.model.SubscriptionStatus
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.model.MyObjectBox
import org.alsi.android.local.model.user.UserAccountSubject
import org.alsi.android.local.store.tv.TvChannelLocalStoreDelegate
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.model.tv.GetTvGroupResponse
import org.alsi.android.moidom.repository.tv.TvChannelDataExpiration
import org.alsi.android.moidom.repository.tv.TvChannelDataRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.store.tv.TvChannelRemoteStoreMoidom
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
import org.joda.time.LocalDate
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

/**
 * @see "https://antonioleiva.com/mockito-2-kotlin/" about mocking final classes
 */
class TvChannelDataRepositoryMoidomTest {
    @Rule @JvmField var mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var repository: TvChannelDataRepositoryMoidom
    @Mock private lateinit var expiration: TvChannelDataExpiration

    @Mock lateinit var settingsRepository: SettingsRepositoryMoidom
    private val settingsDefaults = StreamingServiceDefaults()

    private lateinit var moidomServiceTestBoxStore: BoxStore

    @Mock lateinit var remoteService: RestServiceMoidom
    @Mock lateinit var remoteSession: RemoteSessionRepositoryMoidom

    private val gson: Gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()

    @Before
    fun setUp() {
        mockSettingsRepository()

        repository = TvChannelDataRepositoryMoidom(settingsRepository, settingsDefaults)
        repository.expiration = expiration
        whenever(expiration.directoryExpired(any())).thenReturn(true)

        moidomServiceTestBoxStore = moidomServiceTestBoxStore()

        val accountSubject = UserAccountSubject.create<UserAccount>()
        repository.local = TvChannelLocalStoreDelegate(1L, moidomServiceTestBoxStore,
            accountSubject, settingsRepository, settingsDefaults)

        stubRemoteService()
        stubRemoteSession()

        repository.remote = TvChannelRemoteStoreMoidom(1L, accountSubject, remoteService,
            remoteSession, settingsRepository, settingsDefaults)

        accountSubject.onNext(testAccount())
    }

    private fun mockSettingsRepository() {
        whenever(settingsRepository.lastValues()).thenReturn(
            StreamingServiceSettings(
            language = LanguageOption("en", "English"),
            timeShiftSettingHours = 0
        ))
    }

    //@Test
    fun shouldGetDirectory() {
        val observer = repository.observeDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]
        with (directory) {
            assertEquals(categories.size, 20)
            assertEquals(channels.size, 374)
            assertEquals(channels[0].title, "Russia 1")
            assertEquals(channels[0].features.hasArchive, true)
        }
    }

    @Test
    fun shouldUpdateDirectoryOnLanguageChange() {
        val observer = repository.observeDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]
        with (directory) {
            assertEquals(categories.size, 20)
            assertEquals(categories[0].title, "General")
            assertEquals(channels.size, 374)
            assertEquals(channels[0].title, "Russia 1")
            assertEquals(channels[0].features.hasArchive, true)
        }

        // language update simulated

        whenever(settingsRepository.lastValues()).thenReturn(
            StreamingServiceSettings(
                language = LanguageOption("ru", "Ru"),
                timeShiftSettingHours = 0
            ))

        whenever(expiration.directoryExpired(any())).thenReturn(false)

        stubRemoteService(
            "json/tv_group_ru.json",
            "json/channel_list.json")

        val observer15 = repository.onLanguageChange().test()
        observer15.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer15.assertNoErrors()

        val observer2 = repository.observeDirectory().test()
        observer2.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer2.assertNoErrors()
        assertEquals(observer2.valueCount(), 1)

        val directory2 = observer2.values()[0]
        with (directory2) {
            assertEquals(categories.size, 20)
            assertEquals(categories[0].title, "Общее")
            assertEquals(channels.size, 374)
            assertEquals(channels[0].title, "Russia 1")
            assertEquals(channels[0].features.hasArchive, true)
        }
    }

    //@Test
    fun shouldUpdateDirectoryOnExternalLanguageChange() {
        val observer = repository.observeDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]
        with (directory) {
            assertEquals(categories.size, 20)
            assertEquals(categories[0].title, "General")
            assertEquals(channels.size, 374)
            assertEquals(channels[0].title, "Russia 1")
            assertEquals(channels[0].features.hasArchive, true)
        }

        // language update simulated

        whenever(settingsRepository.lastValues()).thenReturn(
            StreamingServiceSettings(
                language = LanguageOption("ru", "Ru"),
                timeShiftSettingHours = 0
            ))

        whenever(expiration.directoryExpired(any())).thenReturn(false)

        stubRemoteService(
            "json/tv_group_ru.json",
            "json/channel_list.json")

        val observer2 = repository.observeDirectory().test()
        observer2.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer2.assertNoErrors()
        assertEquals(observer2.valueCount(), 1)

        val directory2 = observer2.values()[0]
        with (directory2) {
            assertEquals(categories.size, 20)
            assertEquals(categories[0].title, "Общее")
            assertEquals(channels.size, 374)
            assertEquals(channels[0].title, "Russia 1")
            assertEquals(channels[0].features.hasArchive, true)
        }
    }

    //@Test
    fun shouldGetLocalDirectoryAsNotExpired() {
        // get from remote
        val observer = repository.observeDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]

        val observer2 = repository.observeDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        assertEquals(observer2.valueCount(), 1)
        val directory2 = observer2.values()[0]

        assertEquals(directory.categories.size, 20)
        assertEquals(directory2.categories.size, 20)
        assertEquals(directory.channels.size, 374)
        assertEquals(directory2.channels.size, 374)
        assertEquals(directory.channels[0].title, "Russia 1")
        assertEquals(directory2.channels[0].title, "Russia 1")
        assertEquals(directory.channels[0].features.hasArchive, true)
        assertEquals(directory2.channels[0].features.hasArchive, true)
    }

    //@Test
    fun shouldGetRemoteDirectoryAsExpired() {
        // initially data loaded from the remote store
        val observer = repository.observeDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]

        // this time data loaded from local store and they are the same
        val observer2 = repository.observeDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        assertEquals(observer2.valueCount(), 1)
        val directory2 = observer2.values()[0]

        assertEquals(directory.categories.size, 20)
        assertEquals(directory2.categories.size, 20)
        assertEquals(directory.channels.size, 374)
        assertEquals(directory2.channels.size, 374)
        assertEquals(directory.channels[0].title, "Russia 1")
        assertEquals(directory2.channels[0].title, "Russia 1")
        assertEquals(directory.channels[0].features.hasArchive, true)
        assertEquals(directory2.channels[0].features.hasArchive, true)

        // now, after the data expired they are loaded from remote and they are different
        val expirationMock = mock(TvChannelDataExpiration::class.java)
        whenever(expirationMock.directoryExpired(any())).thenReturn(true)
        repository.expiration = expirationMock

        stubRemoteService("json/tv_group2.json", "json/channel_list2.json")

        val observer3 = repository.observeDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer3.valueCount(), 1)
        val directory3 = observer3.values()[0]
        assertEquals(directory3.categories.size, 19)
        assert(directory3.categories[0].title.contains(":TEST_UPDATE"))
        assertEquals(directory3.channels.size, 374)
        assert(directory3.channels[0].title?.contains(":TEST_UPDATE") ?: false)
        assertEquals(directory3.channels[0].features.hasArchive, true)
    }

    @After
    fun tearDown() {
        moidomServiceTestBoxStore.close()
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        moidomServiceTestBoxStore.closeThreadResources()
    }

    private fun moidomServiceTestBoxStore(): BoxStore {
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        return MyObjectBox.builder().directory(TEST_DATA_DIRECTORY)
                .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
                .build()
    }

    private fun testAccount(): UserAccount {
        return UserAccount(
            loginName = "testLoginName",
            loginPassword = "testLoginPassword",
            subscriptions = listOf(
                ServiceSubscription(
                serviceId = 1L,
                subscriptionPackage = createTestSubscriptionPackage(),
                expirationDate = LocalDate.parse("2023-12-31"),
                status = SubscriptionStatus.ACTIVE
            )),
        )
    }

    private fun createTestSubscriptionPackage() = SubscriptionPackage(
        id = 321L, title = "Test Subscription", termMonths = 3,
        packets = listOf("News", "Sports"),
    )

    private fun stubRemoteSession() {
        whenever(remoteSession.getSessionId()).thenReturn(Single.just("testRemoteSessionId"))
    }

    private fun stubRemoteService(
            categoriesJsonPath: String = "json/tv_group.json",
            channelsJsonPath: String = "json/channel_list.json") {

        whenever(remoteService.getGroups("testRemoteSessionId")).thenReturn(Single.just(
                gson.fromJson(readJsonResourceFile(categoriesJsonPath), GetTvGroupResponse::class.java)))

        whenever(remoteService.getAllChannels("testRemoteSessionId", "+0300")).thenReturn(
                Single.just(gson.fromJson(readJsonResourceFile(channelsJsonPath), ChannelListResponse::class.java)))
    }

    companion object {
        val TEST_DATA_DIRECTORY = File("objectbox/test-db-repository-moidom")
    }
}
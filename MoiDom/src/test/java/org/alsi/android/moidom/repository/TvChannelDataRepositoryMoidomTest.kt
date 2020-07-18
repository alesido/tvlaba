package org.alsi.android.moidom.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.alsi.android.data.framework.test.getJson
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.model.MyObjectBox
import org.alsi.android.moidom.model.LoginEvent
import org.alsi.android.moidom.model.LoginResponse
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.model.tv.GetTvGroupResponse
import org.alsi.android.moidom.repository.tv.TvChannelDataExpiration
import org.alsi.android.moidom.repository.tv.TvChannelDataRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.store.tv.TvChannelRemoteStoreMoidom
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
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

    private lateinit var repository: TvChannelDataRepositoryMoidom

    @Rule @JvmField var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock lateinit var remoteService: RestServiceMoidom
    @Mock lateinit var remoteSession: RemoteSessionRepositoryMoidom

    @InjectMocks lateinit var remoteMock: TvChannelRemoteStoreMoidom

    private val gson: Gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()

    @Before
    fun setUp() {
        repository = TvChannelDataRepositoryMoidom()

        repository.moidomServiceBoxStore = moidomServiceTestBoxStore()

        repository.loginSubject = PublishSubject.create()
        repository.loginSubject?.onNext(testLoginEvent())

        stubRemoteService()
        stubRemoteSession()
        repository.remote = remoteMock
    }

    @Test
    fun shouldGetDirectory() {
        val observer = repository.getDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]
        assertEquals(directory.categories.size, 20)
        assertEquals(directory.channels.size, 374)
        assertEquals(directory.channels[0].title, "Russia 1")
        assertEquals(directory.channels[0].features.hasArchive, true)
    }

    @Test
    fun shouldGetLocalDirectoryAsNotExpired() {
        // get from remote
        val observer = repository.getDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]

        val observer2 = repository.getDirectory().test()
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

    @Test
    fun shouldGetRemoteDirectoryAsExpired() {
        // initially data loaded from the remote store
        val observer = repository.getDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()
        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]

        // this time data loaded from local store and they are the same
        val observer2 = repository.getDirectory().test()
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

        val observer3 = repository.getDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer3.valueCount(), 1)
        val directory3 = observer3.values()[0]
        assertEquals(directory3.categories.size, 19)
        assert(directory3.categories[0].title.contains(":TEST_UPDATE"))
        assertEquals(directory3.channels.size, 374)
        assert(directory3.channels[0].title?.contains(":TEST_UPDATE")?: false)
        assertEquals(directory3.channels[0].features.hasArchive, true)
    }

    @After
    fun tearDown() {
        repository.moidomServiceBoxStore.close()
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        repository.moidomServiceBoxStore.closeThreadResources()
    }

    private fun moidomServiceTestBoxStore(): BoxStore {
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        return MyObjectBox.builder().directory(TEST_DATA_DIRECTORY)
                .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
                .build()
    }

    private fun testLoginEvent(): LoginEvent {
        return LoginEvent(
                account = UserAccount("testLoginName", "testLoginPassword", listOf()),
                data = gson.fromJson(getJson("json/login.json"), LoginResponse::class.java))
    }

    private fun stubRemoteService(
            categoriesJsonPath: String = "json/tv_group.json",
            channelsJsonPath: String = "json/channel_list.json") {

        whenever(remoteService.getGroups("testRemoteSessionId")).thenReturn(Single.just(
                gson.fromJson(getJson(categoriesJsonPath), GetTvGroupResponse::class.java)))

        whenever(remoteService.getAllChannels("testRemoteSessionId", "+0300")).thenReturn(
                Single.just( gson.fromJson(getJson(channelsJsonPath), ChannelListResponse::class.java)))
    }

    private fun stubRemoteSession() {
        whenever(remoteSession.getSessionId()).thenReturn(Single.just("testRemoteSessionId"))
    }

    companion object {
        val TEST_DATA_DIRECTORY = File("objectbox/test-db-repository-moidom")
    }
}
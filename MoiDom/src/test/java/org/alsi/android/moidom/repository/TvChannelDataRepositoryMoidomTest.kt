package org.alsi.android.moidom.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhaarman.mockito_kotlin.whenever
import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.model.MyObjectBox
import org.alsi.android.moidom.model.LoginEvent
import org.alsi.android.moidom.model.LoginResponse
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.model.tv.GetTvGroupResponse
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

    private fun stubRemoteService() {
        val gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()

        whenever(remoteService.getGroups("testRemoteSessionId")).thenReturn(Single.just(
                gson.fromJson(getJson("json/tv_group.json"), GetTvGroupResponse::class.java)))

        whenever(remoteService.getAllChannels("testRemoteSessionId", "+0300")).thenReturn(
                Single.just( gson.fromJson(getJson("json/channel_list.json"), ChannelListResponse::class.java)))
    }

    private fun stubRemoteSession() {
        whenever(remoteSession.getSessionId()).thenReturn(Single.just("testRemoteSessionId"))
    }

    private fun getJson(path : String) : String {
        val uri = this.javaClass.classLoader.getResource(path)
        val file = File(uri.path)
        return String(file.readBytes())
    }

    companion object {
        val TEST_DATA_DIRECTORY = File("objectbox/test-db-repository-moidom")
    }
}
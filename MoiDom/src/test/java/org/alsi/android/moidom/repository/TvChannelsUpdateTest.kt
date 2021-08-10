package org.alsi.android.moidom.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.whenever
import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import io.reactivex.Single
import net.lachlanmckee.timberjunit.TimberTestRule
import org.alsi.android.data.framework.test.readJsonResourceFile
import org.alsi.android.domain.tv.model.guide.TvChannelListWindow
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.framework.Now
import org.alsi.android.local.model.MyObjectBox
import org.alsi.android.local.model.user.UserAccountSubject
import org.alsi.android.local.store.tv.TvChannelLocalStoreDelegate
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.repository.tv.TvChannelDataRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.store.tv.TvChannelRemoteStoreMoidom
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
import org.joda.time.DateTime
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
class TvChannelsUpdateTest {

    private lateinit var repository: TvChannelDataRepositoryMoidom

    @Rule @JvmField var mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var moidomServiceTestBoxStore: BoxStore

    @Mock lateinit var remoteService: RestServiceMoidom
    @Mock lateinit var remoteSession: RemoteSessionRepositoryMoidom

    @InjectMocks lateinit var remoteMock: TvChannelRemoteStoreMoidom

    @Mock lateinit var mockOfNow: Now

    @Rule @JvmField
    var logAllAlwaysRule: TimberTestRule = TimberTestRule.logAllAlways()

    private val gson: Gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()

    private var testChannelsFileIndex = 0

    @Before
    fun setUp() {
        repository = TvChannelDataRepositoryMoidom()

        moidomServiceTestBoxStore = moidomServiceTestBoxStore()

        val accountSubject = UserAccountSubject.create<UserAccount>()
        repository.local = TvChannelLocalStoreDelegate(moidomServiceTestBoxStore, accountSubject)
        accountSubject.onNext(testAccount())

        stubRemoteService()
        stubRemoteSession()
        repository.remote = remoteMock

        val testStartMillis = DateTime.now().millis
        whenever(mockOfNow.millis()).thenAnswer {
            1532294745000L + DateTime.now().millis - testStartMillis
        }
        whenever(mockOfNow.time()).thenAnswer {
            DateTime(1532294745000L + DateTime.now().millis - testStartMillis)
        }
        repository.now = mockOfNow
    }

    @Test
    fun simpleUpdateSequence() {

        // load initial channel list
        val observer = repository.observeDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]
        assertEquals(directory.categories.size, 1)
        assertEquals(directory.channels.size, 18)
        assertEquals(directory.channels[0].title, "House Kids HD")
        assertEquals(directory.channels[0].features.hasArchive, true)

        // start update sequence
        val testChannelsListWindow = TvChannelListWindow(
                listOf(5L, 103L, 6L, 258L, 187L), 1532294762000L)

        repository.scheduleChannelsUpdate(testChannelsListWindow)

        observer.awaitTerminalEvent(80, TimeUnit.SECONDS)
        assertEquals(observer.values().size, 4)
    }

    @After
    fun tearDown() {
        moidomServiceTestBoxStore.close()
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        moidomServiceTestBoxStore.closeThreadResources()
        repository.dispose()
    }

    private fun moidomServiceTestBoxStore(): BoxStore {
        BoxStore.deleteAllFiles(TEST_DATA_DIRECTORY)
        return MyObjectBox.builder().directory(TEST_DATA_DIRECTORY)
                .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
                .build()
    }
    private fun testAccount(): UserAccount {
        return UserAccount("testLoginName", "testLoginPassword", listOf())
    }

    private fun stubRemoteService() {
        whenever(remoteService.getAllChannels("testRemoteSessionId", "+0300"))
                .thenAnswer {
                    val sourceFileName = String.format("%s%d%s",
                            "json/channel_list_update_", testChannelsFileIndex++, ".json")
                    println(sourceFileName)
                    val sourceJson = readJsonResourceFile(sourceFileName)
                    Single.just(gson.fromJson(sourceJson, ChannelListResponse::class.java))
                }
    }

    private fun stubRemoteSession() {
        whenever(remoteSession.getSessionId()).thenReturn(Single.just("testRemoteSessionId"))
    }

    companion object {
        val TEST_DATA_DIRECTORY = File("objectbox/test-db-repository-moidom")
    }
}
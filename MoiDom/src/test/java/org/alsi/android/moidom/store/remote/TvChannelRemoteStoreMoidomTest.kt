package org.alsi.android.moidom.store.remote

import com.google.gson.GsonBuilder
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.model.tv.GetTvGroupResponse
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.store.tv.TvChannelRemoteStoreMoidom
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import java.io.File
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.junit.Rule
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals


class TvChannelRemoteStoreMoidomTest {

    @Rule @JvmField var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock lateinit var remoteService: RestServiceMoidom
    @Mock lateinit var remoteSession: RemoteSessionRepositoryMoidom

    @InjectMocks lateinit var remoteStore: TvChannelRemoteStoreMoidom

    @Before
    fun setUp() {
        stubRemoteService()
        stubRemoteSession()
    }

    @Test
    fun shouldGetDirectory() {
        val observer = remoteStore.getDirectory().test()
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
    fun shouldGetCategories() {
        val observer = remoteStore.getCategories().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val categories = observer.values()[0]
        assertEquals(categories.size, 20)
        assertEquals(categories[0].title, "General")
        assertEquals(categories[1].title, "Entertainment")
    }

    @Test
    fun shouldGetChannels() {
        val observer = remoteStore.getChannels().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val channels = observer.values()[0]
        assertEquals(channels.size, 374)
        assertEquals(channels[0].title, "Russia 1")
        assertEquals(channels[0].features.hasArchive, true)
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
}
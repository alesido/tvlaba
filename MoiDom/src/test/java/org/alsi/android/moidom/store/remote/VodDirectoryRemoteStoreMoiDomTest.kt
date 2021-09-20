package org.alsi.android.moidom.store.remote

import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.alsi.android.data.framework.test.readJsonResourceFile
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.moidom.model.vod.GetVodUrlResponse
import org.alsi.android.moidom.model.vod.VodGenresResponse
import org.alsi.android.moidom.model.vod.VodInfoResponse
import org.alsi.android.moidom.model.vod.VodListResponse
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.EXTRA_GENRE_LAST_ID
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.QUERY_PARAM_VOD_LISTING_TYPE_LAST
import org.alsi.android.moidom.store.vod.VodDirectoryRemoteStoreMoiDom
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

/**
 *  This test covers the remote store methods and the source data mappers.
 *  NOTE Use integration test to verify if the API implemented correctly.
 *  NOTE Ensure mocked service call parameters match that used by correspondent methods
 *  of the remote store.
 */
class VodDirectoryRemoteStoreMoiDomTest {

    @Rule @JvmField var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock lateinit var remoteService: RestServiceMoidom
    @Mock lateinit var remoteSession: RemoteSessionRepositoryMoidom

    @InjectMocks lateinit var remoteStore: VodDirectoryRemoteStoreMoiDom

    private val gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java,
        JsonDeserializerForIntEnablingMap()).create()

    @Before
    fun setUp() {
        stubRemoteService()
        stubRemoteSession()
    }

    @Test
    fun shouldGetDirectory() {

        whenever(remoteService.getVodGenres("testRemoteSessionId")).thenReturn(
            Single.just(gson.fromJson(readJsonResourceFile("json/vod_genres.json"),
                VodGenresResponse::class.java))
        )

        val observer = remoteStore.getDirectory().test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val directory = observer.values()[0]
        assertEquals(directory.sections.size, 1)
        assertEquals(directory.sections[0].units[0].title, "BEST")
        assertEquals(directory.sections[0].units[2].title, "Фэнтези")
    }

    @Test
    fun shouldGetListingPage() {

        whenever(remoteService.getGenreVodList(
            sid = "testRemoteSessionId",
            type = QUERY_PARAM_VOD_LISTING_TYPE_LAST,
            genreId = null,
            pageNumber = 1,
            numberOfItemsPerPage = 20)).thenReturn(
            Single.just(gson.fromJson(readJsonResourceFile("json/vod_list.json"),
                VodListResponse::class.java))
        )

        val observer = remoteStore.getListingPage(
            sectionId = RestServiceMoidom.VOD_SECTION_SUBSTITUTE_ID,
            unitId = EXTRA_GENRE_LAST_ID,
            start = 1, length = 20).test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val list = observer.values()[0]
        assertEquals(list.total, 3229)
        assertEquals(list.start, 1)
        assertEquals(list.items[1].id, 2511)
        assertEquals(list.items[1].title, "Домоправитель")
        assertEquals(list.items[19].id, 2526)
        assertEquals(list.items[19].title, "Шальные деньги: Роскошная жизнь")
    }

    @Test
    fun shouldGetListingItem() {

        whenever(remoteService.getVodInfo("testRemoteSessionId", 1L)).thenReturn(
            Single.just(gson.fromJson(readJsonResourceFile("json/vod_info.json"),
                VodInfoResponse::class.java))
        )

        val observer = remoteStore.getListingItem(1L).test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assertEquals(observer.valueCount(), 1)
        val item = observer.values()[0]

        assertEquals(item.id, 1)
        assertEquals(item.title, "Интерстеллар")

        assertEquals(item.posters?.poster?.authority, "api.telecola.tv")
        assertEquals(item.posters?.poster?.path, "/images/vod_movies/1.png")
        assert(item.video is VodListingItem.Video.Single)

        assertEquals(item.attributes?.durationMillis, 10140000)
        assertEquals(item.attributes?.credits?.elementAt(0)?.name, "Мэттью МакКонахи")
        assertEquals(item.attributes?.credits?.elementAt(0)?.role, VodListingItem.Role.ACTOR)
        assertEquals(item.attributes?.credits?.elementAt(4)?.name, "Майкл Кейн")
        assertEquals(item.attributes?.credits?.elementAt(4)?.role, VodListingItem.Role.ACTOR)
        assertEquals(item.attributes?.credits?.elementAt(11)?.name, "Кристофер Нолан")
        assertEquals(item.attributes?.credits?.elementAt(11)?.role, VodListingItem.Role.DIRECTOR)

        assertEquals(item.attributes?.year, "2014")
        assertEquals(item.attributes?.country, "США ,Канада ,Великобритания ")

        assertEquals(item.attributes?.genres?.elementAt(0)?.genreId, 1)
        assertEquals(item.attributes?.genres?.elementAt(0)?.title, "Фантастика")
    }

    @Test
    fun shouldGetVideoStream() {

        whenever(remoteService.getVodStreamUrl("testRemoteSessionId", 1L)).thenReturn(
            Single.just(gson.fromJson(readJsonResourceFile("json/vod_geturl.json"),
                GetVodUrlResponse::class.java))
        )

        val observer = remoteStore.getSingleVideoStream(1L).test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        val stream = observer.values()[0]
        assertEquals(stream.uri.toString(), "http://5.254.76.34:17070/vod/tully-2018-web-dl-108op-werecut-prod.mp4/index.m3u8?token=MjAxNzIwMTcrMTUzMjI5NjIxNisxNTU5ODI5MjI5KzErKzArMCtWKzArNjM1OWJmZDgxZjJmOTNjYTg5OGQ0N2Q4M2EzNjljMjk%3D")
        assertEquals(stream.kind, VideoStreamKind.RECORD)
    }

    private fun stubRemoteService() {
    }

    private fun stubRemoteSession() {
        whenever(remoteSession.getSessionId()).thenReturn(Single.just("testRemoteSessionId"))
    }
}
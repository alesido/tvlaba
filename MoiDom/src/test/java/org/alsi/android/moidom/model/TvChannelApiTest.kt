package org.alsi.android.moidom.model

import com.google.gson.GsonBuilder
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.alsi.android.data.framework.test.readJsonResourceFile
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.remote.retrofit.RetrofitServiceBuilder
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

/** Tests:
 *
 * - to support development of unified Retrofit Exception and error response identification at one
 * place (in call adapter)
 *
 * - to develop technique for testing of REST API model offline
 *
 * - to test login response data model including error model
 *
 */
@RunWith(JUnit4::class)
class TvChannelApiTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var moiDomService: RestServiceMoidom

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()

        val gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()
        moiDomService = RetrofitServiceBuilder(RestServiceMoidom::class.java, mockServer.url("/").toString())
                .enableRxErrorHandlingCallAdapterFactory()
                .setGson(gson).enableLogging().build()
    }

    @Test
    fun shouldGetCategories() {
        val mockResponse = MockResponse().setResponseCode(200).setBody(readJsonResourceFile("json/tv_group.json"))
        mockServer.enqueue(mockResponse)

        val observer = moiDomService.getGroups("testSessionId").test()
        observer.awaitTerminalEvent(300, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assert(observer.valueCount() == 1)
        val data = observer.values()[0]
        data?.let {
            assertEquals(data.groups.size, 20)
            assertEquals(data.groups_icons[0].base_url, "http://iptv.moi-dom.tv/img/group_icons/120x120/")
        }?: fail()
    }

    @Test
    fun shouldGetChannels() {
        val mockResponse = MockResponse().setResponseCode(200).setBody(readJsonResourceFile("json/channel_list.json"))
        mockServer.enqueue(mockResponse)

        val observer = moiDomService.getAllChannels("testSessionId",
                DateTimeFormat.forPattern("ZZ")
                        .withZone(DateTimeZone.getDefault())
                        .print(0).replace(":", ""))
                .test()
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assert(observer.valueCount() == 1)
        val data = observer.values()[0]
        data?.let {
            assertEquals(data.groups.size, 20)
            assertEquals(data.groups[0].channels.size, 37)
            assertEquals(data.groups[0].channels[0].name, "Russia 1")
            assertEquals(data.groups[0].channels[0].have_archive, 1)
        }?: fail()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }
}
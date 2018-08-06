package org.alsi.android.moidom.model

import com.google.gson.GsonBuilder
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.remote.retrofit.RetrofitServiceBuilder
import org.alsi.android.remote.retrofit.error.RetrofitException
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
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
class LoginApiTest {

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
    fun testSuccessfulLogin() {
        val observer = TestObserver<LoginResponse>()
        val mockResponse = MockResponse().setResponseCode(200).setBody(getJson("json/login.json"))
        mockServer.enqueue(mockResponse)

        subscribeLoginRequest(observer)
        observer.awaitTerminalEvent(300, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assert(observer.valueCount() == 1)
        val data = observer.values()[0]
        data?.let {
            assert(data.account.login == "20172017")
            assert(data.account.packet_expire == 1559829229)
            assert(data.services["archive"] == 1)
            assert(data.settings.language.value == "en")
            assert(data.settings.stream_server.value == "5.254.76.34")
            assert(data.settings.bitrate.value == 1500)
        }?: fail()
    }

    @Test
    fun testLoginApiError() {
        val observer = TestObserver<LoginResponse>()
        val mockResponse = MockResponse().setResponseCode(200).setBody(getJson("json/login.error.json"))
        mockServer.enqueue(mockResponse)

        subscribeLoginRequest(observer)
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)

        val error: RetrofitException? = observer.errors()[0] as? RetrofitException
        error?.let {
            assert(error.errorKind == RetrofitException.Kind.REST_API)
            assert(error.apiResponseCode?.equals(2L)?: false)
        }?: fail()
    }

    @Test
    fun testLoginHttpAndApiError() {
        val observer = TestObserver<LoginResponse>()
        val mockResponse = MockResponse().setResponseCode(422).setBody(getJson("json/login.error.json"))
        mockServer.enqueue(mockResponse)

        subscribeLoginRequest(observer)
        observer.awaitTerminalEvent(300, TimeUnit.SECONDS)

        val error: RetrofitException? = observer.errors()[0] as? RetrofitException
        error?.let {
            assert(error.errorKind == RetrofitException.Kind.REST_API)
            assert(error.apiResponseCode?.equals(2L)?: false)
            assert(error.errorResponse?.code() == 422)
        }?: fail()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    private fun getJson(path : String) : String {
        val uri = this.javaClass.classLoader.getResource(path)
        val file = File(uri.path)
        return String(file.readBytes())
    }

    private fun subscribeLoginRequest(observer: TestObserver<LoginResponse>) {
        moiDomService.login("20172017", "testPassword",
                "settings", "deviceType", 0, 0,
                "s/n", "mac", "model", "man")
                .subscribe(observer)
    }
}
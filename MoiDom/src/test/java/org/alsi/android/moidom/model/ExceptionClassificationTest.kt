package org.alsi.android.moidom.model

import com.google.gson.GsonBuilder
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.alsi.android.data.framework.test.readJsonResourceFile
import org.alsi.android.domain.exception.model.*
import org.alsi.android.remote.retrofit.error.RetrofitExceptionProducer
import org.alsi.android.moidom.mapper.RetrofitExceptionMapper
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.remote.retrofit.RetrofitErrorPostProcessor
import org.alsi.android.remote.retrofit.RetrofitServiceBuilder
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.io.IOException
import java.net.SocketTimeoutException
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
class ExceptionClassificationTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var restService: RestServiceMoidom

    @Rule @JvmField var mockitoRule: MockitoRule = MockitoJUnit.rule()
    @Mock lateinit var messages: ExceptionMessages

    private val gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()
    }

    private fun setupRestService(testException: Throwable? = null) {
        val builder = RetrofitServiceBuilder(RestServiceMoidom::class.java, mockServer.url("/").toString())
            .enableRxErrorHandlingCallAdapterFactory(errorPostProcessor())
            .setGson(gson).enableLogging()
        if (testException != null) {
            val interceptor = RetrofitExceptionProducer(isActivated = true, testException = testException)
            builder.addInterceptor(interceptor)
            interceptor.start(1)
        }
        restService = builder.build()
    }

    @Test
    fun testSocketTimeout() {
        // REST
        setupRestService( SocketTimeoutException(
            "Test SocketTimeoutException - have to be classified as a Server Exception"))

        // Observer
        val observer = TestObserver<LoginResponse>()
        val mockResponse = MockResponse()

        // Run
        subscribeLoginRequest(observer) // not important which request is here
        mockServer.enqueue(mockResponse)
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)

        val error: ClassifiedException? = observer.errors()[0] as? ClassifiedException
        error?.let {
            assert(error is ServerException)
        }?: fail()
    }

    @Test
    fun testIOException() {
        // REST
        setupRestService( IOException(
            "Test IOException - have to be classified as a Network Exception") )

        // Observer
        val observer = TestObserver<LoginResponse>()
        val mockResponse = MockResponse()

        // Run
        subscribeLoginRequest(observer) // not important which request
        mockServer.enqueue(mockResponse)
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)

        val error: ClassifiedException? = observer.errors()[0] as? ClassifiedException
        error?.let {
            assert(error is NetworkException)
        }?: fail()
    }

    @Test
    fun testSessionInvalidCondition() {
        // REST
        setupRestService()

        // Test Observer
        val observer = TestObserver<LoginResponse>()
        val mockResponse = MockResponse().setResponseCode(200).setBody(readJsonResourceFile("json/another_user_session_active.error.json"))
        mockServer.enqueue(mockResponse)

        // Run
        subscribeLoginRequest(observer) // not important which request
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)

        val error: ClassifiedException? = observer.errors()[0] as? ClassifiedException
        error?.let {
            assert(error is UserSessionInvalid)
        }?: fail()
    }

    @Test
    fun testContractInactiveCondition() {
        // REST
        setupRestService()

        // Test Observer
        val observer = TestObserver<LoginResponse>()
        val mockResponse = MockResponse().setResponseCode(200).setBody(readJsonResourceFile("json/contract_inactive.error.json"))
        mockServer.enqueue(mockResponse)

        // Run
        subscribeLoginRequest(observer) // not important which request
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS)

        val error: ClassifiedException? = observer.errors()[0] as? ClassifiedException
        error?.let {
            assert(error is UserContractInactive)
        }?: fail()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    private fun subscribeLoginRequest(observer: TestObserver<LoginResponse>) {
        restService.login("test", "testPassword",
                "settings", "deviceType", 0, 0,
                "s/n", "mac", "model", "man")
                .subscribe(observer)
    }

    private fun errorPostProcessor(): RetrofitErrorPostProcessor {
        val mapper = RetrofitExceptionMapper(ClassifiedExceptionFactory(messages), messages)
        return mapper::map
    }
}
package org.alsi.android.moidom.model

import com.google.gson.GsonBuilder
import io.reactivex.observers.TestObserver
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.remote.retrofit.RetrofitServiceBuilder
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
import org.junit.Assert.assertNotNull
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
class LoginApiIntegrationTest {

    private lateinit var moiDomService: RestServiceMoidom

    @Before
    fun setUp() {
        val gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()
        moiDomService = RetrofitServiceBuilder(RestServiceMoidom::class.java, RestServiceMoidom.SERVICE_URL)
                .enableRxErrorHandlingCallAdapterFactory()
                .setGson(gson).enableLogging().build()
    }

    @Test
    fun testSuccessfulLogin() {
        val observer = TestObserver<LoginResponse>()
        subscribeLoginRequest(observer)
        observer.awaitTerminalEvent(300, TimeUnit.SECONDS)
        observer.assertNoErrors()

        assert(observer.valueCount() == 1)
        val data = observer.values()[0]
        data?.let {
            assertNotNull(data.sid)
            assert(data.account.login == "20172017")
            assertNotNull(data.account.packet_expire)
            assertNotNull(data.services)
        }?: fail()
    }

    private fun subscribeLoginRequest(observer: TestObserver<LoginResponse>) {
        moiDomService.login("20172017", "201717",
                "all", "android", 999, 25,
                "N/A", "00:A0:C9:14:C8", "H906", "man")
                .subscribe(observer)
    }
}
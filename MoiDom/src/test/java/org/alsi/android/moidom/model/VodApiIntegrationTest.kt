package org.alsi.android.moidom.model

import com.google.gson.GsonBuilder
import io.reactivex.observers.TestObserver
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.remote.retrofit.RetrofitServiceBuilder
import org.alsi.android.remote.retrofit.json.IntEnablingMap
import org.alsi.android.remote.retrofit.json.JsonDeserializerForIntEnablingMap
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
class VodApiIntegrationTest {

    private lateinit var restService: RestServiceMoidom

    private lateinit var sid: String

    @Before
    fun setUp()
    {
        val gson = GsonBuilder().registerTypeAdapter(IntEnablingMap::class.java, JsonDeserializerForIntEnablingMap()).create()
        restService = RetrofitServiceBuilder(RestServiceMoidom::class.java, RestServiceMoidom.SERVICE_URL)
                .enableRxErrorHandlingCallAdapterFactory()
                .setGson(gson).enableLogging().build()
        login()
    }

    @Test
    fun shouldGetAll() {
        // genres
        val genresObserver = restService.getVodGenres(sid).test()
        genresObserver.awaitTerminalEvent(300, TimeUnit.SECONDS)
        genresObserver.assertNoErrors()

        assert(genresObserver.valueCount() == 1)
        val genresResponse = genresObserver.values()[0]
        genresResponse?.genres?.let {
            assert(it.isNotEmpty())
        } ?: fail()

        // listing
        val listingObserver = restService.getGenreVodList(
            sid,
            type = RestServiceMoidom.QUERY_PARAM_VOD_LISTING_TYPE_LAST,
            genreId = null,
            pageNumber = 1,
            numberOfItemsPerPage = 20).test()
        listingObserver.awaitTerminalEvent(300, TimeUnit.SECONDS)
        listingObserver.assertNoErrors()

        assert(listingObserver.valueCount() == 1)
        val listingResponse = listingObserver.values()[0]
        with(listingResponse) {
            assert(total > 0)
            assert(vods.isNotEmpty())
            assert(vods[0].name.isNotEmpty())
        }

        // listing item
        val itemObserver = restService.getVodInfo(sid, listingResponse.vods[0].id).test()
        itemObserver.awaitTerminalEvent(300, TimeUnit.SECONDS)
        itemObserver.assertNoErrors()

        assert(itemObserver.valueCount() == 1)
        val itemResponse = itemObserver.values()[0]
        with(itemResponse) {
            assert(id > 0)
            assert(name.isNotEmpty())
        }

        // video stream
        val streamObserver = restService.getVodStreamUrl(sid, itemResponse.id).test()
        streamObserver.awaitTerminalEvent(300, TimeUnit.SECONDS)
        streamObserver.assertNoErrors()

        assert(itemObserver.valueCount() == 1)
        val streamResponse = streamObserver.values()[0]
        with(streamResponse) {
            assert(url.isNotEmpty())
        }
    }

    private fun login() {
        val observer = TestObserver<LoginResponse>()
        restService.login("52", "123",
                "all", "android", 999, 25,
                "N/A", "00:A0:C9:14:C8", "H906", "man")
                .subscribe(observer)
        observer.awaitTerminalEvent(300, TimeUnit.SECONDS)
        observer.assertNoErrors()
        sid = observer.values()[0].sid
    }
}
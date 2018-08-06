package org.alsi.android.moidom.store

import io.reactivex.Single
import okhttp3.ResponseBody
import org.alsi.android.moidom.model.LoginResponse
import org.alsi.android.moidom.model.SettingsSetResponse
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.model.tv.GetTvGroupResponse
import org.alsi.android.moidom.model.tv.GetUrlResponse
import org.alsi.android.moidom.model.vod.GetVodUrlResponse
import org.alsi.android.moidom.model.vod.VodGenresResponse
import org.alsi.android.moidom.model.vod.VodInfoResponse
import org.alsi.android.moidom.model.vod.VodListResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created on 12/8/17.
 */

interface RestServiceMoidom {

    /**
     * Example: http://iptv.moi-dom.tv/api/json/login?login=20172017&pass=201717&device=android&settings=all
     *
     * Extra params: android_version=19&app_version=1.20&serial=D4%3ACF%3AF9%3A08%3A37%3AE8&model=ETV421S&manufacturer=unknown
     */
    @GET("login")
    fun login(
            @Query("login") login: String,
            @Query("pass") password: String,
            @Query("settings") settings: String,
            @Query("device") deviceTypeAndroid: String,
            @Query("app_version") appBuildNumber: Int,
            @Query("android_version") androidSdkNumber: Int,
            @Query("serial") deviceSerialNumber: String,
            @Query("mac_address") macAddress: String,
            @Query("model") deviceModel: String,
            @Query("manufacturer") manufacturer: String
    ): Single<LoginResponse>

    /** Ad-hoc to have group logo icons and names in different languages.
     *
     * @param sid
     * @return
     */
    @GET("tv_groups")
    fun getGroups(
            @Query("PATKER_SSID") sid: String
    ): Single<GetTvGroupResponse>

    /**
     * Precondition: successful login request.
     * Example: http://iptv.moi-dom.tv/api/json/channel_list?show=all&&protect_code=201717
     */
    @GET("channel_list")
    fun getAllChannels(
            @Query("PATKER_SSID") sid: String,
            @Query("tz") timeZone: String,
            @Query("show") selector: String,
            @Query("protect_code") accessCode: String
    ): Single<ChannelListResponse>

    /**
     * Example: http://iptv.moi-dom.tv/api/json/epg?cid=84&day=061217
     */
    @GET("epg")
    fun getChannelSchedule(
            @Query("PATKER_SSID") sid: String,
            @Query("tz") timeZone: String,
            @Query("with_ts") withTimeShift: Int,
            @Query("cid") channelId: String,
            @Query("day") programDayDateString: String
    ): Single<ResponseBody>

    /**
     * Example: http://iptv.moi-dom.tv/api/json/get_url?cid=84&gmt=1512571261&protect_code=201717
     */
    @GET("get_url")
    fun getArchiveVideoStreamUrl(
            @Query("PATKER_SSID") sid: String,
            @Query("cid") channelId: String,
            @Query("stream_mode") streamMode: String,
            @Query("file_mode") fileMode: String,
            @Query("gmt") unixTimeStamp: Long?,
            @Query("protect_code") accessCode: String
    ): Single<GetUrlResponse>

    /**
     * Example: http://iptv.moi-dom.tv/api/json/get_url?cid=84&gmt=1512571261&protect_code=201717
     *
     * NOTE Actually the same as previous, but w/o "gmt" parameter.
     */
    @GET("get_url")
    fun getLiveVideoStreamUrl(
            @Query("PATKER_SSID") sid: String,
            @Query("cid") channelId: String,
            @Query("stream_mode") streamMode: String,
            @Query("protect_code") accessCode: String
    ): Single<GetUrlResponse>


    /** Get list of genres for MDVodVideo On Demand
     */
    @GET("vod_genres")
    fun getVodGenres(
            @Query("PATKER_SSID") sid: String
    ): Single<VodGenresResponse>

    /** Get list of videos of a genre.
     * Example: http://iptv.moi-dom.tv/api/json//vod_list?type=text&page=1&query=&genre=1&nums=16
     */
    /**
     *
     * @param type "best" or "last", order based on rating or chronological order.  is supposed to use here |text ; only "text" value supposed to use in the app
     * @param genreId Movie genre ID.
     * @param pageNumber Order number of a page.
     * @param numberOfItemsPerPage Number of VODs in the resulting listing page.
     * @return
     */
    @GET("vod_list")
    fun getGenreVodList(
            @Query("PATKER_SSID") sid: String,
            @Query("type") type: String,
            @Query("genre") genreId: Int?,
            @Query("page") pageNumber: Int?,
            @Query("nums") numberOfItemsPerPage: Int?
    ): Single<VodListResponse>

    /** Find VODs of a genre which title contains query substring.
     *
     * NOTE The API allows to request certain result page of a given size. However i
     *
     * @param type "text" is supposed here (reserved for search).
     * @param genreId Movie genre ID.
     * @param query Substring to search in the titles.
     * @return VODs containing "query" substring in their titles.
     */
    @GET("vod_list")
    fun searchGenreVodList(
            @Query("PATKER_SSID") sid: String,
            @Query("type") type: String,
            @Query("genre") genreId: Int?,
            @Query("query") query: String
    ): Single<VodListResponse>

    /** Get details of a MDVodVideo-On-Demand given by  ID including list of videos
     * (single or multiple for series).
     */
    @GET("vod_info")
    fun getVodInfo(
            @Query("PATKER_SSID") sid: String,
            @Query("id") vodItemId: String
    ): Single<VodInfoResponse>

    /**
     * Example: http://iptv.moi-dom.tv/api/json/vod_geturl?fileid=5846
     */
    /**
     *
     * @param vodVideoFileId ID of a video file of a VOD to get URL to video stream of.
     * @return
     */
    @GET("vod_geturl")
    fun getVodStreamUrl(
            @Query("PATKER_SSID") sid: String,
            @Query("fileid") vodVideoFileId: Long?,
            @Query("stream_mode") streamMode: String,
            @Query("playlist") playlistVariant: String
    ): Single<GetVodUrlResponse>

    @GET("settings")
    fun getSetting(
            @Query("PATKER_SSID") sid: String,
            @Query("var") settingName: String
    ): Single<ResponseBody>

    @POST("settings_set")
    fun setSetting(
            @Query("PATKER_SSID") sid: String,
            @Query("var") settingName: String,
            @Query("val") settingValue: String
    ): Single<SettingsSetResponse>

    @POST("settings_set")
    fun setProtectionCode(
            @Query("PATKER_SSID") sid: String,
            @Query("var") settingName: String, // do not forget set "pcode" here
            @Query("val") settingValue: String, // the same as "new code"
            @Query("old_code") oldCode: String,
            @Query("new_code") newCode: String,
            @Query("confirm_code") confirmationCode: String
    ): Single<SettingsSetResponse>

    @GET
    fun getApplicationVersion(
            @Url upgradeSourceUrl: String,
            @Query("PATKER_SSID") sid: String,
            @Query("ver") appBuildNumber: Int,
            @Query("android_version") androidSdkNumber: Int,
            @Query("serial") deviceSerialNumber: String,
            @Query("mac_address") macAddress: String,
            @Query("model") deviceModel: String,
            @Query("manufacturer") manufacturer: String
    ): Single<ResponseBody>

    companion object {
        val SERVICE_URL = "http://iptv.moi-dom.tv/api/json/"
        val UPGRADE_URL = "http://android.moidom.tv/android_new/updatemoidom.php"

        val BASE_URL_VOD_POSTERS = "http://iptv.moi-dom.tv/" // http://iptv.moi-dom.tv/img/ico_245x140/74.png

        val TAG_SERVICE_MOIDOM_TV = "tv"
        val TAG_SERVICE_MOIDOM_VOD = "vod"
        val TAG_SERVICE_MEGOGO_VOD = "megogo"
        val TAG_SERVICE_TIMESHIFT = "timeshift"

        val QUERY_PARAM_LOGIN_SETTINGS_DEFAULT = "all"
        val QUERY_PARAM_DEVICE_TYPE = "android"
        val QUERY_PARAM_DEFAULT_CHANNEL_SELECTOR = "all"

        val DEFAULT_APP_INTERFACE_LANGUAGE_CODE = "en"
        val DEFAULT_APP_INTERFACE_LANGUAGE_NAME = "English"

        val DEFAULT_DEVICE_MODEL_ID = -1L

        val QUERY_PARAM_STREAM_MODE_HLS = "hls"
        val QUERY_PARAM_STREAM_MODE_HLS_SIMPLE = "hls_simple"
        val QUERY_PARAM_STREAM_MODE_MPEG_TS = "mpegts"

        val QUERY_PARAM_SETTING_NAME_PCODE = "pcode"
        val QUERY_PARAM_SETTING_NAME_HTTP_CACHING = "http_caching"
        val QUERY_PARAM_SETTING_NAME_STREAM_SERVER = "stream_server"
        val QUERY_PARAM_SETTING_DEVICE_NAME_MODEL = "device_model"
        val QUERY_PARAM_SETTING_LANGUAGE = "language"
        val QUERY_PARAM_SETTING_NAME_TIMESHIFT = "timeshift"
        val QUERY_PARAM_SETTING_NAME_TIMEZONE = "timezone"
        val QUERY_PARAM_SETTING_NAME_BITRATE = "bitrate"

        val QUERY_PARAM_VOD_LISTING_TYPE_BEST = "best"
        val QUERY_PARAM_VOD_LISTING_TYPE_LAST = "last"
        val QUERY_PARAM_VOD_LISTING_TYPE_TEXT = "text"

        val TOKEN_NO_EPG_CHANNEL = "NO EPG"

        val EXTRA_GENRE_BEST_ID = -1001
        val EXTRA_GENRE_LAST_ID = -1002
    }
}

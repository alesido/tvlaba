package org.alsi.android.moidom.model.remote

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class LoginResponse(

        val sid: String,
        val sid_name: String,
        val ip_addr: String,
        val room_n: Int,
        val max_rooms: Int,
        val max_devices: Int,
        val device: String,
        val model_id: Int,
        val account: Account,
        val services: Services,
        val contacts: Contacts,
        val settings: Settings,
        override val error: RequestError?,
        override val servertime: Int

): BaseResponse() {

    data class Services(
            val vod: Int,
            val archive: Int,
            val tshift: Int,
            val megogo: Int
    )


    data class Settings(
            val timeshift: Timeshift,
            val timezone: Timezone,
            val language: Language,
            val stream_server: StreamServer,
            val http_caching: HttpCaching,
            val bitrate: Bitrate,
            val device_model: DeviceModel,
            val rc_codes: List<RcCode>
    ) {

        data class StreamServer(
                val value: String,
                val list: List<X>
        ) {

            data class X(
                    val ip: String,
                    val descr: String,
                    val speed_test: List<SpeedTest>
            ) {

                data class SpeedTest(
                        val type: String,
                        val file_size: Int,
                        val url_http: String
                )
            }
        }


        data class DeviceModel(
                val name: String,
                val value: Int,
                val list: List<X>
        ) {

            data class X(
                    val id: Int,
                    val name: String
            )
        }


        data class Timeshift(
                val value: Int,
                val list: List<Int>
        )


        data class Timezone(
                val value: Int
        )


        data class Language(
                val value: String,
                val list: List<X>
        ) {

            data class X(
                    val id: String,
                    val name: String
            )
        }


        data class HttpCaching(
                val value: Int,
                val list: List<Int>
        )


        data class Bitrate(
                val value: Int,
                val list: List<Int>,
                val names: List<Name>
        ) {

            data class Name(
                    val value: Int,
                    val title: String
            )
        }


        data class RcCode(
                val code: Int,
                val func: String
        )
    }


    data class Account(
            val login: String,
            val packet_name: String,
            val packet_expire: Int
    )


    data class Contacts(
            val name: String,
            val phone: String,
            val email: String,
            val address: String
    )
}
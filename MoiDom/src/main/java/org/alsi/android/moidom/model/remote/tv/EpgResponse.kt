package org.alsi.android.moidom.model.remote.tv

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class EpgResponse(
        val tz_sec: Int,
        val ts_sec: Int,
        val with_ts: Int,
        val epg: List<Epg>,
        override val error: RequestError?,
        override val servertime: Int

): BaseResponse() {

    data class Epg(
            val ut_start: Int,
            val ut_stop: Int,
            val progname: String
    )
}
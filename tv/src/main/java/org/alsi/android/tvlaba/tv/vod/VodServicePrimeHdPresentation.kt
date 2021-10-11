package org.alsi.android.tvlaba.tv.vod

import android.content.Context
import org.alsi.android.domain.implementation.model.LocalRasterImageReference
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.tvlaba.R

class VodServicePrimeHdPresentation(val context: Context, serviceId: Long): StreamingServicePresentation(
    serviceId, StreamingServiceKind.VOD, "",
    logo = LocalRasterImageReference("premier_logo")
) {
    override val title: String get() = context.getString(R.string.service_title_vod_prime_hd)
}
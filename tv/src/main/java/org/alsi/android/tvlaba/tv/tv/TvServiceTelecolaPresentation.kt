package org.alsi.android.tvlaba.tv.tv

import android.content.Context
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.tvlaba.R

class TvServiceTelecolaPresentation(val context: Context, serviceId: Long): StreamingServicePresentation(
    serviceId, StreamingServiceKind.TV, ""
) {
    override val title: String get() = context.getString(R.string.service_title_telecola_tv)
}
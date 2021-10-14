package org.alsi.android.tvlaba.mobile.tv

import android.content.Context
import org.alsi.android.domain.implementation.model.LocalRasterImageReference
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.tvlaba.R

class TvServiceTelecolaPresentation(val context: Context, serviceId: Long): StreamingServicePresentation(
    serviceId,
    StreamingServiceKind.TV,
    "",
    logo = LocalRasterImageReference("telecola_logo_512")
) {
    override val title: String get() = "Telecola NEW"
}
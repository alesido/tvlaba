package org.alsi.android.domain.streaming.model.service

import org.alsi.android.domain.implementation.model.ImageReference

open class StreamingServicePresentation (
    val serviceId: Long,
    val kind: StreamingServiceKind,
    open val title: String,
    open val description: String? = null,
    val logo: ImageReference? = null,
    val poster: ImageReference? = null,
)
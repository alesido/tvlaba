package org.alsi.android.tvlaba.mobile.model

import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults

class StreamingServiceDefaultsMobile: StreamingServiceDefaults() {

    override fun getDefaultLanguageCode(): String = "ru"

    override fun getDefaultLanguageName(): String = "Russian"
}
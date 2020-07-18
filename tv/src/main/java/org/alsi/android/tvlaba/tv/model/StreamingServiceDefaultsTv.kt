package org.alsi.android.tvlaba.tv.model

import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults

class StreamingServiceDefaultsTv: StreamingServiceDefaults() {

    override fun getDefaultLanguageCode(): String = "ru"

    override fun getDefaultLanguageName(): String = "Russian"
}
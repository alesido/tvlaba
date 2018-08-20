package org.alsi.android.domain.tv.model.guide

import java.net.URI

class TvChannel (

        val id: Long,
        val categoryId: Long,
        val number: Int,
        var title: String?,
        var logoUri: URI?,
        var live: TvProgramLive,
        var features: TvChannelFeatures

)

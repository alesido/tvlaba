package org.alsi.android.domain.tv.interactor.guide

import org.alsi.android.domain.tv.model.guide.CreditRole
import java.net.URI

class TvProgramCredit(

        val name: String? = null,

        val role: CreditRole? = null,

        val photoUris: List<URI>? = null
)

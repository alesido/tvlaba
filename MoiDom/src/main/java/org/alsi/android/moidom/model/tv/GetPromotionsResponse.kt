package org.alsi.android.moidom.model.tv

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class GetPromotionsResponse(
        val id: String,
        val promotions: List<PromotionSection>,
): BaseResponse()

data class PromotionSection (
        val id: Long,
        val name: String,
        val programs: List<ProgramPromotion>

)
data class ProgramPromotion (
        val id: Long,
        val cid: Long,
        val name: String,
        val title: String,
        val ut_start: Long,
        val ut_stop: Long,
        val description: String,
        val images: List<String>
)


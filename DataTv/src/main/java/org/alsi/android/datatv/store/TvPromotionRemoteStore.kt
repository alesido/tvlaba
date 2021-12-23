package org.alsi.android.datatv.store

import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvPromotionSet

interface TvPromotionRemoteStore {

    fun getPromotionSet(): Single<TvPromotionSet>
}
package org.alsi.android.domain.tv.repository.guide

import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvPromotionSet

interface TvPromotionRepository {
    fun observePromotionSet(): Observable<TvPromotionSet>
    fun getPromotionSet(): Single<TvPromotionSet>
}
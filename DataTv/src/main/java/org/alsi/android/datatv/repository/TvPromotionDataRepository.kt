package org.alsi.android.datatv.repository

import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.datatv.store.TvPromotionRemoteStore
import org.alsi.android.domain.tv.model.guide.TvPromotionSet
import org.alsi.android.domain.tv.repository.guide.TvPromotionRepository
import javax.inject.Inject

open class TvPromotionDataRepository @Inject constructor(
    val remote: TvPromotionRemoteStore
    ): TvPromotionRepository {

    var promotionCache: TvPromotionSet? = null

    override fun observePromotionSet(): Observable<TvPromotionSet> {
        TODO("Implement when doing promotion update.")
    }

    override fun getPromotionSet(): Single<TvPromotionSet> {
        promotionCache?.let { return@getPromotionSet Single.just(it) }
        return remote.getPromotionSet().map {
            promotionCache = it; it
        }
    }
}
package org.alsi.android.presentationtv.mapper

import android.net.Uri
import co.joebirch.presentation.mapper.Mapper
import io.reactivex.observers.DisposableObserver
import org.alsi.android.domain.implementation.model.IconSet
import org.alsi.android.domain.implementation.model.IconType
import org.alsi.android.domain.tv.interactor.guide.TvCategoryIconsUseCase
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.presentation.mapper.getDrawableIdentifierByName
import org.alsi.android.presentationtv.model.TvCategoryItemViewModel

open class TvCategoryItemViewMapper (
        getIconsUseCase: TvCategoryIconsUseCase)
    : Mapper<TvCategoryItemViewModel, TvChannelCategory> {

    private var iconSet : IconSet? = null

    init {
         getIconsUseCase.execute(IconSetObserver())
    }

    override fun mapToView(type: TvChannelCategory): TvCategoryItemViewModel {
        return when(iconSet?.kind) {
            IconType.LOCAL_VECTOR -> TvCategoryItemViewModel(type.id, type.title, getDrawableIdentifierByName(type.logoReference)?: 0)
            IconType.LOCAL_RASTER -> TvCategoryItemViewModel(type.id, type.title, getDrawableIdentifierByName(type.logoReference)?: 0)
            IconType.REMOTE_RASTER -> TvCategoryItemViewModel(type.id, type.title, Uri.parse(type.logoReference))
            null -> TvCategoryItemViewModel(type.id, type.title)
        }
    }

    inner class IconSetObserver : DisposableObserver<IconSet>() {
        override fun onNext(t: IconSet) {
            iconSet = t
        }
        override fun onComplete() {
            // not applicable
        }
        override fun onError(e: Throwable) {
            // just skip icons if error
        }
    }
}
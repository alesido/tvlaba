package org.alsi.android.tvlaba.mobile.injection.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.alsi.android.presentationtv.model.TvCategoryBrowseViewModel
import org.alsi.android.tvlaba.mobile.injection.ViewModelFactory
import kotlin.reflect.KClass

@Module
abstract class PresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(TvCategoryBrowseViewModel::class)
    abstract fun bindTvCategoryBrowseViewModel(viewModel: TvCategoryBrowseViewModel) : ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
package org.alsi.android.tvlaba.mobile.injection.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.alsi.android.presentationtv.model.TvCategoryBrowseViewModel
import org.alsi.android.presentationtv.model.TvPlaybackPreferencesViewModel
import org.alsi.android.presentationtv.model.TvPlaybackViewModel
import org.alsi.android.tvlaba.mobile.injection.ViewModelFactory
import kotlin.reflect.KClass

@Module
abstract class PresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(TvCategoryBrowseViewModel::class)
    abstract fun bindTvCategoryBrowseViewModel(viewModel: TvCategoryBrowseViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TvPlaybackViewModel::class)
    abstract fun bindTvPlaybackViewModel(viewModel: TvPlaybackViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TvPlaybackPreferencesViewModel::class)
    abstract fun bindTvPlaybackPreferencesViewModel(viewModel: TvPlaybackPreferencesViewModel) : ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
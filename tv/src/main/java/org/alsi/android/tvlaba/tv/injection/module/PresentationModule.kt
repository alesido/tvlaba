package org.alsi.android.tvlaba.tv.injection.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.alsi.android.presentationtv.model.TvChannelDirectoryBrowseViewModel
import org.alsi.android.presentationtv.model.TvPlaybackViewModel
import org.alsi.android.presentationtv.model.TvScheduleViewModel
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import kotlin.reflect.KClass

@Module
abstract class PresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(TvChannelDirectoryBrowseViewModel::class)
    abstract fun bindTvChannelDirectoryBrowseViewModel(viewModel: TvChannelDirectoryBrowseViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TvPlaybackViewModel::class)
    abstract fun bindTvPlaybackViewModel(viewModel: TvPlaybackViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TvScheduleViewModel::class)
    abstract fun bindTvScheduleViewModel(viewModel: TvScheduleViewModel) : ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
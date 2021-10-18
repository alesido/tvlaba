package org.alsi.android.tvlaba.tv.injection.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import org.alsi.android.presentation.AppStartViewModel
import org.alsi.android.presentation.auth.login.model.LoginViewModel
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.settings.ParentalControlViewModel
import org.alsi.android.presentationtv.model.*
import org.alsi.android.presentationvod.model.*
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import kotlin.reflect.KClass

@Module
abstract class PresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(AppStartViewModel::class)
    abstract fun bindAppStartViewModel(viewModel: AppStartViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TvGuideStartViewModel::class)
    abstract fun bindTvGuideStartViewModel(viewModel: TvGuideStartViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(viewModel: LoginViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GeneralSettingsViewModel::class)
    abstract fun bindGeneralSettingsViewModel(viewModel: GeneralSettingsViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ParentalControlViewModel::class)
    abstract fun bindParentalControlViewModel(viewModel: ParentalControlViewModel) : ViewModel

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
    @ViewModelKey(TvPlaybackPreferencesViewModel::class)
    abstract fun bindTvPlaybackPreferencesViewModel(viewModel: TvPlaybackPreferencesViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TvPlaybackFooterViewModel::class)
    abstract fun bindPlaybackFooterViewModel(viewModel: TvPlaybackFooterViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TvProgramDetailsViewModel::class)
    abstract fun bindProgramDetailsViewModel(viewModel: TvProgramDetailsViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VodGuideStartViewModel::class)
    abstract fun bindVodGuideStartViewModel(viewModel: VodGuideStartViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VodDirectoryBrowseViewModel::class)
    abstract fun bindVodDirectoryBrowseViewModel(viewModel: VodDirectoryBrowseViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VodDigestViewModel::class)
    abstract fun bindVodDigestViewModel(viewModel: VodDigestViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VodPlaybackViewModel::class)
    abstract fun bindVodPlaybackViewModel(viewModel: VodPlaybackViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VodPlaybackPreferencesViewModel::class)
    abstract fun bindVodPlaybackPreferencesViewModel(viewModel: VodPlaybackPreferencesViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VodPlaybackFooterViewModel::class)
    abstract fun bindVodPlaybackFooterViewModel(viewModel: VodPlaybackFooterViewModel) : ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
package org.alsi.android.tvlaba.mobile.injection

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import org.alsi.android.domain.DomainModule
import org.alsi.android.local.LocalModule
import org.alsi.android.moidom.MoidomModule
import org.alsi.android.presentationtv.PresentationTvModule
import org.alsi.android.remote.RemoteModule
import org.alsi.android.tvlaba.mobile.MobileVideoStreamingApplication
import org.alsi.android.tvlaba.mobile.injection.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        AssetsModule::class,
        UiModule::class,
        PresentationTvModule::class,
        PresentationModule::class,
        DomainModule::class,
        DataModule::class,
        LocalModule::class,
        RemoteModule::class,
        MoidomModule::class,
        StreamingServicesModule::class
])

interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(app: MobileVideoStreamingApplication)

}
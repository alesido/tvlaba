package org.alsi.android.tvlaba.mobile.injection

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import org.alsi.android.tvlaba.mobile.MobileVideoStreamingApplication
import org.alsi.android.tvlaba.mobile.injection.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        UiModule::class,
        PresentationModule::class,
        DataModule::class,
        RemoteModule::class
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
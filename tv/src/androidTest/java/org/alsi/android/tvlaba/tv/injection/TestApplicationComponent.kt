package org.alsi.android.tvlaba.tv.injection

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import org.alsi.android.domain.DomainModule
import org.alsi.android.local.LocalModule
import org.alsi.android.moidom.MoidomModule
import org.alsi.android.presentationtv.PresentationTvModule
import org.alsi.android.remote.retrofit.RemoteTestModule
import org.alsi.android.remote.retrofit.error.RetrofitExceptionProducer
import org.alsi.android.tvlaba.tv.injection.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        ActivityModule::class,
        AssetsModule::class,
        UiModule::class,
        PresentationModule::class,
        PresentationTvModule::class,
        DomainModule::class,
        DataModule::class,
        RemoteTestModule::class,
        LocalModule::class,
        MoidomModule::class,
        StreamingServicesModule::class
])

interface TestApplicationComponent : ApplicationComponent {

        @Component.Builder
        interface Builder {
                @BindsInstance
                fun application(application: Application): Builder

                fun build(): ApplicationComponent
        }

        fun retrofitExceptionProducer(): RetrofitExceptionProducer
}
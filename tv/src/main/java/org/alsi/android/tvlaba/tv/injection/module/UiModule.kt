package org.alsi.android.tvlaba.tv.injection.module

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.tvlaba.tv.UiThread
import org.alsi.android.tvlaba.tv.tv.TvGuideActivity

@Module
abstract class UiModule {

    @Binds
    abstract fun bindPostExecutionThread(uiThread: UiThread): PostExecutionThread

    @ContributesAndroidInjector
    abstract fun contributesTvGuideActivity(): TvGuideActivity
}
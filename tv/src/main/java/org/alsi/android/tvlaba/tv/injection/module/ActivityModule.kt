package org.alsi.android.tvlaba.tv.injection.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.tvlaba.tv.tv.TvChannelDirectoryFragment
import org.alsi.android.tvlaba.tv.tv.directory.TvChannelDirectoryFragmentModule

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [TvChannelDirectoryFragmentModule::class])
    abstract fun bindChannelDirectoryFragment(): TvChannelDirectoryFragment

}
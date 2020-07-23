package org.alsi.android.tvlaba.tv.tv.directory

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.tvlaba.tv.tv.TvChannelDirectoryFragment

@Module
abstract class TvChannelDirectoryFragmentProvider {

    @ContributesAndroidInjector(modules = [TvChannelDirectoryFragmentModule::class])
    abstract fun provideChannelDirectoryFragmentFactory(): TvChannelDirectoryFragment
}
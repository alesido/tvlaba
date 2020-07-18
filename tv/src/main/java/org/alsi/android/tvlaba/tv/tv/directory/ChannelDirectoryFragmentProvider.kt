package org.alsi.android.tvlaba.tv.tv.directory

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.tvlaba.tv.tv.ChannelDirectoryFragment

@Module
abstract class ChannelDirectoryFragmentProvider {

    @ContributesAndroidInjector(modules = [ChannelDirectoryFragmentModule::class])
    abstract fun provideChannelDirectoryFragmentFactory(): ChannelDirectoryFragment
}
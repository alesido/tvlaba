package org.alsi.android.tvlaba.tv.injection.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.tvlaba.tv.tv.ChannelDirectoryFragment
import org.alsi.android.tvlaba.tv.tv.directory.ChannelDirectoryFragmentModule

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [ChannelDirectoryFragmentModule::class])
    abstract fun bindChannelDirectoryFragment(): ChannelDirectoryFragment

}
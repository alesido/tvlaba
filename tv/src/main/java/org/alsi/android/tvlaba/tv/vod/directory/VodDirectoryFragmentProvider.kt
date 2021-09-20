package org.alsi.android.tvlaba.tv.vod.directory

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VodDirectoryFragmentProvider {

    @ContributesAndroidInjector(modules = [VodDirectoryFragmentModule::class])
    abstract fun provideVodDirectoryFragmentFactory(): VodDirectoryFragment
}
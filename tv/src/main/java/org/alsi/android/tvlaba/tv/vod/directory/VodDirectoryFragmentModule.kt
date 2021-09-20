package org.alsi.android.tvlaba.tv.vod.directory

import dagger.Module
import dagger.Provides

@Module
class VodDirectoryFragmentModule {

    @Provides
    fun provideVodDirectoryFragmentView(
        vodDirectoryFragment: VodDirectoryFragment
    ) = vodDirectoryFragment
}
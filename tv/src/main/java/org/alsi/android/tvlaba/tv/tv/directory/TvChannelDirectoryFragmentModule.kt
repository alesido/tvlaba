package org.alsi.android.tvlaba.tv.tv.directory

import dagger.Module
import dagger.Provides
import org.alsi.android.tvlaba.tv.tv.TvChannelDirectoryFragment

@Module
class TvChannelDirectoryFragmentModule {

    @Provides
    fun provideChannelDirectoryFragmentView(
            channelDirectoryFragment: TvChannelDirectoryFragment
    ) = channelDirectoryFragment
}
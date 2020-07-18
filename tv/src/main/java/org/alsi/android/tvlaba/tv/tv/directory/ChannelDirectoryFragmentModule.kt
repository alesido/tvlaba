package org.alsi.android.tvlaba.tv.tv.directory

import dagger.Module
import dagger.Provides
import org.alsi.android.tvlaba.tv.tv.ChannelDirectoryFragment

@Module
class ChannelDirectoryFragmentModule {

    @Provides
    fun provideChannelDirectoryFragmentView(
            channelDirectoryFragment: ChannelDirectoryFragment
    ) = channelDirectoryFragment
}
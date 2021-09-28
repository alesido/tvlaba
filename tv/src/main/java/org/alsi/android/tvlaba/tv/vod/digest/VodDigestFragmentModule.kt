package org.alsi.android.tvlaba.tv.vod.digest

import dagger.Module
import dagger.Provides

@Module
class VodDigestFragmentModule {

    @Provides
    fun provideVodDigestFragmentView(vodDigestFragment: VodDigestFragment) = vodDigestFragment
}
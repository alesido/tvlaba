package org.alsi.android.tvlaba.tv.vod

import dagger.Module
import dagger.Provides

@Module
class VodGuideStartFragmentModule {

    @Provides
    fun provideTvGuideStartFragmentView(vodGuideStartFragment: VodGuideStartFragment) = vodGuideStartFragment
}
package org.alsi.android.tvlaba.tv.tv

import dagger.Module
import dagger.Provides

@Module
class TvGuideStartFragmentModule {

    @Provides
    fun provideTvGuideStartFragmentView(tvGuideStartFragment: TvGuideStartFragment) = tvGuideStartFragment
}
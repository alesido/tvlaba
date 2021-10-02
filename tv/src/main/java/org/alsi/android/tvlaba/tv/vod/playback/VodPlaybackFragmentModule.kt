package org.alsi.android.tvlaba.tv.vod.playback

import dagger.Module
import dagger.Provides
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.tvlaba.tv.vod.digest.VodDigestFragment

@Module
class VodPlaybackFragmentModule {

    @Provides
    fun provideVodPlaybackFragmentView(vodPlaybackFragment: VodPlaybackFragment) = vodPlaybackFragment
}
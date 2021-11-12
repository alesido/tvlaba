package org.alsi.android.tvlaba.tv.injection.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.tvlaba.auth.login.ui.LoginFragment
import org.alsi.android.tvlaba.auth.login.ui.LoginFragmentModule
import org.alsi.android.tvlaba.settings.GeneralSettingsFragment
import org.alsi.android.tvlaba.settings.GeneralSettingsFragmentModule
import org.alsi.android.tvlaba.settings.parental.ParentalControlSettingFragment
import org.alsi.android.tvlaba.settings.parental.ParentalControlSettingFragmentModule
import org.alsi.android.tvlaba.tv.AppStartFragment
import org.alsi.android.tvlaba.tv.AppStartFragmentModule
import org.alsi.android.tvlaba.tv.tv.TvGuideStartFragment
import org.alsi.android.tvlaba.tv.tv.TvGuideStartFragmentModule
import org.alsi.android.tvlaba.tv.tv.directory.TvChannelDirectoryFragment
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackAndScheduleFragment
import org.alsi.android.tvlaba.tv.tv.directory.TvChannelDirectoryFragmentModule
import org.alsi.android.tvlaba.tv.tv.parental.ParentalControlCheckInFragment
import org.alsi.android.tvlaba.tv.tv.parental.ParentalControlCheckInFragmentModule
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackAndScheduleFragmentModule
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackPreferencesFragment
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackPreferencesFragmentModule
import org.alsi.android.tvlaba.tv.tv.program.TvProgramDetailsFragment
import org.alsi.android.tvlaba.tv.tv.program.TvProgramDetailsFragmentModule
import org.alsi.android.tvlaba.tv.vod.VodGuideStartFragment
import org.alsi.android.tvlaba.tv.vod.VodGuideStartFragmentModule
import org.alsi.android.tvlaba.tv.vod.digest.VodDigestFragment
import org.alsi.android.tvlaba.tv.vod.digest.VodDigestFragmentModule
import org.alsi.android.tvlaba.tv.vod.directory.VodDirectoryFragment
import org.alsi.android.tvlaba.tv.vod.directory.VodDirectoryFragmentModule
import org.alsi.android.tvlaba.tv.vod.playback.VodPlaybackFragment
import org.alsi.android.tvlaba.tv.vod.playback.VodPlaybackFragmentModule

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [AppStartFragmentModule::class])
    abstract fun bindAppStartFragment(): AppStartFragment

    @ContributesAndroidInjector(modules = [LoginFragmentModule::class])
    abstract fun bindLoginFragment(): LoginFragment

    @ContributesAndroidInjector(modules = [GeneralSettingsFragmentModule::class])
    abstract fun bindSettingsFragment(): GeneralSettingsFragment

    @ContributesAndroidInjector(modules = [ParentalControlSettingFragmentModule::class])
    abstract fun bindParentalControlSettingFragment(): ParentalControlSettingFragment

    // TV --

    @ContributesAndroidInjector(modules = [TvGuideStartFragmentModule::class])
    abstract fun bindTvGuideStartFragment(): TvGuideStartFragment

    @ContributesAndroidInjector(modules = [TvChannelDirectoryFragmentModule::class])
    abstract fun bindTvChannelDirectoryFragment(): TvChannelDirectoryFragment

    @ContributesAndroidInjector(modules = [TvPlaybackAndScheduleFragmentModule::class])
    abstract fun bindTvPlaybackAndScheduleFragment(): TvPlaybackAndScheduleFragment

    @ContributesAndroidInjector(modules = [TvPlaybackPreferencesFragmentModule::class])
    abstract fun bindTvPlaybackPreferencesFragment(): TvPlaybackPreferencesFragment

    @ContributesAndroidInjector(modules = [TvProgramDetailsFragmentModule::class])
    abstract fun bindTvProgramDetailsFragment(): TvProgramDetailsFragment

    @ContributesAndroidInjector(modules = [ParentalControlCheckInFragmentModule::class])
    abstract fun bindParentalControlCheckInFragment(): ParentalControlCheckInFragment

    // VOD --

    @ContributesAndroidInjector(modules = [VodGuideStartFragmentModule::class])
    abstract fun bindVodGuideStartFragment(): VodGuideStartFragment

    @ContributesAndroidInjector(modules = [VodDirectoryFragmentModule::class])
    abstract fun bindVodDirectoryFragment(): VodDirectoryFragment

    @ContributesAndroidInjector(modules = [VodDigestFragmentModule::class])
    abstract fun bindVodDigestFragment(): VodDigestFragment

    @ContributesAndroidInjector(modules = [VodPlaybackFragmentModule::class])
    abstract fun bindVodPlaybackFragment(): VodPlaybackFragment
}
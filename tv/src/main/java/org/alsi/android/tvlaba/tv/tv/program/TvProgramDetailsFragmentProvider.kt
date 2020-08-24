package org.alsi.android.tvlaba.tv.tv.program

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TvProgramDetailsFragmentProvider {

    @ContributesAndroidInjector(modules = [TvProgramDetailsFragmentModule::class])
    abstract fun provideProgramDetailsFragmentFactory(): TvProgramDetailsFragment
}
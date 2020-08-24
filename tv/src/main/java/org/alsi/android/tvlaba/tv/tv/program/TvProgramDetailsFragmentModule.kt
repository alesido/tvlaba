package org.alsi.android.tvlaba.tv.tv.program

import dagger.Module
import dagger.Provides

@Module
class TvProgramDetailsFragmentModule {

    @Provides
    fun provideProgramDetailsFragmentView(
            programDetailsFragment: TvProgramDetailsFragment
    ) = programDetailsFragment
}
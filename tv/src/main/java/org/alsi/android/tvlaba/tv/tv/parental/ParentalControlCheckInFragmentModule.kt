package org.alsi.android.tvlaba.tv.tv.parental

import dagger.Module
import dagger.Provides

@Module
class ParentalControlCheckInFragmentModule {

    @Provides
    fun provideProgramDetailsFragmentView(
        parentalControlCheckInFragment: ParentalControlCheckInFragment
    ) = parentalControlCheckInFragment
}
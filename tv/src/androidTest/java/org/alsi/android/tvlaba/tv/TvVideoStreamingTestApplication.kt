package org.alsi.android.tvlaba.tv

import org.alsi.android.tvlaba.tv.injection.ApplicationComponent
import org.alsi.android.tvlaba.tv.injection.DaggerTestApplicationComponent

class TvVideoStreamingTestApplication : TvVideoStreamingApplication() {

    override fun initializeComponent(): ApplicationComponent {
        val component = DaggerTestApplicationComponent.builder().application(this).build()
        component.inject(this)
        return component

    }
}
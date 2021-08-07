package org.alsi.android.tvlaba.tv

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import org.alsi.android.tvlaba.tv.injection.DaggerApplicationComponent
import timber.log.Timber
import javax.inject.Inject

open class TvVideoStreamingApplication : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun activityInjector() = activityInjector
    override fun supportFragmentInjector() = fragmentInjector

    open lateinit var component: ApplicationComponent

    open fun initializeComponent(): ApplicationComponent {
        val component = DaggerApplicationComponent.builder().application(this).build()
        component.inject(this)
        return component
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        component = initializeComponent()
    }
}

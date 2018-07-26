package org.alsi.android.tvlaba.mobile

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector

import dagger.android.HasActivityInjector
import org.alsi.android.tvlaba.mobile.injection.DaggerApplicationComponent
import timber.log.Timber
import javax.inject.Inject

/**
 * Created on 7/7/18.
 */
class MobileVideoStreamingApplication : Application(), HasActivityInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> {
        return androidInjector
    }

    override fun onCreate() {
        super.onCreate()
        setupTimber()

        DaggerApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this)
    }

    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

}

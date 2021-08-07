package org.alsi.android.tvlaba.tv

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class TvLabATestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, TvVideoStreamingTestApplication::class.java.name, context)
    }
}
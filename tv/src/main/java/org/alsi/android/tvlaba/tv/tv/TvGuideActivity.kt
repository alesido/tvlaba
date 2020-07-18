package org.alsi.android.tvlaba.tv.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dagger.android.AndroidInjection
import org.alsi.android.tvlaba.R

class TvGuideActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_guide_activity)
    }
}
package org.alsi.android.tvlaba.tv.tv

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dagger.android.AndroidInjection
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.framework.LanguageContextWrapper


class TvGuideActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Medium)
        setContentView(R.layout.tv_guide_activity)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LanguageContextWrapper.wrap(newBase!!, "ru"))
    }
}
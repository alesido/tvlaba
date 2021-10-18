package org.alsi.android.tvlaba.tv

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import dagger.android.AndroidInjection
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.framework.LanguageContextWrapper

/**
 *  App activity in Single Activity scheme
 */
class AppActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Medium)
        setContentView(R.layout.tv_guide_activity)
    }

    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.tvGuideNavigationHost)
        if (navController.currentBackStackEntry != null)
            super.onBackPressed()
        else
            finish() // TODO Add exit confirmation dialog here
    }

    override fun attachBaseContext(newBase: Context?) {
        if (null == newBase) {
            super.attachBaseContext(newBase)
            return
        }
        val languageCode = PreferenceManager.getDefaultSharedPreferences(newBase)
            .getString("pref_key_app_language", DEFAULT_LANGUAGE_CODE)?: DEFAULT_LANGUAGE_CODE
        super.attachBaseContext(LanguageContextWrapper.wrap(newBase, languageCode))
    }

    companion object {
        const val DEFAULT_LANGUAGE_CODE = "ru"
    }
}
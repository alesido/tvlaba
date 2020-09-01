package org.alsi.android.tvlaba.framework

import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.*

class LanguageContextWrapper(base: Context?) : android.content.ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, languageCode: String): LanguageContextWrapper {
            val newLocale = Locale(languageCode)
            val configuration = context.resources.configuration
            val resultContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(newLocale)
                val localeList = LocaleList(newLocale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
                context.createConfigurationContext(configuration)
            } else {
                configuration.setLocale(newLocale)
                context.createConfigurationContext(configuration)
            }
            return LanguageContextWrapper(resultContext)
        }
    }
}
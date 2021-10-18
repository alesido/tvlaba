package org.alsi.android.tvlaba.settings.parental0

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference

/**
 *  This is just a trick, wrapper to use onDisplayPreferenceDialog callback in order
 *  to activate actual dialog in exchange. The reason for the replacement is at least that
 *  standard DialogPreference does not allow input fields.
 */
class ParentalCodeDialogPreference(context: Context?, attrs: AttributeSet?)
    : DialogPreference(context, attrs)

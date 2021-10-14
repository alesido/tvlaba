package org.alsi.android.tvlaba.framework

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

/**
 *  ... to safely replace application context with activity/fragment context
 */
fun validateContext(replacement: Context, original: Context): Context =
    if (replacement is Activity && !replacement.isDestroyed && !replacement.isFinishing
        || replacement is Fragment && replacement.isVisible
    ) replacement else original



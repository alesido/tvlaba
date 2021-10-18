package org.alsi.android.tvlaba.framework

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created on 6/5/18.
 */
class SoftKeyboard(context: Context) {
    private val inputMethodManager: InputMethodManager
    var isShown = false
        private set

    fun show(view: View?) {
        inputMethodManager.showSoftInput(view, 0)
        isShown = true
    }

    fun hide(view: View) {
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        isShown = false
    }

    init {
        inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
}
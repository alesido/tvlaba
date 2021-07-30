package org.alsi.android.tvlaba.exception

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import org.alsi.android.domain.exception.model.*
import org.alsi.android.tvlaba.R
import javax.inject.Inject

class ClassifiedExceptionHandler @Inject constructor(
    private val context: Context,
    private val messages: ExceptionMessages
) {

    fun handle(
        f: Fragment,
        e: Throwable?
    ): Boolean {

        e?: return false

        when(e) {

            is NetworkException -> dialog(e.message?: "", "Network Connection")
            is ServerException -> dialog(e.message?: "", "Server Access")
            is ProcessingException -> toast(e.message?: messages.genericErrorMessage())

            is ApiException -> when(e) {

                    is UserContractInactive -> {
                        findNavController(f).navigate(R.id.actionGlobalOnContractInvalid)
                    }

                    is UserSessionInvalid -> {
                        dialog(e.message?: "", "Session") {
                            findNavController(f).navigate(R.id.actionGlobalOnSessionInvalid)
                        }
                    }

                    else -> toast(e.message?: messages.genericErrorMessage())
                }

            else -> toast(e.message?: messages.genericErrorMessage())
        }

        return true
    }

    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun dialog(message: String, title: String? = null, iconResId: Int? = null,  ok: (() -> Unit)? = null) {
        val b = AlertDialog.Builder(context).setMessage(message)
        iconResId?.let { b.setIcon(it) }
        title?.let { b.setTitle(it) }
        b.setPositiveButton("OK") { dialog, _ ->
            if (ok != null) ok() else dialog.dismiss()
        }
        b.create().show()
    }

//    fun screen(title: String, message: String, navigationMenu: List<Int>? = null, ok: (() -> Unit)? = null) {
//    }
}
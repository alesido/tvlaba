package org.alsi.android.tvlaba.exception

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import org.alsi.android.domain.exception.model.*
import org.alsi.android.tvlaba.R
import timber.log.Timber
import javax.inject.Inject

class ClassifiedExceptionHandler @Inject constructor(
    private val appContext: Context,
    private val messages: ExceptionMessages
) {

    fun handle(
        f: Fragment,
        e: Throwable?
    ): Boolean {

        e?: return false

        when(e) {

            is NetworkException -> dialog(f.context, e.message?: "", messages.noInternetConnection())
            is ServerException -> dialog(f.context,e.message?: "", messages.serverAccessError())
            is ProcessingException -> toast(e.message?: messages.genericErrorMessage())

            is ApiException -> when(e) {

                    is UserContractInactive -> {
                        findNavController(f).navigate(R.id.actionGlobalOnContractInvalid)
                    }

                    is UserSessionInvalid -> {
                        dialog(f.context, e.message?: "", messages.serviceIsNotAvailable()) {
                            findNavController(f).navigate(R.id.actionGlobalOnSessionInvalid)
                        }
                    }

                    else -> toast(e.message?: messages.genericErrorMessage())
                }

            else -> {
                Timber.e(e, "### An unclassified exception.")
                toast(e.message?: messages.genericErrorMessage())
            }
        }

        return true
    }

    private fun toast(message: String) {
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
    }

    private fun dialog(activityContext: Context?, message: String, title: String? = null, iconResId: Int? = null,  ok: (() -> Unit)? = null) {
        activityContext?: return
        val b = AlertDialog.Builder(activityContext).setMessage(message)
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
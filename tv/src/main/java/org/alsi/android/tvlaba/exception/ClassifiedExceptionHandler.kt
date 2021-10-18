package org.alsi.android.tvlaba.exception

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import io.reactivex.exceptions.CompositeException
import org.alsi.android.domain.exception.model.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.framework.validateContext
import javax.inject.Inject

class ClassifiedExceptionHandler @Inject constructor(
    private var context: Context,
    private val messages: ExceptionMessages
) {
    private val appContext = context

    fun changeContext(replacement: Context) {
        this.context = validateContext(replacement, appContext)
        messages.changeContext(this.context)
    }

    fun run(
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
                //Timber.e(e, "### An unclassified exception.")
                toast((if (e is CompositeException)
                    e.exceptions.reversed().joinToString("\n\n") { it.message.toString() }
                else
                    e.message
                ) ?: messages.genericErrorMessage())
            }
        }

        return true
    }

    private fun toast(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        with(toast) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.lite_red))
            view.setPadding(24)
        }
        toast.show()

    }

    private fun dialog(activityContext: Context?, message: String, title: String? = null, iconResId: Int? = null,  ok: (() -> Unit)? = null) {
        activityContext?: return
        val b = AlertDialog.Builder(activityContext)
        iconResId?.let { b.setIcon(it) }
        title?.let { b.setTitle(it) }
        if (message != title) b.setMessage(message)
        b.setPositiveButton("OK") { dialog, _ ->
            if (ok != null) ok() else dialog.dismiss()
        }
        b.create().show()
    }

//    fun screen(title: String, message: String, navigationMenu: List<Int>? = null, ok: (() -> Unit)? = null) {
//    }
}
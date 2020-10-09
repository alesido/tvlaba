package org.alsi.android.presentationtv.asset

import android.content.Context
import org.alsi.android.domain.tv.interactor.guide.TvRepositoryError
import org.alsi.android.domain.tv.interactor.guide.TvRepositoryErrorKind
import org.alsi.android.presentationtv.R


class TvRepositoryErrorMessages(val context: Context) {

    fun m(kind: TvRepositoryErrorKind): String = context.getString(
            resourceMap[kind]?: defaultErrorMessageRes)

    fun m(e: Throwable?): String {
        e?: return context.getString(defaultErrorMessageRes)
        if (e is TvRepositoryError) return m(e.kind)
        return e.localizedMessage?: context.getString(defaultErrorMessageRes)
    }

    companion object {

        val defaultErrorMessageRes = R.string.message_error_undefined_failure

        val resourceMap = mapOf(

            TvRepositoryErrorKind.RESPONSE_NO_PREVIOUS_CHANNEL
                    to R.string.message_response_no_prevous_channel,
            TvRepositoryErrorKind.RESPONSE_NO_NEXT_CHANNEL
                    to R.string.message_response_no_next_channel

        )
    }
}
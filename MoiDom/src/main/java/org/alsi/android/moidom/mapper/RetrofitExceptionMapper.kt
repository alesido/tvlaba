package org.alsi.android.moidom.mapper

import org.alsi.android.domain.exception.model.ClassifiedException
import org.alsi.android.domain.exception.model.ClassifiedExceptionFactory
import org.alsi.android.domain.exception.model.ExceptionMessages
import org.alsi.android.moidom.model.base.RequestErrorDirectory
import org.alsi.android.remote.retrofit.error.RetrofitException
import java.net.SocketTimeoutException
import javax.inject.Inject

class RetrofitExceptionMapper @Inject constructor(
    private val factory: ClassifiedExceptionFactory,
    private val messages: ExceptionMessages
) {

    fun map(rex: RetrofitException): ClassifiedException {
        return when(rex.errorKind) {

            RetrofitException.Kind.NETWORK -> if (rex.cause is SocketTimeoutException)
                factory.serverException(t = rex) else factory.networkException(t = rex)

            RetrofitException.Kind.HTTP -> factory.serverException(t = rex)

            RetrofitException.Kind.REST_API -> mapApiException(rex)

            RetrofitException.Kind.UNEXPECTED -> factory.processingException()
            else -> factory.processingException(t = rex.cause)
        }
    }

    private fun mapApiException(rex: RetrofitException): ClassifiedException {

        val serverCode: Long? = rex.apiResponseCode
        val serverMessage: String? = rex.apiErrorMessage

        if (null == serverCode && null == serverMessage)
            return factory.processingException() // TODO Return unknown error with message if not null

        return when(RequestErrorDirectory.valueByCode[serverCode]) {

            RequestErrorDirectory.ANOTHER_LOGGED
            -> factory.userSessionInvalid(m = rex.apiErrorMessage, t = rex)

            RequestErrorDirectory.CONTRACT_INACTIVE,
            RequestErrorDirectory.CONTRACT_PAUSED,
            RequestErrorDirectory.PACKET_EXPIRED
            -> factory.userContractInactive(
                m = rex.apiErrorMessage,
                t = rex)

            RequestErrorDirectory.UNKNOWN_ERROR
            -> factory.unknownApiError(t = rex)

            else -> factory.requestError(m = rex.apiErrorMessage, t = rex)
        }
    }
}
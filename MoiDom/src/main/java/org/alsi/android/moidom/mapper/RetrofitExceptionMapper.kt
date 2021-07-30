package org.alsi.android.moidom.mapper

import org.alsi.android.domain.exception.model.ClassifiedException
import org.alsi.android.domain.exception.model.ClassifiedExceptionFactory
import org.alsi.android.domain.exception.model.ExceptionMessages
import org.alsi.android.moidom.model.base.RequestErrorDirectory
import org.alsi.android.remote.retrofit.error.RetrofitException
import javax.inject.Inject

class RetrofitExceptionMapper @Inject constructor(
    val factory: ClassifiedExceptionFactory,
    val resources: ExceptionMessages
) {

    fun map(re: RetrofitException): ClassifiedException {
        return when(re.errorKind) {

            RetrofitException.Kind.NETWORK -> factory.networkException()
            RetrofitException.Kind.HTTP -> factory.serverException()

            RetrofitException.Kind.REST_API -> mapApiException(re)

            RetrofitException.Kind.UNEXPECTED -> factory.processingException()
            else -> factory.processingException()
        }
    }

    private fun mapApiException(re: RetrofitException): ClassifiedException {

        val serverCode: Long? = re.apiResponseCode
        val serverMessage: String? = re.apiErrorMessage

        if (null == serverCode && null == serverMessage)
            return factory.processingException() // TODO Return unknown error with message if not null

        return when(RequestErrorDirectory.valueByCode[serverCode]) {

            RequestErrorDirectory.ANOTHER_LOGGED
            -> factory.userSessionInvalid()

            RequestErrorDirectory.CONTRACT_INACTIVE,
            RequestErrorDirectory.CONTRACT_PAUSED,
            RequestErrorDirectory.PACKET_EXPIRED
            -> factory.userContractInactive()

            RequestErrorDirectory.UNKNOWN_ERROR
            -> factory.unknownApiError(t = re)

            else -> factory.requestError(t = re)
        }
    }
}
package org.alsi.android.domain.exception.model

import javax.inject.Inject

class ClassifiedExceptionFactory @Inject constructor(
    private val messages: ExceptionMessages
) {

    // region  API Request Conditions

    fun networkException(m: String? = null, t: Throwable? = null) = NetworkException(
        m ?: messages.noInternetConnection(), t
    )

    fun serverException(m: String? = null, t: Throwable? = null) = ServerException(
        m ?: messages.serverAccessError(), t
    )

    fun processingException(m: String? = null, t: Throwable? = null) = ServerException(
        m ?: messages.dataProcessingError(), t
    )

    // endregion
    // region API Request Case

    fun unknownApiError(m: String? = null, t: Throwable? = null) = UnknownApiError(
        m ?: messages.genericErrorMessage(), t = t
    )

    fun requestError(m: String? = null, t: Throwable? = null) = RequestError(
        m ?: messages.genericErrorMessage(), t
    )

    fun userSessionInvalid(m: String? = null, t: Throwable? = null) = UserSessionInvalid(
        m ?: t?.message ?: messages.serviceIsNotAvailable(), t
    )

    fun userContractInactive(m: String? = null, t: Throwable? = null) = UserContractInactive(
        m ?: messages.checkServiceSubscriptionAtSite(), t)


    fun tvChannelProtected(m: String? = null, t: Throwable? = null) = TvChannelProtected(
        m ?: t?.message ?: messages.genericErrorMessage(), t
    )

    // endregion
 }
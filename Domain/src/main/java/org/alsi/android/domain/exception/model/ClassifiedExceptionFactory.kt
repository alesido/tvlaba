package org.alsi.android.domain.exception.model

import javax.inject.Inject

class ClassifiedExceptionFactory @Inject constructor(val resources: ExceptionMessages) {

    // region  API Request Conditions

    fun networkException(m: String? = null, t: Throwable? = null) = NetworkException(
        m ?: resources.noInternetConnection(), t
    )

    fun serverException(m: String? = null, t: Throwable? = null) = ServerException(
        m ?: resources.serverAccessError(), t
    )

    fun processingException(m: String? = null, t: Throwable? = null) = ServerException(
        m ?: resources.dataProcessingError(), t
    )

    // endregion
    // region API Request Case

    fun unknownApiError(m: String? = null, t: Throwable? = null) = UnknownApiError(
        m ?: resources.genericErrorMessage(), t = t
    )

    fun requestError(m: String? = null, t: Throwable? = null) = RequestError(
        m ?: resources.genericErrorMessage(), t
    )

    fun userSessionInvalid(m: String? = null, t: Throwable? = null) = UserSessionInvalid(
        m ?: t?.message ?: resources.genericErrorMessage(), t
    )

    fun userContractInactive(m: String? = null, t: Throwable? = null) = UserContractInactive(
        m ?: t?.message ?: resources.genericErrorMessage(), t
    )

    fun tvChannelProtected(m: String? = null, t: Throwable? = null) = TvChannelProtected(
        m ?: t?.message ?: resources.genericErrorMessage(), t
    )

    // endregion
 }
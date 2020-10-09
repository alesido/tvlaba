package org.alsi.android.domain.tv.interactor.guide

class TvRepositoryError(

        val kind: TvRepositoryErrorKind,
        message: String? = null,
        cause: Throwable? = null

) : Throwable(message, cause)

enum class TvRepositoryErrorKind {

    ERROR_WRONG_USE_CASE_PARAMETERS,
    ERROR_CANNOT_ACCESS_TV_REPOSITORY,

    RESPONSE_NO_CHANNELS_IN_CATEGORY,
    RESPONSE_NO_NEXT_CHANNEL,
    RESPONSE_NO_PREVIOUS_CHANNEL,

    ERROR_NETWORK_CONNECTION_FAILURE,
    ERROR_API_LEVEL_ERROR,
}
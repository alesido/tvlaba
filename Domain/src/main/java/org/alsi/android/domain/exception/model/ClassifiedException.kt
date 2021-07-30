package org.alsi.android.domain.exception.model

sealed class ClassifiedException(m: String? = null, t: Throwable? = null): Throwable(m, t) {
    var title: String? = null
}


class NetworkException      (m: String? = null, t: Throwable? = null) : ClassifiedException(m, t)
class ServerException       (m: String? = null, t: Throwable? = null) : ClassifiedException(m, t)
class ProcessingException   (m: String? = null, t: Throwable? = null) : ClassifiedException(m, t)


sealed class ApiException   (m: String? = null, t: Throwable? = null) : ClassifiedException(m, t)

class UnknownApiError       (m: String? = null, t: Throwable? = null) : ApiException(m, t)
class RequestError          (m: String? = null, t: Throwable? = null) : ApiException(m, t)
class UserSessionInvalid    (m: String? = null, t: Throwable? = null) : ApiException(m, t)
class UserContractInactive  (m: String? = null, t: Throwable? = null) : ApiException(m, t)
class TvChannelProtected    (m: String? = null, t: Throwable? = null) : ApiException(m, t)
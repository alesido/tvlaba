package org.alsi.android.remote.retrofit.error

import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type

/** Custom retrofit call adapter and factory extending RX adapter for retrofit 2. Introduced solely
 *  to wrap error into unified object RetrofitException.
 *
 *  @see "https://futurestud.io/tutorials/retrofit-2-introduction-to-call-adapters"
 *  @see "https://gist.github.com/yitz-grocerkey/61a66a15a0c22e8ea5149484676618c9#file-rxerrorhandlingcalladapterfactory-kt"
 *  @see "https://bytes.babbel.com/en/articles/2016-03-16-retrofit2-rxjava-error-handling.html"
 */
class RxErrorHandlingCallAdapterFactory: CallAdapter.Factory() {

    private val original by lazy {
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
    }

    /** Wrap original RX adapter and return it.
     */
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {
        val wrapped = original.get(returnType, annotations, retrofit) as CallAdapter<out Any, *>
        return RxCallAdapterWrapper(retrofit, wrapped)
    }

    /** Wrapper of RX retrofit2 adapter.
     */
    private class RxCallAdapterWrapper<R>(val retrofit: Retrofit, val wrappedCallAdapter: CallAdapter<R, *>)
        : CallAdapter<R, Any> {

        override fun responseType(): Type = wrappedCallAdapter.responseType()

        /** Add RX operation which provides normal resume to the RX sequence in case of error
         * and transforms throwable to a unified RetrofitException as the emission content.
         */
        @Suppress("UNCHECKED_CAST")
        override fun adapt(call: Call<R>): Any {
            val emitter = wrappedCallAdapter.adapt(call)
            return when (emitter) {

                is Observable<*> -> emitter.flatMap { response -> rexR(call, response)?.let {
                    Observable.error<R>(it) } ?: Observable.just(response)
                }.onErrorResumeNext { t: Throwable -> Observable.error(rexT(t)) }

                is Flowable<*> -> emitter.flatMap { response -> rexR(call, response)?.let {
                    Flowable.error<R>(it) } ?: Flowable.just(response)
                }.onErrorResumeNext { t: Throwable -> Flowable.error(rexT(t)) }

                is Single<*> -> emitter.flatMap { response -> rexR(call, response)?.let {
                    Single.error<R>(it) } ?: Single.just(response)
                }.onErrorResumeNext { t: Throwable -> Single.error(rexT(t)) }

                is Maybe<*> -> emitter.flatMap { response -> rexR(call, response)?.let {
                    Maybe.error<R>(it) } ?: Maybe.just(response)
                }.onErrorResumeNext { t: Throwable -> Maybe.error(rexT(t)) }

                is Completable -> emitter.onErrorResumeNext { t: Throwable -> Completable.error(rexT(t)) }

                else -> emitter
            }
        }

        private fun rexR(call: Call<R>, response : Any): RetrofitException? {
            return if (response is RetrofitExceptionSource && response.isErrorResponse()) {
                RetrofitException(call, response, retrofit, wrappedCallAdapter.responseType())
            } else null
        }

        /** Create RetrofitException from a throwable
         */
        private fun rexT(t: Throwable) : RetrofitException? = t as? RetrofitException
                ?: RetrofitException(t, retrofit, wrappedCallAdapter.responseType())
    }
}

package org.alsi.android.presentation.state

class Resource<out T> constructor(val status: ResourceState,
                                  val data: T?,
                                  val message: String?,
                                  val throwable: Throwable? = null) {
    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(ResourceState.SUCCESS, data, null)
        }

        fun <T> success(): Resource<T> {
            return Resource(ResourceState.SUCCESS, null, null)
        }

        fun <T> error(message: String?): Resource<T> {
            return Resource(ResourceState.ERROR, null, message)
        }
        
        fun <T> error(throwable: Throwable?): Resource<T> {
            return Resource(ResourceState.ERROR, null, null, throwable)
        }

        fun <T> loading(): Resource<T> {
            return Resource(ResourceState.LOADING, null, null)
        }
    }
}
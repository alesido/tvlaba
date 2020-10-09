package org.alsi.android.tvlaba.framework

import android.content.Context
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentationtv.asset.TvRepositoryErrorMessages

class TvErrorMessaging(val context: Context) {

    private val tvRepositoryErrorMessages = TvRepositoryErrorMessages(context)

    fun <T> m(resource: Resource<T>): String {
        if (resource.message != null) return resource.message?:""
        return tvRepositoryErrorMessages.m(resource.throwable)
    }
}
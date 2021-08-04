package org.alsi.android.tvlaba.exception

import android.content.Context
import androidx.annotation.StringRes
import org.alsi.android.domain.exception.model.ExceptionMessages
import org.alsi.android.tvlaba.R
import javax.inject.Inject

class ExceptionMessageStrings @Inject constructor(val context: Context): ExceptionMessages {

    override fun genericErrorMessage() = s(R.string.exception_message_generic_error)

    override fun noInternetConnection() = s(R.string.noInternetConnection)
    override fun serverAccessError()    = s(R.string.serverAccessError)
    override fun dataProcessingError()  = s(R.string.dataProcessingError)

    override fun unknownAccountId() = s(R.string.exception_message_generic_error)

    override fun unregisteredLoginName() = s(R.string.exception_message_generic_error)

    override fun serviceIsNotAvailable() =
        s(R.string.exception_message_generic_error)

    override fun visitSiteToSubscribeService(siteUrl: String?, serviceName: String?)
    = if (null == siteUrl || null == serviceName)
        s(R.string.visitSiteToSubscribeService)
    else
        context.getString(R.string.visitSiteToSubscribeService2, siteUrl, serviceName)

    override fun checkServiceSubscriptionAtSite(siteUrl: String?, serviceName: String?)
    = if (null == siteUrl || null == serviceName)
        s(R.string.checkServiceSubscriptionAtSite)
    else
        context.getString(R.string.checkServiceSubscriptionAtSite2, serviceName, siteUrl)

    override fun errorGettingTvChannelCategories() =
        s(R.string.exception_message_generic_error)

    override fun errorGettingTvChannelDirectory() =
        s(R.string.exception_message_generic_error)

    override fun errorUpdatingTvChannelDirectory() =
        s(R.string.exception_message_generic_error)

    override fun errorGettingTvProgramSchedule() =
        s(R.string.exception_message_generic_error)

    override fun errorGettingTvProgramData() =
        s(R.string.exception_message_generic_error)

    override fun noTvProgramDataAvailable() =
        s(R.string.exception_message_generic_error)

    override fun errorGettingStreamURL() =
        s(R.string.exception_message_generic_error)

    override fun videoIsNotAvailable() = s(R.string.exception_message_generic_error)

    override fun errorGettingSetting() = s(R.string.exception_message_generic_error)

    override fun errorChangingSetting() =
        s(R.string.exception_message_generic_error)

    override fun errorSwitchingStreamerServer() =
        s(R.string.exception_message_generic_error)

    override fun errorSettingStreamCacheSize() =
        s(R.string.exception_message_generic_error)

    override fun errorCheckingIfAppUpdateAvailable() =
        s(R.string.exception_message_generic_error)

    override fun errorGettingAppUpdateFile() =
        s(R.string.exception_message_generic_error)

    override fun wrongChannelNumber() = s(R.string.exception_message_generic_error)

    override fun cannotSwitchChannel() = s(R.string.exception_message_generic_error)

    override fun tvServiceRepositoryIsNotAvailable() =
        s(R.string.exception_message_generic_error)

    override fun tvDirectoryRepositoryIsNotAvailable() =
        s(R.string.exception_message_generic_error)

    override fun noParametersToGetPlaybackData() =
        s(R.string.exception_message_generic_error)

    override fun wrongNewPlaybackParameters() =
        s(R.string.exception_message_generic_error)
    
    fun s(@StringRes id: Int) = context.getString(id)
}
     
 
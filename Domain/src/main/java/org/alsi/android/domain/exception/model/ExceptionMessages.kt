package org.alsi.android.domain.exception.model

interface ExceptionMessages {

    // General

    fun genericErrorMessage(): String

    fun noInternetConnection(): String
    fun serverAccessError(): String
    fun dataProcessingError(): String

    // Auth

    fun unknownAccountId(): String
    fun unregisteredLoginName(): String

    // Service

    fun serviceIsNotAvailable(): String
    fun visitSiteToSubscribeService(siteUrl: String? = null, serviceName: String? = null): String
    fun checkServiceSubscriptionAtSite(siteUrl: String? = null, serviceName: String? = null): String

    // TV Channel Directory

    fun errorGettingTvChannelCategories(): String
    fun errorGettingTvChannelDirectory(): String
    fun errorUpdatingTvChannelDirectory(): String

    // TV Schedule & Program

    fun errorGettingTvProgramSchedule(): String
    fun errorGettingTvProgramData(): String
    fun noTvProgramDataAvailable(): String

    // Video Stream

    fun errorGettingStreamURL(): String
    fun videoIsNotAvailable(): String

    // Settings

    fun errorGettingSetting(): String
    fun errorChangingSetting(): String

    fun errorSwitchingStreamerServer(): String
    fun errorSettingStreamCacheSize(): String

    // App Update

    fun errorCheckingIfAppUpdateAvailable(): String
    fun errorGettingAppUpdateFile(): String

    // TV UI

    fun wrongChannelNumber(): String
    fun cannotSwitchChannel(): String


    // Internal, to be interpreted for user comprehension

    fun tvServiceRepositoryIsNotAvailable(): String
    fun tvDirectoryRepositoryIsNotAvailable(): String

    fun noParametersToGetPlaybackData(): String
    fun wrongNewPlaybackParameters(): String
}
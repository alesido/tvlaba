package org.alsi.android.moidom.repository.tv

import android.text.format.DateUtils
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.framework.Now

/** NOTE Avoid using System.currentTimeMillis() 'cause static methods cannot be mocked easily,
 *  particularly Mockito does not allow this. PowerMock can be used for that, but it
 *  have to be really necessary.
 */
class TvChannelDataExpiration {

    private var lastCategoriesUpdateMillis = 0L
    private var lastChannelsUpdateMillis = 0L
    private val current = Now()

    fun checkInDirectory() {
        lastCategoriesUpdateMillis = current.millis()
        lastChannelsUpdateMillis = lastCategoriesUpdateMillis
    }

    fun checkInCategories() {
        lastCategoriesUpdateMillis = current.millis()
    }

    fun checkInChannels() {
        lastChannelsUpdateMillis = current.millis()
    }

    fun directoryExpired(directory: TvChannelDirectory)
            = categoriesExpired(directory.categories) || channelsExpired(directory.channels)

    fun categoriesExpired(categories: List<TvChannelCategory>): Boolean
            = categories.isEmpty() || lastCategoriesUpdateMillis == 0L
            || System.currentTimeMillis() - lastCategoriesUpdateMillis > EXPIRATION_CATEGORIES_MILLIS

    fun channelsExpired(channels: List<TvChannel>): Boolean
            = channels.isEmpty() || lastChannelsUpdateMillis == 0L
            || System.currentTimeMillis() - lastChannelsUpdateMillis > EXPIRATION_CHANNELS_MILLIS
            || channelProgramDataExpired(channels)

    fun channelProgramDataExpired(channels: List<TvChannel>): Boolean {
        val nowMillis = System.currentTimeMillis()
        for (channel in channels) {
            channel.live.time?.let { if (it.endUnixTimeMillis < nowMillis) return true }
        }
        return false
    }

    companion object {
        const val EXPIRATION_CATEGORIES_MILLIS = DateUtils.MINUTE_IN_MILLIS * 60
        const val EXPIRATION_CHANNELS_MILLIS = DateUtils.MINUTE_IN_MILLIS * 30
    }
}

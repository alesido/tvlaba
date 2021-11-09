package org.alsi.android.local.store.tv

import android.text.format.DateUtils
import org.alsi.android.domain.implementation.model.IconType
import org.alsi.android.domain.implementation.model.TypedIconReference
import org.alsi.android.domain.tv.model.guide.*
import java.net.URI

object TvChannelTestDataFactory {

    fun categories(start: Int, size: Int): List<TvChannelCategory> {
        val categories: MutableList<TvChannelCategory> = mutableListOf()
        val end = start + size - 1
        for (i in start..end) {
            categories.add( TvChannelCategory(
                    i.toLong(),
                    i,
                    "TV Channel Category #$i",
                    TypedIconReference(IconType.REMOTE_RASTER,
                            "http://test.example.com/catcha$i.png")))
        }
        return categories
    }

    fun channels(start:Int, size: Int): List<TvChannel> {
        val channels: MutableList<TvChannel> = mutableListOf()
        val nowMillis = System.currentTimeMillis()
        var channelId = start.toLong()
        var channelNumber = 1
        val end = start + size - 1
        for (i in start..end) {
            for (j in start..end) {
                channels.add( TvChannel(

                        id = channelId++,
                        categoryId = i.toLong(),
                        number = channelNumber++,
                        title = "Channel #$j @$i",
                        logoUri = URI.create("http://test.example.com/chalog$i.png"),

                        live = TvProgramLive(
                                time = TvProgramTimeInterval(
                                        nowMillis + j * DateUtils.MINUTE_IN_MILLIS,
                                        nowMillis + (j + 1) * DateUtils.MINUTE_IN_MILLIS),
                                title = "Channel #$j @$i live broadcast"),

                        features = TvChannelFeatures())
                )
            }
        }
        return channels
    }

    fun categoryChannelIndex(channels: List<TvChannel>) = channels.groupBy { it.categoryId }
}
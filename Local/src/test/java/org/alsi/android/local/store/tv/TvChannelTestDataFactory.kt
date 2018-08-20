package org.alsi.android.local.store.tv

import android.text.format.DateUtils
import org.alsi.android.domain.implementation.model.IconType
import org.alsi.android.domain.implementation.model.TypedIconReference
import org.alsi.android.domain.tv.model.guide.*
import java.net.URI

object TvChannelTestDataFactory {

    fun categories(): List<TvChannelCategory> {
        val categories: MutableList<TvChannelCategory> = mutableListOf()
        for (i in 1..10) {
            categories.add( TvChannelCategory(i.toLong(),
                    "TV Channel Category #$i",
                    TypedIconReference(IconType.REMOTE_RASTER,
                            "http://test.example.com/catcha$i.png")))
        }
        return categories
    }

    fun channels(): List<TvChannel> {
        val channels: MutableList<TvChannel> = mutableListOf()
        val nowMillis = System.currentTimeMillis()
        var channelId = 0L
        var channelNumber = 1
        for (i in 1..10) {
            for (j in 1..10) {

                val categoryId = i.toLong()
                channelId++

                channels.add( TvChannel(

                        id = channelId,
                        categoryId = categoryId,
                        number = channelNumber++,
                        title = "Channel #$j @$i",
                        logoUri = URI.create("http://test.example.com/chalog$i.png"),

                        live = TvProgramLive(
                                time = TvProgramTimeInterval(
                                        nowMillis - j * DateUtils.MINUTE_IN_MILLIS,
                                        nowMillis + j * DateUtils.MINUTE_IN_MILLIS),
                                title = "Channel #$j @$i live broadcast"),

                        features = TvChannelFeatures())
                )
            }
        }
        return channels
    }
}
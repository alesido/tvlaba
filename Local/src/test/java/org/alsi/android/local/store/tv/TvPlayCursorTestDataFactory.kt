package org.alsi.android.local.store.tv

import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.session.TvPlayCursor
import java.net.URI

object TvPlayCursorTestDataFactory {

    fun testPlayCursor() = TvPlayCursor(
        categoryId = 12L,
        seekTime = 123L,
        timeStamp = 1234L,
        playback = TvPlayback(
            channelId = 321L,
            programId = 4321L,
            stream = VideoStream(
                uri = URI("http://stream.test.com"),
                kind = VideoStreamKind.LIVE
            ),
            title = "Test Program Title",
            description = "Test Program Description",
            isLive = true
        )
    )

    fun testPlayCursor2(): TvPlayCursor {
        val playback = TvPlayback(
            channelId = 321L,
            programId = 4321L,
            stream = VideoStream(
                uri = URI("http://stream2.test.com"),
                kind = VideoStreamKind.RECORD
            ),
            title = "Test Program Title 2",
            description = "Test Program Description 2",
            isLive = true
        )
        playback.position = 789L
        return TvPlayCursor(
            categoryId = 12L,
            seekTime = 456L,
            timeStamp = 4567L,
            playback = playback)
    }
}
package org.alsi.android.tvlaba.tv.tv.playback

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramDisposition.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.TvProgramPlaybackDetailsBinding
import org.joda.time.LocalDate
import java.util.*

class TvProgramPlaybackDetailsPresenter(val context: Context) : Presenter() {

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val itemBinding = TvProgramPlaybackDetailsBinding.inflate(LayoutInflater.from(context))
        return MyViewHolder(itemBinding, context)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        (holder as MyViewHolder).bind(item as TvPlaybackLeanbackGlue)
    }

    internal class MyViewHolder(private val ib: TvProgramPlaybackDetailsBinding,
                                private val context: Context
                                ): ViewHolder(ib.root) {

        fun bind(item: TvPlaybackLeanbackGlue) {
            item.playback?.let {

                // title
                ib.tvProgramPlaybackDetailsPrimaryTitle.text = it.title

                // subtitle: time & channel's title
                val programTime = playbackTimeString(it)
                val channelReferences = if (it.channelTitle != null) context.getString(
                    R.string.statement_program_on_channel, it.channelTitle) else ""
                ib.tvProgramPlaybackDetailsSecondaryTitle.text = String.format("%s %s", programTime, channelReferences)

                // body
                ib.tvProgramPlaybackDetailsDescriptionText.text = buildCompactDigestExtract(it)

                // disposition
                ib.tvProgramPlaybackDetailsDispositionLive.visibility =
                    if (it.disposition == LIVE) VISIBLE else GONE
                ib.tvProgramPlaybackDetailsDispositionRecord.visibility =
                    if (it.disposition == RECORD
                        || it.stream?.kind == VideoStreamKind.RECORD) VISIBLE else GONE
                ib.tvProgramPlaybackDetailsDispositionFuture.visibility =
                    if (it.disposition == FUTURE) VISIBLE else GONE
            }
        }

        private fun playbackTimeString(it: TvPlayback): String {
            val t = it.time?: return ""
            val now = LocalDate.now()
            val s = t.startDateTime.toLocalDate()
            val e = t.endDateTime.toLocalDate()
            return if (s == e && s == now) t.shortString else t.toString()
        }

        private fun buildCompactDigestExtract(playback: TvPlayback): String? {
            val parts: MutableList<String?> = ArrayList()
            with (playback) {
                releaseDates?.let { if (it.trim().isNotEmpty()) parts.add(it) }
                categoryNames?.let { if (it.isNotEmpty()) parts.add(it.joinToString(", ")) }
                countryNames?.let { if (it.isNotEmpty()) parts.add(it.joinToString(", ")) }
            }
            if (parts.isEmpty()) return null
            return parts.joinToString(", ")
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        // not applicable here
    }
}
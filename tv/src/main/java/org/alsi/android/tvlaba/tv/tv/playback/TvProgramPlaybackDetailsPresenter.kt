package org.alsi.android.tvlaba.tv.tv.playback

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import kotlinx.android.synthetic.main.tv_program_playback_details.view.*
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramDisposition.*
import org.alsi.android.tvlaba.R
import java.util.*

class TvProgramPlaybackDetailsPresenter(val context: Context) : Presenter() {

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view: View = LayoutInflater.from(context)
                .inflate(R.layout.tv_program_playback_details, null)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val playback = (item as TvPlaybackLeanbackGlue).playback
        playback?: return
        with(viewHolder.view) {
            playback.let {

                // title
                tvProgramPlaybackDetailsPrimaryTitle.text = it.title

                // subtitle: time & channel's title
                val programTime = it.time?.shortString?:""
                val channelReferences = if (it.channelTitle != null) context.getString(
                        R.string.statement_program_on_channel, it.channelTitle) else ""
                tvProgramPlaybackDetailsSecondaryTitle.text = "$programTime $channelReferences"

                // body
                tvProgramPlaybackDetailsDescriptionText.text = buildCompactDigestExtract(playback)

                // disposition
                tvProgramPlaybackDetailsDispositionLive.visibility =
                        if (it.disposition == LIVE) VISIBLE else GONE
                tvProgramPlaybackDetailsDispositionRecord.visibility =
                        if (it.disposition == RECORD) VISIBLE else GONE
                tvProgramPlaybackDetailsDispositionFuture.visibility =
                        if (it.disposition == FUTURE) VISIBLE else GONE
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        // not applicable here
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
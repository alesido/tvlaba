package org.alsi.android.tvlaba.tv.tv.directory

import android.app.Activity
import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.tv_channel_card_view.view.*
import org.alsi.android.domain.tv.model.guide.TvChannel

class TvDirectoryChannelCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            TvChannelCardView(parent.context).apply {
                setBackgroundColor(Color.DKGRAY)
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {

        val tvChannel = item as TvChannel
        val cardView = viewHolder.view as TvChannelCardView

        // channel title
        cardView.channelTitleText = tvChannel.title?: ""

        with(tvChannel.live, {

            // live program title
            cardView.programTitleText = (time?.shortString ?: "") + " " + (title ?: "")

            val isCurrent = tvChannel.live.time?.isCurrent

            // live program progress
            cardView.tvChannelCardProgramProgress.progress =
                    if (isCurrent == true) time?.progress ?: 0 else 0

            // program update status
            cardView.showIsActual(isCurrent == true)
        })

        // logo
        val activity = cardView.tvChannelCardPoster.context as Activity
        if (!activity.isFinishing && !activity.isDestroyed) {
            Glide.with(activity).load(tvChannel.logoUri.toString()).into(cardView.tvChannelCardPoster)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

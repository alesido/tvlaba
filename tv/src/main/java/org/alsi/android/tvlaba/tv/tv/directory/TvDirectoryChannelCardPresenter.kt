package org.alsi.android.tvlaba.tv.tv.directory

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
        cardView.channelTitleText = tvChannel.title?: ""
        with(tvChannel.live, {
            cardView.programTitleText = (time?.shortString ?: "") + " " + (title ?: "")
            cardView.tvChannelCardProgramProgress.progress = if (time?.isCurrent == true) time?.progress ?: 0 else 0
            cardView.showIsActual(time?.isCurrent == true)
        })
        val context = cardView.tvChannelCardPoster.context
        Glide.with(context).load(tvChannel.logoUri.toString()).into(cardView.tvChannelCardPoster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

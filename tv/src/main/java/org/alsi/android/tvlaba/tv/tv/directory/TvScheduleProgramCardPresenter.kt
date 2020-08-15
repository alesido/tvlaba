package org.alsi.android.tvlaba.tv.tv.directory

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.tv_channel_card_view.view.*
import org.alsi.android.domain.tv.model.guide.TvProgramIssue

class TvScheduleProgramCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            TvChannelCardView(parent.context).apply {
                setBackgroundColor(Color.DKGRAY)
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val tvProgram = item as TvProgramIssue
        val cardView = viewHolder.view as TvChannelCardView
        cardView.channelTitleText = tvProgram.title?: ""
        with(tvProgram, {
            cardView.programTitleText = (time?.shortString?:"") + " " + (title?:"")
            cardView.tvChannelCardProgramProgress.progress = time?.progress?:0
        })
        val context = cardView.tvChannelCardPoster.context
        Glide.with(context).load(tvProgram.mainPosterUri.toString())
                .into(cardView.tvChannelCardPoster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

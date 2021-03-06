package org.alsi.android.tvlaba.tv.tv.schedule

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import org.alsi.android.domain.tv.model.guide.TvProgramIssue

class TvScheduleProgramCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            TvProgramCardView(parent.context).apply {
                setBackgroundColor(Color.DKGRAY)
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val tvProgram = item as TvProgramIssue
        val cardView = viewHolder.view as TvProgramCardView
        with(tvProgram, {
            cardView.programTimeText = time?.shortString?:""
            cardView.programTitleText = title?:"" + "\n" + (description?:"")
        })
        val context = cardView.vb.tvProgramCardPoster.context
        Glide.with(context).load(tvProgram.mainPosterUri.toString())
                .into(cardView.vb.tvProgramCardPoster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

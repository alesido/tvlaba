package org.alsi.android.tvlaba.tv.tv.directory

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import org.alsi.android.domain.tv.model.guide.TvProgramPromotion
import org.alsi.android.tvlaba.R

class TvProgramPromotionCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            TvProgramPromotionCardView(parent.context).apply {
                setBackgroundColor(ContextCompat.getColor(parent.context, R.color.appColorPrimaryDark))
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val program = item as TvProgramPromotion
        val cardView = viewHolder.view as TvProgramPromotionCardView
        with(program) {
            cardView.programTimeText = time?.toString()?:""
            cardView.channelTitleText = channel?.title?:""
            cardView.programTitleText = title
        }
        val context = cardView.vb.tvProgramCardPoster.context
        Glide.with(context).load(program.mainPosterUri.toString())
                .into(cardView.vb.tvProgramCardPoster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

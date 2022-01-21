package org.alsi.android.tvlaba.tv.tv.directory

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.framework.setImageToView
import org.alsi.android.tvlaba.tv.model.CardMenuItem

class TvMenuCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            TvMenuCardView(parent.context).apply {
                setBackgroundColor(ContextCompat.getColor(parent.context, R.color.appColorPrimaryDark))
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {

        val tvMenuItem = item as CardMenuItem
        val cardView = viewHolder.view as TvMenuCardView

        // channel title
        cardView.menuTitleText = tvMenuItem.title?:""

        // logo
        val logoView = cardView.vb.tvMenuCardLogo
        if (item.payload !is StreamingServicePresentation ||
            !item.payload.setImageToView(logoView)) {
                item.logoDrawableRes?.let {
                    logoView.setImageResource(item.logoDrawableRes)
                }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

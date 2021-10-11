package org.alsi.android.tvlaba.tv.vod.directory

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.tvlaba.framework.setImageToView
import org.alsi.android.tvlaba.tv.model.CardMenuItem

class VodMenuCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            VodMenuCardView(parent.context).apply {
                setBackgroundColor(Color.DKGRAY)
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {

        if (item !is CardMenuItem) return

        val cardView = viewHolder.view as VodMenuCardView

        // channel title
        cardView.menuTitleText = item.title?:""

        // logo drawable ...
        val logoView = cardView.vb.vodMenuCardLogo
        if (item.payload !is StreamingServicePresentation ||
            !item.payload.setImageToView(logoView)) {
            item.logoDrawableRes?.let {
                logoView.setImageResource(item.logoDrawableRes)
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

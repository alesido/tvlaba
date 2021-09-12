package org.alsi.android.tvlaba.tv.tv.directory

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import org.alsi.android.tvlaba.tv.model.TvMenuItem

class TvMenuCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            TvMenuCardView(parent.context).apply {
                setBackgroundColor(Color.DKGRAY)
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {

        val tvMenuItem = item as TvMenuItem
        val cardView = viewHolder.view as TvMenuCardView

        // channel title
        cardView.menuTitleText = tvMenuItem.title

        // logo drawable ...
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

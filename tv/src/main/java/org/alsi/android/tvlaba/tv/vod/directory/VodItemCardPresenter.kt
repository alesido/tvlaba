package org.alsi.android.tvlaba.tv.vod.directory

import android.app.Activity
import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.tvlaba.R

class VodItemCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            VodItemCardView(parent.context).apply {
                setBackgroundColor(Color.DKGRAY)
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {

        val vodItem = item as VodListingItem

        val cardView = viewHolder.view as VodItemCardView

        vodItem.posters?: run  {
            cardView.vb.vodItemPoster.setImageResource(R.drawable.logo_kinopoisk_bold)
            return@onBindViewHolder
        }

        // logo
        val activity = cardView.vb.vodItemPoster.context as Activity
        if (!activity.isFinishing && !activity.isDestroyed) {
            Glide.with(activity).load(vodItem.posters?.poster.toString()).centerCrop()
                .into(cardView.vb.vodItemPoster)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

package org.alsi.android.tvlaba.tv.tv.directory

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import coil.api.load
import org.alsi.android.domain.tv.model.guide.TvChannel

class TvDirectoryChannelPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) =
            ViewHolder(ImageCardView(parent.context).apply {
                isFocusable = true
                isFocusableInTouchMode = true
                setBackgroundColor(Color.DKGRAY)
                //infoVisibility = View.GONE
    })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val metadata = item as TvChannel
        val card = viewHolder.view as ImageCardView
//
//        // Computes the card width from the given height and metadata aspect ratio
//        val cardWidth = TvLazuncherUtils.parseAspectRatio(metadata.artAspectRatio).let {
//            cardHeight * it.numerator / it.denominator
//        }

        card.titleText = metadata.title
//        card.setMainImageDimensions(cardWidth, cardHeight)
        card.mainImageView.load(metadata.logoUri.toString())
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}
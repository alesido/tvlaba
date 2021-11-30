package org.alsi.android.tvlaba.tv.tv.program

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import org.alsi.android.tvlaba.R

class VideoPosterCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            VideoPoster4x3CardView(parent.context).apply {
                setBackgroundColor(Color.RED)
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val posterUri = item as Uri
        val cardView = viewHolder.view as VideoPoster4x3CardView
        Glide.with(cardView.context).load(posterUri.toString())
            .placeholder(R.drawable.settings_icon_metal)

            .listener (object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })

            .into(cardView.vb.videoPosterCardView4x3)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

package org.alsi.android.tvlaba.tv.vod.playback

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import org.alsi.android.tvlaba.databinding.VodItemPlaybackDetailsBinding

class VodItemPlaybackDetailsPresenter(val context: Context) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val itemBinding = VodItemPlaybackDetailsBinding.inflate(LayoutInflater.from(context))
        return VodItemViewHolder(itemBinding, context)
    }

    override fun onBindViewHolder(holder: ViewHolder?, item: Any?) {
        if (null == holder || item !is VodPlaybackLeanbackGlue) return
        (holder as VodItemViewHolder).bind(item)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) { /** not applicable */ }

    internal class VodItemViewHolder(private val ib: VodItemPlaybackDetailsBinding,
                                     private val context: Context) : ViewHolder(ib.root) {
        fun bind(item: VodPlaybackLeanbackGlue) {
            item.playback?.let {
                ib.vodPlaybackDetailsPrimaryTitle.text = it.title
                ib.vodPlaybackDetailsDescriptionText.text = it.description
            }
        }
    }
}
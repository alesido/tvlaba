package org.alsi.android.tvlaba.tv.vod.digest

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.presentationvod.model.VodDigestLiveData
import org.alsi.android.tvlaba.databinding.VodItemDescriptionExtendedBinding
import kotlin.math.roundToInt

class VodItemDescriptionPresenter(val context: Context): Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val itemBinding = VodItemDescriptionExtendedBinding.inflate(LayoutInflater.from(context))
        return VodItemDescriptionViewHolder(itemBinding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?)
    = (viewHolder as VodItemDescriptionViewHolder).bind(item as VodDigestLiveData)

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) { /** not applicable here **/ }

    inner class VodItemDescriptionViewHolder(private val ib: VodItemDescriptionExtendedBinding)
    : ViewHolder(ib.root) {
        fun bind(liveData: VodDigestLiveData) {
            liveData.details?.let {

                // title
                ib.vodItemTitle.text = it.title

                // description
                ib.vodItemDescriptionText.text = buildBodyText(it)

                // top banner: age limitation
                if (it.attributes?.ageLimit != null && it.attributes?.ageLimit!! > 0) {
                    ib.vodItemBannerAgeLimitation.visibility = VISIBLE
                    ib.vodItemBannerAgeLimitation.text = String.format("%d+",
                        it.attributes?.ageLimit.toString())
                }
                else {
                    ib.vodItemBannerAgeLimitation.visibility = GONE
                }

                // top banner: rates
                if (it.attributes?.kinopoiskRate != null) {
                    ib.vodItemBannerKinopoiskLogo.visibility = VISIBLE
                    bindRate(ib.vodItemBannerKinopoiskRate,
                        ib.vodItemBannerKinopoiskLogo, it.attributes?.kinopoiskRate)
                }
                else {
                    ib.vodItemBannerKinopoiskLogo.visibility = GONE
                }
                if (it.attributes?.imdbRate != null) {
                    ib.vodItemBannerImdbLogo.visibility = VISIBLE
                    bindRate(ib.vodItemBannerImdbRate,
                        ib.vodItemBannerImdbLogo, it.attributes?.imdbRate)
                }
                else {
                    ib.vodItemBannerImdbLogo.visibility = GONE
                }
            }
        }
    }

    private fun buildBodyText(item: VodListingItem?): String? {
        item?: return null
        val parts: MutableList<String> = mutableListOf()
        with(parts) {
            item.description?.let { add(it)}
            buildCompactDigestExtract(item)?.let { add(it) }
        }
        return parts.joinToString ("\n\n")
    }

    private fun buildCompactDigestExtract(item: VodListingItem?): String? {
        item?.attributes?: return null
        val parts: MutableList<String> = mutableListOf()
        with(item.attributes!!) {
            year?.let { parts.add(it) }
            country?.let { parts.add(it) }
            genres?.let { genres ->
                parts.add(genres.map{ it.title }.joinToString (", "))
            }
        }
        return parts.joinToString ("\n\n")
    }

    private fun bindRate(textView: TextView, logoView: ImageView, rate: Float?) {
        if (rate != null && rate > 0) {
            textView.text = rate.roundToInt().toString()
            logoView.visibility = VISIBLE; textView.visibility = VISIBLE
        } else {
            textView.visibility = GONE; logoView.visibility = GONE
        }
    }
}
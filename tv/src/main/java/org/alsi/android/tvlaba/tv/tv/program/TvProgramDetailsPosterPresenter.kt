package org.alsi.android.tvlaba.tv.tv.program

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.leanback.widget.DetailsOverviewLogoPresenter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.Presenter
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.TvProgramDetailsSlideShowBinding

/**
 * Alternative, not used version of {@link TvProgramDetailsPosterSimplePresenter}.
 * Created, probably, to test DataBinding here and with intent to make logo
 * slide show.
 **/

class TvProgramDetailsPosterPresenter: DetailsOverviewLogoPresenter() {

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val itemBinding = TvProgramDetailsSlideShowBinding
            .inflate(LayoutInflater.from(parent.context))

        val imageView = itemBinding.tvProgramDetailsSlideShowImage
        val res = parent.resources
        val width = res.getDimensionPixelSize(R.dimen.tv_program_detail_poster_width)
        val height = res.getDimensionPixelSize(R.dimen.tv_program_detail_poster_height)
        imageView.layoutParams = MarginLayoutParams(width, height)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: Presenter.ViewHolder, item: Any) {
        (holder as MyViewHolder).bind(item as DetailsOverviewRow, this)
    }

    internal class MyViewHolder(private val ib: TvProgramDetailsSlideShowBinding)
        : DetailsOverviewLogoPresenter.ViewHolder(ib.root) {

        override fun getParentPresenter(): FullWidthDetailsOverviewRowPresenter {
            return mParentPresenter
        }

        override fun getParentViewHolder(): FullWidthDetailsOverviewRowPresenter.ViewHolder {
            return mParentViewHolder
        }

        fun bind(item: DetailsOverviewRow, presenter: TvProgramDetailsPosterPresenter) {
            val imageView = ib.tvProgramDetailsSlideShowImage
            imageView.setImageDrawable(item.imageDrawable)
            if (presenter.isBoundToImage(this, item)) {
                parentPresenter.notifyOnBindLogo(parentViewHolder)
            }
        }
    }
}
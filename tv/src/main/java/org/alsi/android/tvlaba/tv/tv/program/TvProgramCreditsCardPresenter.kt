package org.alsi.android.tvlaba.tv.tv.program

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import org.alsi.android.domain.tv.interactor.guide.TvProgramCreditPicture
import org.alsi.android.tvlaba.R

class TvProgramCreditsCardPresenter: Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup) = ViewHolder(
            TvProgramCreditsCardView(parent.context).apply {
                setBackgroundColor(Color.DKGRAY)
            })

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val creditPicture = item as TvProgramCreditPicture
        val cardView = viewHolder.view as TvProgramCreditsCardView
        val context = cardView.context
        val roleTitles = context.resources.getStringArray(R.array.program_role_title)
        with(creditPicture, {
            val roleText = if (role != null && role!!.ordinal < roleTitles.size)
                roleTitles[role!!.ordinal] else null
            val titleText = if (name != null && roleText != null) "$name, $roleText"
            else if (name != null) name else ""
            cardView.tvProgramCreditsCardText = titleText?: ""
        })
        Glide.with(context).load(creditPicture.uri.toString())
            .into(cardView.vb.tvProgramCreditsCardPoster)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) = Unit
}

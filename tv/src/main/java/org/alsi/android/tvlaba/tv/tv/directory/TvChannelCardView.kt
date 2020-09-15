package org.alsi.android.tvlaba.tv.tv.directory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.tv_channel_card_view.view.*
import org.alsi.android.tvlaba.R

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class TvChannelCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    var channelTitleText: String = ""
        set(value) {
            field = value
            tvChannelCardChannelTitle.text = value
        }

    var programTitleText: String = ""
        set(value) {
            field = value
            tvChannelCardProgramTitle.text = value
        }

    fun showIsActual(actual: Boolean) {
        tvChannelCardFooterBlur.visibility = if (actual) GONE else VISIBLE
    }

    init {
        View.inflate(context, R.layout.tv_channel_card_view, this)
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
package org.alsi.android.tvlaba.tv.tv.directory

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.alsi.android.tvlaba.databinding.TvChannelCardViewBinding

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class TvChannelCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    val vb = TvChannelCardViewBinding.inflate(LayoutInflater.from(context),
        this, true)

    var channelTitleText: String = ""
        set(value) {
            field = value
            vb.tvChannelCardChannelTitle.text = value
        }

    var programTitleText: String = ""
        set(value) {
            field = value
            vb.tvChannelCardProgramTitle.text = value
        }

    fun showIsActual(actual: Boolean) {
        vb.tvChannelCardFooterBlur.visibility = if (actual) GONE else VISIBLE
    }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
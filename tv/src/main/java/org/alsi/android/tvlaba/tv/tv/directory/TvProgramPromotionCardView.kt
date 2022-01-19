package org.alsi.android.tvlaba.tv.tv.directory

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.alsi.android.tvlaba.databinding.TvProgramPromotionCardViewBinding

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class TvProgramPromotionCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    val vb = TvProgramPromotionCardViewBinding.inflate(
        LayoutInflater.from(context),
        this, true)


    var programTimeText: String = ""
        set(value) {
            field = value
            vb.tvProgramCardTitle.text = value
        }

    var channelTitleText: String = ""
        set(value) {
            field = value
            vb.tvProgramChannelTitle.text = value
        }

    var programTitleText: String = ""
        set(value) {
            field = value
            vb.tvProgramCardProgramTitle.text = value
        }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
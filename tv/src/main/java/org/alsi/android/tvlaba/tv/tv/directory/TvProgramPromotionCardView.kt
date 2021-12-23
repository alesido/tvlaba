package org.alsi.android.tvlaba.tv.tv.directory

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.TvProgramCardViewBinding

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class TvProgramPromotionCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    val vb = TvProgramCardViewBinding.inflate(
        LayoutInflater.from(context),
        this, true)


    var programTimeText: String = ""
        set(value) {
            field = value
            vb.tvProgramCardTitle.text = value
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
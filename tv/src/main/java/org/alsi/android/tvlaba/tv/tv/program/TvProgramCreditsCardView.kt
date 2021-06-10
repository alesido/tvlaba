package org.alsi.android.tvlaba.tv.tv.program

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.alsi.android.tvlaba.databinding.TvProgramCreditsCardViewBinding

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class TvProgramCreditsCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    val vb = TvProgramCreditsCardViewBinding.inflate(
        LayoutInflater.from(context),
        this, true)

    var tvProgramCreditsCardText: String = ""
        set(value) {
            field = value
            vb.tvProgramCreditsCardTitle.text = value
        }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
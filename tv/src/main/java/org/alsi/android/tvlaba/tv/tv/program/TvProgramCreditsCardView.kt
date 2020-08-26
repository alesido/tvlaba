package org.alsi.android.tvlaba.tv.tv.program

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.tv_program_credits_card_view.view.*
import org.alsi.android.tvlaba.R

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class TvProgramCreditsCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    var tvProgramCreditsCardText: String = ""
        set(value) {
            field = value
            tvProgramCreditsCardTitle.text = value
        }

    init {
        View.inflate(context, R.layout.tv_program_credits_card_view, this)
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
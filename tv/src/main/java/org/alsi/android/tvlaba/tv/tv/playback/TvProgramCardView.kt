package org.alsi.android.tvlaba.tv.tv.playback

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.tv_program_card_view.view.*
import org.alsi.android.tvlaba.R

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class TvProgramCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    var programTimeText: String = ""
        set(value) {
            field = value
            tvProgramCardTitle.text = value
        }

    var programTitleText: String = ""
        set(value) {
            field = value
            tvProgramCardProgramTitle.text = value
        }

    init {
        View.inflate(context, R.layout.tv_program_card_view, this)
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
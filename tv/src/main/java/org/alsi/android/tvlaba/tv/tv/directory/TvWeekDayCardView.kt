package org.alsi.android.tvlaba.tv.tv.directory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.tv_program_card_view.view.*
import kotlinx.android.synthetic.main.tv_week_day_card_view.view.*
import org.alsi.android.tvlaba.R

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class TvWeekDayCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    var monthDayText: String = ""
        set(value) {
            field = value
            tvWeekDayMonthDay.text = value
        }

    var weekDayText: String = ""
        set(value) {
            field = value
            tvWeekDayWeekDay.text = value
        }

    init {
        View.inflate(context, R.layout.tv_week_day_card_view, this)
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
package org.alsi.android.tvlaba.tv.tv.weekdays

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.alsi.android.tvlaba.databinding.TvWeekDayCardViewBinding

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class TvWeekDayCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    private val vb = TvWeekDayCardViewBinding.inflate(LayoutInflater.from(context),
        this, true)

    var monthDayText: String = ""
        set(value) {
            field = value
            vb.tvWeekDayMonthDay.text = value
        }

    var weekDayText: String = ""
        set(value) {
            field = value
            vb.tvWeekDayWeekDay.text = value
        }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
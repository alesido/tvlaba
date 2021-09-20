package org.alsi.android.tvlaba.tv.vod.directory

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.alsi.android.tvlaba.databinding.VodMenuCardViewBinding

/**
 * @see "https://skillbox.ru/media/code/razrabotka_pod_android_tv_part2/"
 */
class VodMenuCardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    val vb = VodMenuCardViewBinding.inflate(LayoutInflater.from(context),
        this, true)

    var menuTitleText: String = ""
        set(value) {
            field = value
            vb.tvMenuCardTitle.text = value
        }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
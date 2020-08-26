package org.alsi.android.tvlaba.tv.tv.program

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import org.alsi.android.tvlaba.R

class VideoPoster4x3CardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.video_poster_card_4x3, this)
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
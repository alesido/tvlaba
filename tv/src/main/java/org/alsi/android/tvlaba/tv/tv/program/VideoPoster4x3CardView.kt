package org.alsi.android.tvlaba.tv.tv.program

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.alsi.android.tvlaba.databinding.VideoPosterCard4x3Binding

class VideoPoster4x3CardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    val vb = VideoPosterCard4x3Binding.inflate(LayoutInflater.from(context))

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }
}
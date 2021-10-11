package org.alsi.android.tvlaba.framework

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide
import org.alsi.android.domain.implementation.model.LocalRasterImageReference
import org.alsi.android.domain.implementation.model.LocalVectorImageReference
import org.alsi.android.domain.implementation.model.RemoteRasterImageReference
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.presentation.mapper.getDrawableIdentifierByName

fun StreamingServicePresentation.setImageToView(view: ImageView): Boolean {
    logo ?: return false
    val activity = view.context as Activity
    if (activity.isFinishing || activity.isDestroyed) return false
    when (logo) {
        is LocalRasterImageReference -> {
            getDrawableIdentifierByName(activity,
                (logo as LocalRasterImageReference).reference)?.let { drawableResId ->
                    view.setImageResource(drawableResId)
                }
        }
        is LocalVectorImageReference -> {
            getDrawableIdentifierByName(activity,
                (logo as LocalVectorImageReference).reference)?.let { drawableResId ->
                    view.setImageResource(drawableResId)
                }
        }
        is RemoteRasterImageReference -> {
            Glide.with(activity)
                .load((logo as RemoteRasterImageReference).reference)
                .into(view)
        }
    }
    return true
}


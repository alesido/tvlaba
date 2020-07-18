package org.alsi.android.presentation.mapper

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

/**
 * Created on 7/6/18.
 */
fun getDrawableByName(reference: String) : Drawable? {
    val resources = Resources.getSystem()
    val drawableId = resources.getIdentifier(reference, "drawable", null)
    return ResourcesCompat.getDrawable(resources, drawableId, null)
}

fun getDrawableIdentifierByName(reference: String) : Int? {
    val resources = Resources.getSystem()
    return resources.getIdentifier(reference, "drawable", null)
}


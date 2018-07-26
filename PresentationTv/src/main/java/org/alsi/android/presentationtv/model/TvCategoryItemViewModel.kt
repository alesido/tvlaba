package org.alsi.android.presentationtv.model

import android.net.Uri
import android.support.annotation.DrawableRes

/**
 * Created on 7/5/18.
 */
class TvCategoryItemViewModel(val id: Long, val title: String) {

    @DrawableRes var logoDrawableRes: Int? = null
    var logoRasterUri: Uri? = null

    constructor(id: Long, title: String, @DrawableRes aLogoDrawableRes: Int): this(id, title) {
            logoDrawableRes = aLogoDrawableRes
    }

    constructor(id: Long, title: String, aLogoRasterUri: Uri): this(id, title) {
        logoRasterUri = aLogoRasterUri
    }
}
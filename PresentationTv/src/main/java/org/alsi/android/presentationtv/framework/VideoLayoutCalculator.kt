package org.alsi.android.presentationtv.framework

import android.content.Context
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio.*

class VideoLayoutCalculator(

        context: Context,

        /** Source video width as it defined in the stream */
        sourceWidth: Int,

        /** Source video height as it defined in the stream */
        sourceHeight: Int
) {

    private val source = Size(sourceWidth, sourceHeight)

    private var screen: Size

    init {
        val dm = context.resources.displayMetrics
        screen = Size(dm.widthPixels, dm.heightPixels)
    }

    fun calculate(aspectRatio: VideoAspectRatio): Size {
       if (screen.isEmpty() || source.isEmpty()) return Size.empty()
       return when (aspectRatio) {
           ASPECT_FILL_SCREEN -> screen
           ASPECT_ORIGINAL -> source
           ASPECT_FILL_HEIGHT -> Size(
                   width = (source.width.toDouble() *
                           (screen.height.toDouble() / source.height.toDouble())).toInt(),
                   height = screen.height
           )
           ASPECT_FILL_WIDTH -> Size(
                   width = screen.width,
                   height = (source.height.toDouble() *
                           (screen.width.toDouble() / source.width.toDouble())).toInt()
           )
           ASPECT_16_9 -> byProportion(16.0, 9.0)
           ASPECT_4_3 -> byProportion(4.0, 3.0)
           ASPECT_ADAPT -> byProportion(source.width.toDouble(), source.height.toDouble())
           ASPECT_SCALE -> Size(
                   width = (screen.width.toDouble() * 1.33).toInt(),
                   height = (screen.height.toDouble() * 1.33).toInt()
           )
       }
   }

    private fun byProportion(x: Double, y: Double): Size {
        var h = (y * screen.width.toDouble() / x).toInt()
        val w = if (h <= screen.height) screen.width else {
            h = screen.height
            (x * h.toDouble() / y).toInt()
        }
        return Size(w, h)
    }
}

class Size(var width: Int, var height: Int) {
    fun isEmpty() = width == 0 || height == 0
    companion object {
        fun empty() = Size(0, 0)
    }
}
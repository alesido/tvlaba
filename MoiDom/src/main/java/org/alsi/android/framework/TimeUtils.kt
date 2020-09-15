package org.alsi.android.framework

/**
 * Formats time in milliseconds to hh:mm:ss string format.
 */
fun formatMillis(srcMillis: Int): String? {
    var millis = srcMillis
    var result: String? = ""
    val hr = millis / 3600000
    millis %= 3600000
    val min = millis / 60000
    millis %= 60000
    val sec = millis / 1000
    if (hr > 0) {
        result += "$hr:"
    }
    if (min >= 0) {
        result += if (min > 9) {
            "$min:"
        } else {
            "0$min:"
        }
    }
    if (sec > 9) {
        result += sec
    } else {
        result += "0$sec"
    }
    return result
}

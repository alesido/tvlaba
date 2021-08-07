package org.alsi.android.tvlaba.framework

import android.app.Instrumentation
import android.view.KeyEvent

class EventKeyHelper(private val instrumentation: Instrumentation) {
    /** ... simply a sequence of DPAD key UP down presses without a release
     * followed by a single release
     */
    fun seekForwardFaster(steps: Int) {
        for (i in 0 until steps) {
            instrumentation.sendKeySync(KEY_FASTER_FORWARD_MOVE)
        }
        instrumentation.sendKeySync(KEY_FASTER_FORWARD_STOP)
    }

    /** ... simply a sequence of DPAD key DOWN down presses without a release
     * followed by a single release
     */
    fun seekForward(steps: Int) {
        for (i in 0 until steps) {
            instrumentation.sendKeySync(KEY_FAST_FORWARD_MOVE)
        }
        instrumentation.sendKeySync(KEY_FAST_FORWARD_STOP)
    }

    fun pausePlayback() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER)
        onAfterPress()
    }

    fun dpadCenter() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER)
        onAfterPress()
    }

    fun dpadLeft() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT)
        onAfterPress()
    }

    fun dpadLeft(times: Int) {
        for (i in 0 until times) {
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT)
            onAfterPress()
        }
    }

    fun dpadRight() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT)
    }

    fun dpadRight(times: Int) {
        for (i in 0 until times) {
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT)
            onAfterPress()
        }
    }

    fun dpadUp() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP)
        onAfterPress()
    }

    fun dpadUp(times: Int) {
        for (i in 0 until times) {
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP)
            onAfterPress()
        }
    }

    fun dpadDown() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN)
        onAfterPress()
    }

    fun dpadDown(times: Int) {
        for (i in 0 until times) {
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN)
            onAfterPress()
        }
    }

    fun back() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        onAfterPress()
    }

    private fun onAfterPress() {
        try { Thread.sleep(3000); } catch (ignored: InterruptedException) {}
    }


    companion object {
        //
        // -- Player related key events
        //
        val KEY_FASTER_FORWARD_MOVE = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP)
        val KEY_FASTER_FORWARD_STOP = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN)
        val KEY_FAST_FORWARD_MOVE = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT)
        val KEY_FAST_FORWARD_STOP = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT)
    }
}
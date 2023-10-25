package br.com.keyboard_utils.keyboard

import android.app.Activity
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver

class KeyboardNewUtils : ViewTreeObserver.OnGlobalLayoutListener {
    var windowContentView: View? = null

    var recordRectHeight: Int = 0
    var density: Float = 0f
    var recordKeyboardHeight = 0f
    var listener: KeyboardHeightListener? = null

    fun registerKeyboardHeightListener(activity: Activity, listener: KeyboardHeightListener) {
        this.listener = listener
        windowContentView = activity.findViewById(android.R.id.content)
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        density = displayMetrics.density
        windowContentView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
    }

    fun unregisterKeyboardHeightListener() {
        windowContentView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        if (windowContentView == null) {
            return
        }
        println("显示 原生 onGlobalLayout")
        val currentViewHeight = windowContentView?.height ?: 0
        val rect = Rect()
        windowContentView?.getWindowVisibleDisplayFrame(rect)

        val rectHeight = rect.height()
        if (recordRectHeight == 0) {
            recordRectHeight = rectHeight
            println("显示 rectHeight=$rectHeight")
        }
        val keyboardHeight: Float = (recordRectHeight - rectHeight).toFloat()

        val currentViewRootHeight = windowContentView?.rootView?.height ?: 0
        val newState: Double = rectHeight / currentViewRootHeight.toDouble()
        val keyboardOpen = newState < 0.85

        val result = "显示 onGlobalLayout " +
                "\nrectHeight=$rectHeight " +
                "\ncurrentViewHeight=$currentViewHeight " +
                "\nrect=$rect " +
                "\ndiff=$keyboardHeight " +
                "\ndiffDpi=${keyboardHeight / density} " +
                "\nkeyboardOpen=$keyboardOpen " +
                "\nnewState=$newState "


        if (keyboardHeight != recordKeyboardHeight) {
            println("显示 result=$result")
            recordKeyboardHeight = keyboardHeight
            val keyboardHeightDp = keyboardHeight / density
            if (keyboardOpen) {
                println("显示 键盘打开 recordKeyboardHeight=$recordKeyboardHeight dpi=$keyboardHeightDp")
                listener?.open(recordKeyboardHeight)
            } else {
                println("显示 键盘关闭")
                listener?.hide()
            }
        }
    }
}
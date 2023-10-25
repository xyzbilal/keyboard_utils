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
        val currentViewRootHeight = windowContentView?.rootView?.height ?: 0

        val diffHeight = currentViewRootHeight - currentViewHeight

        val rect = Rect()
        windowContentView?.getWindowVisibleDisplayFrame(rect)

        var rectHeight = rect.height()
        var rectBottom = rect.bottom;
        println("显示 after rectHeight=$rectHeight rectBottom=$rectBottom")
        val newState: Double = rectHeight / currentViewRootHeight.toDouble()
        val keyboardHeight: Float = currentViewHeight - rectBottom.toFloat()
        val keyboardOpen = newState < 0.85

        val result = "显示 onGlobalLayout " +
                "\nrectHeight=$rectHeight " +
                "\nrectBottom=$rectBottom " +
                "\ncurrentViewHeight=$currentViewHeight" +
                "\nrect=$rect " +
                "\ndiffHeight=${currentViewRootHeight - currentViewHeight} " +
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
                listener?.open(keyboardHeightDp)
            } else {
                println("显示 键盘关闭")
                listener?.hide()
            }
        }
    }
}
package br.com.keyboard_utils.keyboard;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import br.com.keyboard_utils.keyboard.NavigationBarCallback;

/**
 * 软键盘监听操作
 */
public final class KeyboardUtils {
    KeyboardAbove31Utils keyboardAbove31Utils;
    KeyboardBelow31Utils keyboardBelow31Utils;


    /**
     * 注册监听
     */
    public void registerKeyboardHeightListener(final Activity activity, final KeyboardHeightListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (keyboardAbove31Utils == null) {
                keyboardAbove31Utils = new KeyboardAbove31Utils();
                keyboardAbove31Utils.registerKeyboardHeightListener(activity, listener);
            }
        } else {
            if (keyboardBelow31Utils == null) {
                keyboardBelow31Utils = new KeyboardBelow31Utils();
                keyboardBelow31Utils.registerKeyboardHeightListener(activity, listener);
            }
        }
    }


    /**
     * 注销监听
     */
    public void unregisterKeyboardHeightListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (keyboardAbove31Utils != null) {
                keyboardAbove31Utils.unregisterKeyboardHeightListener();
            }
        } else {
            if (keyboardBelow31Utils != null) {
                keyboardBelow31Utils.unregisterKeyboardHeightListener();
            }
        }
    }

}

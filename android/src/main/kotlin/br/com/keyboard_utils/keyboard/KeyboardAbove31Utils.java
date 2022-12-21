package br.com.keyboard_utils.keyboard;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.RequiresApi;
import androidx.core.view.WindowInsetsCompat;

public class KeyboardAbove31Utils {

    private Activity activity;

    private KeyboardHeightListener listener;


    /**
     * 注册监听
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void registerKeyboardHeightListener(final Activity activity, final KeyboardHeightListener listener) {
        if (this.activity != null) return;
        this.activity = activity;
        this.listener = listener;
        invokeAbove31();
    }


    /**
     * 注销监听
     */
    public void unregisterKeyboardHeightListener() {

    }

    /**
     * 软键盘监听
     * 31及以上版本
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void invokeAbove31() {
        activity.getWindow().getDecorView().setOnApplyWindowInsetsListener((v, insets) -> {
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            boolean hasNavigationBar = insets.isVisible(WindowInsetsCompat.Type.navigationBars()) &&
                    insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0;
            int height = hasNavigationBar ? Math.max(imeHeight - navHeight, 0) : imeHeight;
            System.out.println("test 软键盘 31以上 height=" + height);
            if (height == 0) {
                listener.hide();
            } else {
                listener.open(height);
            }
            return insets;
        });
    }
}

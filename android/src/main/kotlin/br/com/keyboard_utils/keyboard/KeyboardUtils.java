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
    private Activity activity;

    // 上一个高度
    private int sDecorViewInvisibleHeightPre;
    // 全局布局监听
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    // 导航高度
    private int mNavHeight;
    // 倒计时
    private CountDownTimer keyboardSessionTimer;
    private int sDecorViewDelta = 0;

    private KeyboardHeightListener listener;


    /**
     * 注册监听
     */
    public void registerKeyboardHeightListener(final Activity activity, final KeyboardHeightListener listener) {
        if (this.activity != null) return;
        this.activity = activity;
        this.listener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            invokeAbove31();
        } else {
            invokeBelow31();
        }
    }


    /**
     * 注销监听
     */
    public void unregisterKeyboardHeightListener() {
        onGlobalLayoutListener = null;
        View contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        if (contentView == null) return;
        contentView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    /**
     * 软键盘监听
     * 31及以上版本
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void invokeAbove31() {
        activity.getWindow().getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
                int navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                boolean hasNavigationBar = insets.isVisible(WindowInsetsCompat.Type.navigationBars()) &&
                        insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0;
                int height = hasNavigationBar ? Math.max(imeHeight - navHeight, 0) : imeHeight;
                if (height == 0) {
                    listener.hide();
                } else {
                    listener.open(height);
                }
                return insets;
            }
        });
    }

    /**
     * 软键盘监听
     * 31以下版本
     */
    private void invokeBelow31() {
        final int flags = activity.getWindow().getAttributes().flags;
        if ((flags & WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        final FrameLayout contentView = activity.findViewById(android.R.id.content);
        sDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight();

        onGlobalLayoutListener = () -> {
            int height = getDecorViewInvisibleHeight();
            if (sDecorViewInvisibleHeightPre != height) {
                handleKeyboardHeightByBelow31Changed();
                sDecorViewInvisibleHeightPre = height;
            }
        };

        //获取到导航栏高度之后再添加布局监听
        getNavigationBarHeight((height, hasNav) -> {
            mNavHeight = height;
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        });

    }


    /**
     * 软键盘高度变化
     * 31以下版本
     */
    public void handleKeyboardHeightByBelow31Changed() {
        if (keyboardSessionTimer != null) return;
        keyboardSessionTimer = new CountDownTimer(150, 1) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                int height = getDecorViewInvisibleHeight();
                if (height == 0) {
                    listener.hide();
                } else {
                    listener.open(height);
                }
                keyboardSessionTimer.cancel();
                keyboardSessionTimer = null;

            }
        };
        keyboardSessionTimer.start();
    }

    private int getDecorViewInvisibleHeight() {
        final View decorView = activity.getWindow().getDecorView();
        if (decorView == null) return sDecorViewInvisibleHeightPre;
        final Rect outRect = new Rect();
        decorView.getWindowVisibleDisplayFrame(outRect);

        int delta = Math.abs(decorView.getBottom() - outRect.bottom);
        if (delta <= mNavHeight) {
            sDecorViewDelta = delta;
            return 0;
        }
        return delta - sDecorViewDelta;
    }


    /**
     * 获取底部导航栏高度
     */
    private int getNavBarHeight() {
        Resources res = Resources.getSystem();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    /**
     * 获取导航栏高度
     */
    public void getNavigationBarHeight(NavigationBarCallback callback) {
        View view = activity.getWindow().getDecorView();
        boolean attachedToWindow = view.isAttachedToWindow();

        if (attachedToWindow) {
            WindowInsetsCompat windowInsets = ViewCompat.getRootWindowInsets(view);
            assert windowInsets != null;
            int height = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            boolean hasNavigationBar = windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars()) &&
                    windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0;
            if (height > 0) {
                callback.onHeight(height, hasNavigationBar);
            } else {
                callback.onHeight(getNavBarHeight(), hasNavigationBar);
            }

        } else {
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    WindowInsetsCompat windowInsets = ViewCompat.getRootWindowInsets(v);
                    assert windowInsets != null;
                    int height = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

                    boolean hasNavigationBar = windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars()) &&
                            windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0;

                    if (height > 0) {
                        callback.onHeight(height, hasNavigationBar);
                    } else {
                        callback.onHeight(getNavBarHeight(), hasNavigationBar);
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                }
            });
        }
    }


}

package android.src.main.kotlin.br.com.keyboard_utils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
public class Utils {

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void test(Activity activity) {
        activity.getWindow().getDecorView().addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {

            System.out.println("test 软键盘 31以上 " +
                    "left=" + left +
                    " top=" + top +
                    " right=" + right +
                    " bottom=" + bottom +
                    " oldLeft=" + oldLeft +
                    " oldTop=" + oldTop +
                    " oldRight=" + oldRight +
                    " oldBottom=" + oldBottom);
        });
//        activity.getWindow().getDecorView().setOnApplyWindowInsetsListener((v, insets) -> {
//            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
//            int navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
//            boolean hasNavigationBar = insets.isVisible(WindowInsetsCompat.Type.navigationBars()) &&
//                    insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom > 0;
//            int height = hasNavigationBar ? Math.max(imeHeight - navHeight, 0) : imeHeight;
//            System.out.println("test 软键盘 31以上 height=" + height);
//            return insets;
//        });
    }

    public int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Status height:" + height);
        return height;
    }

    public int getStatusBarHeight2(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object object = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(object);
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
        return height;
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }
}

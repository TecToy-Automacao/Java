package br.com.tectoy.tectoysunmi.activity;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.Display;

/**
 * Created by highsixty on 2018/3/7.
 * mail  gaolulin@sunmi.com
 */

public class ScreenManager {

    private final String TAG = ScreenManager.class.getSimpleName();
    public static ScreenManager manager = null;
    private Display[] displays = null;//屏幕数组
    boolean isMinScreen;//小屏

    private ScreenManager() {
    }

    public static ScreenManager getInstance() {
        if (null == manager) {
            synchronized (ScreenManager.class) {
                if (null == manager) {
                    manager = new ScreenManager();
                }
            }
        }
        return manager;
    }

    public void init(Context context) {
        DisplayManager mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        displays = mDisplayManager.getDisplays();
        Log.d(TAG, "init: ----------->" + displays.length);

        if (displays.length > 1) {
            Rect outSize0 = new Rect();
            displays[0].getRectSize(outSize0);

            Rect outSize1 = new Rect();
            displays[1].getRectSize(outSize1);

            if (outSize0.right - outSize1.right > 100) {
                //是小屏
                isMinScreen = true;
            }
        }

    }

    public boolean isMinScreen() {
        return isMinScreen;
    }

    public Display[] getDisplays() {
        return displays;
    }

    public Display getPresentationDisplays() {
        for (int i = 0; i < displays.length; i++) {
            Log.e(TAG, "屏幕" + displays[i]);
            if ((displays[i].getFlags() & Display.FLAG_SECURE) != 0
                    && (displays[i].getFlags() & Display.FLAG_SUPPORTS_PROTECTED_BUFFERS) != 0
                    && (displays[i].getFlags() & Display.FLAG_PRESENTATION) != 0) {
                Log.e(TAG, "第一个真实存在的副屏屏幕" + displays[i]);
                return displays[i];
            }
        }
        return null;

    }
}

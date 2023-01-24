package com.tectoy.play;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.Display;

public class ScreenManager {
    private final String TAG = ScreenManager.class.getSimpleName();
    public static ScreenManager manager = null;
    private Display[] displays = null;
    boolean isMinScreen;

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
        Log.d("Display", "init: ----------->" + displays.length);

        if (displays.length > 1) {
            Rect outSize0 = new Rect();
            displays[0].getRectSize(outSize0);

            Rect outSize1 = new Rect();
            displays[1].getRectSize(outSize1);

            if (outSize0.right - outSize1.right > 100) {
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
            if ((displays[i].getFlags() & Display.FLAG_SECURE) != 0
                    && (displays[i].getFlags() & Display.FLAG_SUPPORTS_PROTECTED_BUFFERS) != 0
                    && (displays[i].getFlags() & Display.FLAG_PRESENTATION) != 0) {
                return displays[i];
            }
        }
        return null;

    }
}

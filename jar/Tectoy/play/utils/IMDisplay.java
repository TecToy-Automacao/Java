package com.tectoy.play.utils;

import android.view.SurfaceHolder;
import android.view.View;

public interface IMDisplay extends IMPlayListener {

    View getDisplayView();
    SurfaceHolder getHolder();

}


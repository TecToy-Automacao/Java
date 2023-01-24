package com.tectoy.play;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.graphics.drawable.ColorDrawable;

import com.tectoy.play.utils.IMPlayListener;
import com.tectoy.play.utils.IMPlayer;
import com.tectoy.play.utils.MPlayer;
import com.tectoy.play.utils.MPlayerException;
import com.tectoy.play.utils.MinimalDisplay;

import java.io.File;

public class PlayVideo extends BasePresentation{

    private SurfaceView mPlayerView;
    private MPlayer player;
    private final String TAG = "SUNMI";
    private String path;
    private Context myContext;
    private Activity activity;
    public static int position = 0;
    private int videoX  = 0;
    private int videoY  = 0;
    private int videoW  = 0;
    private int videoH  = 0;

    public PlayVideo(Context context, Display display) {
        super(context, display);
        activity  = (Activity) context;
        Window window = activity.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        myContext = context;
    }

    public void setPath(String path){
        this.path = path;
        File file = new File(path);
    }

    public void play(){
        try {
            player.setBounds(videoX, videoY, videoW, videoH);
            player.setSource(path, 0);
            player.play();
        } catch (MPlayerException e) {}
    }

    public void pause(){
        player.pause();
    }

    public void resume(){
        try {
            position = player.getPosition();
            player.setBounds(videoX, videoY, videoW, videoH);
            player.setSource(path, position);
            player.onResume();
        } catch (MPlayerException e) {}
    }

    public void setBounds(int x, int y, int w, int h) {
        videoX = x;
        videoY = y;
        videoW = w;
        videoH = h;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlayerView = new SurfaceView(myContext);
        setContentView(mPlayerView);

        player = new MPlayer();
        player.setView(mPlayerView);
        player.setDisplay(new MinimalDisplay(mPlayerView));
        player.setPlayListener(new IMPlayListener() {
            @Override
            public void onStart(IMPlayer player) {}

            @Override
            public void onPause(IMPlayer player) {}

            @Override
            public void onResume(IMPlayer player) {}

            @Override
            public void onComplete(IMPlayer player) {
                try {
                    player.setSource(path, 0);
                } catch (MPlayerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onSelect(boolean isShow) {
    }

    @Override
    public void onDisplayRemoved() {
        super.onDisplayRemoved();
        player.onDestroy();
    }

}

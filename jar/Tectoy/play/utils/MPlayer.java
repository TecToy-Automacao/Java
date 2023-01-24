package com.tectoy.play.utils;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;


public class MPlayer implements IMPlayer, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener, SurfaceHolder.Callback {

    private MediaPlayer player;
    private String source;
    private IMDisplay display;
    private SurfaceView myView;
    private boolean isVideoSizeMeasured = false;
    private boolean isMediaPrepared = false;
    private boolean isSurfaceCreated = false;
    private boolean isResumed = false;
    private boolean mIsCrop = false;
    private boolean isSeekComplete = false;
    private IMPlayListener mPlayListener;
    private int currentVideoWidth;
    private int currentVideoHeight;
    private int positon = 0;
    private int videoX  = 0;
    private int videoY  = 0;
    private int videoW  = 0;
    private int videoH  = 0;


    public void setView(SurfaceView view){
        myView = view;
    }

    private void createPlayerIfNeed() {
        if (null == player) {
            player = new MediaPlayer();
            player.setScreenOnWhilePlaying(true);
            player.setOnBufferingUpdateListener(this);
            player.setOnVideoSizeChangedListener(this);
            player.setOnCompletionListener(this);
            player.setOnPreparedListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnErrorListener(this);
        }
    }

    private void playStart() {
        if (isVideoSizeMeasured && isMediaPrepared && isSurfaceCreated && isResumed) {
            player.setDisplay(display.getHolder());
            player.start();
            display.onStart(this);
            if (mPlayListener != null) {
                mPlayListener.onStart(this);
            }
        }
    }

    private void playPause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            display.onPause(this);
            if (mPlayListener != null) {
                mPlayListener.onPause(this);
            }
        }
    }


    private boolean checkPlay() {
        if (source == null || source.length() == 0) {
            return false;
        }
        return true;
    }

    public void setPlayListener(IMPlayListener listener) {
        this.mPlayListener = listener;
    }

    public void setCrop(boolean isCrop) {
        this.mIsCrop = isCrop;
        if (display != null && currentVideoWidth > 0 && currentVideoHeight > 0) {
            tryResetSurfaceSize(myView, currentVideoWidth, currentVideoHeight);
        }
    }

    public boolean isCrop() {
        return mIsCrop;
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public void setBounds(int x, int y, int w, int h) {
    videoX = x;
    videoY = y;
    videoW = w;
    videoH = h;
    }

    private void tryResetSurfaceSize(final View view, int videoWidth, int videoHeight) {
        ViewGroup parent = (ViewGroup) view.getParent();

//      Aqui definir a janela de exibição

        if (videoX == 0 && videoY == 0 && videoW == 0 && videoH == 0) {
            int width  = parent.getWidth();
            int height = parent.getHeight();
            if (width > 0 && height > 0) {
                final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                if (mIsCrop) {
                    float scaleVideo = videoWidth / (float) videoHeight;
                    float scaleSurface = width / (float) height;
                    if (scaleVideo < scaleSurface) {
                        params.width = width;
                        params.height = (int) (width / scaleVideo);
                        params.setMargins(0, (height - params.height) / 2, 0, (height - params.height) / 2);
                    } else {
                        params.height = height;
                        params.width = (int) (height * scaleVideo);
                        params.setMargins((width - params.width) / 2, 0, (width - params.width) / 2, 0);
                    }
                } else {
                    if (videoWidth > width || videoHeight > height) {
                        float scaleVideo = videoWidth / (float) videoHeight;
                        float scaleSurface = width / height;
                        if (scaleVideo > scaleSurface) {
                            params.width = width;
                            params.height = (int) (width / scaleVideo);
                            params.setMargins(100, (height - params.height) / 2, 100, (height - params.height) / 2);
                        } else {
                            params.height = height;
                            params.width = (int) (height * scaleVideo);
                            params.setMargins((width - params.width) / 2, 0, (width - params.width) / 2, 0);
                        }
                    }
                }
                view.setLayoutParams(params);
            }
        }
    else {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        float Height = 0;
        if (videoH == 0) {
           Height = parent.getHeight() - (videoY + (videoW / (videoWidth / (float) videoHeight)));
           }
        else {
           Height = parent.getHeight() - (videoY + videoH);
           }
        
        params.setMargins(videoX, videoY, (parent.getWidth() - (videoX + videoW)), (int) Height);
        view.setLayoutParams(params);
        }


    }

    @Override
    public void setSource(String url, int position) throws MPlayerException {
        this.source = url;
        this.positon = position;
        if (!checkPlay()) {
            throw new MPlayerException("Please setSource");
        }
        createPlayerIfNeed();
        isMediaPrepared = false;
        isVideoSizeMeasured = false;
        currentVideoWidth = 0;
        currentVideoHeight = 0;
        player.reset();
        try {
            player.setDataSource(url);
            player.prepareAsync();
            player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        } catch (IOException e) {
            throw new MPlayerException("set source error", e);
        }
    }


    @Override
    public void setDisplay(IMDisplay display) {
        if (this.display != null && this.display.getHolder() != null) {
            this.display.getHolder().removeCallback(this);
        }
        this.display = display;
        this.display.getHolder().addCallback(this);
    }

    @Override
    public void play() {
        isResumed = true;
        this.positon = 0;
        playStart();
    }

    @Override
    public void pause() {
        playPause();
    }

    @Override
    public void onPause() {
        isResumed = false;
        playPause();
    }

    @Override
    public void onResume() {
        isResumed = true;
        playStart();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        display.onComplete(this);
        if (mPlayListener != null) {
            mPlayListener.onComplete(this);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("FILE", "onPrepared: -------------->");
        isMediaPrepared = true;
        player.seekTo(positon);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (width > 0 && height > 0) {
            this.currentVideoWidth = width;
            this.currentVideoHeight = height;
            tryResetSurfaceSize(myView, width, height);
            isVideoSizeMeasured = true;
            playStart();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        isSeekComplete = true;
        playStart();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (display != null && holder == display.getHolder()) {
            isSurfaceCreated = true;
            if (TextUtils.isEmpty(source)) {
                return;
            }
            try {
                setSource(source, positon);
            } catch (MPlayerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        log("surface");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (display != null && holder == display.getHolder()) {
            isSurfaceCreated = false;
            positon = player.getCurrentPosition();
            player.stop();
            player.release();
            player = null;
        }
    }

    private void log(String content) {
        Log.e("MPlayer", content);
    }

    public int getPosition() {
        if (player != null) {
            positon = player.getCurrentPosition();
            return positon;
        }
        return 0;
    }
}

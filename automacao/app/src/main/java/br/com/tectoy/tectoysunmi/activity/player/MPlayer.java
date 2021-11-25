
package br.com.tectoy.tectoysunmi.activity.player;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;

/**
 * Description:
 */
public class MPlayer implements IMPlayer, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener, SurfaceHolder.Callback {

    private MediaPlayer player;
    private String source;
    private IMDisplay display;
    private boolean isVideoSizeMeasured = false;  //视频宽高是否已获取，且不为0
    private boolean isMediaPrepared = false;      //视频资源是否准备完成
    private boolean isSurfaceCreated = false;     //Surface是否被创建
    private boolean isResumed = false;            //是否在Resume状态
    private boolean mIsCrop = false;
    private boolean isSeekComplete = false;// 是否定位完成
    private IMPlayListener mPlayListener;
    private int currentVideoWidth;              //当前视频宽度
    private int currentVideoHeight;             //当前视频高度
    private int positon = 0;


    /**
     * 检查是否需要重新创建资源
     */
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

    /**
     * 开始播放
     */
    private void playStart() {
        if (isVideoSizeMeasured && isMediaPrepared && isSurfaceCreated && isResumed) {
            player.setDisplay(display.getHolder());
            player.start();
//            player.setLooping(true);
            display.onStart(this);
            if (mPlayListener != null) {
                mPlayListener.onStart(this);
            }
        }
    }

    /**
     * 暂停播放
     */
    private void playPause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            display.onPause(this);
            if (mPlayListener != null) {
                mPlayListener.onPause(this);
            }
        }
    }


    /**
     * 检测资源是否合法
     *
     * @return
     */
    private boolean checkPlay() {
        if (source == null || source.length() == 0) {
            return false;
        }
        return true;
    }

    public void setPlayListener(IMPlayListener listener) {
        this.mPlayListener = listener;
    }

    /**
     * 设置是否裁剪视频，若裁剪，则视频按照DisplayView的父布局大小显示。
     * 若不裁剪，视频居中于DisplayView的父布局显示
     *
     * @param isCrop 是否裁剪视频
     */
    public void setCrop(boolean isCrop) {
        this.mIsCrop = isCrop;
        if (display != null && currentVideoWidth > 0 && currentVideoHeight > 0) {
            tryResetSurfaceSize(display.getDisplayView(), currentVideoWidth, currentVideoHeight);
        }
    }

    public boolean isCrop() {
        return mIsCrop;
    }

    /**
     * 视频状态
     *
     * @return 视频是否正在播放
     */
    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    //根据设置和视频尺寸，调整视频播放区域的大小
    private void tryResetSurfaceSize(final View view, int videoWidth, int videoHeight) {
        ViewGroup parent = (ViewGroup) view.getParent();
        int width = parent.getWidth();
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
                        params.setMargins(0, (height - params.height) / 2, 0, (height - params.height) / 2);
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
        } catch (IOException e) {
            Log.d("SUNMI", "setSource: ------------->" + e.getMessage());
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
    public void play() throws MPlayerException {

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
        log("O tamanho do vídeo foi alterado->" + width + "/" + height);
        if (width > 0 && height > 0) {
            this.currentVideoWidth = width;
            this.currentVideoHeight = height;
            tryResetSurfaceSize(display.getDisplayView(), width, height);
            isVideoSizeMeasured = true;
            playStart();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d("FILE", "onSeekComplete: --------->");
        isSeekComplete = true;
        playStart();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GLL", "surfaceCreated: ----------->" + (player != null));
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
            log("Mudança de tamanho");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        log("surface大小改变");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (display != null && holder == display.getHolder()) {
            log("surface被销毁");
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

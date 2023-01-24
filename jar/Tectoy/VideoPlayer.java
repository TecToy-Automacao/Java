package com.tectoy.play;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.SurfaceView;
import android.view.View;
import android.util.Log;
import android.hardware.display.DisplayManager;
import android.content.Context;

public class VideoPlayer {
    
	private ScreenManager screenManager = ScreenManager.getInstance();
	private PlayVideo FPlay = null;

	public VideoPlayer(Activity activity) {
		screenManager.init(activity);
		Display[] displays = screenManager.getDisplays();
		Display display = screenManager.getPresentationDisplays();
		FPlay = new PlayVideo(activity, display);
		}

	public void setPath(String Path) 	{
		FPlay.setPath(Path);
		}

      public void show() {
		FPlay.show();
            }

      public void hide() {
            FPlay.hide();
            }

	public void play() {
		FPlay.play();
		}

	public void pause() {
		FPlay.pause();
		}

	public void resume() {
		FPlay.resume();
		}

      public void setBounds(int x, int y, int w, int h) {
            FPlay.setBounds(x, y, w, h);
            }

}

package com.tectoy.secondDisplay;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.util.Log;
import android.hardware.display.DisplayManager;
import android.content.Context;


public class SecondScreen {
    
	private SecondScreenPresentation SecondPresentation;
	private Display[] displays;
	private Display display;
	private View view;


	public SecondScreen(Activity activity) {
		DisplayManager mDisplayManager = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
          displays = mDisplayManager.getDisplays();
          display = getPresentationDisplays();
		SecondPresentation = new SecondScreenPresentation(activity, display);
		SecondPresentation.show();
		view = SecondPresentation.GetView();
		}


	public View getView()
		{
		return view;
		}


	public int getWidth()
		{
		return view.getWidth();
		}

	public int  getHeight()
		{
		return view.getHeight();
		}
	
	public void setBitmap(Bitmap aBitmap, int aX, int aY) {
		SecondPresentation.setBitmap(aBitmap, aX, aY);
		}



     private Display getPresentationDisplays() {
         for (int i = 0; i < displays.length; i++) {
             if ((displays[i].getFlags() & Display.FLAG_SECURE) != 0
                     && (displays[i].getFlags() & Display.FLAG_SUPPORTS_PROTECTED_BUFFERS) != 0
                     && (displays[i].getFlags() & Display.FLAG_PRESENTATION) != 0) {
                 return displays[i];
             }
         }
         return null;
     }


	public class SecondScreenPresentation extends Presentation {
    
		private myView presentationView;
	
		public SecondScreenPresentation(Context outerContext, Display display) {
			super(outerContext, display);
			presentationView = new myView(outerContext);
			setContentView(presentationView);
			}


     	// Retorna a view utilizada 
 
		public View GetView()
			{
			return this.presentationView;
			}


		// Informa o bitmap a ser exibido na segunda tela

		public void setBitmap(Bitmap aBitmap, int aX, int aY) {
			presentationView.setBitmap(aBitmap, aX, aY);
			}

		private class myView extends View {

			private Bitmap BitmapView;
			private int X, Y;
			private Paint myPaint;

			public myView(Context context) {
				super(context);
				setWillNotDraw(false);
				myPaint = new Paint();
			}

			public void setBitmap(Bitmap aBitmap, int aX, int aY) {
				BitmapView = aBitmap;
				X = aX;
				Y = aY;
				invalidate();
			}

			@Override
		    	protected void onDraw(Canvas canvas) {
				if (BitmapView != null) {
					canvas.drawBitmap(BitmapView, X, Y, myPaint);
				}
			}
		}
	}

}
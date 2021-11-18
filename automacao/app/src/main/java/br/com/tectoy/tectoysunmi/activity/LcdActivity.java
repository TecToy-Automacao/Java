package br.com.tectoy.tectoysunmi.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;


public class LcdActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lcd);
        setMyTitle(R.string.lcd_title);
        setBack();

        //init lcd 、light up lcd and clear screen
        TectoySunmiPrint.getInstance().controlLcd(1);
        TectoySunmiPrint.getInstance().controlLcd(2);
        TectoySunmiPrint.getInstance().controlLcd(4);
    }


    public void text(View view) {
        TectoySunmiPrint.getInstance().sendTextToLcd();
    }

    public void texts(View view) {
        TectoySunmiPrint.getInstance().sendTextsToLcd();
    }

    public void pic(View view) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //No scaling
        options.inScaled = false;

        options.inDensity = getResources().getDisplayMetrics().densityDpi;

        TectoySunmiPrint.getInstance().sendPicToLcd(BitmapFactory.decodeResource(getResources(),
                R.drawable.mini, options));
    }
}

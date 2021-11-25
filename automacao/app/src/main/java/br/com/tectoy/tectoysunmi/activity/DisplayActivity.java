package br.com.tectoy.tectoysunmi.activity;

import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.widget.TextView;

import androidx.annotation.Nullable;

import br.com.tectoy.tectoysunmi.R;

public class DisplayActivity extends BaseActivity{

    TextView btn_display;
    private VideoDisplay videoDisplay = null;
    private ScreenManager screenManager = ScreenManager.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setMyTitle(R.string.lcd_title);
        setBack();
        btn_display = findViewById(R.id.btn_display);
        initview();
    }
    public void initview(){
        screenManager.init(this);
        Display[] displays = screenManager.getDisplays();
        Display display = screenManager.getPresentationDisplays();
        videoDisplay = new VideoDisplay(this, display, Environment.getExternalStorageDirectory().getPath() + "/video_01.mp4");
    }
}

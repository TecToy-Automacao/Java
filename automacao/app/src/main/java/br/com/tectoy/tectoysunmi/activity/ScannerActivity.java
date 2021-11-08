package br.com.tectoy.tectoysunmi.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.com.tectoy.tectoysunmi.R;
public class ScannerActivity extends AppCompatActivity {

    private TextView tvNote;
    private ScrollView scrollView;
    private SunmiScanner sunmiScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scrollView = findViewById(R.id.scrollView);
        tvNote = findViewById(R.id.tv_note);
        initScanner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sunmiScanner != null)
            sunmiScanner.destory();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        int action = event.getAction();

        switch (action){
            case KeyEvent.ACTION_DOWN:
                if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                        || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN
                        || event.getKeyCode() == KeyEvent.KEYCODE_BACK
                        || event.getKeyCode() == KeyEvent.KEYCODE_MENU
                        || event.getKeyCode() == KeyEvent.KEYCODE_HOME
                        || event.getKeyCode() == KeyEvent.KEYCODE_POWER)
                    return super.dispatchKeyEvent(event);
                if (sunmiScanner != null)
                    sunmiScanner.analysisKeyEvent(event);
                return true;

            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    private void initScanner(){
        sunmiScanner = new SunmiScanner(getApplicationContext());
        sunmiScanner.analysisBroadcast();

        sunmiScanner.setScannerListener(new SunmiScanner.OnScannerListener() {
            @Override
            public void onScanData(String data, SunmiScanner.DATA_DISCRIBUTE_TYPE type) {
                append("Tipo de Dado:" + type + "\nCodigo:" + data + "\n");
            }

            @Override
            public void onResponseData(String data, SunmiScanner.DATA_DISCRIBUTE_TYPE type) {
            }

            @Override
            public void onResponseTimeout() {

            }
        });
    }
    private void append(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvNote.append(message);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.smoothScrollTo(0, tvNote.getBottom());
                    }
                });
            }
        });
    }
}

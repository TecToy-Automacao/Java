package br.com.tectoy.tectoysunmi.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sunmi.scanner.IScanInterface;

import br.com.tectoy.tectoysunmi.R;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.ListDialog;

public class ScanActivity extends BaseActivity {

    TextView mBtn, charcter_set_content, prompt_mode_scan, scan_mode_content, scan_trigger_content;
    private  IScanInterface scanInterface;
    private TextView tvNote;
    private ScrollView scrollView;
    private int error_level = 3;
    private SunmiScanner sunmiScanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setMyTitle(R.string.qr_title);
        bindScannerService();
        mBtn = findViewById(R.id.btn_scan);
        scrollView = findViewById(R.id.scrollView);
        tvNote = findViewById(R.id.tv_note);
        charcter_set_content = findViewById(R.id.charcter_set_content);
        prompt_mode_scan = findViewById(R.id.prompt_mode_scan);
        scan_mode_content = findViewById(R.id.scan_mode_content);
        scan_trigger_content = findViewById(R.id.scan_trigger_content);

        findViewById(R.id.charcter_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] el = new String[]{"UTF-8", "GBK", "ISO-8859-1", "SHIFT-JLS", "Compatibility"};
                final ListDialog listDialog = DialogCreater.createListDialog(ScanActivity.this, getResources().getString(R.string.error_qrcode), getResources().getString(R.string.cancel), el);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        charcter_set_content.setText(el[position]);
                        error_level = position;
                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });


        findViewById(R.id.prompt_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] el = new String[]{"Beep+Vibrate", "Beep", "Vibrate"};
                final ListDialog listDialog = DialogCreater.createListDialog(ScanActivity.this, getResources().getString(R.string.error_qrcode), getResources().getString(R.string.cancel), el);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        prompt_mode_scan.setText(el[position]);
                        error_level = position;
                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });

        findViewById(R.id.scan_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] el = new String[]{"Trigger mode", "Continuos mode", "Pulse mode"};
                final ListDialog listDialog = DialogCreater.createListDialog(ScanActivity.this, getResources().getString(R.string.error_qrcode), getResources().getString(R.string.cancel), el);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        scan_mode_content.setText(el[position]);
                        error_level = position;
                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });

        findViewById(R.id.scan_trigger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] el = new String[]{"none", "On-screen Button"};
                final ListDialog listDialog = DialogCreater.createListDialog(ScanActivity.this, getResources().getString(R.string.error_qrcode), getResources().getString(R.string.cancel), el);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        scan_trigger_content.setText(el[position]);
                        error_level = position;
                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    scanInterface.scan();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


        initScanner();

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
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            scanInterface = IScanInterface.Stub.asInterface(service);
            Log.i("setting", "Scanner Service Connected!");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("setting", "Scanner Service Disconnected!");
            scanInterface = null;
        }
    };
    public void bindScannerService() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.scanner");
        intent.setAction("com.sunmi.scanner.IScanInterface");
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }


}

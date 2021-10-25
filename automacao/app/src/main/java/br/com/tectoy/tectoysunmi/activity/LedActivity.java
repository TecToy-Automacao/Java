package br.com.tectoy.tectoysunmi.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.sunmi.statuslampmanager.IStateLamp;

import br.com.tectoy.tectoysunmi.R;

public class LedActivity extends BaseActivity{

    Button  btn_vermelho, btn_desliga;
    private IStateLamp mService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);
        setMyTitle(R.string.function_led);
        setBack();
        // Conectando Serviço de Led
        connectService();

        btn_vermelho = findViewById(R.id.btn_vermelho);
        btn_desliga = findViewById(R.id.btn_desligar);



        btn_vermelho.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mService.closeAllLamp();
                    mService.controlLamp(0, "Led-1");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_desliga.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mService.closeAllLamp();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IStateLamp.Stub.asInterface(service);
            Log.d("darren", "Service Connected.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("darren", "Service Disconnected.");
            mService = null;
        }
    };
    private void connectService() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.statuslampmanager");
        intent.setAction("com.sunmi.statuslamp.service");
        startService(intent);
        bindService(intent, con, BIND_AUTO_CREATE);
    }
}

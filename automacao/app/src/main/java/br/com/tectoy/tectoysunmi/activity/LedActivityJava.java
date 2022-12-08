package br.com.tectoy.tectoysunmi.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.sunmi.statuslampmanager.IStateLamp;

import java.util.HashMap;
import java.util.Map;

import br.com.tectoy.tectoysunmi.R;

public class LedActivityJava extends BaseActivity{

    Button  btn_vermelho, btn_desliga, btn_verde, btn_azul, btn_amarelo, btn_roxo, btn_azulclaro;
    private IStateLamp mService;
    long r = 12;
    public static boolean isK1 = false;
    public static boolean isVertical = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setMyTitle(R.string.function_led);
        setBack();

        // Conectando Serviço de Led
        connectService();

        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// Largura da tela
        int height = dm.heightPixels;// Largura da tela
        isVertical = height > width;
        isK1 = isHaveCamera() && isVertical;

        if (isK1 = true && height > 1856){
            setContentView(R.layout.activity_ledk);
            btn_verde = findViewById(R.id.btn_verde);
            btn_azul = findViewById(R.id.btn_azul);
            btn_amarelo = findViewById(R.id.btn_amarelo);
            btn_roxo = findViewById(R.id.btn_roxo);
            btn_azulclaro = findViewById(R.id.btn_azulclaro);

            btn_azulclaro.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        mService.closeAllLamp();
                        mService.controlLamp(0, "Led-3");
                        mService.controlLampForLoop(0,500000,100,"Led-2");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            btn_amarelo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        mService.closeAllLamp();
                        mService.controlLamp(0, "Led-2");
                        mService.controlLampForLoop(0,50000,100,"Led-1");

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            btn_roxo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        mService.controlLamp(0, "Led-3");
                        mService.controlLampForLoop(0,500000,100,"Led-1");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            btn_azul.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        mService.closeAllLamp();
                        mService.controlLamp(0, "Led-3");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            btn_verde.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        mService.closeAllLamp();
                        mService.controlLamp(0, "Led-2");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            setContentView(R.layout.activity_led);
        }

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
    public boolean isHaveCamera() {
        HashMap<String, UsbDevice> deviceHashMap = ((UsbManager) getSystemService(Activity.USB_SERVICE)).getDeviceList();
        for (Map.Entry entry : deviceHashMap.entrySet()) {
            UsbDevice usbDevice = (UsbDevice) entry.getValue();
            if (!TextUtils.isEmpty(usbDevice.getInterface(0).getName()) && usbDevice.getInterface(0).getName().contains("Orb")) {
                return true;
            }
            if (!TextUtils.isEmpty(usbDevice.getInterface(0).getName()) && usbDevice.getInterface(0).getName().contains("Astra")) {
                return true;
            }
        }
        return false;
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

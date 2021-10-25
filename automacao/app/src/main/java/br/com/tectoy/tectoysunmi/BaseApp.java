package br.com.tectoy.tectoysunmi;

import android.app.Application;

import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    /**
     * Connect print servive through interface library
     */
    private void init(){
        TectoySunmiPrint.getInstance().initSunmiPrinterService(this);
    }
}

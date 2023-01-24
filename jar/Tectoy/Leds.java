package com.tectoy.leds;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import woyou.aidlservice.jiuiv5.IStateLamp;

public class Leds {

	private IStateLamp StateLampService;

	private ServiceConnection connService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			StateLampService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			StateLampService = IStateLamp.Stub.asInterface(service);
		}
	};


	public Leds(Context context) {
		Intent intent = new Intent();
		intent.setPackage("woyou.aidlservice.jiuiv5");
		intent.setAction("woyou.aidlservice.jiuiv5.IStateLamp");
		context.startService(intent);
		context.bindService(intent, connService, Context.BIND_AUTO_CREATE);
	}

	public void controlLamp(int status, java.lang.String lamp) {
	/**
	* Função: Controle uma única luz de alarme
	* Parâmetros:
	* [in]status       0 ligado, 1 desligado
	* [in]lamp         LED, parâmetros:
	*                      "Led-1"
	*                      "Led-2"
	*                      "Led-3"
	*                      "Led-4"
	*                      "Led-5"
	*                      "Led-6"
	* Valor de retorno: Nenhum
	*/
		try {
			StateLampService.controlLamp(status, lamp);
		}
		catch(Exception ex){
			Log.d("Printer", "Error "+ ex.getMessage());
		}
	}


	public void controlLampForLoop(int status, long lightTime, long putoutTime, java.lang.String lamp) {
	/**
	* Função         : controle um único display de ciclo de luz de alarme
	* Parâmetros:
	* [in]status     : status, 0 inicia o loop, 1 interrompe o loop
	* [in]lightTime  : O tempo de ativação da luz do alarme, unidade: milissegundo (ms)
	* [in]putoutTime : Tempo de desligamento da luz do alarme, unidade: milissegundos (ms)
	* [in]lamp       : Led da lâmpada, parâmetros:
	*                      "Led-1"
	*                      "Led-2"
	*                      "Led-3"
	*                      "Led-4"
	*                      "Led-5"
	*                      "Led-6"          * Valor de retorno: Nenhum
	*/
		try {
			StateLampService.controlLampForLoop(status, lightTime, putoutTime, lamp);
		}
		catch(Exception ex){
			Log.d("Printer", "Error "+ ex.getMessage());
		}
	}

	public void closeAllLamp() {
	/**
	* Função: desligar todas as luzes de advertência
	*/
		try {
			StateLampService.closeAllLamp();
		}
		catch(Exception ex){
			Log.d("Printer", "Error "+ ex.getMessage());
		}

	}
}

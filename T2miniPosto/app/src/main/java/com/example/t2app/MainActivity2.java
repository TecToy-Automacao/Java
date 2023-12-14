package com.example.t2app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;
import br.com.itfast.tectoy.TecToyNfcCallback;


public class MainActivity2 extends AppCompatActivity {
    private PendingIntent pendingIntent;
   private boolean NFCIniciado = false;
    String retornoNFC;
    Button btnbutton;
    TecToyNfcCallback nfcCallback = new TecToyNfcCallback() {
        @Override
        public void retornarValor(String strValor) {
            retornoNFC += " | NFC Valor: " + strValor;
            btnbutton.callOnClick();
        }
        @Override
        public void retornarId(String strID) {
            retornoNFC += " | NFC ID: " + strID;
            btnbutton.callOnClick();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        MainActivity.t2m = new TecToy(Dispositivo.T2_MINI, this);
        btnbutton = findViewById(R.id.button_toqueaqui);
        MainActivity.t2m.iniciarNFC(getIntent(), nfcCallback);
        NFCIniciado = true;

        btnbutton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
                NFCIniciado = false;
                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MainActivity.t2m.onNewIntentNFC(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(this.NFCIniciado == true) {
            MainActivity.t2m.onPauseNFC(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.NFCIniciado == true) {
            MainActivity.t2m.onResumeNFC(this, pendingIntent);
        }
    }

}



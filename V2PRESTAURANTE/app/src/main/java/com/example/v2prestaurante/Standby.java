package com.example.v2prestaurante;

import androidx.appcompat.app.AlertDialog;
import  androidx.appcompat.app.AppCompatActivity;


import android.app.PendingIntent;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;
import br.com.itfast.tectoy.TecToyException;
import br.com.itfast.tectoy.TecToyNfcCallback;
import br.com.itfast.tectoy.TecToyScannerCallback;

public class Standby extends AppCompatActivity {
    Button btnImpCracha, btnLoginGarcon;
    TecToy tectoy;
    String strNumMesa;
    String strGarconSelecionadoPassado = "0";
    String strStatusMesa = "STATUS DA MESA : FECHADA";
    TextView mesaSelecionada;
    private PendingIntent pendingIntent;






    TecToyScannerCallback callbackCod = new TecToyScannerCallback() {
        @Override
        public void retornarCodigo(String s) {
            strGarconSelecionadoPassado = s;
        }
    };





    TecToyNfcCallback  callback = new TecToyNfcCallback() {
        @Override
        public void retornarValor(String strValor) {
            strNumMesa = strValor;

            ItemSelecionado();

            Intent intentEnviadora = new Intent(getApplicationContext(), TelaPrincipal.class);
            Bundle parametros = new Bundle();

            parametros.putString("chave_num", strNumMesa);
            parametros.putString("chave_status", strStatusMesa);
            parametros.putString("chave_GarconLogin", strGarconSelecionadoPassado);
            intentEnviadora.putExtras(parametros);

            if (strGarconSelecionadoPassado.equals("0")) {
                AlertDialog.Builder alrtLoginInvalido = new AlertDialog.Builder(Standby.this);
                alrtLoginInvalido.setTitle("ALERTA");
                alrtLoginInvalido.setMessage("Usuário deslogado, clique no botão 'IMPRIMIR CRACHAS' e após isso no botão prato no canto superior direito para ler os cod.barras gerados para logar");

                alrtLoginInvalido.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alrtLoginInvalido.create().show();

        }else{startActivity(intentEnviadora);}

        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

////////////////////////Endereçando//////////////////////////////////////////////////
        mesaSelecionada = findViewById(R.id.mesaSelecionada);
        btnImpCracha = findViewById(R.id.btnImpCracha);
        btnLoginGarcon = findViewById(R.id.btnLoginGarcon);
/////////////////////////////////////////////////////////////////////////////////////

        tectoy = new TecToy(Dispositivo.V2_PRO, Standby.this.getApplicationContext());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        tectoy.iniciarNFC(getIntent(), callback);


        btnImpCracha.setOnClickListener(new View.OnClickListener() { // BOTÃO DE IMPRIMIR CRACHA
            public void onClick(View view) {
                String strCracha;
                strCracha = ""+((char) 0x1B)+((char) 0x61)+((char) 0x01)+((char) 0x1B)+((char) 0x45)+((char) 0x01)+"LOGIN\n"
                              +((char) 0x1B)+((char) 0x45)+((char) 0x30)+"GARCON CHEFE\n"
                              +((char) 0x1D)+((char) 0x6B)+((char) 0x02)+"0000000000109"+((char) 0x00) // adicionar 9 no final
                              +"\n\n\n\n\n"
                              +((char) 0x1B)+((char) 0x61)+((char) 0x01)+((char) 0x1B)+((char) 0x45)+((char) 0x01)+"LOGIN\n"
                              +((char) 0x1B)+((char) 0x45)+((char) 0x30)+"GARCON 2\n"
                              +((char) 0x1D)+((char) 0x6B)+((char) 0x02)+"0000000000116"+((char) 0x00) // adicionar 6 no final
                              +"\n\n\n\n\n"
                              +((char) 0x1B)+((char) 0x61)+((char) 0x01)+((char) 0x1B)+((char) 0x45)+((char) 0x01)+"LOGIN\n"
                              +((char) 0x1B)+((char) 0x45)+((char) 0x30)+"PROPRIETARIO\n"
                              +((char) 0x1D)+((char) 0x6B)+((char) 0x02)+"0000000000017"+((char) 0x00) // adicionar 7 no final
                              +"\n\n\n\n\n"
                              +((char) 0x1B)+((char) 0x61)+((char) 0x01)+((char) 0x1B)+((char) 0x45)+((char) 0x01)+"LOGIN\n"
                              +((char) 0x1B)+((char) 0x45)+((char) 0x30)+"BALCAO\n"
                              +((char) 0x1D)+((char) 0x6B)+((char) 0x02)+"0000000000031"+((char) 0x00) // adicionar 1 no final
                              +"\n\n\n\n\n";
                tectoy.imprimir(strCracha);
            }
        });

        btnLoginGarcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thrTest;
                try {
                    btnImpCracha.setVisibility(View.INVISIBLE);
                    thrTest = new Thread(threadLerScan);
                    thrTest.start();
                    thrTest.join();
                } catch (TecToyException e) {
                    Log.e("TECTOY_V2", e.getMessage());
                } catch (Exception e) {
                    Log.e("TECTOY_V2", e.getMessage());
                }
                btnImpCracha.setVisibility(View.VISIBLE);
            }
        });


    }//onCreate




    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tectoy.onNewIntentNFC(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        tectoy.onPauseNFC(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        tectoy.onResumeNFC(this, pendingIntent);
    }



    private Runnable threadLerScan = new Runnable() {
        @Override
        public void run() {
            try{
                Looper.prepare();
                try{
                    tectoy.iniciarScanner(callbackCod);
                } catch (Exception er) {
                    Log.e("ERRO", er.getMessage() != null ? er.getMessage() : er.toString());
                }
            } catch (Exception er) {
                Log.e("ERRO", er.getMessage() != null ? er.getMessage() : er.toString());
            }


        }
    };



    void ItemSelecionado(){

        if (strNumMesa.equals("1")){
            mesaSelecionada.setText("MESA SELECIONADA : 1");
        }else if (strNumMesa.equals("2")){
            mesaSelecionada.setText("MESA SELECIONADA : 2");
        }
    }


}// Standby
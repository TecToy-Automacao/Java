package br.com.itfast.automacao.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;
import br.com.itfast.tectoy.TecToyException;
import br.com.itfast.tectoy.TecToyNfcCallback;

public class MainActivity extends AppCompatActivity {
    private TextView txtReadedValue;
    private TecToy tectoy;
    private PendingIntent pendingIntent;

    TecToyNfcCallback callback = new TecToyNfcCallback() {
        @Override
        public void retornarValor(String strValor) {
            txtReadedValue.setText(strValor);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CRIA OBJETO TECTOY PASSANDO O DISPOSITIVO UTILIZADO E O CONTEXTO
        tectoy = new TecToy(Dispositivo.V2_PRO, this);
        //CRIA UM PENDINGINTENT Q VAI SER ACIONADO QUANDO O ANDROID GERAR UM EVENTO DO TIPO NFC
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        /*
        INICIA ROTINAS PARA TRABALHAR COM NFC INFORMANDO A INTENT QUE DESEJA E O OBJETO DE CALLBACK DO TIPO TecToyNfcCallback
        QUE VAI RECEBER O RETORNO DA LEITURA DO NFC
        */
        tectoy.iniciarNFC(getIntent(), callback);

        TextView txtValueToWrite = (TextView) findViewById(R.id.txtWriteValue);
        txtReadedValue = (TextView) findViewById(R.id.txtReadedValue);
        Button btnWrite = (Button) findViewById(R.id.btnWriteTag);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    tectoy.escreverNFC(txtValueToWrite.getText().toString());
                    Toast.makeText(MainActivity.this, "Escrita NFC com sucesso", Toast.LENGTH_SHORT).show();
                }catch(TecToyException tex){
                    Toast.makeText(MainActivity.this, tex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

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

}
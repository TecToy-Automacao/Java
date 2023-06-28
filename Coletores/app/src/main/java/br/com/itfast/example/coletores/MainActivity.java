package br.com.itfast.example.coletores;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//imports para consumir IT4R
import org.w3c.dom.Text;

import br.com.daruma.framework.mobile.exception.DarumaException;
import br.com.itfast.tectoy.TecToyNfcCallback;
import br.com.itfast.tectoy.TecToyScannerCallback;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;

public class MainActivity extends AppCompatActivity {

    private TecToy tecToy;
    private PendingIntent pendingIntent;

    //declaracao objetos da interface
    private Button btnAcionarScanner;
    private TextView txtCodLido;
    private TextView txtRetornoNFC;
    private EditText txtGravarNFC;
    private Button btnGravarNFC;

    TecToyNfcCallback callbackNFC = new TecToyNfcCallback() {
        @Override
        public void retornarValor(String strValor) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtRetornoNFC.setText(strValor);
                }
            });
        }
    };

    private TecToyScannerCallback scannerCallback = new TecToyScannerCallback() {
        @Override
        public void retornarCodigo(String strCodigo) {
            tecToy.pararScanner();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtCodLido.setText(strCodigo);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
        }
        if (checkSelfPermission(Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.NFC}, 0);
        }

        //iniciando objetos da interface
        btnAcionarScanner = (Button) findViewById(R.id.btnAcionarScanner);
        txtCodLido = (TextView) findViewById(R.id.txtCodLido);
        txtRetornoNFC = (TextView) findViewById(R.id.txtRetornoNFC);
        txtGravarNFC = (EditText) findViewById(R.id.txtGravarNFC);
        btnGravarNFC = (Button) findViewById(R.id.btnGravarNFC);
        //fim objetos de interface

        tecToy = new TecToy(Dispositivo.L2Ks, this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        tecToy.iniciarNFC(getIntent(), callbackNFC);

        btnAcionarScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thrIniciaScanner;
                try {
                    thrIniciaScanner = new Thread(iniciarScanner);
                    thrIniciaScanner.start();
                    thrIniciaScanner.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        btnGravarNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tecToy.escreverNFC(txtGravarNFC.getText().toString());
                Toast.makeText(MainActivity.this, "Escrita NFC com sucesso", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tecToy.onNewIntentNFC(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        tecToy.onPauseNFC(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        tecToy.onResumeNFC(this, pendingIntent);
    }

    private Runnable iniciarScanner = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                try {
                    tecToy.iniciarScanner(scannerCallback);
                } catch (Exception e) {
                    Log.e("ERRO", e.getMessage() != null ? e.getMessage() : e.toString());
                }

            } catch (DarumaException de) {
                throw de;
            }
        }
    };

}
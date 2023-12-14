package br.com.itfast.automacao.LerEscreverNFC;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.com.itfast.automacao.myapplication.R;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;
import br.com.itfast.tectoy.TecToyNfcCallback;


public class MainActivity extends AppCompatActivity {
    public static TecToy tectoy;
    private PendingIntent pendingIntent;
    private boolean NFCIniciado = false;
    private Button btnIniciarNFC;
    private Button btnEscreverNFC;
    private TextView txtRetorno;
    String retornoNFC;

    TecToyNfcCallback nfcCallbackK2 = new TecToyNfcCallback() {
        @Override
        public void retornarValor(String strValor) {
            retornoNFC = "Conteudo do NFC:" + strValor;
            runOnUiThread(() -> txtRetorno.setText(retornoNFC));
        }
        @Override
        public void retornarId(String s) {

        }
    };
    TecToyNfcCallback nfcCallback = new TecToyNfcCallback() {
        @Override
        public void retornarValor(String strValor) {
            txtRetorno.setText(strValor);
        }

        @Override
        public void retornarId(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.NFC}, 0);
            }
        }

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        EditText editTextoNFC = findViewById(R.id.editTextoNFC);
        btnIniciarNFC = findViewById(R.id.btnIniciarNFC);
        btnEscreverNFC = findViewById(R.id.btnEscreverNFC);
        txtRetorno = findViewById(R.id.txtRetorno);
        Button btnIniciar = findViewById(R.id.btnIniciar);
        Spinner spinnerDispositivos = findViewById(R.id.spinnerDispositivos);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dispositivos, R.layout.spinner_item);
        spinnerDispositivos.setAdapter(adapter);

        btnIniciar.setOnClickListener(v -> {
            switch (spinnerDispositivos.getSelectedItem().toString()) {
                case "T2_MINI":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.T2_MINI, getApplicationContext());
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    editTextoNFC.setVisibility(View.VISIBLE);
                    txtRetorno.setVisibility(View.VISIBLE);
                    break;
                case "D2_MINI":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.D2_MINI, getApplicationContext());
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    editTextoNFC.setVisibility(View.VISIBLE);
                    txtRetorno.setVisibility(View.VISIBLE);
                    break;
                case "V2_PRO":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.V2_PRO, getApplicationContext());
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    editTextoNFC.setVisibility(View.VISIBLE);
                    txtRetorno.setVisibility(View.VISIBLE);
                    break;
                case "K2":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.K2, getApplicationContext());
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    txtRetorno.setVisibility(View.VISIBLE);
                    break;
            }
        });

        btnIniciarNFC.setOnClickListener(v -> {
            String dispositivo = spinnerDispositivos.getSelectedItem().toString();

            if (dispositivo.equals("K2"))
            {
                try{
                    tectoy.iniciarNFC(getIntent(), nfcCallbackK2);
                    NFCIniciado = true;
                    Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
                }catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                try{
                    tectoy.iniciarNFC(getIntent(), nfcCallback);
                    NFCIniciado = true;
                    Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();

                }catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnEscreverNFC.setOnClickListener(v -> {
            try{
                MainActivity.tectoy.escreverNFC(editTextoNFC.getText().toString());
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch(Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tectoy.onNewIntentNFC(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(this.NFCIniciado == true) {
            tectoy.onPauseNFC(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.NFCIniciado == true) {
            tectoy.onResumeNFC(this, pendingIntent);
        }
    }


    private void goneButtons() {
        btnIniciarNFC.setVisibility(View.GONE);
        btnEscreverNFC.setVisibility(View.GONE);
        txtRetorno.setVisibility(View.GONE);
    }
}
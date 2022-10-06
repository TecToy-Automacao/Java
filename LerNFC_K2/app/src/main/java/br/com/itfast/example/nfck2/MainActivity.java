package br.com.itfast.example.nfck2;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;
import br.com.itfast.tectoy.TecToyException;
import br.com.itfast.tectoy.TecToyNfcCallback;


public class MainActivity extends AppCompatActivity {

    private TecToy tectoy;
    private TextView txtRetorno;

    final TecToyNfcCallback callback = new TecToyNfcCallback() {
        @Override
        public void retornarValor(String strValor) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(strValor.length() < 2){
                        txtRetorno.setText(txtRetorno.getText() + "\n" + "");
                    }
                    txtRetorno.setText(txtRetorno.getText() + "\n" + "DADOS DO CARTAO: " + strValor.substring(1));
                    StringBuffer sb = new StringBuffer();
                    //Converting string to character array
                    char ch[] = strValor.substring(1).toCharArray();
                    for(int i = 0; i < ch.length; i++) {
                        String hexString = Integer.toHexString(ch[i]);
                        sb.append(hexString);
                    }
                    txtRetorno.setText(txtRetorno.getText() + "\n" + "HEX: " + sb.toString() + "\n\n");
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnIniciarLeitura = findViewById(R.id.btnIniciarLeitura);
        txtRetorno = findViewById(R.id.txtDadosLidos);
        tectoy = new TecToy(Dispositivo.K2, this);

        btnIniciarLeitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tectoy.iniciarNFC(getIntent(), callback);
                    btnIniciarLeitura.setEnabled(false);
                } catch (TecToyException e) {
                    Log.e("TECTOY", e.getMessage());
                    Toast.makeText(MainActivity.this, "Erro ao iniciar NFC", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("TECTOY", e.getMessage());
                    Toast.makeText(MainActivity.this, "Erro ao iniciar NFC", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
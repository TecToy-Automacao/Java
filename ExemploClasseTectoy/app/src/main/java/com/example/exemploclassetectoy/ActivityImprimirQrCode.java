package com.example.exemploclassetectoy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityImprimirQrCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imprimir_qr_code);

        EditText txtNivelCorrecao =  findViewById(R.id.txtNivelCorrecaoQrCodeImpressao);
        EditText txtConteudoQrCode = findViewById(R.id.txtConteudoQrCodeImpressao);
        EditText txtLarguraModulo = findViewById(R.id.txtLarguraModuloQrCodeImpressao);
        Button btnEnviarCmd = findViewById(R.id.btnEnviarImprimirQrCode);

        btnEnviarCmd.setOnClickListener(v -> {
            try {
                MainActivity.tectoy.imprimirQrCode(txtConteudoQrCode.getText().toString(), txtNivelCorrecao.getText().toString(), Integer.valueOf(txtLarguraModulo.getText().toString()));
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
package com.example.exemploclassetectoy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityQrCodeDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_display);

        Button btnEnviarQrCodeDisplay = findViewById(R.id.btnEnviarQrCodeDisplay);
        EditText txtValorQrCodeDisplay = findViewById(R.id.txtValorQrCodeDisplay);

        btnEnviarQrCodeDisplay.setOnClickListener(v -> {
            try {
                MainActivity.tectoy.qrCodeDisplay(txtValorQrCodeDisplay.getText().toString());
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
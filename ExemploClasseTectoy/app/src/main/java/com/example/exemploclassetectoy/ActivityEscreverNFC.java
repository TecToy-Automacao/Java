package com.example.exemploclassetectoy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityEscreverNFC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escrever_nfc);
        Button btnEscreverNFC = findViewById(R.id.btnEnviarEscritaNFC);
        EditText editTextoNFC = findViewById(R.id.edtTextoNFC);

        btnEscreverNFC.setOnClickListener(v -> {
            try{
                MainActivity.tectoy.escreverNFC(editTextoNFC.getText().toString());
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch(Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
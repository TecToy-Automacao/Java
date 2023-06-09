package com.example.exemploclassetectoy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityEscreverDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escrever_display);
        EditText edtLinhaUnica = findViewById(R.id.editLinhaUnica);
        EditText edtLinha1 = findViewById(R.id.editLinha1);
        EditText edtLinha2 = findViewById(R.id.editLinha2);
        Button btnEscreverDisplayLinhaUnica = findViewById(R.id.btnEscreverDisplay1Linha);
        Button btnEscreverDisplay2Linhas = findViewById(R.id.btnEscreverDisplay2Linhas);

        btnEscreverDisplayLinhaUnica.setOnClickListener(v -> {
            try{
                MainActivity.tectoy.escreveDisplay(edtLinhaUnica.getText().toString());
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnEscreverDisplay2Linhas.setOnClickListener(v -> {
            try{
                MainActivity.tectoy.escreveDisplay(edtLinha1.getText().toString(), edtLinha2.getText().toString());
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.exemploclassetectoy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import br.com.itfast.tectoy.CorLed;

public class ActivityLerdIndicacao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lerd_indicacao);

        Spinner spinnerCorLiga = findViewById(R.id.spinnerCorLiga);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.coresLed, R.layout.spinner_item);
        spinnerCorLiga.setAdapter(adapter);

        Spinner spinnerCorLigaLoop = findViewById(R.id.spinnerCorLigaLoop);
        spinnerCorLigaLoop.setAdapter(adapter);

        Button btnSendLigarLedIndicacaoLoop = findViewById(R.id.btnSendLigarLedIndicacaoLoop);
        Button btnSendLigarLedIndicacao = findViewById(R.id.btnSendLigarLedIndicacao);
        EditText txtValorTempoLoop = findViewById(R.id.txtTempoLoop);

        btnSendLigarLedIndicacaoLoop.setOnClickListener(view -> {
            try{
                switch (spinnerCorLigaLoop.getSelectedItem().toString()){
                    case "AZUL":
                        MainActivity.tectoy.loopLigarStatus(CorLed.AZUL, Integer.valueOf(txtValorTempoLoop.getText().toString()));
                        break;
                    case "VERDE":
                        MainActivity.tectoy.loopLigarStatus(CorLed.VERDE, Integer.valueOf(txtValorTempoLoop.getText().toString()));
                        break;
                    case "VERMELHO":
                        MainActivity.tectoy.loopLigarStatus(CorLed.VERMELHO, Integer.valueOf(txtValorTempoLoop.getText().toString()));
                        break;
                }
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnSendLigarLedIndicacao.setOnClickListener(view -> {
            try{
                switch (spinnerCorLiga.getSelectedItem().toString()){
                    case "AZUL":
                        MainActivity.tectoy.ligarLedStatus(CorLed.AZUL);
                        break;
                    case "VERDE":
                        MainActivity.tectoy.ligarLedStatus(CorLed.VERDE);
                        break;
                    case "VERMELHO":
                        MainActivity.tectoy.ligarLedStatus(CorLed.VERMELHO);
                        break;
                }
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
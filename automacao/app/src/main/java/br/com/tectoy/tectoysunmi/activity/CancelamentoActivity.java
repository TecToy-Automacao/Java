package br.com.tectoy.tectoysunmi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import br.com.tectoy.tectoysunmi.R;

public class CancelamentoActivity extends AppCompatActivity {

    Button btnCancelar, btnConfirmar;
    EditText txtNSU, txtCodigoAutorizacao, txtDataOperacao, txtValorOperacao;

    private Intent acoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelamento);

        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnCancelar =  findViewById(R.id.btnCancelar);
        txtNSU = findViewById(R.id.txtNSU);
        txtCodigoAutorizacao = findViewById(R.id.txtCodigoAutorizacao);
        txtDataOperacao = findViewById(R.id.txtDataOperacao);
        txtValorOperacao = findViewById(R.id.txtValorOperacao);

        maskTextEdits();

        acoes = getIntent();
        if( acoes != null && acoes.getStringExtra("nsu") != null){
            txtNSU.setText(acoes.getStringExtra("nsu"));
            txtCodigoAutorizacao.setText(acoes.getStringExtra("codigoAutorizacao"));
            txtDataOperacao.setText(acoes.getStringExtra("dataOperacao"));
            txtValorOperacao.setText(acoes.getStringExtra("valorOperacao" ));
        }


        eventClick();
    }

    private void maskTextEdits() {
        txtValorOperacao.addTextChangedListener(new MoneyTextWatcher(txtValorOperacao));
        txtDataOperacao.addTextChangedListener(Mask.insert("##/##/####", txtDataOperacao));
    }

    private void eventClick(){

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("nsu", txtNSU.getText().toString());
                i.putExtra("codigoAutorizacao", txtCodigoAutorizacao.getText().toString());
                i.putExtra("dataOperacao", txtDataOperacao.getText().toString());
                i.putExtra("valorOperacao", Mask.unmask(txtValorOperacao.getText().toString()));
                setResult(RESULT_OK, i);
                finish();
            }
        });

    }
}

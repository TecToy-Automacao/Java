package br.com.tectoy.tectoysunmi.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import br.com.tectoy.tectoysunmi.databinding.ActivityCancelamentoBinding

class CancelamentoActivity: AppCompatActivity() {
    lateinit var btnCancelar: Button
    lateinit var btnConfirmar: Button
    lateinit var txtNSU: EditText
    lateinit var txtCodigoAutorizacao: EditText
    lateinit var txtDataOperacao: EditText
    lateinit var txtValorOperacao: EditText

    private lateinit var acoes: Intent

    private lateinit var binding: ActivityCancelamentoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCancelamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnConfirmar = binding.btnConfirmar
        btnCancelar = binding.btnCancelar
        txtNSU = binding.txtNSU
        txtCodigoAutorizacao = binding.txtCodigoAutorizacao
        txtDataOperacao = binding.txtDataOperacao
        txtValorOperacao = binding.txtValorOperacao

        maskTextEdits()

        acoes = intent
        if (acoes != null && acoes.getStringExtra("nsu") != null){
            txtNSU.setText(acoes.getStringExtra("nsu"))
            txtCodigoAutorizacao.setText(acoes.getStringExtra("codigoAutorizacao"))
            txtDataOperacao.setText(acoes.getStringExtra("dataOperacao"))
            txtValorOperacao.setText(acoes.getStringExtra("valorOperacao"))
        }

        eventClick()
    }

    fun maskTextEdits(){
        txtValorOperacao.addTextChangedListener(MoneyTextWatcher(txtValorOperacao))
        txtDataOperacao.addTextChangedListener(Mask.insert("##/##/####", txtDataOperacao))
    }

    fun eventClick(){
        btnCancelar.setOnClickListener(object : OnClickListener{
            override fun onClick(view: View?) {
                var i: Intent = Intent()
                setResult(RESULT_CANCELED)
                finish()
            }
        })

        btnConfirmar.setOnClickListener(object : OnClickListener{
            override fun onClick(view: View?) {
                var i: Intent = Intent()
                i.putExtra("nsu", txtNSU.text.toString())
                i.putExtra("codigoAutorizacao", txtCodigoAutorizacao.text.toString())
                i.putExtra("dataOperacao", txtDataOperacao.text.toString())
                i.putExtra("valorOperacao", Mask.unmask(txtValorOperacao.text.toString()))
                setResult(RESULT_OK, i)
                finish()
            }
        })
    }
}
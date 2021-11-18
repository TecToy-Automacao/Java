package br.com.tectoy.tectoysunmi.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.setis.interfaceautomacao.AplicacaoNaoInstaladaExcecao;
import br.com.setis.interfaceautomacao.Cartoes;
import br.com.setis.interfaceautomacao.Confirmacoes;
import br.com.setis.interfaceautomacao.DadosAutomacao;
import br.com.setis.interfaceautomacao.EntradaTransacao;
import br.com.setis.interfaceautomacao.Financiamentos;
import br.com.setis.interfaceautomacao.ModalidadesPagamento;
import br.com.setis.interfaceautomacao.Operacoes;
import br.com.setis.interfaceautomacao.Personalizacao;
import br.com.setis.interfaceautomacao.QuedaConexaoTerminalExcecao;
import br.com.setis.interfaceautomacao.SaidaTransacao;
import br.com.setis.interfaceautomacao.StatusTransacao;
import br.com.setis.interfaceautomacao.TerminalNaoConfiguradoExcecao;
import br.com.setis.interfaceautomacao.Transacoes;
import br.com.setis.interfaceautomacao.ViasImpressao;
import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;

public class Paygo extends BaseActivity {

    TextView mTexto;
    EditText valor_operacao, n_parcelas;
    TextView tipo_parcelamento, adquirente;
    CheckBox cb_manual, cb_loj_cli, cb_completa, cb_alternatia;
    Button btn_pagar, btn_cancelar, btn_adm;
    Spinner tipo_pagamento;

    private static final String DEBUG_TAG = MainActivity.class.getName();

    private Confirmacoes mConfirmacao = new Confirmacoes();
    private DadosAutomacao mDadosAutomacao = null;
    private Personalizacao mPersonalizacao;
    private Transacoes mTransacoes = null;
    private SaidaTransacao mSaidaTransacao;
    private EntradaTransacao mEntradaTransacao;
    private String versoes;
    private static String mensagem = null;
    private static Handler mHandler = new Handler();
    private String nsu, dataOperacao, codigoAutorizacao, valorOperacao;

    private static int REQUEST_CODE = 1000;

    private TectoySunmiPrint tectoySunmiPrint;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        setMyTitle(R.string.label_paygo);
        setBack();



        mTexto = findViewById(R.id.textView9);
        valor_operacao = findViewById(R.id.txtValorOperacao);
        n_parcelas = findViewById(R.id.edt_parcelas);
        tipo_pagamento = findViewById(R.id.spTipoPagamento);
        tipo_parcelamento = findViewById(R.id.spTipoParcelamento);
        adquirente = findViewById(R.id.spAdiquirente);
        cb_manual = findViewById(R.id.cbConfirManual);
        cb_loj_cli = findViewById(R.id.cbViaDif);
        cb_alternatia = findViewById(R.id.cbInterfaceAlternativa);
        cb_completa = findViewById(R.id.cbViaReduzida);
        btn_adm = findViewById(R.id.btnAdministrativo);
        btn_cancelar = findViewById(R.id.btnCancelamento);
        btn_pagar = findViewById(R.id.btnPagar);

        ArrayAdapter adapter_pagamento = ArrayAdapter.createFromResource(this, R.array.tipo_pagamento, R.layout.spinner_item);
        tipo_pagamento.setAdapter(adapter_pagamento);

        eventClick();
        iniPayGoInterface(false);
        }
    private final Runnable resultadoOperacacao = new Runnable() {
        @Override
        public void run() {
            int resultado = (mSaidaTransacao != null ?
                    mSaidaTransacao.obtemResultadoTransacao() : -999999);
            traduzResultadoOperacao(resultado);
            mensagem = null;
        }
    };
    private void traduzResultadoOperacao(int resultado) {

        boolean confirmaOperacaoManual = false;
        boolean existeTransacaoPendente = false;
        AlertDialog.Builder caixaMensagem = new AlertDialog.Builder(this);

        String requerConfirmacao = "";

        if (resultado == 0) {

            if (mSaidaTransacao.obtemInformacaoConfirmacao()) {
                if (cb_manual.isChecked()) {
                    Log.d(DEBUG_TAG, "Não ter Confirmação manual");
                    confirmaOperacaoManual = true;

                } else {
                    Log.d(DEBUG_TAG, "Não ter Confirmação automatica");
                    mConfirmacao.informaStatusTransacao(StatusTransacao.CONFIRMADO_AUTOMATICO);
                    mTransacoes.confirmaTransacao(mConfirmacao);
                    caixaMensagem.setPositiveButton("OK", null);
                }
            } else if (mSaidaTransacao.existeTransacaoPendente()) {
                mConfirmacao = new Confirmacoes();
                caixaMensagem.setTitle("Transações Pendentes");
                caixaMensagem.setPositiveButton("Confirme", (dialog, which) -> {
                    mConfirmacao.informaStatusTransacao(StatusTransacao.CONFIRMADO_AUTOMATICO);
                });
                caixaMensagem.setPositiveButton("Cancelar", (dialog, which) -> {
                    mConfirmacao.informaStatusTransacao(StatusTransacao.DESFEITO_ERRO_IMPRESSAO_AUTOMATICO);
                    mTransacoes.resolvePendencia(mSaidaTransacao.obtemDadosTransacaoPendente(), mConfirmacao);
                });
            } else {
                Log.d(DEBUG_TAG, "Não requer confirmação");
                caixaMensagem.setPositiveButton("OK", null);
            }
        }else if(mSaidaTransacao.existeTransacaoPendente()){
            Log.d(DEBUG_TAG, "Existe uma transação pendente");
            mConfirmacao = new Confirmacoes();
            existeTransacaoPendente = true;
        }else {
            caixaMensagem.setTitle("Erro");
            caixaMensagem.setPositiveButton("OK", null);
        }

        String mensagemRetorno = (mSaidaTransacao != null ? mSaidaTransacao.obtemMensagemResultado() : "");

        if (mensagemRetorno.length() > 1) {

            caixaMensagem.setTitle(mensagemRetorno + requerConfirmacao);

            StringBuilder builder = new StringBuilder();

            builder.append("\nID do Cartão: " + mSaidaTransacao.obtemAidCartao());

            builder.append("\n\nNome Portador Cartão: " + mSaidaTransacao.obtemNomePortadorCartao());
            builder.append("\nNome Cartão Padrão: " + mSaidaTransacao.obtemNomeCartaoPadrao());
            builder.append("\nNome Estabelecimento: " + mSaidaTransacao.obtemNomeEstabelecimento());

            builder.append("\n\nPan Mascarado Cartão: " + mSaidaTransacao.obtemPanMascaradoPadrao());
            builder.append("\nPan Mascarado: " + mSaidaTransacao.obtemPanMascarado());

            builder.append("\n\nIdentificador Transação: " + mSaidaTransacao.obtemIdentificadorConfirmacaoTransacao());

            builder.append("\n\nNSU Original: " + mSaidaTransacao.obtemNsuLocalOriginal());
            builder.append("\nNSU Local: " + mSaidaTransacao.obtemNsuLocal());
            builder.append("\nNSU Transação: " + mSaidaTransacao.obtemNsuHost());

            builder.append("\n\nNome Cartão: " + mSaidaTransacao.obtemNomeCartao());
            builder.append("\nNome Provedor: " + mSaidaTransacao.obtemNomeProvedor());

            builder.append("\n\nModo Verificação Senha: " + mSaidaTransacao.obtemModoVerificacaoSenha());

            builder.append("\n\nCod Autorização: " + mSaidaTransacao.obtemCodigoAutorizacao());
            builder.append("\nCod Autorização Original: " + mSaidaTransacao.obtemCodigoAutorizacaoOriginal());
            builder.append("\nPonto Captura: " + mSaidaTransacao.obtemIdentificadorPontoCaptura());

            builder.append("\n\nValor da Operação: " + mSaidaTransacao.obtemValorTotal());
            builder.append("\nSalvo Voucher: " + mSaidaTransacao.obtemSaldoVoucher());

            Log.d(DEBUG_TAG, builder.toString());

            if (resultado == 0) {
                caixaMensagem.setMessage(builder.toString());
                valorOperacao = valor_operacao.getText().toString();
                nsu = mSaidaTransacao.obtemNsuHost();
                codigoAutorizacao = mSaidaTransacao.obtemCodigoAutorizacao();
                dataOperacao =  dateFormat.format(mSaidaTransacao.obtemDataHoraTransacao());
            }

        } else if (mensagem == null) {
            caixaMensagem.setMessage((resultado == 0) ? "Operação OK"
                    : ("Erro: " + resultado));
        } else {
            caixaMensagem.setMessage(mensagem);
        }

        //AlertDialog alert = caixaMensagem.create();
        //alert.setCancelable(true);
       // alert.setCanceledOnTouchOutside(true);

        if (resultado == 0) {

            if (confirmaOperacaoManual) {
               // confirmaOperacao(alert);
            } else {
                trataComprovante();
               // alert.show();
            }

        } else {
            if (existeTransacaoPendente){
              //  existeTransacaoPendente(alert);
            }else{
              //  alert.show();
            }

        }
    }
    private void eventClick() {

        btn_pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuaOperacao(Operacoes.VENDA);
            }
        });

        btn_adm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuaOperacao(Operacoes.ADMINISTRATIVA);
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CancelamentoActivity.class);
                if(nsu != null){
                    i.putExtra("nsu", nsu);
                    i.putExtra("codigoAutorizacao", codigoAutorizacao);
                    i.putExtra("dataOperacao", dataOperacao);
                    i.putExtra("valorOperacao", valor_operacao.getText().toString());
                }
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }
    private void efetuaOperacao(Operacoes operacoes) {
        int identificacaoAutomacao = new Random().nextInt(99999);

        iniPayGoInterface(cb_alternatia.isChecked());

        mEntradaTransacao = new EntradaTransacao(operacoes,
                String.valueOf(identificacaoAutomacao));

        if (operacoes == Operacoes.VENDA) {
            mEntradaTransacao.informaDocumentoFiscal(String.valueOf(identificacaoAutomacao));
            mEntradaTransacao.informaValorTotal(Mask.unmask(valor_operacao.getText().toString()));
        }

        if (operacoes == Operacoes.CANCELAMENTO) {
            mEntradaTransacao.informaNsuTransacaoOriginal(nsu);
            mEntradaTransacao.informaCodigoAutorizacaoOriginal(codigoAutorizacao);
            try {
                mEntradaTransacao.informaDataHoraTransacaoOriginal(dateFormat.parse(dataOperacao));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Informa novamente o valor para realizar a operação de cancelamento
            mEntradaTransacao.informaValorTotal(valorOperacao);
        }

        if (tipo_pagamento.toString() == "Não Definido") {
            mEntradaTransacao.informaModalidadePagamento(ModalidadesPagamento.PAGAMENTO_CARTAO);
            mEntradaTransacao.informaTipoCartao(Cartoes.CARTAO_DESCONHECIDO);

        } else if (tipo_pagamento.toString() == "Crédito") {
            mEntradaTransacao.informaModalidadePagamento(ModalidadesPagamento.PAGAMENTO_CARTAO);
            mEntradaTransacao.informaTipoCartao(Cartoes.CARTAO_CREDITO);

        } else if (tipo_pagamento.toString() == "Débito") {
            mEntradaTransacao.informaModalidadePagamento(ModalidadesPagamento.PAGAMENTO_CARTAO);
            mEntradaTransacao.informaTipoCartao(Cartoes.CARTAO_DEBITO);

        } else if (tipo_pagamento.toString() == "Carteira Digital") {
            mEntradaTransacao.informaModalidadePagamento(ModalidadesPagamento.PAGAMENTO_CARTEIRA_VIRTUAL);
            mEntradaTransacao.informaTipoCartao(Cartoes.CARTAO_VOUCHER);
        }

        if (false) {
            mEntradaTransacao.informaTipoFinanciamento(Financiamentos.FINANCIAMENTO_NAO_DEFINIDO);

        } else if (true) {
            mEntradaTransacao.informaTipoFinanciamento(Financiamentos.A_VISTA);

        } else if (false) {
            mEntradaTransacao.informaTipoFinanciamento(Financiamentos.PARCELADO_EMISSOR);
            mEntradaTransacao.informaNumeroParcelas(Integer.parseInt(n_parcelas.getText().toString()));

        } else if (false) {
            mEntradaTransacao.informaTipoFinanciamento(Financiamentos.PARCELADO_ESTABELECIMENTO);
            mEntradaTransacao.informaNumeroParcelas(Integer.parseInt(n_parcelas.getText().toString()));

        }

        if (adquirente.toString() == "PROVEDOR DESCONHECIDO") {
            mEntradaTransacao.informaNomeProvedor(adquirente.toString());
        }

        mEntradaTransacao.informaCodigoMoeda("986"); // Real

        mConfirmacao = new Confirmacoes();

        new Thread(() -> {
            try {
                mDadosAutomacao.obtemPersonalizacaoCliente();
                mSaidaTransacao = mTransacoes.realizaTransacao(mEntradaTransacao);

                if(mSaidaTransacao == null)
                    return;

                mConfirmacao
                        .informaIdentificadorConfirmacaoTransacao(
                                mSaidaTransacao.obtemIdentificadorConfirmacaoTransacao()
                        );

            } catch (QuedaConexaoTerminalExcecao e) {
                e.printStackTrace();
                mensagem = "Queda de Conexão";

            } catch (TerminalNaoConfiguradoExcecao terminalNaoConfiguradoExcecao) {
                terminalNaoConfiguradoExcecao.printStackTrace();
                mensagem = "Cliente não configurado!";

            } catch (AplicacaoNaoInstaladaExcecao aplicacaoNaoInstaladaExcecao) {
                aplicacaoNaoInstaladaExcecao.printStackTrace();
                mensagem = "Aplicação não instalada!";

            } finally {
                // Trata o Fim da Operação
                mEntradaTransacao = null;
                mHandler.post(resultadoOperacacao);
            }
        }).start();
    }
    private void iniPayGoInterface(boolean mudaCor) {

        String versaoAutomacao;
        try {
            versaoAutomacao = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versaoAutomacao = "Indisponivel";
        }

        mPersonalizacao = setPersonalizacao(mudaCor);

        mDadosAutomacao = new DadosAutomacao("TecToy Automação", "Automação", versaoAutomacao,
                true, true, cb_loj_cli.isChecked(), cb_completa.isChecked(), mPersonalizacao);

        mTransacoes = Transacoes.obtemInstancia(mDadosAutomacao, this);

        versoes = String.valueOf(mTransacoes.obtemVersoes());
    }
    private void trataComprovante() {

        List<String> comprovante = new ArrayList<String>();

        if (cb_loj_cli.isChecked()) {
            ViasImpressao vias = mSaidaTransacao.obtemViasImprimir();

            if (vias == ViasImpressao.VIA_CLIENTE || vias == ViasImpressao.VIA_CLIENTE_E_ESTABELECIMENTO) {
                comprovante = mSaidaTransacao.obtemComprovanteDiferenciadoPortador();
                if (comprovante == null || comprovante.size() <= 1) {
                    // Verifica se tem via completa
                    comprovante = mSaidaTransacao.obtemComprovanteCompleto();
                    if (comprovante == null) {
                        return;
                    }
                }
                printComprovante("Via do Cliente", comprovante);
            }

            if (vias == ViasImpressao.VIA_ESTABELECIMENTO || vias == ViasImpressao.VIA_CLIENTE_E_ESTABELECIMENTO) {
                comprovante = mSaidaTransacao.obtemComprovanteDiferenciadoLoja();
                if (comprovante == null || comprovante.size() <= 1) {
                    // Verifica se tem via completa
                    comprovante = mSaidaTransacao.obtemComprovanteCompleto();
                    if (comprovante == null) {
                        return;
                    }
                }
                printComprovante("Via do Estabelecimento", comprovante);
            }

        } else {
            comprovante = mSaidaTransacao.obtemComprovanteCompleto();
            if (comprovante == null || comprovante.size() <= 1) {
                return;
            }
            printComprovante("Comprovante Full", comprovante);
        }

    }

    private void printComprovante(String mensagem, List<String> comprovante) {

        AlertDialog.Builder caixaMensagem = new AlertDialog.Builder(this);
        String cupom = "";
        for (String linha : comprovante) {
            cupom += linha;
        }
        caixaMensagem.setTitle("Impressão de comprovante");
        caixaMensagem.setMessage("Deseja imprimir " + mensagem);
        String finalCupom = cupom;
        caixaMensagem.setPositiveButton("Sim", (dialog, which) -> {
            try {
                Log.d(DEBUG_TAG, "Fazendo a impressão do cupom " + mensagem);
                //printer.getStatusImpressora();
                //if (printer.isImpressoraOK()) {
                if (true) {
                    TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                    TectoySunmiPrint.getInstance().printStyleBold(true);
                    TectoySunmiPrint.getInstance().printText(finalCupom);
                    TectoySunmiPrint.getInstance().print3Line();
                    TectoySunmiPrint.getInstance().cutpaper();
                    mTexto.setText(finalCupom);
                    //printer.imprimeTexto(finalCupom);
                    //printer.avancaLinha(150);
                    //printer.ImpressoraOutput();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        caixaMensagem.setNegativeButton("Não", null);
        AlertDialog alert = caixaMensagem.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                nsu = data.getStringExtra("nsu");
                dataOperacao = data.getStringExtra("dataOperacao");
                codigoAutorizacao = data.getStringExtra("codigoAutorizacao");
                valorOperacao = data.getStringExtra("valorOperacao");
                efetuaOperacao(Operacoes.CANCELAMENTO);
            }
        }
    }

    private void confirmaOperacao(AlertDialog dialog2){

        AlertDialog.Builder confirmaOperacao = new AlertDialog.Builder(this);

        confirmaOperacao.setTitle("Confirmação manual");
        confirmaOperacao.setMessage("Deseja confirmar a operação de forma manual?");

        Log.d(DEBUG_TAG, "Confirmação manual");
        confirmaOperacao.setPositiveButton("Confirme", (dialog, which) -> {
            Log.d(DEBUG_TAG, "Operação foi confirmada pelo operador.");
            mConfirmacao.informaStatusTransacao(StatusTransacao.CONFIRMADO_MANUAL);
            mTransacoes.confirmaTransacao(mConfirmacao);
            trataComprovante();
            dialog2.show();
        });

        confirmaOperacao.setNegativeButton("Cancelar", (dialog, which) -> {
            Log.d(DEBUG_TAG, "Operação não foi confirmada pelo operador.");
            mConfirmacao.informaStatusTransacao(StatusTransacao.DESFEITO_MANUAL);
            mTransacoes.confirmaTransacao(mConfirmacao);
            trataComprovante();
            dialog2.show();
        });

        confirmaOperacao.setNegativeButton("Não", null);
        AlertDialog alert = confirmaOperacao.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    private void existeTransacaoPendente(AlertDialog dialog2){

        AlertDialog.Builder confirmaOperacao = new AlertDialog.Builder(this);

        confirmaOperacao.setTitle("Transação Pendente");
        confirmaOperacao.setMessage("Deseja confirmar a transação que esta PENDENTE?");

        Log.d(DEBUG_TAG, "Confirmação manual");
        confirmaOperacao.setPositiveButton("Confirme", (dialog, which) -> {
            Log.d(DEBUG_TAG, "Transação Pendente foi CONFIRMADO_MANUAL .");
            mConfirmacao.informaStatusTransacao(StatusTransacao.CONFIRMADO_MANUAL);
            mTransacoes.resolvePendencia(mSaidaTransacao.obtemDadosTransacaoPendente(), mConfirmacao);
            // trataComprovante();
            // dialog2.show();
        });

        confirmaOperacao.setNegativeButton("Cancelar", (dialog, which) -> {
            Log.d(DEBUG_TAG, "Transação Pendente foi DESFEITO_ERRO_IMPRESSAO_AUTOMATICO .");
            mConfirmacao.informaStatusTransacao(StatusTransacao.DESFEITO_ERRO_IMPRESSAO_AUTOMATICO);
            mTransacoes.confirmaTransacao(mConfirmacao);
            // trataComprovante();
            // dialog2.show();
        });

        confirmaOperacao.setNegativeButton("Não", null);
        AlertDialog alert = confirmaOperacao.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }
    private Personalizacao setPersonalizacao(boolean isInverse) {

        Personalizacao.Builder pb = new Personalizacao.Builder();
        try {
            if (isInverse) {
                pb.informaCorFonte( "#000000" );
                pb.informaCorFonteTeclado("#000000");
                pb.informaCorFundoCaixaEdicao("#FFFFFF");
                pb.informaCorFundoTela("#F4F4F4");
                pb.informaCorFundoTeclado("#F4F4F4");
                pb.informaCorFundoToolbar("#FF8C00");
                pb.informaCorTextoCaixaEdicao("#000000");
                pb.informaCorTeclaPressionadaTeclado("#e1e1e1");
                pb.informaCorTeclaLiberadaTeclado("#dedede");
                pb.informaCorSeparadorMenu("#FF8C00");
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Verifique valores de\nconfiguração", Toast.LENGTH_SHORT).show();
        }

        return pb.build();
    }
}

package br.com.tectoy.tectoysunmi.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;

public class Msitef extends BaseActivity{


    Button btn_pagar, btn_adm, btn_cancelar, btn_reimpressao;
    EditText edt_valor, edt_ip, edt_parcelas;
    Spinner edt_tipo;

    private final String API_VERSION = "1.04";

    private final String CREDITO = "1";
    private final String DEBITO = "2";
    private final String VOUCHER = "4";
    private final String REIMPRESSAO = "18";

    private final String SEMPARCELAMENTO = "0";
    private final String PARCELADO_LOJA = "1";
    private final String PARCELADO_ADM = "2";

    private final String DESABILITA_IMPRESSAO = "0";
    private final String HABILITA_IMPRESSAO = "1";

    private final String VENDA = "1";
    private final String CANCELAMENTO = "2";
    private final String FUNCOES = "3";
    public static String acao = "venda";

    //Gson gson = new Gson();

    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("pos7api://pos7"));

    Venda venda = new Venda();

    private TectoySunmiPrint tectoySunmiPrint;
    private Random r = new Random();
    private Date dt = new Date();
    private String op = String.valueOf(r.nextInt(99999));
    private String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
    private String currentDateTimeStringT = String.valueOf(dt.getHours()) + String.valueOf(dt.getMinutes()) + String.valueOf(dt.getSeconds());

    /// Fim Defines Operação

    private Locale mLocale = new Locale("pt", "BR");


    ///  Defines tef
    private static int REQ_CODE = 4321;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msitef);

        edt_valor = findViewById(R.id.txtValorOperacao);
        edt_ip = findViewById(R.id.edt_ip);
        edt_parcelas = findViewById(R.id.edt_parcelas);
        btn_pagar = findViewById(R.id.btnPagar);
        btn_adm = findViewById(R.id.btnAdministrativo);
        btn_cancelar = findViewById(R.id.btnRepressao);
        edt_tipo = findViewById(R.id.spTipoPagamento);
        btn_pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "venda";
            //    if (Mask.unmask(edt_valor.getText().toString()).equals("000")) {
                    System.out.println("O valor de venda digitado deve ser maior que 0");
             //   } else {
                    if (edt_parcelas.getText().toString().isEmpty() || edt_parcelas.getText().toString().equals("0")) {
                  //  } else {
                        execulteSTefVenda();
                //    }
                }
            }
        });
    }
        // Faz Transação
        private void execulteSTefVenda() {
            Intent intentSitef = new Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF");
            intentSitef.putExtra("empresaSitef", "00000000");
            intentSitef.putExtra("enderecoSitef", edt_ip.getText().toString().replaceAll("\\s+", ""));
            intentSitef.putExtra("operador", "0001");
            intentSitef.putExtra("data", "20200324");
            intentSitef.putExtra("hora", "130358");
            intentSitef.putExtra("numeroCupom", op);

//            intentSitef.putExtra("valor", Mask.unmask(edt_valor.getText().toString()));
            intentSitef.putExtra("CNPJ_CPF", "03654119000176");
            intentSitef.putExtra("comExterna", "0");

            if (edt_tipo.equals("Credito")) {
                intentSitef.putExtra("modalidade", "3");
                if (edt_parcelas.getText().toString().equals("0") || edt_parcelas.getText().toString().equals("1")) {
                    intentSitef.putExtra("transacoesHabilitadas", "26");
                } else if (true) {
                    // Essa informações habilida o parcelamento Loja
                    intentSitef.putExtra("transacoesHabilitadas", "27");
                }
                intentSitef.putExtra("numParcelas", edt_parcelas.getText().toString());
            }

            if (edt_tipo.equals("Debito")) {
                intentSitef.putExtra("modalidade", "2");
                intentSitef.putExtra("transacoesHabilitadas", "16");
            }

            intentSitef.putExtra("isDoubleValidation", "0");
            intentSitef.putExtra("caminhoCertificadoCA", "ca_cert_perm");
            startActivityForResult(intentSitef, REQ_CODE);
        }
    private void execulteSTefCancelamento() {
        Intent intentSitef = new Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF");

        intentSitef.putExtra("empresaSitef", "00000000");
        intentSitef.putExtra("enderecoSitef", edt_ip.getText().toString().replaceAll("\\s+", ""));
        intentSitef.putExtra("operador", "0001");
        intentSitef.putExtra("data", currentDateTimeString);
        intentSitef.putExtra("hora", currentDateTimeStringT);
        intentSitef.putExtra("numeroCupom", op);

      //  intentSitef.putExtra("valor", Mask.unmask(edt_valor.getText().toString()));
        intentSitef.putExtra("CNPJ_CPF", "03654119000176");
        intentSitef.putExtra("comExterna", "0");

        intentSitef.putExtra("modalidade", "200");

        intentSitef.putExtra("isDoubleValidation", "0");
        intentSitef.putExtra("caminhoCertificadoCA", "ca_cert_perm");

        startActivityForResult(intentSitef, REQ_CODE);
    }
    private void execulteSTefFuncoes() {
        Intent intentSitef = new Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF");

        intentSitef.putExtra("empresaSitef", "00000000");
        intentSitef.putExtra("enderecoSitef", edt_ip.getText().toString().replaceAll("\\s+", ""));
        intentSitef.putExtra("operador", "0001");
        intentSitef.putExtra("data", currentDateTimeString);
        intentSitef.putExtra("hora", currentDateTimeStringT);
        intentSitef.putExtra("numeroCupom", op);

       // intentSitef.putExtra("valor", Mask.unmask(edt_valor.getText().toString()));
        intentSitef.putExtra("CNPJ_CPF", "03654119000176");
        intentSitef.putExtra("comExterna", "0");

        intentSitef.putExtra("isDoubleValidation", "0");
        intentSitef.putExtra("caminhoCertificadoCA", "ca_cert_perm");
        intentSitef.putExtra("modalidade", "110");
        intentSitef.putExtra("restricoes", "transacoesHabilitadas=16;26;27");

        startActivityForResult(intentSitef, REQ_CODE);
    }
    private void execulteSTefReimpressao() {
        Intent intentSitef = new Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF");

        intentSitef.putExtra("empresaSitef", "00000000");
        intentSitef.putExtra("enderecoSitef", edt_ip.getText().toString().replaceAll("\\s+", ""));
        intentSitef.putExtra("operador", "0001");
        intentSitef.putExtra("data", "20200324");
        intentSitef.putExtra("hora", "130358");
        intentSitef.putExtra("numeroCupom", op);

      //  intentSitef.putExtra("valor", Mask.unmask(edt_valor.getText().toString()));
        intentSitef.putExtra("CNPJ_CPF", "03654119000176");
        intentSitef.putExtra("comExterna", "0");

        intentSitef.putExtra("modalidade", "114");

        intentSitef.putExtra("isDoubleValidation", "0");
        intentSitef.putExtra("caminhoCertificadoCA", "ca_cert_perm");

        startActivityForResult(intentSitef, REQ_CODE);
    }
    boolean validaIp(String ipserver) {

        Pattern p = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        Matcher m = p.matcher(ipserver);
        boolean b = m.matches();
        return b;
    }
    private void maskTextEdits() {
        edt_valor.addTextChangedListener(new MoneyTextWatcher(edt_valor));
    }

    private void dialodTransacaoAprovadaMsitef(RetornoMsiTef retornoMsiTef) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        StringBuilder cupom = new StringBuilder();
        cupom.append("CODRESP: " + retornoMsiTef.getCodResp() + "\n");
        cupom.append("COMP_DADOS_CONF: " + retornoMsiTef.getCompDadosConf() + "\n");
        cupom.append("CODTRANS: " + retornoMsiTef.getCodTrans() + "\n");
        cupom.append("CODTRANS (Name): " + retornoMsiTef.getNameTransCod() + "\n");
        cupom.append("VLTROCO: " + retornoMsiTef.getvlTroco() + "\n");
        cupom.append("REDE_AUT: " + retornoMsiTef.getRedeAut() + "\n");
        cupom.append("BANDEIRA: " + retornoMsiTef.getBandeira() + "\n");
        cupom.append("NSU_SITEF: " + retornoMsiTef.getNSUSitef() + "\n");
        cupom.append("NSU_HOST: " + retornoMsiTef.getNSUHOST() + "\n");
        cupom.append("COD_AUTORIZACAO: " + retornoMsiTef.getCodAutorizacao() + "\n");
        cupom.append("NUM_PARC: " + retornoMsiTef.getParcelas() + "\n");
        alertDialog.setTitle("Ação executada com sucesso");
        alertDialog.setMessage(cupom.toString());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Não existe nenhuma ação
            }
        });
        alertDialog.show();
    }
    public String respSitefToJson(Intent data) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("CODRESP", data.getStringExtra("CODRESP"));
        json.put("COMP_DADOS_CONF", data.getStringExtra("COMP_DADOS_CONF"));
        json.put("CODTRANS", data.getStringExtra("CODTRANS"));
        json.put("VLTROCO", data.getStringExtra("VLTROCO"));
        json.put("REDE_AUT", data.getStringExtra("REDE_AUT"));
        json.put("BANDEIRA", data.getStringExtra("BANDEIRA"));
        json.put("NSU_SITEF", data.getStringExtra("NSU_SITEF"));
        json.put("NSU_HOST", data.getStringExtra("NSU_HOST"));
        json.put("COD_AUTORIZACAO", data.getStringExtra("COD_AUTORIZACAO"));
        json.put("NUM_PARC", data.getStringExtra("NUM_PARC"));
        json.put("TIPO_PARC", data.getStringExtra("TIPO_PARC"));
        json.put("VIA_ESTABELECIMENTO", data.getStringExtra("VIA_ESTABELECIMENTO"));
        json.put("VIA_CLIENTE", data.getStringExtra("VIA_CLIENTE"));
        return json.toString();
    }
    private void dialogImpressaoGPOS(String texto, int size) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        StringBuilder cupom = new StringBuilder();
        cupom.append("Deseja realizar a impressão pela aplicação ?");
        alertDialog.setTitle("Realizar Impressão");
        alertDialog.setMessage(cupom.toString());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String textoEstabelecimento = "";
                String textoCliente = "";

                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
                TectoySunmiPrint.getInstance().setSize(size);
                TectoySunmiPrint.getInstance().printStyleBold(true);
                try {
                    TectoySunmiPrint.getInstance().printerStatus();
                    if (true) {
                        if (true) {
                            textoEstabelecimento = texto.substring(0, texto.indexOf("\f"));
                            textoCliente = texto.substring(texto.indexOf("\f"));
                         //   TectoySunmiPrint.getInstance().printText(textoEstabelecimento);
                            TectoySunmiPrint.getInstance().print3Line();
                           // TectoySunmiPrint.getInstance().printText(textoCliente);
                        } else {
                       //     TectoySunmiPrint.getInstance().printText(texto);
                        }
                        TectoySunmiPrint.getInstance().print3Line();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //        não executa nada
            }
        });
        alertDialog.show();
    }
}


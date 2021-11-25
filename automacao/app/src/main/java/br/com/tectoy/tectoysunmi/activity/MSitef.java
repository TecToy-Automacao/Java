package br.com.tectoy.tectoysunmi.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;

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
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.ListDialog;

public class MSitef extends BaseActivity {


    Button btn_pagar, btn_adm, btn_cancelar, btn_reimpressao;
    EditText edt_valor, edt_ip, edt_parcelas;
    TextView edt_tipo;
    CheckBox chc_usb, chc_blu;
    Boolean usb, blu;

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
    String teste;

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
    Gson gson = new Gson();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msitef);

        chc_blu = findViewById(R.id.chc_blu);
        chc_usb = findViewById(R.id.chc_USB);
        edt_valor = findViewById(R.id.txtValorOperacao);
        edt_ip = findViewById(R.id.edt_ip);
        edt_parcelas = findViewById(R.id.edt_parcelas);
        btn_pagar = findViewById(R.id.btnPagar);
        btn_adm = findViewById(R.id.btnAdministrativo);
        btn_adm.setVisibility(View.GONE);
        btn_cancelar = findViewById(R.id.btnCancelamento);
        btn_reimpressao = findViewById(R.id.btnRepressao);
        edt_tipo = findViewById(R.id.spTipoPagamento);

        findViewById(R.id.tipo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] mStrings = new String[]{getResources().getString(R.string.nao_definido), getResources().getString(R.string.credito), getResources().getString(R.string.debito), getResources().getString(R.string.carteira_digital)};
                final ListDialog listDialog = DialogCreater.createListDialog(MSitef.this, getResources().getString(R.string.array_qrcode), getResources().getString(R.string.cancel), mStrings);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        edt_tipo.setText(mStrings[position]);
                        teste = mStrings[position];
                        listDialog.cancel();
                        System.out.println(edt_tipo);
                        System.out.println("teste");
                    }
                });
                listDialog.show();
            }
        });
        btn_reimpressao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acao = "reimpressao";
                execulteSTefReimpressao();
            }
        });
        btn_pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "venda";
                        execulteSTefVenda();
            }
        });
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acao = "cancelamento";
                execulteSTefCancelamento();
            }
        });

    }

        // Faz Transação
        private void execulteSTefVenda() {
            Intent intentSitef = new Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF");
            intentSitef.putExtra("empresaSitef", "00000000");
            intentSitef.putExtra("enderecoSitef", "172.17.102.96");
            intentSitef.putExtra("operador", "0001");
            intentSitef.putExtra("data", "20200324");
            intentSitef.putExtra("hora", "130358");
            intentSitef.putExtra("numeroCupom", op);
            intentSitef.putExtra("valor", Mask.unmask(edt_valor.getText().toString()));
            intentSitef.putExtra("CNPJ_CPF", "03654119000176");
            intentSitef.putExtra("comExterna", "0");
            if (chc_usb.isChecked()){
             intentSitef.putExtra("pinpadMac", "00:00:00:00:00:00");
            }
            if(teste.equals("Não Definido")){
                intentSitef.putExtra("modalidade", "0");
            } else if ("Crédito".equals(teste)) {
                intentSitef.putExtra("modalidade", "3");
                if (edt_parcelas.getText().toString().equals("0") || edt_parcelas.getText().toString().equals("1")) {
                    intentSitef.putExtra("transacoesHabilitadas", "26");
                } else if (true) {
                    // Essa informações habilida o parcelamento Loja
                    intentSitef.putExtra("transacoesHabilitadas", "27");
                }
                intentSitef.putExtra("numParcelas", edt_parcelas.getText().toString());
            } else if ("Débito".equals(teste)) {
                intentSitef.putExtra("modalidade", "2");
                //intentSitef.putExtra("transacoesHabilitadas", "16");
            } else if ("Carteira Digital".equals(teste)) {
            }
            intentSitef.putExtra("isDoubleValidation", "0");
            intentSitef.putExtra("caminhoCertificadoCA", "ca_cert_perm");
            startActivityForResult(intentSitef, REQ_CODE);
        }
    private void execulteSTefCancelamento() {
        Intent intentSitef = new Intent("br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF");
        intentSitef.putExtra("empresaSitef", "00000000");
        intentSitef.putExtra("enderecoSitef", "172.17.102.96");
        intentSitef.putExtra("operador", "0001");
        intentSitef.putExtra("data", currentDateTimeString);
        intentSitef.putExtra("hora", currentDateTimeStringT);
        intentSitef.putExtra("numeroCupom", op);
        intentSitef.putExtra("valor", Mask.unmask(edt_valor.getText().toString()));
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
        intentSitef.putExtra("enderecoSitef", "172.17.102.96");
        intentSitef.putExtra("operador", "0001");
        intentSitef.putExtra("data", currentDateTimeString);
        intentSitef.putExtra("hora", currentDateTimeStringT);
        intentSitef.putExtra("numeroCupom", op);
        intentSitef.putExtra("valor", Mask.unmask(edt_valor.getText().toString()));
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
        intentSitef.putExtra("enderecoSitef", "172.17.102.96");
        intentSitef.putExtra("operador", "0001");
        intentSitef.putExtra("data", "20200324");
        intentSitef.putExtra("hora", "130358");
        intentSitef.putExtra("numeroCupom", op);
        intentSitef.putExtra("valor", Mask.unmask(edt_valor.getText().toString()));
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            RetornoMsiTef retornoSitef = null;
            if (data == null)
                return;

            try {
                retornoSitef = gson.fromJson(respSitefToJson(data), RetornoMsiTef.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (requestCode == REQ_CODE && resultCode == RESULT_OK) {
                if (retornoSitef.getCodResp().equals("0")) {
                    String impressao = "";
                    // Verifica se tem algo pra imprimir
                    if (!retornoSitef.textoImpressoCliente().isEmpty()) {
                        impressao += retornoSitef.textoImpressoCliente();
                    }
                    if (!retornoSitef.textoImpressoEstabelecimento().isEmpty()) {
                        impressao += "\n\n-----------------------------     \n";
                        impressao += retornoSitef.textoImpressoEstabelecimento();
                    }
                    if (!impressao.isEmpty() && acao.equals("reimpressao")) {
                        dialogImpressao(impressao, 17);
                    }
                }
                // Verifica se ocorreu um erro durante venda ou cancelamento
                if (acao.equals("venda") || acao.equals("cancelamento")) {
                    if (retornoSitef.getCodResp().isEmpty() || !retornoSitef.getCodResp().equals("0") || retornoSitef.getCodResp() == null) {
                        //dialodTransacaoNegadaMsitef(retornoSitef);
                    } else {
                        dialodTransacaoAprovadaMsitef(retornoSitef);
                    }
                }
            } else {
                // ocorreu um erro
                if (acao.equals("venda") || acao.equals("cancelamento")) {
                    //dialodTransacaoNegadaMsitef(retornoSitef);
                }
            }
    }

    private void dialodTransacaoAprovadaMsitef(RetornoMsiTef retornoMsiTef) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        StringBuilder cupom = new StringBuilder();
        StringBuilder teste = new StringBuilder();

        cupom.append("Via Cliente \n" + retornoMsiTef.getVIA_CLIENTE() + "\n");
        teste.append("Via Estabelecimento \n" + retornoMsiTef.getVIA_ESTABELECIMENTO() + "\n");

        alertDialog.setTitle("Ação executada com sucesso");
        alertDialog.setMessage(cupom.toString());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TectoySunmiPrint.getInstance().setSize(20);
                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                TectoySunmiPrint.getInstance().printStyleBold(true);
                TectoySunmiPrint.getInstance().printText(cupom.toString());
                TectoySunmiPrint.getInstance().print3Line();

                TectoySunmiPrint.getInstance().feedPaper();
                TectoySunmiPrint.getInstance().printText(teste.toString());
                TectoySunmiPrint.getInstance().print3Line();

                TectoySunmiPrint.getInstance().cutpaper();
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
    private void dialogImpressao(String texto, int size) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        StringBuilder cupom = new StringBuilder();
        TectoySunmiPrint.getInstance().printText(texto);
        //cupom.append("Deseja realizar a impressão pela aplicação ?");
       // alertDialog.setTitle("Realizar Impressão");
        //alertDialog.setMessage(cupom.toString());
        //alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sim", new DialogInterface.OnClickListener() {
         //   @Override
          //  public void onClick(DialogInterface dialogInterface, int i) {

          //      String textoEstabelecimento = "";
           //     String textoCliente = "";

            //    TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
             //   TectoySunmiPrint.getInstance().setSize(size);
             //   TectoySunmiPrint.getInstance().printStyleBold(true);

            //    try {
            //        TectoySunmiPrint.getInstance().printerStatus();
            //        if (true) {
             //           if (true) {
              //              textoEstabelecimento = texto.substring(0, texto.indexOf("\f"));
               //             textoCliente = texto.substring(texto.indexOf("\f"));
                 //           TectoySunmiPrint.getInstance().printText(textoEstabelecimento);
                 //           TectoySunmiPrint.getInstance().print3Line();
                 //           TectoySunmiPrint.getInstance().printText(textoCliente);
                 //       } else {
                 //           TectoySunmiPrint.getInstance().printText(texto);
                 //       }
                 //       TectoySunmiPrint.getInstance().print3Line();
                 //   }
               // } catch (Exception e) {
              //      e.printStackTrace();
              //  }

          //  }

      // });
      //  alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Não", new DialogInterface.OnClickListener() {
      //      @Override
       //     public void onClick(DialogInterface dialogInterface, int i) {
       //         //        não executa nada
       ////     }
       // });
       // alertDialog.show();
    }
}


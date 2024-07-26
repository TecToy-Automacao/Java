package br.com.itfast.t2minirestaurante;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import android_serialport_api.SerialPort;
import br.com.daruma.framework.mobile.DarumaMobile;
import br.com.daruma.framework.mobile.webservice.TrustedManagerManipulator;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;


public class MainActivity extends AppCompatActivity {
    ExecutorService service = Executors.newSingleThreadExecutor();
    Intent inttTela, inttSettings;
    Button btnLanche, btnBatata, btnSService, btnRefri, btnSuco, btnSobremesa, btnSorvete, btnSettings,
            btnLanche2, btnBatata2, btnRefri2, btnSuco2;
    Button btnEncerra, btnVolta;
    private ProgressBar load;

    TextView txtLanche, txtBatata, txtSService, txtRefri, txtSuco, txtSobremesa, txtSorvete, txtItens, txtValor;

    private TecToy tectoy;
    private DarumaMobile dmf;
    private String strUsuario, strSenha, idLoja, strToken, strPreco;
    String strProduto, hamburguerValue,fritasValue, selfservValue, refrigeranteValue, sucoValue,  sorveteValue, sobremesaValue;
    int iNum = 0;
    int iItens = 0;
    int iLanche = 0;
    int iBatata = 0;
    Double iSService = 0.00;
    int iRefrig = 0;
    int iSuco = 0;
    Double iSobrem = 0.00;
    Double iSorvt = 0.00;
    int count , cTot;
    String soma = "0";
    int i;
    String strNum, strAux, sservicePeso, sorvetePeso, sobremesaPeso;
    char[] resp = new char[5];
    Produtos produtos ;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    @Override
    protected void onResume() {
        super.onResume();
        try{
            count=0;
            consultaProdutos(); //api etiquetas
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Erro na rotina de Login ESL/ Configuração XML: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler(Looper.getMainLooper());
        load = findViewById(R.id.progressBar);
        txtItens = findViewById(R.id.txtItens);
        txtValor = findViewById(R.id.txtValor);
        btnLanche = findViewById(R.id.btnLanche);
        btnLanche2 = findViewById(R.id.btnLanche2);
        txtLanche = findViewById(R.id.txtLanche);
        btnBatata = findViewById(R.id.btnBatata);
        btnBatata2 = findViewById(R.id.btnBatata2);
        txtBatata = findViewById(R.id.txtBatata);
        btnSService = findViewById(R.id.btnSService);
        txtSService = findViewById(R.id.txtSService);
        btnRefri = findViewById(R.id.btnRefri);
        btnRefri2 = findViewById(R.id.btnRefri2);
        txtRefri = findViewById(R.id.txtRefri);
        btnSuco = findViewById(R.id.btnSuco);
        btnSuco2 = findViewById(R.id.btnSuco2);
        txtSuco = findViewById(R.id.txtSuco);
        btnSobremesa = findViewById(R.id.btnSobremesa);
        txtSobremesa = findViewById(R.id.txtSobremesa);
        btnSorvete = findViewById(R.id.btnSorvete);
        txtSorvete = findViewById(R.id.txtSorvete);
        btnEncerra = findViewById(R.id.btnEncerra);
        btnSettings = findViewById(R.id.btnSettings);
        btnVolta = findViewById(R.id.btnVolta);


        try {
            //verifica se tem permissoes necessarias
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
                if (checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.INTERNET}, 0);
                }
            }

            dmf = DarumaMobile.inicializar(MainActivity.this, "@FRAMEWORK(TRATAEXCECAO=TRUE;TIMEOUTWS=10000;);@BLUETOOTH(NAME=InnerPrinter;ATTEMPTS=100;TIMEOUT=10000)");

            Thread thrCgf;
            strAux = "";
            tectoy = new TecToy(Dispositivo.T2_MINI, this);
            produtos = new Produtos();
            count=0;


            try{
                efetuaLogin(); //api etiquetas
                thrCgf = new Thread(config); //configurar para emissão de NFCE
                thrCgf.start();
                thrCgf.join();
            }catch (Exception e){
                Toast.makeText(MainActivity.this, "Erro na rotina de Login ESL/ Configuração XML: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            btnSettings.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (iItens > 0) { zerarValores(); }
                    if (inttSettings == null) {
                        inttSettings = new Intent(MainActivity.this, SettingsActivity.class);
                    }
                    startActivity(inttSettings);
                }
            });
            btnVolta.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (iItens > 0) {
                        zerarValores();
                    }
                    if (inttTela == null) {
                        inttTela = new Intent(MainActivity.this, MainActivity2.class);
                    }
                    tectoy.limparDisplay();
                    startActivity(inttTela);
                }
            });
            btnLanche.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    hamburguerValue = produtos.getPreço("588968759433");
                    strNum = String.valueOf(txtLanche.getText());
                    soma(strNum, hamburguerValue);
                    tectoy.escreveDisplay("Hamburguer", "R$"+hamburguerValue);
                    txtLanche.setText(strNum);
                }
            });
            btnLanche2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    hamburguerValue = produtos.getPreço("588968759433");
                    strNum = String.valueOf(txtLanche.getText());
                    remove(strNum, hamburguerValue);
                    tectoy.escreveDisplay("Hamburguer", "-R$"+hamburguerValue);
                    txtLanche.setText(strNum);
                }
            });
            btnBatata.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    fritasValue = produtos.getPreço("588968759436");
                    strNum = String.valueOf(txtBatata.getText());
                    soma(strNum, fritasValue);
                    tectoy.escreveDisplay("Batata Frita", "R$"+fritasValue);
                    txtBatata.setText(strNum);
                }
            });
            btnBatata2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    fritasValue = produtos.getPreço("588968759436");
                    strNum = String.valueOf(txtBatata.getText());
                    remove(strNum, fritasValue);
                    tectoy.escreveDisplay("Batata Frita", "-R$" + fritasValue);
                    txtBatata.setText(strNum);
                }
            });
            btnRefri.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    refrigeranteValue = produtos.getPreço("588968759438");
                    strNum = String.valueOf(txtRefri.getText());
                    soma(strNum, refrigeranteValue);
                    tectoy.escreveDisplay("Refrigerante", "R$"+refrigeranteValue);
                    txtRefri.setText(strNum);
                }
            });
            btnRefri2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    refrigeranteValue = produtos.getPreço("588968759438");
                    strNum = String.valueOf(txtRefri.getText());
                    remove(strNum, refrigeranteValue);
                    tectoy.escreveDisplay("Refrigerante", "-R$"+refrigeranteValue);
                    txtRefri.setText(strNum);
                }
            });
            btnSuco.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    sucoValue = produtos.getPreço("588968759512");
                    strNum = String.valueOf(txtSuco.getText());
                    soma(strNum, sucoValue);
                    tectoy.escreveDisplay("Suco", "R$"+sucoValue);
                    txtSuco.setText(strNum);
                }
            });
            btnSuco2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    sucoValue = produtos.getPreço("588968759512");
                    strNum = String.valueOf(txtSuco.getText());
                    remove(strNum, sucoValue);
                    tectoy.escreveDisplay("Suco", "-R$"+sucoValue);
                    txtSuco.setText(strNum);
                }
            });
            try {
                //Configura a porta e velocidade de comunicação com a balança.
                // A porta ttyS3 que está no exemplo é para o dispositivo D2s, para outros dispositivos consultar o link abaixo da Sunmi
                //https://developer.sunmi.com/docs/en-US/xeghjk491/cideghjk524
                SerialPort serialPort = new SerialPort(new File("/dev/ttyHSL3"), 9600, 0);// para T2MINI - ttyHSL3
                mOutputStream = serialPort.getOutputStream();
                mInputStream = serialPort.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btnSService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Double vlrSService;
                    if (txtSService.getText().equals("0")) {
                        sservicePeso = lePeso();
                        if (sservicePeso.contains("III")) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            sservicePeso = lePeso();
                        }
                        vlrSService = (Double.parseDouble(selfservValue) * Double.parseDouble(sservicePeso) / 1000);
                        sservicePeso = String.format("%.3f", (Double.parseDouble(sservicePeso) / 1000)).replace(",", ".");
                        soma("0", String.format("%.3f", vlrSService));
                        tectoy.escreveDisplay("SelfService", "R$" + String.format("%.3f", vlrSService).replace(",", "."));
                        txtSService.setText(sservicePeso);
                    }

                }
            });
            btnSorvete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Double vlrSorvete;
                    if (txtSorvete.getText().equals("0")) {
                        sorvetePeso = lePeso();
                        if (sorvetePeso.contains("III")) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            sorvetePeso = lePeso();
                        }
                        vlrSorvete = (Double.parseDouble(sorveteValue) * Double.parseDouble(sorvetePeso) / 1000);
                        sorvetePeso = String.format("%.3f", (Double.parseDouble(sorvetePeso) / 1000)).replace(",", ".");
                        soma("0", String.format("%.3f", vlrSorvete));
                        tectoy.escreveDisplay("Sorvete/Kg", "R$" + String.format("%.3f", vlrSorvete).replace(",", "."));
                        txtSorvete.setText(sorvetePeso);
                    }

                }
            });
            btnSobremesa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Double vlrSobrem;
                        if (txtSobremesa.getText().equals("0")){
                            sobremesaPeso = lePeso();
                            if (sobremesaPeso.contains("III")){
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                sobremesaPeso = lePeso();
                            }
                            vlrSobrem = (Double.parseDouble(sobremesaValue)*Double.parseDouble(sobremesaPeso)/1000);
                            sobremesaPeso = String.format("%.3f", Double.parseDouble(sobremesaPeso)/1000).replace(",",".");
                            soma("0", String.format("%.3f", vlrSobrem));
                            tectoy.escreveDisplay("Sorvete/Kg", "R$"+ String.format("%.3f", vlrSobrem).replace(",","."));
                            txtSobremesa.setText(sobremesaPeso);
                        }
                    }
            });

            btnEncerra.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (iItens>0) {
                        strAux="";
                        try {
                            service.execute(new Runnable() {
                                int retorno = 0;
                                @Override
                                public void run() {
                                    runOnUiThread(() -> {load.setVisibility(View.VISIBLE);});
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tectoy.escreveDisplay("Finalizando...", "Volte Sempre!");
                                            Log.i("DEMO T2MINI", "Encerramento iniciou....................");
                                        }
                                    });
                                    retorno = vendaBuferizadaNFCE();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            zerarValores();
                                            runOnUiThread(() -> {load.setVisibility(View.GONE);});
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                                                btnVolta.callOnClick();
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (Exception e) {
                            strAux= e.getMessage();
                            Log.i("DEMO T2MINI", "Ocorreu erro na venda.: " +strAux);
                            Toast.makeText(MainActivity.this, "Ocorreu erro na venda: "+ strAux, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    else{
                        Log.i("DEMO T2MINI", "Venda VAZIA.....");
                        Toast.makeText(MainActivity.this, "Venda VAZIA, escolha os itens para pagamento.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Não tem permissões: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

    } //OnCreate

    String lePeso(){
        byte[] retArray = new byte[10];
        String pesoLido;
        byte[] mBufferW = new byte[1];
        Arrays.fill(mBufferW, (byte) 0x05); //comando que deve ser enviado para que a balança retorne o peso.
        byte[] mBuffer = new byte[10];
        Arrays.fill(mBuffer, (byte) 0x00);
        try {
            mOutputStream.write(mBufferW); //escrita do comando
            mInputStream.read(mBuffer); //leitura da resposta da balança
            int iPos = 0;
            for(int i = 0; i< mBuffer.length; i++) {
                if(mBuffer[i] != 0x02 && mBuffer[i] != 0x03) {
                    if((byte)mBuffer[i] == (byte)0x03) {
                        break;
                    }
                    retArray[iPos] = mBuffer[i];
                    iPos++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(retArray);
    }
    void zerarValores(){
        iItens= 0;
        soma = "0";
        iNum=0;
        txtLanche.setText("0");
        txtBatata.setText("0");
        txtSService.setText("0");
        txtRefri.setText("0");
        txtSuco.setText("0");
        txtSobremesa.setText("0");
        txtSorvete.setText("0");
        txtItens.setText(String.valueOf(iItens));
        txtValor.setText(soma);
    }

    void venderItem(int Item, int iQtd){
        switch (Item){
            case 1:
                dmf.aCFVenderCompleto_NFCe("0", String.valueOf(iQtd), hamburguerValue, "D$", "0.00", "1111", "18063210", "5102", "UN", "Lanche", "CEST=1705700;cEAN=7896022204969;cEANTrib=7896022204969;");
                break;
            case 2:
                dmf.aCFVenderCompleto_NFCe("0", String.valueOf(iQtd), fritasValue, "D$", "0.00", "1112", "18063210", "5102", "UN", "Batata", "CEST=1705700;cEAN=7896022204969;cEANTrib=7896022204969;");
                break;
            case 3:
                dmf.aCFVenderCompleto_NFCe("0", sservicePeso, selfservValue, "D$", "0.00", "1113", "18063210", "5102", "UN", "SelfService p/ Kg", "CEST=1705700;cEAN=7896022204969;cEANTrib=7896022204969;");
                break;
            case 4:
                dmf.aCFVenderCompleto_NFCe("0", String.valueOf(iQtd), refrigeranteValue, "D$", "0.00", "1114", "18063210", "5102", "UN", "Refrigerante", "CEST=1705700;cEAN=7896022204969;cEANTrib=7896022204969;");
                break;
            case 5:
                dmf.aCFVenderCompleto_NFCe("0", String.valueOf(iQtd), sucoValue, "D$", "0.00", "1115", "18063210", "5102", "UN", "Suco", "CEST=1705700;cEAN=7896022204969;cEANTrib=7896022204969;");
                break;
            case 6:
                dmf.aCFVenderCompleto_NFCe("0", sobremesaPeso, sobremesaValue, "D$", "0.00", "1116", "18063210", "5102", "UN", "Sobremesa por kilo", "CEST=1705700;cEAN=7896022204969;cEANTrib=7896022204969;");
                break;
            case 7:
                dmf.aCFVenderCompleto_NFCe("0", sorvetePeso, sorveteValue, "D$", "0.00", "1117", "18063210", "5102", "UN", "Sorvete por kilo", "CEST=1705700;cEAN=7896022204969;cEANTrib=7896022204969;");
                break;
        }

    }
    void soma(String strNumero, String iValor){
        iNum= Integer.parseInt(strNumero);
        iNum= iNum+1;
        strNum= String.valueOf(iNum);
        iItens= iItens+1;
        Double tmp = (Double.parseDouble(soma.replace(",",".")) + Double.parseDouble(iValor.replace(",",".")));
        soma = String.format("%.2f",tmp);
        txtItens.setText(String.valueOf(iItens));
        txtValor.setText(soma);
    }
    void remove(String strNumero, String iValor){
        iNum= Integer.parseInt(strNumero);
        if (iNum>0) {
            iNum = iNum - 1;
            if (iItens > 0) {
                iItens = iItens - 1;
//                soma = String.format("%.2f",String.valueOf(Float.parseFloat(soma.replace(",","."))  -+ Float.parseFloat(iValor.replace(",","."))));
                float tmp = (Float.parseFloat(soma.replace(",",".")) - Float.parseFloat(iValor.replace(",",".")));
                soma = String.format("%.2f",tmp);
            }
        }
        strNum= String.valueOf(iNum);
        txtItens.setText(String.valueOf(iItens));
        txtValor.setText(soma);
    }
    private Runnable vendaNFCE = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                vendaBuferizadaNFCE();
            } catch (Exception de) {
                strAux= de.getMessage();
                Toast.makeText(MainActivity.this, "Erro na venda: "+strAux, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };
    int vendaBuferizadaNFCE(){
        try {
            int ret = 0;
            iLanche = Integer.parseInt(String.valueOf(txtLanche.getText()));
            iBatata = Integer.parseInt(String.valueOf(txtBatata.getText()));
            iSService = Double.parseDouble((String) txtSService.getText());
            iRefrig = Integer.parseInt(String.valueOf(txtRefri.getText()));
            iSuco = Integer.parseInt(String.valueOf(txtSuco.getText()));
            iSobrem = Double.parseDouble((String) txtSobremesa.getText());
            iSorvt = Double.parseDouble((String) txtSorvete.getText());
//        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\EstadoCFe","0");
            dmf.aCFAbrir_NFCe("", "", "", "", "", "", "", "", "");


            if (iLanche>0){
                venderItem(1, iLanche);
            }
            if (iBatata>0){
                venderItem(2, iBatata);
            }
            if (iSService>0.000){
                venderItem(3, 1);
            }
            if (iRefrig>0){
                venderItem(4, iRefrig);
            }
            if (iSuco>0){
                venderItem(5, iSuco);
            }
            if (iSobrem>0.000){
                venderItem(6, 1);
            }
            if (iSorvt>0.000){
                venderItem(7, 1);
            }
            dmf.aCFTotalizar_NFCe("D$", "0.00");
            dmf.aCFEfetuarPagamento_NFCe("01", String.valueOf(soma));
            ret = dmf.tCFEncerrar_NFCe("DEMONSTRAÇÃO - TECTOY demonstração de uso do T2Mini com a IT4R(NFCE) + API Etiquetas. TecToy Automação");
            dmf.enviarComando(" \n \n \n \n \n"+ ((char) 0x1D) + ((char) 0x56)+ ((char) 0x30));
            tectoy.escreveDisplay("OBRIGADO POR COMPRAR CONOSCO", "VOLTE SEMPRE");

            return ret;
        }  catch (Exception e){
            Log.i("DEMO T2MINI", "Erro no encerramento: "+e.getMessage());
            return 0;
        }
    }
    void efetuaLogin(){
        strUsuario = "seu@Login"; //seu usuário do portal Etiquetas-TecToy
        strSenha  = "sua@senha"; //sua senha
        strToken = "";
        idLoja = "99"; //id da sua loja
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("login", strUsuario);
                        jsonObject.put("password", strSenha);
                        String jsonString = jsonObject.toString();
                        new PostDataLogin().execute(jsonString);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
       Log.i("DEMO T2Mini", "Token: "+strToken);
    }

    public Runnable config = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                configGneToNfce();
            } catch (Exception de) {
                strAux= de.getMessage();
                Toast.makeText(MainActivity.this, "Erro na configuração: "+strAux, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    void consultaProdutos(){
    strPreco = "";
    Log.i("DEMO Coletor", "Código pra consulta: " + strProduto);
    String[] arrayProds = {"588968759433", "588968759436", "588968759513", "588968759438", "588968759512", "588968759475", "588968759514"};
    cTot = arrayProds.length;
    for(int i=0; i<arrayProds.length; i++){
        strProduto = arrayProds[i];
        try {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("product_id", strProduto);
            jsonObject2.put("shop_id", idLoja);
            String jsonString2 = jsonObject2.toString();
            new PostDataConsulta().execute(jsonString2);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
    void configGneToNfce() {
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\TipoAmbiente", "2", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\EmpPK", "<sua chave de parceiro>", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\EmpCK", "<sua chave de cliente>", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\Token", "<seu token sefaz (CSC)", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\EmpCO", "001", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\IdToken", "000001", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\ArredondarTruncar", "A", false);
        dmf.RegAlterarValor_NFCe("EMIT\\CRT", "3", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\Impressora", "TECTOY_80", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\AvisoContingencia", "1", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\ImpressaoCompleta", "1", false);
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\NumeracaoAutomatica", "1", false);
        //configurar com as informações do emitente
        dmf.RegAlterarValor_NFCe("IDE\\cUF", "43", false);
        dmf.RegAlterarValor_NFCe("IDE\\cMunFG", "4321808", false);
        dmf.RegAlterarValor_NFCe("EMIT\\CNPJ", "99999999000199", false);
        dmf.RegAlterarValor_NFCe("EMIT\\IE", "9999999999", false);
        dmf.RegAlterarValor_NFCe("EMIT\\xNome", "IT FAST - TESTES", false);
        dmf.RegAlterarValor_NFCe("EMIT\\ENDEREMIT\\UF", "SP", false);
        //imposto para as vendas
        dmf.RegAlterarValor_NFCe("IMPOSTO\\ICMS\\ICMS00\\orig", "0", false);
        dmf.RegAlterarValor_NFCe("IMPOSTO\\ICMS\\ICMS00\\CST", "00", false);
        dmf.RegAlterarValor_NFCe("IMPOSTO\\ICMS\\ICMS00\\modBC", "3", false);
        dmf.RegAlterarValor_NFCe("IMPOSTO\\PIS\\PISNT\\CST", "07", false);
        dmf.RegAlterarValor_NFCe("IMPOSTO\\COFINS\\COFINSNT\\CST", "07", false);
        dmf.RegPersistirXML_NFCe();

        dmf.confNumSeriesNF_NFCe("03", "890");
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\HabilitarSAT", "0");
        dmf.RegAlterarValor_NFCe("CONFIGURACAO\\EstadoCFe", "0");
    }

// Classe para ler o Token - realizar login
    class PostDataLogin extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                // url d API para login
                URL url = new URL("https://itfastesl.azurewebsites.net/login");
                // Abrir conexão
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                // definindo o método post
                client.setRequestMethod("POST");
                // definindo tipo de conteudo envio e resposta.
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestProperty("Accept", "application/json");
                // configurando
                client.setDoOutput(true);
                // Criando o envio, enviando
                try (OutputStream os = client.getOutputStream()) {
                    byte[] input = strings[0].getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                // criando/ inicializando buffer
                if(client.getResponseCode() == 500) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(client.getErrorStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;

                        // escrevendo a resposta
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        strToken = response.toString();
                    }
                } else {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(client.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;

                        // escrevendo a resposta
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        strToken = response.toString();
                    }
                }

            } catch (Exception e) {
                Log.d("DEMO T2Mini", "Falha ao fazer login : " + e.getMessage() );
            }
            return strToken;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                Log.i("DEMO T2Mini", "antes de remover aspas: " + s );
                if (s!=null || s.length()!=0 || !s.equals("") ){
                    if (!s.contains("err")){
                        strToken = s.replace("\"", "");
                        produtos.setToken(strToken);
                    }else {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Erro, login não realizado.", Toast.LENGTH_LONG).show();
                        });
                    }
                    Log.i("DEMO T2Mini", "postExecute Token: "+strToken);
                }
                load.setVisibility(View.INVISIBLE);
                consultaProdutos();
            }catch (Exception e){
                Log.d("DEMO T2Mini", e.getMessage());

            }

        }
    }

    private static TrustManager[] trustManagers;
// Consultar Produto /api/product/getInfo
    class PostDataConsulta extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        // verifica se o host pertence ao webservice que temos acesso
                        return hostname.contains("itfastesl");
                    }
                });

                SSLContext context = null;
                if (trustManagers == null) {
                    trustManagers = new TrustManager[]{new TrustedManagerManipulator()};
                }

                try {
                    context = SSLContext.getInstance("TLS");
                    context.init(null, trustManagers, new SecureRandom());

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();

                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }

                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
                // url d API para Consulta Produto
                URL url = new URL("https://itfastesl.azurewebsites.net/api/product/getInfo");
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                //TrustedManagerManipulator.allowAllSSL();
                client.setRequestMethod("POST");
                client.setRequestProperty("Authorization", strToken);
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestProperty("Accept", "application/json");
                client.setDoOutput(true);
                try (OutputStream os = client.getOutputStream()) {
                    byte[] input = strings[0].getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                if(client.getResponseCode() == 500) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(client.getErrorStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        strProduto = response.toString();
                    }
                } else {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(client.getInputStream(), "utf-8"))) {
                        StringBuilder respProd = new StringBuilder();
                        String responseLine = null;
                        // escrevendo a resposta
                        while ((responseLine = br.readLine()) != null) {
                            respProd.append(responseLine.trim());
                        }
                        strProduto = respProd.toString();
                    }
                }
            } catch (Exception e) {
                Log.d("DEMO T2Mini", "Erro na consulta: " + e.getMessage() );
            }

            return strProduto;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if (s!=null || s.length()!=0){
                    if(!s.contains("err")){
                        JSONObject jsonProduto = new JSONObject(s);
                        JSONObject prod = new JSONObject(jsonProduto.getString("data").toString());
                        strProduto = prod.getString("seq_num").toString();
                        strPreco =prod.getString("price").toString();

                        if (strProduto.equals("588968759433")) {// Hamburguer
                            hamburguerValue = strPreco;
                            produtos.setPreco("588968759433", strPreco);
                            Log.i("DEMO T2Mini","Consulta preencheu: "+strProduto+" | " +strPreco);
                        } else if (strProduto.equals("588968759436")) {// fritas
                            fritasValue = strPreco;
                            produtos.setPreco("588968759436", strPreco);
                            Log.i("DEMO T2Mini","Consulta preencheu: "+strProduto+" | " +strPreco);
                        } else if (strProduto.equals("588968759513")) {// selfservice
                            selfservValue  = strPreco;
                            produtos.setPreco("588968759513", strPreco);
                            Log.i("DEMO T2Mini","Consulta preencheu: "+strProduto+" | " +strPreco);
                        } else if (strProduto.equals("588968759438")) {// refrigerante
                            refrigeranteValue  = strPreco;
                            produtos.setPreco("588968759438", strPreco);
                            Log.i("DEMO T2Mini","Consulta preencheu: "+strProduto+" | " +strPreco);
                        } else if (strProduto.equals("588968759512")) { //suco
                            sucoValue  = strPreco;
                            produtos.setPreco("588968759512", strPreco);
                            Log.i("DEMO T2Mini","Consulta preencheu: "+strProduto+" | " +strPreco);
                        } else if (strProduto.equals("588968759475")) { //sorvete
                            sorveteValue  = strPreco;
                            produtos.setPreco("588968759475", strPreco);
                            Log.i("DEMO T2Mini","Consulta preencheu: "+strProduto+" | " +strPreco);
                        } else if (strProduto.equals("588968759514")) {// sobremesa
                            sobremesaValue  = strPreco;
                            produtos.setPreco("588968759514", strPreco);
                            Log.i("DEMO T2Mini","Consulta preencheu: "+strProduto+" | " +strPreco);
                        } else {
                            Log.i("DEMO T2Mini", "Consulta Preço retornou produto inválido.");
                        }
                        count++;
                    }else{
                        Log.d("DEMO T2Mini",  "Ocorreu erro: " + s);
                    }
                }
            }catch (JSONException e) {
                Log.d("DEMO T2Mini", e.getMessage());
            }
            if (count >= (cTot-1)){
                load.setVisibility(View.GONE);
            }

        }
    }

}

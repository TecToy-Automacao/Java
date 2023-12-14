package com.example.t2app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.daruma.framework.mobile.DarumaMobile;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    DarumaMobile dmf;
    public static TecToy t2m;
    Button btnVoltar, btnVenda, btnItem;
    TextView txtQtd, txtVlrUnit, txtTotItem, txtNota;
    Spinner spnComb, spnBico;

    int flag=0;
    int iLitros=0;
    int iItens=0;
    int item=0;
    int total=0;
    int i=0;
    int iValor=0;
    public int iBomba=0, iBico=0;
    String strXML, strNum;
    char[] resp=new char[10];
    String strNota;
    String strItemAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //verifica se tem permissoes necessarias
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
            if (checkSelfPermission(Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.NFC}, 0);
            }
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 0);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
            }
        }



        txtQtd = findViewById(R.id.txtQuant);
        txtTotItem = findViewById(R.id.txtVlrTotal);
        txtVlrUnit = findViewById(R.id.txtVlrUnit);
        spnComb = findViewById(R.id.spnComb);
        spnBico = findViewById(R.id.spnBico);
        txtNota = findViewById(R.id.txtNota);

        //dmf = DarumaMobile.inicializar(MainActivity.this, "@FRAMEWORK(TRATAEXCECAO=TRUE;TIMEOUTWS=10000;);@DISPOSITIVO(NAME=K2_MINI)");
        dmf = DarumaMobile.inicializar(MainActivity.this, "@FRAMEWORK(TRATAEXCECAO=TRUE;LOGMEMORIA=25;TIMEOUTWS=10000;);@BLUETOOTH(NAME=InnerPrinter;ATTEMPTS=100;TIMEOUT=10000)");

        t2m = new TecToy(Dispositivo.T2_MINI, this);

        strNota = "TecToy Automação - T2 Mini\n"
                + "acesse e saiba mais: \n"
                + "https://www.tectoyautomacao.com.br\n"
                + "-----------------------------------------------\n"
                + "AppDemo de uso do T2 Mini - Venda Combustível\n"
                + "-----------------------------------------------\n";

        Thread thrCgf;
        try {
            thrCgf = new Thread(config);
            thrCgf.start();
            thrCgf.join();
        } catch (Exception e) {
            mensagem("Erro na inicialização: " + e.getMessage());
        }

        btnVoltar = findViewById(R.id.button_back);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                t2m.limparDisplay();
                zerarValores();
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        btnItem = findViewById(R.id.button_add_item);
        btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(String.valueOf(txtQtd.getText()))>0){
                    soma(strNum, iValor);
                    strNota += strItemAdd;
                    txtNota.setText(strNota);
                    t2m.escreveDisplay("Tot item: "+String.valueOf(item)+".00","Subtotal: RS"+String.valueOf(total)+"0.00" );
                }
                else mensagem("Quantidade zerada...");
            }
        });

        btnVenda = findViewById(R.id.button_finish);
        btnVenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thrImp;
                if (iItens > 0) {
                    try {
                        String strAux = "";
                        strNota += " ============================================ \n";
                        strNota += " TOTAL - R$"+total+".00";
                        try {
                            flag++;
                            thrImp = new Thread(nfceImp);
                            thrImp.start();
                            thrImp.join();
                        } catch (Exception e) {
                            mensagem("Ocorreu erro na impressão:" + e.getMessage());
                        }
                        zerarValores();
                        btnVoltar.callOnClick();
                    } catch (Exception e) {
                        mensagem("Ocorreu erro durante a venda" + e.getMessage());
                    }
                } else {
                    mensagem("Venda VAZIA, escolha os itens para pagamento.");
                }
                t2m.limparDisplay();
            }
        });

        spnComb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizaValores();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnBico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizaValores();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        txtQtd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                strNum = String.valueOf(txtQtd.getText());
                iLitros = Integer.parseInt(strNum);

                if (iLitros>0) atualizaValores();
            }
        });

    }//onCreate

    void mensagem(String msg){
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    void atualizaValores(){
        if (spnComb.getSelectedItem().equals("1 - Gasolina")){
            if (spnBico.getSelectedItemPosition()==0)  {
                txtVlrUnit.setText("R$ 5.00");
                iValor = 5;
                strItemAdd = "GASOLINA COMUM         ";
            } else{
                txtVlrUnit.setText("R$ 6.00");
                iValor = 6;
                strItemAdd = "Gasolina Aditivada     ";
            }
            strNota = strNota+"";
        }else if (spnComb.getSelectedItem().equals("2 - Etanol")){
            if (spnBico.getSelectedItemPosition()==0){
                txtVlrUnit.setText("R$ 4.00");
                iValor = 4;
                strItemAdd = "ETANOL HIDRATADO COMUM ";
            } else {
                txtVlrUnit.setText("R$ 5.00");
                iValor = 5;
                strItemAdd = "ETANOL V-Power         ";
            }
        }else if (spnComb.getSelectedItem().equals("3 - Diesel")){
            if (spnBico.getSelectedItemPosition()==0){
                txtVlrUnit.setText("R$ 6.00");
                iValor = 6;
                strItemAdd = "DIESEL B5 COMUM        ";
            } else{
                txtVlrUnit.setText("R$ 7.00");
                iValor = 6;
                strItemAdd = "DIESEL B5              ";
            }

        }

    }

    private Runnable nfceImp = new Runnable() {
        @Override
        public void run() {
            if (flag==1){
                strXML = "<nfeProc versao=\"4.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\"><NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\"><infNFe Id=\"NFe43221106354976000149650670000016331026846523\" versao=\"4.00\"><ide><cUF>43</cUF><cNF>02684652</cNF><natOp>Venda</natOp><mod>65</mod><serie>67</serie><nNF>1633</nNF><dhEmi>2022-11-09T19:28:41-02:00</dhEmi><tpNF>1</tpNF><idDest>1</idDest><cMunFG>4321808</cMunFG><tpImp>4</tpImp><tpEmis>1</tpEmis><cDV>3</cDV><tpAmb>2</tpAmb><finNFe>1</finNFe><indFinal>1</indFinal><indPres>1</indPres><procEmi>0</procEmi><verProc>1.0</verProc></ide><emit><CNPJ>11111111111111</CNPJ><xNome>IT FAST - TESTES</xNome><enderEmit><xLgr>Av. Shishima Hifumi</xLgr><nro>2911</nro><xBairro>Urbanova</xBairro><cMun>4321808</cMun><xMun>S.J.C.</xMun><UF>SP</UF><CEP>11111111</CEP></enderEmit><IE>1234567890</IE><CRT>3</CRT></emit><det nItem=\"1\"><prod><cProd>1113</cProd><cEAN>7896022204969</cEAN><xProd>NOTA FISCAL EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UN</uCom><qCom>2</qCom><vUnCom>10.00</vUnCom><vProd>20.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UN</uTrib><qTrib>2</qTrib><vUnTrib>10.00</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>20.00</vBC><pICMS>0.00</pICMS><vICMS>0.00</vICMS></ICMS00></ICMS><PIS><PISNT><CST>07</CST></PISNT></PIS><COFINS><COFINSNT><CST>07</CST></COFINSNT></COFINS></imposto></det><det nItem=\"2\"><prod><cProd>1114</cProd><cEAN>7896022204969</cEAN><xProd>Refrigerante</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UN</uCom><qCom>1</qCom><vUnCom>5.00</vUnCom><vProd>5.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UN</uTrib><qTrib>1</qTrib><vUnTrib>5.00</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>5.00</vBC><pICMS>0.00</pICMS><vICMS>0.00</vICMS></ICMS00></ICMS><PIS><PISNT><CST>07</CST></PISNT></PIS><COFINS><COFINSNT><CST>07</CST></COFINSNT></COFINS></imposto></det><total><ICMSTot><vBC>25.00</vBC><vICMS>0.00</vICMS><vICMSDeson>0.00</vICMSDeson><vFCP>0.00</vFCP><vBCST>0.00</vBCST><vST>0.00</vST><vFCPST>0.00</vFCPST><vFCPSTRet>0.00</vFCPSTRet><vProd>25.00</vProd><vFrete>0.00</vFrete><vSeg>0.00</vSeg><vDesc>0.00</vDesc><vII>0.00</vII><vIPI>0.00</vIPI><vIPIDevol>0.00</vIPIDevol><vPIS>0.00</vPIS><vCOFINS>0.00</vCOFINS><vOutro>0.00</vOutro><vNF>25.00</vNF></ICMSTot></total><transp><modFrete>9</modFrete></transp><pag><detPag><tPag>01</tPag><vPag>25.00</vPag></detPag></pag><infAdic><infCpl>Teste IT FAST demonstracao.</infCpl></infAdic><infRespTec><CNPJ>06354976000149</CNPJ><xContato>Adilson Weddigen</xContato><email>adilsonweddigen@migrate.info</email><fone>5535354800</fone></infRespTec></infNFe><infNFeSupl><qrCode><![CDATA[https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx?p=43221106354976000149650670000016331026846523|2|2|1|2815F2B11827699A7DF518B601097FBA89F18F5D]]></qrCode><urlChave>www.sefaz.rs.gov.br/nfce/consulta</urlChave></infNFeSupl><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\" /><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><Reference URI=\"#NFe43221106354976000149650670000016331026846523\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\" /></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><DigestValue>rLSKkFib6NDR2BdbZzr9f2Fi6rQ=</DigestValue></Reference></SignedInfo><SignatureValue>0G4ep/bUFCRN0Dq6iPIYtcFAt7yMXYlEDFdfkG7CwbcW1BkV8dxTFOvFR7kIFTVAfImVPNdN52GaZINhSUnGfMIzfuLbcZIL1oVgroAePeiU6TquZsITFKCnHTG3UYl9UqkrMM/iqbLJh/p7/qdoS70ndElWcWF18MWDqGKAQhGkGQWmfvi0bOcJtifC/PSjmRAGVyXsohcaO8zfQWdJs8zu5+UJ9PRNQbNN5vqwXDSOAktIJkKK9QbWaq6bHTIVbws+vQrLV+JjmZisvD5YLgeTesAs33ZZ80YH1X4ob9X0P3a/GA+6q/1knze3VGr1Bm85Av/cNxU8w9EiyIOTPA==</SignatureValue><KeyInfo><X509Data><X509Certificate>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</X509Certificate></X509Data></KeyInfo></Signature></NFe><protNFe versao=\"4.00\"><infProt><tpAmb>2</tpAmb><verAplic>RSnfce202210200825</verAplic><chNFe>43221106354976000149650670000016331026846523</chNFe><dhRecbto>2022-11-09T18:28:44-03:00</dhRecbto><nProt>143220000797520</nProt><digVal>rLSKkFib6NDR2BdbZzr9f2Fi6rQ=</digVal><cStat>100</cStat><xMotivo>Autorizado o uso da NF-e</xMotivo></infProt></protNFe></nfeProc>";
            }
            else if (flag==2){
                strXML = "<nfeProc versao=\"4.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\"><NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\"><infNFe Id=\"NFe43221106354976000149650670000016351026912105\" versao=\"4.00\"><ide><cUF>43</cUF><cNF>02691210</cNF><natOp>Venda</natOp><mod>65</mod><serie>67</serie><nNF>1635</nNF><dhEmi>2022-11-09T19:29:52-02:00</dhEmi><tpNF>1</tpNF><idDest>1</idDest><cMunFG>4321808</cMunFG><tpImp>4</tpImp><tpEmis>1</tpEmis><cDV>5</cDV><tpAmb>2</tpAmb><finNFe>1</finNFe><indFinal>1</indFinal><indPres>1</indPres><procEmi>0</procEmi><verProc>1.0</verProc></ide><emit><CNPJ>11111111111111</CNPJ><xNome>IT FAST - TESTES</xNome><enderEmit><xLgr>Av. Shishima Hifumi</xLgr><nro>2911</nro><xBairro>Urbanova</xBairro><cMun>4321808</cMun><xMun>S.J.C.</xMun><UF>SP</UF><CEP>11111111</CEP></enderEmit><IE>1234567890</IE><CRT>3</CRT></emit><det nItem=\"1\"><prod><cProd>1112</cProd><cEAN>7896022204969</cEAN><xProd>NOTA FISCAL EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UN</uCom><qCom>4</qCom><vUnCom>7.00</vUnCom><vProd>28.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UN</uTrib><qTrib>4</qTrib><vUnTrib>7.00</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>28.00</vBC><pICMS>0.00</pICMS><vICMS>0.00</vICMS></ICMS00></ICMS><PIS><PISNT><CST>07</CST></PISNT></PIS><COFINS><COFINSNT><CST>07</CST></COFINSNT></COFINS></imposto></det><det nItem=\"2\"><prod><cProd>1114</cProd><cEAN>7896022204969</cEAN><xProd>Refrigerante</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UN</uCom><qCom>7</qCom><vUnCom>5.00</vUnCom><vProd>35.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UN</uTrib><qTrib>7</qTrib><vUnTrib>5.00</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>35.00</vBC><pICMS>0.00</pICMS><vICMS>0.00</vICMS></ICMS00></ICMS><PIS><PISNT><CST>07</CST></PISNT></PIS><COFINS><COFINSNT><CST>07</CST></COFINSNT></COFINS></imposto></det><det nItem=\"3\"><prod><cProd>1116</cProd><cEAN>7896022204969</cEAN><xProd>Tortinha</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UN</uCom><qCom>2</qCom><vUnCom>5.00</vUnCom><vProd>10.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UN</uTrib><qTrib>2</qTrib><vUnTrib>5.00</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>10.00</vBC><pICMS>0.00</pICMS><vICMS>0.00</vICMS></ICMS00></ICMS><PIS><PISNT><CST>07</CST></PISNT></PIS><COFINS><COFINSNT><CST>07</CST></COFINSNT></COFINS></imposto></det><total><ICMSTot><vBC>73.00</vBC><vICMS>0.00</vICMS><vICMSDeson>0.00</vICMSDeson><vFCP>0.00</vFCP><vBCST>0.00</vBCST><vST>0.00</vST><vFCPST>0.00</vFCPST><vFCPSTRet>0.00</vFCPSTRet><vProd>73.00</vProd><vFrete>0.00</vFrete><vSeg>0.00</vSeg><vDesc>0.00</vDesc><vII>0.00</vII><vIPI>0.00</vIPI><vIPIDevol>0.00</vIPIDevol><vPIS>0.00</vPIS><vCOFINS>0.00</vCOFINS><vOutro>0.00</vOutro><vNF>73.00</vNF></ICMSTot></total><transp><modFrete>9</modFrete></transp><pag><detPag><tPag>01</tPag><vPag>73.00</vPag></detPag></pag><infAdic><infCpl>Teste IT FAST demonstracao.</infCpl></infAdic><infRespTec><CNPJ>06354976000149</CNPJ><xContato>Adilson Weddigen</xContato><email>adilsonweddigen@migrate.info</email><fone>5535354800</fone></infRespTec></infNFe><infNFeSupl><qrCode><![CDATA[https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx?p=43221106354976000149650670000016351026912105|2|2|1|2B7B79C12C2F8F428778EE16B036323D10983EBA]]></qrCode><urlChave>www.sefaz.rs.gov.br/nfce/consulta</urlChave></infNFeSupl><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\" /><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><Reference URI=\"#NFe43221106354976000149650670000016351026912105\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\" /></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><DigestValue>2nAg5o3tqaYj1JKg5tqY/VAdqgc=</DigestValue></Reference></SignedInfo><SignatureValue>gkSIdFhAXMs6RXTa342HgmgQ4bzrnerRzlCrClP+AGId0OskArGRv+F7Ex8u7QLRraCKTyFMsHaIFZK2h1DUbBBfj421dQnflxhHoHG8jLj208EAoIh1RtVIykSgOCDqUWGV/TLkrmw0SZcHpSzPsa/29sxQvFnrOtdpc+CIVJOUfBkm/nxULtighSLztPhC6Q69EID4Q7E8ErBUUJ4uCEPnxHQzAHBMa+A8DEhV+pxG3XKkAcr74w4p/kaiIciqZG71uxUTugWkVUfuOGonHmgiWlfeRT2yj5CTMS5gt75YIk6aOINSGobLpZtsuPSvmnTF2gsMhm7HPm0jEyEoxg==</SignatureValue><KeyInfo><X509Data><X509Certificate>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</X509Certificate></X509Data></KeyInfo></Signature></NFe><protNFe versao=\"4.00\"><infProt><tpAmb>2</tpAmb><verAplic>RSnfce202210200825</verAplic><chNFe>43221106354976000149650670000016351026912105</chNFe><dhRecbto>2022-11-09T18:29:55-03:00</dhRecbto><nProt>143220000797523</nProt><digVal>2nAg5o3tqaYj1JKg5tqY/VAdqgc=</digVal><cStat>100</cStat><xMotivo>Autorizado o uso da NF-e</xMotivo></infProt></protNFe></nfeProc>";
            }
            else if (flag==3){
                strXML = "<nfeProc versao=\"4.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\"><NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\"><infNFe Id=\"NFe43221106354976000149650670000016361026944926\" versao=\"4.00\"><ide><cUF>43</cUF><cNF>02694492</cNF><natOp>Venda</natOp><mod>65</mod><serie>67</serie><nNF>1636</nNF><dhEmi>2022-11-09T19:33:15-02:00</dhEmi><tpNF>1</tpNF><idDest>1</idDest><cMunFG>4321808</cMunFG><tpImp>4</tpImp><tpEmis>1</tpEmis><cDV>6</cDV><tpAmb>2</tpAmb><finNFe>1</finNFe><indFinal>1</indFinal><indPres>1</indPres><procEmi>0</procEmi><verProc>1.0</verProc></ide><emit><CNPJ>11111111111111</CNPJ><xNome>IT FAST - TESTES</xNome><enderEmit><xLgr>Av. Shishima Hifumi</xLgr><nro>2911</nro><xBairro>Urbanova</xBairro><cMun>4321808</cMun><xMun>S.J.C.</xMun><UF>SP</UF><CEP>11111111</CEP></enderEmit><IE>1234567890</IE><CRT>3</CRT></emit><det nItem=\"1\"><prod><cProd>1111</cProd><cEAN>7896022204969</cEAN><xProd>NOTA FISCAL EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UN</uCom><qCom>1</qCom><vUnCom>10.00</vUnCom><vProd>10.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UN</uTrib><qTrib>1</qTrib><vUnTrib>10.00</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>10.00</vBC><pICMS>0.00</pICMS><vICMS>0.00</vICMS></ICMS00></ICMS><PIS><PISNT><CST>07</CST></PISNT></PIS><COFINS><COFINSNT><CST>07</CST></COFINSNT></COFINS></imposto></det><det nItem=\"2\"><prod><cProd>1112</cProd><cEAN>7896022204969</cEAN><xProd>Batata</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UN</uCom><qCom>1</qCom><vUnCom>7.00</vUnCom><vProd>7.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UN</uTrib><qTrib>1</qTrib><vUnTrib>7.00</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>7.00</vBC><pICMS>0.00</pICMS><vICMS>0.00</vICMS></ICMS00></ICMS><PIS><PISNT><CST>07</CST></PISNT></PIS><COFINS><COFINSNT><CST>07</CST></COFINSNT></COFINS></imposto></det><det nItem=\"3\"><prod><cProd>1113</cProd><cEAN>7896022204969</cEAN><xProd>Nuggets</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UN</uCom><qCom>1</qCom><vUnCom>10.00</vUnCom><vProd>10.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UN</uTrib><qTrib>1</qTrib><vUnTrib>10.00</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>10.00</vBC><pICMS>0.00</pICMS><vICMS>0.00</vICMS></ICMS00></ICMS><PIS><PISNT><CST>07</CST></PISNT></PIS><COFINS><COFINSNT><CST>07</CST></COFINSNT></COFINS></imposto></det><total><ICMSTot><vBC>27.00</vBC><vICMS>0.00</vICMS><vICMSDeson>0.00</vICMSDeson><vFCP>0.00</vFCP><vBCST>0.00</vBCST><vST>0.00</vST><vFCPST>0.00</vFCPST><vFCPSTRet>0.00</vFCPSTRet><vProd>27.00</vProd><vFrete>0.00</vFrete><vSeg>0.00</vSeg><vDesc>0.00</vDesc><vII>0.00</vII><vIPI>0.00</vIPI><vIPIDevol>0.00</vIPIDevol><vPIS>0.00</vPIS><vCOFINS>0.00</vCOFINS><vOutro>0.00</vOutro><vNF>27.00</vNF></ICMSTot></total><transp><modFrete>9</modFrete></transp><pag><detPag><tPag>01</tPag><vPag>27.00</vPag></detPag></pag><infAdic><infCpl>Teste IT FAST demonstracao.</infCpl></infAdic><infRespTec><CNPJ>06354976000149</CNPJ><xContato>Adilson Weddigen</xContato><email>adilsonweddigen@migrate.info</email><fone>5535354800</fone></infRespTec></infNFe><infNFeSupl><qrCode><![CDATA[https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx?p=43221106354976000149650670000016361026944926|2|2|1|6A18B5AE19427D0C0F5996DC83B3F468DA9D0360]]></qrCode><urlChave>www.sefaz.rs.gov.br/nfce/consulta</urlChave></infNFeSupl><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\" /><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\" /><Reference URI=\"#NFe43221106354976000149650670000016361026944926\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\" /><Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\" /></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\" /><DigestValue>ST/28VkyvbzBqmo8xPh7cByvJFQ=</DigestValue></Reference></SignedInfo><SignatureValue>zJMKj1/MCkFtNWh1Koq6h7IGTWJv6Jn7AIK+2PSQHh4uCzqE3MyqYhFxWS9E7nakCFy+44T0xW4hAfSosPRCSHYTE4zpuU3Kz0+uPpU0/P9mASbtsk+oQb7CIVh67eG30FpDu8KkZ/4G7BCbOMML21DlqLjR70XpXfCMyeuNf9gczRRSBohgrIxEmVJqrYABtalHdXuwqZ1SwHwhjA1MaUIu+cQtqOMLdmwMRxJS6K1p20Pbk0cYHr5zXzFQKnfxIuJUWIG3o26ZxzH37mBLhK6GBWEqGVkxL/h0ybd3VGMX6+/ILHuteQU/QYW+my9JcRjmuQsysKlE7s14ESW4gQ==</SignatureValue><KeyInfo><X509Data><X509Certificate>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</X509Certificate></X509Data></KeyInfo></Signature></NFe><protNFe versao=\"4.00\"><infProt><tpAmb>2</tpAmb><verAplic>RSnfce202210200825</verAplic><chNFe>43221106354976000149650670000016361026944926</chNFe><dhRecbto>2022-11-09T18:33:18-03:00</dhRecbto><nProt>143220000797525</nProt><digVal>ST/28VkyvbzBqmo8xPh7cByvJFQ=</digVal><cStat>100</cStat><xMotivo>Autorizado o uso da NF-e</xMotivo></infProt></protNFe></nfeProc>";
                flag=0;
            }
            try {
                Looper.prepare();
                try {
                    t2m.imprimir(strNota);
                    t2m.acionarGuilhotina();
                    dmf.iCFImprimirParametrizado_NFCe(strXML,strXML,"",48, 1, "");
                    Thread.sleep(100);
                    t2m.acionarGuilhotina();
                }catch (Exception e){
                   mensagem("Erro: "+ e.getMessage());
                }
            }catch (Exception e){
                throw e;
            }
        }
    };

    void soma(String strNumero, int iValor){
        iLitros= Integer.parseInt(strNumero);
        iItens= iItens+1;
        item = iLitros * iValor;
        total = total + item;
        txtTotItem.setText("R$ "+ String.valueOf(total) +".00");
        strItemAdd +=""+iLitros+" lts x "+iValor+".00\n";
        strItemAdd +="                                      R$"+ item +".00\n";
        spnComb.setSelection(0);
        spnBico.setSelection(0);
        txtQtd.setText("0");

    }

    void zerarValores(){
        iItens= 0;
        total = 0;
        iLitros=0;
        ///zerar valores mostrados na tela
        spnComb.setSelection(0);
        spnBico.setSelection(0);
        txtQtd.setText("0");
        txtTotItem.setText("RS 0.00");
        txtNota.setText("Selecione o combustivel e \nClique em ADICIONAR ITEM ");
        strNota = "TecToy Automação - T2 Mini\n"
                + "acesse e saiba mais: \n"
                + "https://www.tectoyautomacao.com.br\n"
                + "-----------------------------------------------\n"
                + "AppDemo de uso do T2 Mini - Venda Combustível\n"
                + "-----------------------------------------------\n";
    }

    public Runnable config = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                configGneToNfce();
            } catch (Exception de) {
                throw de;
            }
        }
    };
    void configGneToNfce() {
            ///Confgs minimas para impressão XML pronto
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\Impressora", "Q4", false);// impressora de 80mm
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\ImpressaoCompleta", "1", false);
            dmf.RegPersistirXML_NFCe();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}



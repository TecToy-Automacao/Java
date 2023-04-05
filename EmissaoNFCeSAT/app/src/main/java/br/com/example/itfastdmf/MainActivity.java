package br.com.example.itfastdmf;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.phi.gertec.sat.satger.SatGerLib; //quando usando SAT EPSON

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.daruma.framework.mobile.DarumaMobile;
import br.com.daruma.framework.mobile.SatCallback;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;

public class MainActivity extends Activity {

        private DarumaMobile dmf;
        private TecToy objTecToy;
        int iRet = 0;
        char[] resposta = new char[50];
        char[] registroSAT = new char[200];
        int numDocs=0;

        ///Formatações de texto

    String centro = "" + ((char) 0x1B) + ((char) 0x61) + ((char) 0x31);
    String deslCentro = "" + ((char) 0x1B) + ((char) 0x61) + ((char) 0x30);
    String direita = "" + ((char) 0x1B) + ((char) 0x61) + ((char) 0x32);
    String deslDireita = "" + ((char) 0x1B) + ((char) 0x61) + ((char) 0x30);
    String extra = "" + ((char) 0x1B) + ((char) 0x21) + ((char) 0x16);
    String deslExtra = "" + ((char) 0x1B) + ((char) 0x21) + ((char) 0x00);
    String negrito = "" + ((char) 0x1B) + ((char) 0x45) + ((char) 0x31);
    String deslNegrito = "" + ((char) 0x1B) + ((char) 0x45) + ((char) 0x30);
    String invetImp = ""+((char) 0x1D) + ((char) 0x42) + ((char) 0x31);
    String desligInvert= ""+ ((char) 0x1D) + ((char) 0x42) + ((char) 0x30);

    String strAux; ///msg de retorno
    String cmdHead;
    String qrcode;


    ///Callback que ao encerrar venda SAT pega o conteúdo do XML da venda SAT
    private SatCallback satCallback = new SatCallback() {
        @Override
        public void XmlRetorno(String strXml) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    Toast.makeText(MainActivity.this.getBaseContext(), strXml, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            // instancia a biblioteca Android para emissão de DOCs fiscais, e seta dispositivo tectoy de impressão.
            dmf = DarumaMobile.inicializar(MainActivity.this, "@FRAMEWORK(TRATAEXCECAO=TRUE;LOGMEMORIA=25;TIMEOUTWS=10000;);@DISPOSITIVO(NAME=T2S)");
            //instancia a biblioiteca Android pra emissão de DOCs fiscais e seleciona a impressão via Bluetooth por address default
            // dmf = DarumaMobile.inicializar(MainActivity.this, "@FRAMEWORK(TRATAEXCECAO=TRUE;LOGMEMORIA=25;TIMEOUTWS=10000;);@BLUETOOTH(ADDRESS=00:11:22:33:44:55;ATTEMPTS=100;TIMEOUT=10000)");
//          dmf = DarumaMobile.inicializar(MainActivity.this, "@FRAMEWORK(TRATAEXCECAO=TRUE;LOGMEMORIA=25;TIMEOUTWS=10000;);@BLUETOOTH(NAME=InnerPrinter;ATTEMPTS=100;TIMEOUT=10000)");
//          dmf = DarumaMobile.inicializar(MainActivity.this, "@FRAMEWORK(LOGMEMORIA=200;TRATAEXCECAO=TRUE;TIMEOUTWS=10000;);@SOCKET(HOST=192.168.210.94;PORT=9100;)")

            //instancia objeto da classe Tectoy usada para impressão de texto livre
            objTecToy = new TecToy(Dispositivo.T2S, MainActivity.this.getApplicationContext());


            //Seta callback para receber XML de retorno de venda e cancelamento do SAT
            dmf.setSatCallback(satCallback);

            //inicializando SAT EPSON
            SatGerLib objEpson = new SatGerLib(this, null);
            dmf.iniciarSatEPSON(objEpson);


            //verificando a versão da IT4R utilizada
            String strVersao = "";
            strVersao = dmf.retornaVersao();  //versão da biblioteca DMF em uso
            TextView txtVersao = (TextView) findViewById(R.id.txtVersao);
            txtVersao.setText(strVersao);

            Button btnImp = (Button) findViewById(R.id.btnImprimi);
            btnImp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    cmdHead = invetImp+ centro + extra + negrito + "          * * * T E C T O Y * * *          " + deslNegrito + deslExtra +"\n"+ desligInvert ;
                    cmdHead +=  dateFormat.format(date) + "\n";
                    cmdHead += centro + extra + "Teste formatado" + " / " + "Suporte" + deslExtra + "\n\n";
                    cmdHead += deslCentro+ extra + "Fonte extra" + " - " + "TecToy Automacao" + deslExtra + "\n\n";
                    cmdHead += centro + extra + negrito + " Resumo do Pedido " + deslNegrito + deslExtra + "\n\n";
                    cmdHead += deslCentro+"\n\n\n\n";
                    objTecToy.imprimir(cmdHead);
                    objTecToy.imprimirQrCode("https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx?p=43230306354976000149650649555659931913106361|2|2|1|DA7D61882BC5BA3965D5AD8556EA8A6208A6FF93", "L", 3);
                    objTecToy.acionarGuilhotina();

                };
            });

            Button btnConfig = findViewById(R.id.btnCfg); // configura ambiente NFCE
            btnConfig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strAux = "Configurações: ";
                    Thread thrCgf;
                    try {
                        thrCgf = new Thread(configura);
                        thrCgf.start();
                        thrCgf.join();
                    } catch (Exception e) {
                        strAux += "ERRO["+ e.getMessage()+"]";
                    }
                    mensagem(strAux);
                }
            });

            Button btnNFCE = findViewById(R.id.btnDanfe); //Imprime DANFE NFCE
            btnNFCE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strAux = "Imprime DANFE";
                    Thread thrImp;
                    try {
                        thrImp = new Thread(nfceImp);
                        thrImp.start();
                        thrImp.join();

                    } catch (Exception e) {
                        strAux += "ERRO["+ e.getMessage()+"]";
                    }
                    mensagem(strAux);
                }
            });

            Button btnVdNFCE = (Button) findViewById(R.id.btnVendeNFCE);//realiza venda SAT - emissão/geração da venda e impressão
            btnVdNFCE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strAux = "Venda NFCE";
                    Thread thrVendNFCE;
                    try {
                        thrVendNFCE = new Thread(fazVenda);
                        thrVendNFCE.start();
                        thrVendNFCE.join();
                    } catch (Exception e) {
                        strAux += "ERRO["+ e.getMessage()+"]";
                    }
                    mensagem(strAux);
                }
            });

            Button btnCncNFCE = (Button) findViewById(R.id.btnCancNFCE);//Cancela venda NFCe
            btnCncNFCE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strAux = "Cancelar NFCE ";
                    Thread ThrCancNFCE;
                    try {
                        ThrCancNFCE = new Thread(cancVenda);
                        ThrCancNFCE.start();
                        ThrCancNFCE.join();
                        Toast.makeText(MainActivity.this, "NFCE Cancelada com sucesso", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        strAux += "ERRO["+ e.getMessage()+"]";
                    }
                    mensagem(strAux);
                }
            });

            Button btnSAT = findViewById(R.id.btnCfgSAT);//configura ambiente SAT
            btnSAT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strAux = "Configura SAT: ";
                    Thread thrSAT;
                    try {
                        thrSAT = new Thread(configura2);
                        thrSAT.start();
                        thrSAT.join();
                    } catch (Exception e) {
                        strAux += "ERRO["+ e.getMessage()+"]";
                    }
                    mensagem(strAux);
                }
            });

            Button btnImpSAT = (Button) findViewById(R.id.btnCfe);//imprime CFe SAT - precisa ter o arquivo na pasta
            btnImpSAT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strAux = "Imprime CFe SAT ";
                    Thread thrImpSAT;
                    try {
                        thrImpSAT = new Thread(satImp);
                        thrImpSAT.start();
                        thrImpSAT.join();
                    } catch (Exception e) {
                        strAux += "ERRO["+ e.getMessage()+"]";
                    }
                    mensagem(strAux);
                }
            });

            Button btnVendeSAT = (Button) findViewById(R.id.btnVendeSAT);//realiza venda SAT - emissão/geração da venda e impressão
            btnVendeSAT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strAux = "Venda SAT ";
                    Thread thrVendeSAT;
                    dmf.RegAlterarValor_NFCe("CONFIGURACAO\\HabilitarSAT", "1"); //Ligando tradução de comando NFCE para SAT
                    try {
                        thrVendeSAT = new Thread(fazVenda);
                        thrVendeSAT.start();
                        thrVendeSAT.join();
                        char strCod[] = new char[5];
                        char strmsg[] = new char[1024];
                        dmf.rAvisoErro_NFCe(strCod, strmsg);
                        mensagem("Mensagem retornada AvisoErro: ["+ new String(strCod).trim()+ "]"+new String(strmsg).trim());
                    } catch (Exception e) {
                        strAux += "ERRO["+ e.getMessage()+"]";
                        mensagem(strAux);
                        return;
                    }
                }
            });

            Button btnStatusOPsat = (Button) findViewById(R.id.btnStatusOPsat);
            btnStatusOPsat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String retSat = dmf.rConsultaStatusOperacional();
                    mensagem("Status Operacional: " + retSat);
                }
            });

    }

        private Runnable configura = new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    configGneToNfce();
                } catch (Exception de) {
                    strAux += "ERRO: "+ de.getMessage();
                    return;
                }
            }
        };
        private Runnable configura2 = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                configSatTraduzido();
            } catch (Exception de) {
                strAux += "ERRO: "+ de.getMessage();
                return;
            }
        }
    };
        private Runnable nfceImp = new Runnable() {
            @Override
            public void run() {
                String strXML = "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\"><infNFe Id=\"NFe43210106354976000149650320000285611815759284\" versao=\"4.00\"><ide><cUF>43</cUF><cNF>81575928</cNF><natOp>Venda</natOp><mod>65</mod><serie>32</serie><nNF>28561</nNF><dhEmi>2021-01-26T11:42:04-03:00</dhEmi><tpNF>1</tpNF><idDest>1</idDest><cMunFG>4321808</cMunFG><tpImp>4</tpImp><tpEmis>1</tpEmis><cDV>4</cDV><tpAmb>2</tpAmb><finNFe>1</finNFe><indFinal>1</indFinal><indPres>1</indPres><procEmi>0</procEmi><verProc>1.0</verProc></ide><emit><CNPJ>06354976000149</CNPJ><xNome>IT Fast TESTE</xNome><enderEmit><xLgr>Logradouro</xLgr><nro>001</nro><xBairro>Bairro</xBairro><cMun>4321808</cMun><xMun>Municipio</xMun><UF>RS</UF><CEP>11111111</CEP></enderEmit><IE>1470049241</IE><IM>8948</IM><CRT>3</CRT></emit><det nItem='1'><prod><cProd>1234</cProd><cEAN>7896022204969</cEAN><xProd>NOTA FISCAL EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UND</uCom><qCom>20.0000</qCom><vUnCom>0.10</vUnCom><vProd>2.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UND</uTrib><qTrib>20.0000</qTrib><vUnTrib>0.10</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>2.00</vBC><pICMS>7.00</pICMS><vICMS>0.14</vICMS><pFCP>2.00</pFCP><vFCP>0.04</vFCP></ICMS00></ICMS></imposto><infAdProd>pFCP:   2.00, vFCP:    0.04</infAdProd></det><det nItem='2'><prod><cProd>1234</cProd><cEAN>7896022204969</cEAN><xProd>Item teste 2</xProd><NCM>18063210</NCM><CEST>1705700</CEST><CFOP>5102</CFOP><uCom>UND</uCom><qCom>20.0000</qCom><vUnCom>0.10</vUnCom><vProd>2.00</vProd><cEANTrib>7896022204969</cEANTrib><uTrib>UND</uTrib><qTrib>20.0000</qTrib><vUnTrib>0.10</vUnTrib><indTot>1</indTot></prod><imposto><ICMS><ICMS00><orig>0</orig><CST>00</CST><modBC>3</modBC><vBC>2.00</vBC><pICMS>7.00</pICMS><vICMS>0.14</vICMS><pFCP>2.00</pFCP><vFCP>0.04</vFCP></ICMS00></ICMS></imposto><infAdProd>pFCP:   2.00, vFCP:    0.04</infAdProd></det><total><ICMSTot><vBC>4.00</vBC><vICMS>0.28</vICMS><vICMSDeson>0.00</vICMSDeson><vFCP>0.08</vFCP><vBCST>0.00</vBCST><vST>0.00</vST><vFCPST>0.00</vFCPST><vFCPSTRet>0.00</vFCPSTRet><vProd>4.00</vProd><vFrete>0.00</vFrete><vSeg>0.00</vSeg><vDesc>0.00</vDesc><vII>0.00</vII><vIPI>0.00</vIPI><vIPIDevol>0.00</vIPIDevol><vPIS>0.00</vPIS><vCOFINS>0.00</vCOFINS><vOutro>0.00</vOutro><vNF>4.00</vNF></ICMSTot></total><transp><modFrete>9</modFrete></transp><pag><detPag><indPag>0</indPag><tPag>01</tPag><vPag>4.00</vPag></detPag></pag><infAdic><infAdFisco>vFCP:    0.08</infAdFisco><infCpl>Impressao teste pelo exemplo Android itfastdmf.</infCpl></infAdic><infRespTec><CNPJ>06354976000149</CNPJ><xContato>Glauber Weddigen</xContato><email>glauberweddigen@migrate.info</email><fone>5535354800</fone></infRespTec></infNFe><infNFeSupl><qrCode><![CDATA[https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx?p=43210106354976000149650320000285611815759284|2|2|1|DEBEE06D96980D4ED33CC7B91468DDEC9968A6DD]]></qrCode><urlChave>www.sefaz.rs.gov.br/nfce/consulta</urlChave></infNFeSupl></NFe><Documento><DocModelo>NFCe</DocModelo><DocNumero>28561</DocNumero><DocSerie>32</DocSerie><DocChaAcesso>43210106354976000149650320000285611815759284</DocChaAcesso><DocProtocolo>143210000043498</DocProtocolo><DocEvenSeq>0</DocEvenSeq><DocEveTp>0</DocEveTp><DocEveId /><DocPDFBase64 /><DocPDFDownload /><DocDhAut>2021-01-26T11:42:05-03:00</DocDhAut><DocDigestValue>P1kapBpMh6maxflbntrUTjX3LDI=</DocDigestValue><DocXMLBase64>PG5mZVByb2MgdmVyc2FvPSI0LjAwIiB4bWxucz0iaHR0cDovL3d3dy5wb3J0YWxmaXNjYWwuaW5mLmJyL25mZSI+PE5GZSB4bWxucz0iaHR0cDovL3d3dy5wb3J0YWxmaXNjYWwuaW5mLmJyL25mZSI+PGluZk5GZSBJZD0iTkZlNDMyMTAxMDYzNTQ5NzYwMDAxNDk2NTAzMjAwMDAyODU2MTE4MTU3NTkyODQiIHZlcnNhbz0iNC4wMCI+PGlkZT48Y1VGPjQzPC9jVUY+PGNORj44MTU3NTkyODwvY05GPjxuYXRPcD5WZW5kYTwvbmF0T3A+PG1vZD42NTwvbW9kPjxzZXJpZT4zMjwvc2VyaWU+PG5ORj4yODU2MTwvbk5GPjxkaEVtaT4yMDIxLTAxLTI2VDExOjQyOjA0LTAzOjAwPC9kaEVtaT48dHBORj4xPC90cE5GPjxpZERlc3Q+MTwvaWREZXN0PjxjTXVuRkc+NDMyMTgwODwvY011bkZHPjx0cEltcD40PC90cEltcD48dHBFbWlzPjE8L3RwRW1pcz48Y0RWPjQ8L2NEVj48dHBBbWI+MjwvdHBBbWI+PGZpbk5GZT4xPC9maW5ORmU+PGluZEZpbmFsPjE8L2luZEZpbmFsPjxpbmRQcmVzPjE8L2luZFByZXM+PHByb2NFbWk+MDwvcHJvY0VtaT48dmVyUHJvYz4xLjA8L3ZlclByb2M+PC9pZGU+PGVtaXQ+PENOUEo+MDYzNTQ5NzYwMDAxNDk8L0NOUEo+PHhOb21lPk1pZ3JhdGU8L3hOb21lPjxlbmRlckVtaXQ+PHhMZ3I+TG9ncmFkb3VybzwveExncj48bnJvPjAwMTwvbnJvPjx4QmFpcnJvPkJhaXJybzwveEJhaXJybz48Y011bj40MzIxODA4PC9jTXVuPjx4TXVuPk11bmljaXBpbzwveE11bj48VUY+UlM8L1VGPjxDRVA+MTExMTExMTE8L0NFUD48L2VuZGVyRW1pdD48SUU+MTQ3MDA0OTI0MTwvSUU+PElNPjg5NDg8L0lNPjxDUlQ+MzwvQ1JUPjwvZW1pdD48ZGV0IG5JdGVtPSIxIj48cHJvZD48Y1Byb2Q+MTIzNDwvY1Byb2Q+PGNFQU4+Nzg5NjAyMjIwNDk2OTwvY0VBTj48eFByb2Q+Tk9UQSBGSVNDQUwgRU1JVElEQSBFTSBBTUJJRU5URSBERSBIT01PTE9HQUNBTyAtIFNFTSBWQUxPUiBGSVNDQUw8L3hQcm9kPjxOQ00+MTgwNjMyMTA8L05DTT48Q0VTVD4xNzA1NzAwPC9DRVNUPjxDRk9QPjUxMDI8L0NGT1A+PHVDb20+VU5EPC91Q29tPjxxQ29tPjIwLjAwMDA8L3FDb20+PHZVbkNvbT4wLjEwPC92VW5Db20+PHZQcm9kPjIuMDA8L3ZQcm9kPjxjRUFOVHJpYj43ODk2MDIyMjA0OTY5PC9jRUFOVHJpYj48dVRyaWI+VU5EPC91VHJpYj48cVRyaWI+MjAuMDAwMDwvcVRyaWI+PHZVblRyaWI+MC4xMDwvdlVuVHJpYj48aW5kVG90PjE8L2luZFRvdD48L3Byb2Q+PGltcG9zdG8+PElDTVM+PElDTVMwMD48b3JpZz4wPC9vcmlnPjxDU1Q+MDA8L0NTVD48bW9kQkM+MzwvbW9kQkM+PHZCQz4yLjAwPC92QkM+PHBJQ01TPjcuMDA8L3BJQ01TPjx2SUNNUz4wLjE0PC92SUNNUz48cEZDUD4yLjAwPC9wRkNQPjx2RkNQPjAuMDQ8L3ZGQ1A+PC9JQ01TMDA+PC9JQ01TPjwvaW1wb3N0bz48aW5mQWRQcm9kPnBGQ1A6ICAgMi4wMCwgdkZDUDogICAgMC4wNDwvaW5mQWRQcm9kPjwvZGV0PjxkZXQgbkl0ZW09IjIiPjxwcm9kPjxjUHJvZD4xMjM0PC9jUHJvZD48Y0VBTj43ODk2MDIyMjA0OTY5PC9jRUFOPjx4UHJvZD5JdGVtIHRlc3RlIDI8L3hQcm9kPjxOQ00+MTgwNjMyMTA8L05DTT48Q0VTVD4xNzA1NzAwPC9DRVNUPjxDRk9QPjUxMDI8L0NGT1A+PHVDb20+VU5EPC91Q29tPjxxQ29tPjIwLjAwMDA8L3FDb20+PHZVbkNvbT4wLjEwPC92VW5Db20+PHZQcm9kPjIuMDA8L3ZQcm9kPjxjRUFOVHJpYj43ODk2MDIyMjA0OTY5PC9jRUFOVHJpYj48dVRyaWI+VU5EPC91VHJpYj48cVRyaWI+MjAuMDAwMDwvcVRyaWI+PHZVblRyaWI+MC4xMDwvdlVuVHJpYj48aW5kVG90PjE8L2luZFRvdD48L3Byb2Q+PGltcG9zdG8+PElDTVM+PElDTVMwMD48b3JpZz4wPC9vcmlnPjxDU1Q+MDA8L0NTVD48bW9kQkM+MzwvbW9kQkM+PHZCQz4yLjAwPC92QkM+PHBJQ01TPjcuMDA8L3BJQ01TPjx2SUNNUz4wLjE0PC92SUNNUz48cEZDUD4yLjAwPC9wRkNQPjx2RkNQPjAuMDQ8L3ZGQ1A+PC9JQ01TMDA+PC9JQ01TPjwvaW1wb3N0bz48aW5mQWRQcm9kPnBGQ1A6ICAgMi4wMCwgdkZDUDogICAgMC4wNDwvaW5mQWRQcm9kPjwvZGV0Pjx0b3RhbD48SUNNU1RvdD48dkJDPjQuMDA8L3ZCQz48dklDTVM+MC4yODwvdklDTVM+PHZJQ01TRGVzb24+MC4wMDwvdklDTVNEZXNvbj48dkZDUD4wLjA4PC92RkNQPjx2QkNTVD4wLjAwPC92QkNTVD48dlNUPjAuMDA8L3ZTVD48dkZDUFNUPjAuMDA8L3ZGQ1BTVD48dkZDUFNUUmV0PjAuMDA8L3ZGQ1BTVFJldD48dlByb2Q+NC4wMDwvdlByb2Q+PHZGcmV0ZT4wLjAwPC92RnJldGU+PHZTZWc+MC4wMDwvdlNlZz48dkRlc2M+MC4wMDwvdkRlc2M+PHZJST4wLjAwPC92SUk+PHZJUEk+MC4wMDwvdklQST48dklQSURldm9sPjAuMDA8L3ZJUElEZXZvbD48dlBJUz4wLjAwPC92UElTPjx2Q09GSU5TPjAuMDA8L3ZDT0ZJTlM+PHZPdXRybz4wLjAwPC92T3V0cm8+PHZORj40LjAwPC92TkY+PC9JQ01TVG90PjwvdG90YWw+PHRyYW5zcD48bW9kRnJldGU+OTwvbW9kRnJldGU+PC90cmFuc3A+PHBhZz48ZGV0UGFnPjxpbmRQYWc+MDwvaW5kUGFnPjx0UGFnPjAxPC90UGFnPjx2UGFnPjQuMDA8L3ZQYWc+PC9kZXRQYWc+PC9wYWc+PGluZkFkaWM+PGluZkFkRmlzY28+dkZDUDogICAgMC4wODwvaW5mQWRGaXNjbz48aW5mQ3BsPkVtaXNzYW8gZGUgY3Vwb20gQ29tcGxldG8gcGVsbyBleGVtcGxvIEMoYW5zaSk8L2luZkNwbD48L2luZkFkaWM+PGluZlJlc3BUZWM+PENOUEo+MDYzNTQ5NzYwMDAxNDk8L0NOUEo+PHhDb250YXRvPkdsYXViZXIgV2VkZGlnZW48L3hDb250YXRvPjxlbWFpbD5nbGF1YmVyd2VkZGlnZW5AbWlncmF0ZS5pbmZvPC9lbWFpbD48Zm9uZT41NTM1MzU0ODAwPC9mb25lPjwvaW5mUmVzcFRlYz48L2luZk5GZT48aW5mTkZlU3VwbD48cXJDb2RlPjwhW0NEQVRBW2h0dHBzOi8vd3d3LnNlZmF6LnJzLmdvdi5ici9ORkNFL05GQ0UtQ09NLmFzcHg/cD00MzIxMDEwNjM1NDk3NjAwMDE0OTY1MDMyMDAwMDI4NTYxMTgxNTc1OTI4NHwyfDJ8MXxERUJFRTA2RDk2OTgwRDRFRDMzQ0M3QjkxNDY4RERFQzk5NjhBNkREXV0+PC9xckNvZGU+PHVybENoYXZlPnd3dy5zZWZhei5ycy5nb3YuYnIvbmZjZS9jb25zdWx0YTwvdXJsQ2hhdmU+PC9pbmZORmVTdXBsPjxTaWduYXR1cmUgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiPjxTaWduZWRJbmZvPjxDYW5vbmljYWxpemF0aW9uTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvVFIvMjAwMS9SRUMteG1sLWMxNG4tMjAwMTAzMTUiIC8+PFNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNyc2Etc2hhMSIgLz48UmVmZXJlbmNlIFVSST0iI05GZTQzMjEwMTA2MzU0OTc2MDAwMTQ5NjUwMzIwMDAwMjg1NjExODE1NzU5Mjg0Ij48VHJhbnNmb3Jtcz48VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI2VudmVsb3BlZC1zaWduYXR1cmUiIC8+PFRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnL1RSLzIwMDEvUkVDLXhtbC1jMTRuLTIwMDEwMzE1IiAvPjwvVHJhbnNmb3Jtcz48RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiIC8+PERpZ2VzdFZhbHVlPlAxa2FwQnBNaDZtYXhmbGJudHJVVGpYM0xEST08L0RpZ2VzdFZhbHVlPjwvUmVmZXJlbmNlPjwvU2lnbmVkSW5mbz48U2lnbmF0dXJlVmFsdWU+cW5HVHdVVnlEc3JOMW4zdHA0TDd0RWdUQlp6QnRXRkZVTmpHR0xjRGpsYmEvL3V5QVhDYXVqWXhQUlZLY25LdTJ5Y1BRZjcrSkVLMm41KzhiYzloRVJUdVl5RUU1QUVCV1duRDZYRytFU0lNR1dWQXJRWlMvM2ltNU5ZS2RqaUZPcXFJMGtMRmRuanJtbEZ1NkFseGZrM2phczVlWE9yUDk2elcrZlRLTENNY0JseFhKdmN4YWVac1YydDd5aFR0N1ByY1dxUkpYZ2lKT0hFcTE5ZlZFUlJ5UXFUc3hVdUwvRFNvMW5obmk2VnJlNXJoK0Z4OUZnU2dzdUF1VElZUE1oZ3VJK3g0TGg2MkpKTitQdFF1bGMxdW9kRjAxOVh0dER1b1VHLzYwSzE4a2VLcWJwNk16eFhqK1c3M2VBV3ZmUDliQnppNlEvUTVLUUF5T2hFRTBnPT08L1NpZ25hdHVyZVZhbHVlPjxLZXlJbmZvPjxYNTA5RGF0YT48WDUwOUNlcnRpZmljYXRlPk1JSUhUekNDQlRlZ0F3SUJBZ0lJUHF3aEFSVTBxOEl3RFFZSktvWklodmNOQVFFTEJRQXdXVEVMTUFrR0ExVUVCaE1DUWxJeEV6QVJCZ05WQkFvVENrbERVQzFDY21GemFXd3hGVEFUQmdOVkJBc1RERUZESUZOUFRGVlVTU0IyTlRFZU1Cd0dBMVVFQXhNVlFVTWdVMDlNVlZSSklFMTFiSFJwY0d4aElIWTFNQjRYRFRJeE1ERXhOVEUyTlRjd01Gb1hEVEl5TURFeE5URTJOVGN3TUZvd2dmWXhDekFKQmdOVkJBWVRBa0pTTVJNd0VRWURWUVFLRXdwSlExQXRRbkpoYzJsc01Rc3dDUVlEVlFRSUV3SlNVekVWTUJNR0ExVUVCeE1NVkhKbGN5QmtaU0JOWVdsdk1SNHdIQVlEVlFRTEV4VkJReUJUVDB4VlZFa2dUWFZzZEdsd2JHRWdkalV4RnpBVkJnTlZCQXNURGpBek1UVXhNakF3TURBd01UTXpNUk13RVFZRFZRUUxFd3BRY21WelpXNWphV0ZzTVJvd0dBWURWUVFMRXhGRFpYSjBhV1pwWTJGa2J5QlFTaUJCTVRGRU1FSUdBMVVFQXhNN1RVbEhVa0ZVUlNCRFQwMVFRVTVaSUZOSlUxUkZUVUZUSUVSRklFbE9SazlTVFVGRFFVOGdURlJFUVM0Nk1EWXpOVFE1TnpZd01EQXhORGt3Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLQW9JQkFRREhqQUFUVzJUcGdoOU9XczVZbmxma1ROcEJKcmhCejFtQmRMcEpDRHVVL2FZVjErWWNVZ0FRcVpTVndZV1BpNU9lSXF2ZTgzSTFDT1lvTDFNRWdIcy9HNmx5Wmdweld0NCt1NVNXSG9SWFMxNmhWSmJua2dxLzNWczk1anlmK3Vub1o1MDhMU3MvZVplWlNtMkJvYjdobUJXVFBFK095M0QzZkEwbDlqaU5zRTRqQ2l1c1JJMzVHczZZRlhPR3RpZ0VNdVJXcVI1bE5PS3hNMEZ0ODVWdTlUcVF0ZXpyNEkwbUMzV0lXNStOS0J4VWNGdDh6OWg3T3NPMHZLbzEzV1QvV0poNFhnRllZV29mRVJueEs5TkZkcy8yUDhVNUV3SFJpRWlnT0ROU1ZvUjBQRHE2VkNJaGp2cVBEaXpQTFBFZFBPWUlQbkZDMzdVR0FrRHRCMTZmQWdNQkFBR2pnZ0o3TUlJQ2R6QUpCZ05WSFJNRUFqQUFNQjhHQTFVZEl3UVlNQmFBRk1WUzdTV0FDZCtjZ3NpZlI4YmR0Rjh4M2JteE1GUUdDQ3NHQVFVRkJ3RUJCRWd3UmpCRUJnZ3JCZ0VGQlFjd0FvWTRhSFIwY0RvdkwyTmpaQzVoWTNOdmJIVjBhUzVqYjIwdVluSXZiR055TDJGakxYTnZiSFYwYVMxdGRXeDBhWEJzWVMxMk5TNXdOMkl3Z2JZR0ExVWRFUVNCcmpDQnE0RVhabWx1WVc1alpXbHliMEJ0YVdkeVlYUmxMbWx1Wm0rZ0lnWUZZRXdCQXdLZ0dSTVhRVVJKVEZOUFRpQk5UMEZEU1ZJZ1YwVkVSRWxIUlU2Z0dRWUZZRXdCQXdPZ0VCTU9NRFl6TlRRNU56WXdNREF4TkRtZ09BWUZZRXdCQXdTZ0x4TXRNamd3TlRFNU56ZzNOek0xTXpBMU5qQTFNekF3TURBd01EQXdNREF3TURBd01EQXdNREF3TURBd01EQXdvQmNHQldCTUFRTUhvQTRURERBd01EQXdNREF3TURBd01EQmRCZ05WSFNBRVZqQlVNRklHQm1CTUFRSUJKakJJTUVZR0NDc0dBUVVGQndJQkZqcG9kSFJ3T2k4dlkyTmtMbUZqYzI5c2RYUnBMbU52YlM1aWNpOWtiMk56TDJSd1l5MWhZeTF6YjJ4MWRHa3RiWFZzZEdsd2JHRXVjR1JtTUIwR0ExVWRKUVFXTUJRR0NDc0dBUVVGQndNQ0JnZ3JCZ0VGQlFjREJEQ0JqQVlEVlIwZkJJR0VNSUdCTUQ2Z1BLQTZoamhvZEhSd09pOHZZMk5rTG1GamMyOXNkWFJwTG1OdmJTNWljaTlzWTNJdllXTXRjMjlzZFhScExXMTFiSFJwY0d4aExYWTFMbU55YkRBL29EMmdPNFk1YUhSMGNEb3ZMMk5qWkRJdVlXTnpiMngxZEdrdVkyOXRMbUp5TDJ4amNpOWhZeTF6YjJ4MWRHa3RiWFZzZEdsd2JHRXRkalV1WTNKc01CMEdBMVVkRGdRV0JCU1hqY3p5WGpaRjNYUmpRWmVLQWJuUFBiRGlmVEFPQmdOVkhROEJBZjhFQkFNQ0JlQXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnSUJBRUUxeGh6T2ZBRkgvNE96ejZjbjRkNnh6UHhnWUJTb0RRcllkWGVwc3Rjbk10SC9WdXdpaGNDWWRvMG1PRUh3aU9PUlVDZG53Y2ZTcG5uQ1BKOFRwT1dlSWVPMkhtUmY3ajA4S3I3N2xVbWRZUGd1VXNia0NXQVJVOXhLY0ZZZ3BGNmgyY0tJOVNhczdOTEEwZlRqNG4xbm91NzFnVW92K2M1WXd4ZFZZcUl5cWVmWjNqL3lIRHpkTkhSRG1ZR3NJWjBoZGVjK083am52R2M4ZXh0RG5ZMFJ1RHBEZHQramlEODBDUTluaWZrb2ZlcWJBUFZ1alFEMDBNWU00aTRUUjk1R0o0K2NhY2JJR0s5c0drMG9VUWluMTRPZ21sczV0ZmtYaFVOdnBMM1R1Z1p3UXhJUThDbFo5Nmo4VUJvUWdIbTNFTWNJa2hOY0JuaDVNUFZXUmF2VHM0dGZFY2x6UTduZTJmL3BFWklwcWd3MWFVMUVXcm5KSnphT2xPcnZGM2tSUG5SQVYxY2ZaRzg0eGU0L1RreUVwWVYvcFJHdHh6cGMvNVJYMVNlQlVLSHFjS2g3L1VtTElxeEZrNHVTKzN4d3c1cHY4QnZJZ2VmYWxFd0xTUUNrWjF4VzNaeE9RVHJzUXRrRnNEQ1MzTkJZbmxzTWdTOEsxZi85cUZSOUVCb3l6Mm5KeCtxWmJxSiszTmh4b1EyNGZJVnc1K0EwbGFHa1FQZTFmNDQzNnhybjdkZVBEaExIOFVxQkFlWHBBSHp3S3VraDRKellsblVLQUovTkh0NlRnRERyRTVxU3pnMVVXbC8wOHpjcTN3ZUllSnhlVTJZajZ1bWNlb1crS0ZtZERYaXhMUURkZ3BiU09BQlNnd21lU3cvajN1T3FLbEs3QWtPa25nYXQ8L1g1MDlDZXJ0aWZpY2F0ZT48L1g1MDlEYXRhPjwvS2V5SW5mbz48L1NpZ25hdHVyZT48L05GZT48cHJvdE5GZSB2ZXJzYW89IjQuMDAiPjxpbmZQcm90Pjx0cEFtYj4yPC90cEFtYj48dmVyQXBsaWM+UlNuZmNlMjAxOTExMjkwODUyPC92ZXJBcGxpYz48Y2hORmU+NDMyMTAxMDYzNTQ5NzYwMDAxNDk2NTAzMjAwMDAyODU2MTE4MTU3NTkyODQ8L2NoTkZlPjxkaFJlY2J0bz4yMDIxLTAxLTI2VDExOjQyOjA1LTAzOjAwPC9kaFJlY2J0bz48blByb3Q+MTQzMjEwMDAwMDQzNDk4PC9uUHJvdD48ZGlnVmFsPlAxa2FwQnBNaDZtYXhmbGJudHJVVGpYM0xEST08L2RpZ1ZhbD48Y1N0YXQ+MTAwPC9jU3RhdD48eE1vdGl2bz5BdXRvcml6YWRvIG8gdXNvIGRhIE5GLWU8L3hNb3Rpdm8+PC9pbmZQcm90PjwvcHJvdE5GZT48L25mZVByb2M+</DocXMLBase64><DocXMLDownload>https://homolog.invoicy.com.br///HNUC002.aspx?gvjBbcQmIWIxjpfkKLR/N1etTfpGdWgTxMjALs7UQU4=</DocXMLDownload><DocImpressora /><Situacao><SitCodigo>100</SitCodigo><SitDescricao>Autorizado o uso da NF-e</SitDescricao></Situacao></Documento>";
                try {
                    Looper.prepare();
                    try {
                        dmf.iCFImprimirParametrizado_NFCe(strXML,strXML,"",34, 1, "");
                    }catch (Exception e){
                        strAux += "ERRO: "+ e.getMessage();
                        return;
                    }
                }catch (Exception de){
                    strAux += "ERRO: "+ de.getMessage();
                    return;
                }
            }
        };
        private Runnable satImp = new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    try {//colocar um xml de sat na pasta da aplicação e ajustar o seu nome no comando abaixo
                        dmf.iImprimirCFe_SAT("CFe35211261099008000141599000167120002480289418.xml", "1");
                        Toast.makeText(MainActivity.this, "Imprimiu SAT" + iRet, Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        strAux += "ERRO: "+ e.getMessage();
                    }
                }catch (Exception de){
                    strAux += "ERRO: "+ de.getMessage();
                    return;
                }
            }
        };
        private Runnable fazVenda = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                try {
                    venderGenerico();
                }catch (Exception e){
                    strAux += "ERRO: "+ e.getMessage();
                    return;
                }
            } catch (Exception de) {
                strAux += "ERRO: "+ de.getMessage();
                return;
            }
        }
    };
        private Runnable cancVenda = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                try {
                    dmf.tCFCancelar_NFCe("","","","","");

                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(e.getMessage()).setNeutralButton("OK", null);
                    builder.show();
                }
            } catch (Exception de) {
                strAux += "ERRO: "+ de.getMessage();
                return;
            }
        }
    };

        void configGneToNfce() {//configurar Daados para emissão NFCe/
            //DAdos da Software House para teste junto ao parceiro Migrate <USAR DADOS PROPRIOS DE ACORDO COM O EMITENTE QUE SERÁ USADO NO TESTE.>
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\TipoAmbiente", "2", false); //// 2- HOMOLOGACAO/ 1 - PRODUCAO
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\EmpPK", "<USAR DADOS PROPRIOS>", false); //// Chave de Parceiro obtida junto à Migrate
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\EmpCK", "<USAR DADOS PROPRIOS>", false); ////Chave de acesso do EMITENTE obtida junto à Migrate
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\Token", "<USAR DADOS PROPRIOS>", false);//// CSC obtido com a SEFAZ do respectivo EMITENTE
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\EmpCO", "001", false);//// Num PDV/ caixa
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\IdToken", "000001", false); //// ID do CSC
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\ArredondarTruncar","A", false);
            dmf.RegAlterarValor_NFCe("EMIT\\CRT", "3", false);
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\Impressora", "EPSON", false);
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\AvisoContingencia", "1", false);
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\ImpressaoCompleta", "1", false);
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\NumeracaoAutomatica", "1", false);
            dmf.RegAlterarValor_NFCe("IDE\\cUF", "43", false);
            dmf.RegAlterarValor_NFCe("IDE\\cMunFG", "4321808", false);
            dmf.RegAlterarValor_NFCe("EMIT\\CNPJ", "11111111111111", false);
            dmf.RegAlterarValor_NFCe("EMIT\\IE", "111111111", false);
            dmf.RegAlterarValor_NFCe("EMIT\\xNome", "NOME FANTASIA SAI NO CABEÇALHO DA DANFE", false);
            dmf.RegAlterarValor_NFCe("EMIT\\ENDEREMIT\\UF", "RS", false);
            dmf.RegPersistirXML_NFCe(); /// Grava infos configuradas no GNE xml
            //File dir = new File(getExternalFilesDir(null).getPath());
            //dir.mkdirs();
            //dmf.RegAlterarValor_NFCe("CONFIGURACAO\\LocalArquivos", dir.getAbsolutePath()+"/");
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\LocalArquivos", "");
            //dmf.RegAlterarValor_NFCe("IDE\\indPres", "1");
            // dmf.RegAlterarValor_NFCe("IDE\\indIntermed", ""); // 0 site proprio     1 site terceiro
            // dmf.RegAlterarValor_NFCe("infIntermed\\CNPJ", "12123123000123");
            // dmf.RegAlterarValor_NFCe("infIntermed\\idCadIntTran", "Usuario Teste",);
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\HabilitarSAT", "0");

            //dmf.RegAlterarValor_NFCe("IDE\\nNF", "7020");
            //dmf.RegAlterarValor_NFCe("IDE\\Serie", "64");
        }

        void configSatTraduzido() {
            /// DADOS ABAIXO SÃO DO SAT DA CSDEVICES - KIT DESENVOLVIMENTO.
           /* dmf.RegAlterarValor_SAT("PROD\\indRegra", "A");
            dmf.RegAlterarValor_SAT("IDENTIFICACAO_CFE\\numeroCaixa", "001");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\marcaSAT", "SATCR");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\Impressora", "TECTOY_80");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\codigoDeAtivacao", "12345678");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\IDENTIFICACAO_CFE\\CNPJ", "16716114000172");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\IDENTIFICACAO_CFE\\signAC", "SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\EMIT\\CNPJ", "30832338000170");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\EMIT\\IE", "111111111111");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\EMIT\\cRegTribISSQN", "3");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\EMIT\\indRatISSQN", "N");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\VersaoDadosEnt", "0.07");
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\HabilitarSAT", "1");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\LocalArquivos", "sdcard/SAT"); ///definindo local para gravação dos arquivos.
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\CopiaSeguranca", "1");
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\ImpressaoCompleta", "1"); //o valor 1 imprime completo, e o valor 2 não imprime. Valor 0 imprime reduzido (sem bloco de itens)

            */



            /// DADOS ABAIXO SÃO DO SAT A-10 Epson - KIT DESENVOLVIMENTO.
            dmf.RegAlterarValor_SAT("PROD\\indRegra", "A");
            dmf.RegAlterarValor_SAT("IDENTIFICACAO_CFE\\numeroCaixa", "001");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\marcaSAT", "EPSON");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\Impressora", "EPSON"); ///Impressora do T2S.
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\codigoDeAtivacao", "00000000");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\IDENTIFICACAO_CFE\\CNPJ", "16716114000172");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\IDENTIFICACAO_CFE\\signAC", "SGR-SAT SISTEMA DE GESTAO E RETAGUARDA DO SAT");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\EMIT\\CNPJ", "03654119000176");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\EMIT\\IE", "000052619494");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\EMIT\\cRegTribISSQN", "3");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\EMIT\\indRatISSQN", "N");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\VersaoDadosEnt", "0.07");
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\HabilitarSAT", "1");
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\LocalArquivos", "sdcard/SAT"); ///definindo local para gravação dos arquivos, pasta deve existir a IT4R não cria pasta..
            dmf.RegAlterarValor_SAT("CONFIGURACAO\\CopiaSeguranca", "1");
            dmf.RegAlterarValor_NFCe("CONFIGURACAO\\ImpressaoCompleta", "2"); //o valor 1 imprime completo, e o valor 2 não imprime. Valor 0 imprime reduzido (sem bloco de itens)

        }

        void venderGenerico(){
            dmf.aCFAbrir_NFCe("","","","","","","","","");//Abertura de venda sem identificação consumidor e usando numeração automatica de NFCe. No SAT é o equipamento que indica número da venda.
            dmf.aCFConfImposto_NFCe("ICMS00", "0;00;3;;07,00;;;;");//configurando imposto para o proximo item
            dmf.aCFConfPisNT_NFCe(String.valueOf("06"));
            dmf.aCFConfCofinsNT_NFCe(String.valueOf("06"));
            dmf.aCFVenderCompleto_NFCe("0", "1", "8.00", "D$", "0.00", "1110", "18063210", "5102", "UN", "CAFE", "CEST=1705700;cEAN=7896022204969;cEANTrib=7896022204969;");
            dmf.aCFConfImposto_NFCe("ICMS00", "0;00;3;;07,00;;;;");// configurando imposto para mais um item
            dmf.aCFConfPisNT_NFCe(String.valueOf("07"));
            dmf.aCFConfCofinsNT_NFCe(String.valueOf("07"));
            dmf.aCFVenderCompleto_NFCe("0", "1", "8.00", "D$", "0.00", "2222", "18063210", "5102", "UN", "AGUA", "CEST=1705700;cEAN=7896022204969;cEANTrib=7896022204969;");
            dmf.aCFTotalizar_NFCe("D$", "0.01");//Totalizando venda
            dmf.aCFEfetuarPagamento_NFCe("Dinheiro", "15.99"); //pagamento pode ser indicado pelo código ou pela descrição conforme a tabela de pagamentos SEFAZ.
            dmf.tCFEncerrar_NFCe("Teste de venda utilizando DMF Android IT FAST.");
        }

        void mensagem(String msg){
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
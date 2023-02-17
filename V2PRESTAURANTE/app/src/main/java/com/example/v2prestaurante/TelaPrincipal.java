package com.example.v2prestaurante;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.daruma.framework.mobile.comunicacao.exception.DarumaException;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;
import br.com.itfast.tectoy.TecToyException;
import br.com.itfast.tectoy.TecToyScannerCallback;



public class TelaPrincipal extends AppCompatActivity {

    private Button btnSair, btnFazerPedido, btnFecharConta, btnMaisQnt, btnMenosQnt,btnOk;
    private TecToy tectoy2;
    private TextView txtViewPedido, txtQntBatata, txtQntHamb1 ,txtQntHamb2, txtQntNuggets, txtQntRefri, txtQntTortinha1, txtQntTortinha2, txtQntSorvete, txtQntSuco;
    private TextView txtViewGarcon;
    private String strGarconSelecionado = "0", strNotaPadrao, strNotaPedidos, strNota1, strNota2, strConferenciaMesa, strItemConferenciaMesa;
    private String strNotaPedidosBatata, strNotaPedidosHamb1, strNotaPedidosHamb2, strNotaPedidosNuggets, strNotaPedidosRefri, strNotaPedidosTortinha1, strNotaPedidosTortinha2, strNotaPedidosSorvete, strNotaPedidosSuco;
    private String strItemConferenciaMesaBatata, strItemConferenciaMesaHamb1, strItemConferenciaMesaHamb2, strItemConferenciaMesaNuggets, strItemConferenciaMesaRefri, strItemConferenciaMesaTortinha1, strItemConferenciaMesaTortinha2, strItemConferenciaMesaSorvete, strItemConferenciaMesaSuco;



    int CtrlImg = 0;
    int iQuantBatata = 0,  iQuantHamb1 = 0,  iQuantHamb2 = 0,  iQuantNuggets = 0,  iQuantRefri = 0,  iQuantTortinha1 = 0,  iQuantTortinha2 = 0,  iQuantSorvete = 0,  iQuantSuco = 0;
    int NumLista = 0;
    int iValorItemBatata = 12, iValorItemHamb1 = 12, iValorItemHamb2 = 14, iValorItemNuggets = 15, iValorItemTortinha1 = 10, iValorItemTortinha2 = 10, iValorItemRefri = 12, iValorItemSuco = 16, iValorItemSorvete = 14;
    int iSubTotal = 0;
    int iTotal = 0;
    int iTotalReal = 0;
    int iSoma = 0;
    int Control = 1;
    int iQuantMais = 1, iQuantMenos = 0;



    String reinicImp = "" + ((char) 0x1B) + ((char) 0x40); //Ajuda o desempenhho da  impressão da impressora
    String cmdCanModoChines = "" + ((char) 0x1C) + ((char) 0x2E); //Desativa o modo chines
    String centro = "" + ((char) 0x1B) + ((char) 0x61) + ((char) 0x31); //Ativa alinhamento centro
    String deslCentro = "" + ((char) 0x1B) + ((char) 0x61) + ((char) 0x30);
    String extra = "" + ((char) 0x1B) + ((char) 0x21) + ((char) 0x01);
    String deslExtra = "" + ((char) 0x1B) + ((char) 0x21) + ((char) 0x00);
    String qrcode;
    String strCod;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tectoy2 = new TecToy(Dispositivo.V2_PRO, TelaPrincipal.this.getApplicationContext());



        btnOk = findViewById(R.id.btnOk);
        btnSair = findViewById(R.id.btnSair);
        btnFazerPedido = findViewById(R.id.btnFazerPedido);
        btnFecharConta = findViewById(R.id.btnFecharConta);
        btnMaisQnt = findViewById(R.id.btnMaisQnt);
        btnMenosQnt = findViewById(R.id.btnMenosQnt);

        txtViewPedido = findViewById(R.id.txtViewPedido);
        txtViewGarcon = findViewById(R.id.txtNomeGarcon);
        txtQntBatata = findViewById(R.id.txtQntBatata);
        txtQntHamb1 = findViewById(R.id.txtQntHamb1);
        txtQntHamb2 = findViewById(R.id.txtQntHamb2);
        txtQntNuggets = findViewById(R.id.txtQntNuggets);
        txtQntRefri = findViewById(R.id.txtQntRefri);
        txtQntTortinha1 = findViewById(R.id.txtQntTortinha1);
        txtQntTortinha2 = findViewById(R.id.txtQntTortinha2);
        txtQntSorvete = findViewById(R.id.txtQntSorvete);
        txtQntSuco = findViewById(R.id.txtQntSuco);


        // Recebendo informações da activity "Standy"
        Intent intentRecebedora = getIntent();
        Bundle extras = intentRecebedora.getExtras();

        strGarconSelecionado = extras.getString("chave_GarconLogin");
        String strNumMesaPasado = extras.getString("chave_num");
        final String[] strStatusMesa = {extras.getString("chave_status")};


        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        strNota1 = "TecToy Automacao  V2Pro\n" +
                "Saiba mais em:\n" +
                "www.tectouautomacao.com.br\n" +
                "=============================\n"+
                "Data/Hora: " +  dateFormat.format(date) +
                "\nComanda/ Mesa: " + strNumMesaPasado +
                "\nN. Item             xQtde\n";

        strNota2 = "TecToy Automacao  V2Pro\n" +
                "Saiba mais em:\n" +
                "www.tectouautomacao.com.br\n" +
                "=============================\n"+
                centro + "Conferencia de Mesa\n" + deslCentro +
                "Data/Hora: " +  dateFormat.format(date) +
                "\nComanda/ Mesa: " + strNumMesaPasado +
                "\n=============================\n" +
                "N. Item        xQtde VlrUn. Tot.\n";

        txtViewPedido.setMovementMethod(new ScrollingMovementMethod());

        NomeGarcons();

        if(strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {// Comparando string com string

            txtViewPedido.setVisibility(View.INVISIBLE);

        } //deixa o txtview invisivel no começo da aplicação

        if (strNumMesaPasado != null) {
            Toast.makeText(TelaPrincipal.this, "" + strStatusMesa[0], Toast.LENGTH_SHORT).show();
        } // aviso do status da mesa selecionada

        if (strGarconSelecionado != null) {
            Toast.makeText(TelaPrincipal.this, "" + strGarconSelecionado, Toast.LENGTH_SHORT).show();
        } // aviso do status da mesa selecionada

        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });// Botão Sair

        btnFazerPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    CtrlImg = 1;
                    txtViewPedido.setVisibility(View.INVISIBLE);

                    ControleImagemAparecer();
                    NotaPedidos();

            }
        });// Botão Fazer pedido

        btnFecharConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Control == 1) {

                    AlertDialog.Builder alrtImpressaoPedido = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtImpressaoPedido.setTitle("IMPRESSÃO PEDIDO");
                    alrtImpressaoPedido.setMessage("Ainda não foram adicionados itens na conta");

                    alrtImpressaoPedido.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }

                    });
                    alrtImpressaoPedido.create().show();
                } else {


                    AlertDialog.Builder alrtImpressaoPedido = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtImpressaoPedido.setTitle("FECHAR PEDIDO");
                    alrtImpressaoPedido.setMessage("Deseja visualizar o pedido antes de imprimir ?");

                    alrtImpressaoPedido.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            CtrlImg = 0;
                            ControleImagemDesaparecer();
                            txtViewPedido.setText(strNota1);
                            txtViewPedido.setVisibility(View.VISIBLE);

                            if(iQuantBatata>0){
                                iQuantBatata = 0;
                                txtQntBatata.setText(String.valueOf(0));
                            }
                            if(iQuantHamb1>0){
                                iQuantHamb1 = 0;
                                txtQntHamb1.setText(String.valueOf(0));
                            }
                            if(iQuantHamb2>0){
                                iQuantHamb2 = 0;
                                txtQntHamb2.setText(String.valueOf(0));
                            }
                            if(iQuantNuggets>0){
                                iQuantNuggets = 0;
                                txtQntNuggets.setText(String.valueOf(0));
                            }
                            if(iQuantRefri>0){
                                iQuantRefri = 0;
                                txtQntRefri.setText(String.valueOf(0));
                            }
                            if(iQuantTortinha1>0){
                                iQuantTortinha1 = 0;
                                txtQntTortinha1.setText(String.valueOf(0));
                            }
                            if(iQuantTortinha2>0){
                                iQuantTortinha2 = 0;
                                txtQntTortinha2.setText(String.valueOf(0));
                            }
                            if(iQuantSorvete>0){
                                iQuantSorvete = 0;
                                txtQntSorvete.setText(String.valueOf(0));
                            }
                            if(iQuantSuco>0){
                                iQuantSuco = 0;
                                txtQntSuco.setText(String.valueOf(0));
                            }

                        }
                    });
                    alrtImpressaoPedido.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Calculos();
                            Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : FECHADA", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    alrtImpressaoPedido.create().show();

                }
            }
        });// Botão Fechar conta

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Batata
                if (iQuantBatata != 0) {

                    NumLista++;


                        strNotaPadrao = NumLista + ".  " + strNotaPedidosBatata;
                        strNotaPadrao += "x" + iQuantBatata + "\n";

                        iTotal = iQuantBatata * iValorItemBatata;
                        iSoma = iTotal + iSoma;

                        strConferenciaMesa = NumLista + ". " + strItemConferenciaMesaBatata;
                        strConferenciaMesa += "x" + iQuantBatata + "  " + iValorItemBatata + ".00 " + iTotal + ".00" + "\n";

                        if (strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {
                            Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTO", Toast.LENGTH_SHORT).show();
                            strStatusMesa[0] = "STATUS DA MESA : ABERTO";
                        }

                        strNota1 += strNotaPadrao;
                        strNota2 += strConferenciaMesa;
                        Control = 0;

                }

                // Hamb1
                if(iQuantHamb1!=0) {

                    NumLista++;

                        strNotaPadrao = NumLista + ".  " + strNotaPedidosHamb1;
                        strNotaPadrao += "x" + iQuantHamb1 + "\n";

                        iTotal = iQuantHamb1 * iValorItemHamb1;
                        iSoma = iTotal + iSoma;

                        strConferenciaMesa = NumLista + ". " + strItemConferenciaMesaHamb1;
                        strConferenciaMesa += "x" + iQuantHamb1 + "  " + iValorItemHamb1 + ".00 " + iTotal + ".00" + "\n";

                        if (strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {
                            Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTO", Toast.LENGTH_SHORT).show();
                            strStatusMesa[0] = "STATUS DA MESA : ABERTO";
                        }

                        strNota1 += strNotaPadrao;
                        strNota2 += strConferenciaMesa;
                        Control = 0;

                }

                // Hamb2
                if(iQuantHamb2!=0) {

                    NumLista++;


                        strNotaPadrao = NumLista + ".  " + strNotaPedidosHamb2;
                        strNotaPadrao += "x" + iQuantHamb2 + "\n";

                        iTotal = iQuantHamb2 * iValorItemHamb2;
                        iSoma = iTotal + iSoma;

                        strConferenciaMesa = NumLista + ". " + strItemConferenciaMesaHamb2;
                        strConferenciaMesa += "x" + iQuantHamb2 + "  " + iValorItemHamb2 + ".00 " + iTotal + ".00" + "\n";

                        if (strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {
                            Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTO", Toast.LENGTH_SHORT).show();
                            strStatusMesa[0] = "STATUS DA MESA : ABERTO";
                        }

                        strNota1 += strNotaPadrao;
                        strNota2 += strConferenciaMesa;
                        Control = 0;

                }

                // Nuggets
                if(iQuantNuggets!=0) {

                    NumLista++;


                        strNotaPadrao = NumLista + ".  " + strNotaPedidosNuggets;
                        strNotaPadrao += "x" + iQuantNuggets + "\n";

                        iTotal = iQuantNuggets * iValorItemNuggets;
                        iSoma = iTotal + iSoma;

                        strConferenciaMesa = NumLista + ". " + strItemConferenciaMesaNuggets;
                        strConferenciaMesa += "x" + iQuantNuggets + "  " + iValorItemNuggets + ".00 " + iTotal + ".00" + "\n";

                        if (strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {
                            Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTO", Toast.LENGTH_SHORT).show();
                            strStatusMesa[0] = "STATUS DA MESA : ABERTO";
                        }

                        strNota1 += strNotaPadrao;
                        strNota2 += strConferenciaMesa;
                        Control = 0;

                }

                // Refri
                if(iQuantRefri!=0) {

                    NumLista++;

                        strNotaPadrao = NumLista + ".  " + strNotaPedidosRefri;
                        strNotaPadrao += "x" + iQuantRefri + "\n";

                        iTotal = iQuantRefri * iValorItemRefri;
                        iSoma = iTotal + iSoma;

                        strConferenciaMesa = NumLista + ". " + strItemConferenciaMesaRefri;
                        strConferenciaMesa += "x" + iQuantRefri + "  " + iValorItemRefri + ".00 " + iTotal + ".00" + "\n";

                        if (strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {
                            Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTO", Toast.LENGTH_SHORT).show();
                            strStatusMesa[0] = "STATUS DA MESA : ABERTO";
                        }

                        strNota1 += strNotaPadrao;
                        strNota2 += strConferenciaMesa;
                        Control = 0;

                }

                // Tortinha1
                if(iQuantTortinha1!=0) {

                    NumLista++;


                        strNotaPadrao = NumLista + ".  " + strNotaPedidosTortinha1;
                        strNotaPadrao += "x" + iQuantTortinha1 + "\n";

                        iTotal = iQuantTortinha1 * iValorItemTortinha1;
                        iSoma = iTotal + iSoma;

                        strConferenciaMesa = NumLista + ". " + strItemConferenciaMesaTortinha1;
                        strConferenciaMesa += "x" + iQuantTortinha1 + "  " + iValorItemTortinha1 + ".00 " + iTotal + ".00" + "\n";

                        if (strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {
                            Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTO", Toast.LENGTH_SHORT).show();
                            strStatusMesa[0] = "STATUS DA MESA : ABERTO";
                        }

                        strNota1 += strNotaPadrao;
                        strNota2 += strConferenciaMesa;
                        Control = 0;

                }

                // Tortinha2
                if(iQuantTortinha2!=0) {

                    NumLista++;


                        strNotaPadrao = NumLista + ".  " + strNotaPedidosTortinha2;
                        strNotaPadrao += "x" + iQuantTortinha2 + "\n";

                        iTotal = iQuantTortinha2 * iValorItemTortinha2;
                        iSoma = iTotal + iSoma;

                        strConferenciaMesa = NumLista + ". " + strItemConferenciaMesaTortinha2;
                        strConferenciaMesa += "x" + iQuantTortinha2 + "  " + iValorItemTortinha2 + ".00 " + iTotal + ".00" + "\n";

                        if (strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {
                            Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTO", Toast.LENGTH_SHORT).show();
                            strStatusMesa[0] = "STATUS DA MESA : ABERTO";
                        }

                        strNota1 += strNotaPadrao;
                        strNota2 += strConferenciaMesa;
                        Control = 0;

                }

                // Suco
                if(iQuantSuco!=0) {

                    NumLista++;


                        strNotaPadrao = NumLista + ".  " + strNotaPedidosSuco;
                        strNotaPadrao += "x" + iQuantSuco + "\n";

                        iTotal = iQuantSuco * iValorItemSuco;
                        iSoma = iTotal + iSoma;

                        strConferenciaMesa = NumLista + ". " + strItemConferenciaMesaSuco;
                        strConferenciaMesa += "x" + iQuantSuco + "  " + iValorItemSuco + ".00 " + iTotal + ".00" + "\n";

                        if (strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {
                            Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTO", Toast.LENGTH_SHORT).show();
                            strStatusMesa[0] = "STATUS DA MESA : ABERTO";
                        }

                        strNota1 += strNotaPadrao;
                        strNota2 += strConferenciaMesa;
                        Control = 0;

                }

                // Sorvete
                if(iQuantSorvete!=0) {

                    NumLista++;

                    strNotaPadrao = NumLista + ".  " + strNotaPedidosSorvete;
                    strNotaPadrao += "x" + iQuantSorvete + "\n";

                    iTotal = iQuantSorvete * iValorItemSorvete;
                    iSoma = iTotal + iSoma;

                    strConferenciaMesa = NumLista + ". " + strItemConferenciaMesaSorvete;
                    strConferenciaMesa += "x" + iQuantSorvete + "  " + iValorItemSorvete + ".00 " + iTotal + ".00" + "\n";

                    if (strStatusMesa[0].equals("STATUS DA MESA : FECHADA")) {
                        Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTO", Toast.LENGTH_SHORT).show();
                        strStatusMesa[0] = "STATUS DA MESA : ABERTO";
                    }

                    strNota1 += strNotaPadrao;
                    strNota2 += strConferenciaMesa;
                    Control = 0;
                }

                ////////////////////////

                if(iQuantBatata>0){
                    iQuantBatata = 0;
                    txtQntBatata.setText(String.valueOf(0));
                }
                if(iQuantHamb1>0){
                    iQuantHamb1 = 0;
                    txtQntHamb1.setText(String.valueOf(0));
                }
                if(iQuantHamb2>0){
                    iQuantHamb2 = 0;
                    txtQntHamb2.setText(String.valueOf(0));
                }
                if(iQuantNuggets>0){
                    iQuantNuggets = 0;
                    txtQntNuggets.setText(String.valueOf(0));
                }
                if(iQuantRefri>0){
                    iQuantRefri = 0;
                    txtQntRefri.setText(String.valueOf(0));
                }
                if(iQuantTortinha1>0){
                    iQuantTortinha1 = 0;
                    txtQntTortinha1.setText(String.valueOf(0));
                }
                if(iQuantTortinha2>0){
                    iQuantTortinha2 = 0;
                    txtQntTortinha2.setText(String.valueOf(0));
                }
                if(iQuantSorvete>0){
                    iQuantSorvete = 0;
                    txtQntSorvete.setText(String.valueOf(0));
                }
                if(iQuantSuco>0){
                    iQuantSuco = 0;
                    txtQntSuco.setText(String.valueOf(0));
                }


                AlertDialog.Builder alrtImpressaoPedido  = new AlertDialog.Builder(TelaPrincipal.this);
                alrtImpressaoPedido.setTitle("IMPRESSÃO PEDIDO");
                alrtImpressaoPedido.setMessage("Deseja realizar a impressao ?");

                alrtImpressaoPedido.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        strNota1 += "\n";
                        tectoy2.imprimir(reinicImp);
                        tectoy2.imprimir(cmdCanModoChines);
                        tectoy2.imprimir(strNota1);
                        Toast.makeText(TelaPrincipal.this, "STATUS DA MESA : ABERTA", Toast.LENGTH_SHORT).show();
                    }
                });
                alrtImpressaoPedido.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alrtImpressaoPedido.create().show();


            }
        });// Botão OK


    }//onCreate



    void NomeGarcons() {

        if (strGarconSelecionado.equals("0") ) {
            txtViewGarcon.setText("LOGUE VOLTANDO E CLICANDO NO PRATO");
        } else if(strGarconSelecionado.equals("000000000109")){
            txtViewGarcon.setText("GARCON CHEFE");
        } else if (strGarconSelecionado.equals("000000000116")) {
            txtViewGarcon.setText("GARCON 2");
        } else if (strGarconSelecionado.equals("000000000017")){
            txtViewGarcon.setText("PROPRIETARIO");
        } else if (strGarconSelecionado.equals("000000000031")){
            txtViewGarcon.setText("BALCAO");
        }

    }//Seta o nome dos garcons

    void ControleImagemAparecer(){
        if(CtrlImg == 1){


            txtQntBatata.setVisibility(View.VISIBLE);
            txtQntHamb1.setVisibility(View.VISIBLE);
            txtQntHamb2.setVisibility(View.VISIBLE);
            txtQntNuggets.setVisibility(View.VISIBLE);
            txtQntTortinha1.setVisibility(View.VISIBLE);
            txtQntTortinha2.setVisibility(View.VISIBLE);
            txtQntSuco.setVisibility(View.VISIBLE);
            txtQntRefri.setVisibility(View.VISIBLE);
            txtQntSorvete.setVisibility(View.VISIBLE);
            txtQntNuggets.setVisibility(View.VISIBLE);
            btnMaisQnt.setVisibility(View.VISIBLE);
            btnMenosQnt.setVisibility(View.VISIBLE);
            btnOk.setVisibility(View.VISIBLE);

        }
    } // Faz as imagens aparecerem quando vai fazer o pedido

    void ControleImagemDesaparecer(){
        if(CtrlImg == 0){

            txtQntBatata.setVisibility(View.INVISIBLE);
            txtQntHamb1.setVisibility(View.INVISIBLE);
            txtQntHamb2.setVisibility(View.INVISIBLE);
            txtQntNuggets.setVisibility(View.INVISIBLE);
            txtQntTortinha1.setVisibility(View.INVISIBLE);
            txtQntTortinha2.setVisibility(View.INVISIBLE);
            txtQntSuco.setVisibility(View.INVISIBLE);
            txtQntSorvete.setVisibility(View.INVISIBLE);
            txtQntNuggets.setVisibility(View.INVISIBLE);
            txtQntRefri.setVisibility(View.INVISIBLE);
            btnMaisQnt.setVisibility(View.INVISIBLE);
            btnMenosQnt.setVisibility(View.INVISIBLE);
            btnOk.setVisibility(View.INVISIBLE);

        }
    } // Faz as imagens desaparecerem quando vai fazer o pedido

    void NotaPedidos(){

        txtQntBatata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNotaPedidosBatata = "Batata Frita      ";// 18 char até o qnt
                strItemConferenciaMesaBatata = "Batata Frita ";

                if (iQuantMenos == 1) {
                    iQuantBatata--;
                }else{
                    iQuantBatata++;
                }

                if (iQuantBatata < 0) {
                    AlertDialog.Builder alrtQuantNegativa = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtQuantNegativa.setTitle("Quantidade Invalida");
                    alrtQuantNegativa.setMessage("Quantidade negativa.");
                    alrtQuantNegativa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alrtQuantNegativa.create().show();
                    txtQntBatata.setText(String.valueOf(0));
                    iQuantBatata = 0;
                }
                txtQntBatata.setText(String.valueOf(iQuantBatata));
            }
        });

        txtQntHamb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNotaPedidosHamb1 = "X Salada          " ;
                strItemConferenciaMesaHamb1 = "X Salada     " ;

                if (iQuantMenos == 1) {
                    iQuantHamb1--;
                }else{
                    iQuantHamb1++;
                }

                if (iQuantHamb1 < 0) {
                    AlertDialog.Builder alrtQuantNegativa = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtQuantNegativa.setTitle("Quantidade Invalida");
                    alrtQuantNegativa.setMessage("Quantidade negativa.");
                    alrtQuantNegativa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alrtQuantNegativa.create().show();
                    txtQntHamb1.setText(String.valueOf(0));
                    iQuantHamb1 = 0;
                }
                txtQntHamb1.setText(String.valueOf(iQuantHamb1));
            }
        });

        txtQntHamb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNotaPedidosHamb2 = "X Chicken         ";
                strItemConferenciaMesaHamb2 = "X Chicken    ";
                if (iQuantMenos == 1) {
                    iQuantHamb2--;
                }else{
                    iQuantHamb2++;
                }

                if (iQuantHamb2 < 0) {
                    AlertDialog.Builder alrtQuantNegativa = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtQuantNegativa.setTitle("Quantidade Invalida");
                    alrtQuantNegativa.setMessage("Quantidade negativa.");
                    alrtQuantNegativa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alrtQuantNegativa.create().show();
                    txtQntHamb2.setText(String.valueOf(0));
                    iQuantHamb2 = 0;
                }
                txtQntHamb2.setText(String.valueOf(iQuantHamb2));
            }
        });

        txtQntSorvete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNotaPedidosSorvete = "Sorvete           ";
                strItemConferenciaMesaSorvete = "Sorvete      ";
                if (iQuantMenos == 1) {
                    iQuantSorvete--;
                }else{
                    iQuantSorvete++;
                }

                if (iQuantSorvete < 0) {
                    AlertDialog.Builder alrtQuantNegativa = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtQuantNegativa.setTitle("Quantidade Invalida");
                    alrtQuantNegativa.setMessage("Quantidade negativa.");
                    alrtQuantNegativa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alrtQuantNegativa.create().show();
                    txtQntSorvete.setText(String.valueOf(0));
                    iQuantSorvete = 0;
                }
                txtQntSorvete.setText(String.valueOf(iQuantSorvete));
            }
        });

        txtQntNuggets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNotaPedidosNuggets = "Nuggets           ";
                strItemConferenciaMesaNuggets = "Nuggets      ";
                if (iQuantMenos == 1) {
                    iQuantNuggets--;
                }else{
                    iQuantNuggets++;
                }

                if (iQuantNuggets < 0) {
                    AlertDialog.Builder alrtQuantNegativa = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtQuantNegativa.setTitle("Quantidade Invalida");
                    alrtQuantNegativa.setMessage("Quantidade negativa.");
                    alrtQuantNegativa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alrtQuantNegativa.create().show();
                    txtQntNuggets.setText(String.valueOf(0));
                    iQuantNuggets = 0;
                }
                txtQntNuggets.setText(String.valueOf(iQuantNuggets));
            }
        });

        txtQntRefri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNotaPedidosRefri = "Refrigerante      ";
                strItemConferenciaMesaRefri = "Refrigerante ";
                if (iQuantMenos == 1) {
                    iQuantRefri--;
                }else{
                    iQuantRefri++;
                }

                if (iQuantRefri < 0) {
                    AlertDialog.Builder alrtQuantNegativa = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtQuantNegativa.setTitle("Quantidade Invalida");
                    alrtQuantNegativa.setMessage("Quantidade negativa.");
                    alrtQuantNegativa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alrtQuantNegativa.create().show();
                    txtQntRefri.setText(String.valueOf(0));
                    iQuantRefri = 0;
                }
                txtQntRefri.setText(String.valueOf(iQuantRefri));
            }
        });

        txtQntTortinha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNotaPedidosTortinha1 = "Torta Choco       ";
                strItemConferenciaMesaTortinha1 = "Torta Choco  ";
                if (iQuantMenos == 1) {
                    iQuantTortinha1--;
                }else{
                    iQuantTortinha1++;
                }

                if (iQuantTortinha1 < 0) {
                    AlertDialog.Builder alrtQuantNegativa = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtQuantNegativa.setTitle("Quantidade Invalida");
                    alrtQuantNegativa.setMessage("Quantidade negativa.");
                    alrtQuantNegativa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alrtQuantNegativa.create().show();
                    txtQntTortinha1.setText(String.valueOf(0));
                    iQuantTortinha1 = 0;
                }
                txtQntTortinha1.setText(String.valueOf(iQuantTortinha1));

            }
        });

        txtQntTortinha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNotaPedidosTortinha2 = "Torta Creme       ";
                strItemConferenciaMesaTortinha2 = "Torta Creme  ";
                if (iQuantMenos == 1) {
                    iQuantTortinha2--;
                }else{
                    iQuantTortinha2++;
                }

                if (iQuantTortinha2 < 0) {
                    AlertDialog.Builder alrtQuantNegativa = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtQuantNegativa.setTitle("Quantidade Invalida");
                    alrtQuantNegativa.setMessage("Quantidade negativa.");
                    alrtQuantNegativa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alrtQuantNegativa.create().show();
                    txtQntTortinha2.setText(String.valueOf(0));
                    iQuantTortinha2 = 0;
                }
                txtQntTortinha2.setText(String.valueOf(iQuantTortinha2));
            }
        });

        txtQntSuco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNotaPedidosSuco =  "Suco              ";
                strItemConferenciaMesaSuco =  "Suco         ";
                if (iQuantMenos == 1) {
                    iQuantSuco--;
                }else{
                    iQuantSuco++;
                }

                if (iQuantSuco < 0) {
                    AlertDialog.Builder alrtQuantNegativa = new AlertDialog.Builder(TelaPrincipal.this);
                    alrtQuantNegativa.setTitle("Quantidade Invalida");
                    alrtQuantNegativa.setMessage("Quantidade negativa.");
                    alrtQuantNegativa.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alrtQuantNegativa.create().show();
                    txtQntSuco.setText(String.valueOf(0));
                    iQuantSuco = 0;
                }
                txtQntSuco.setText(String.valueOf(iQuantSuco));
            }
        });

        btnMaisQnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQuantMenos = 0;
                iQuantMais = 1;
            }
        });

        btnMenosQnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iQuantMais = 0;
                iQuantMenos = 1;
            }
        });

    }

    void Calculos(){

        iSubTotal = iSoma+iSubTotal;
        iTotalReal = iSubTotal+5;

        strNota2 +="=============================\n";
        strNota2 +="SubTotal :             " +   "R$ " + iSubTotal + ".00\n";
        strNota2 +="Servico R$ 5.00 :      R$  5.00\n";
        strNota2 +="Total :                " +   "R$ " +iTotalReal + ".00\n\n";
        strNota2 +="Obrigada pela sua visita\n\n";
        strNota2 += "Agora voce pode pagar com PIX\n\n";
        strNota2 +="\n\n\n";

        tectoy2.imprimir(reinicImp);
        tectoy2.imprimir(cmdCanModoChines);
        tectoy2.imprimir(strNota2);
        tectoy2.imprimirQrCode("https://www.tectoyautomacao.com.br", "M", 3);
    }

}//Tela Principal



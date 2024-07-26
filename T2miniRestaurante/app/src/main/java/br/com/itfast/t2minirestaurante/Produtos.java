package br.com.itfast.t2minirestaurante;

import android.util.Log;

public class Produtos {
    String strCodProduto;
    public String strPreço, strToken;
    public String[] preços = new String[7];
    public static Produtos myInstance = null;
    public static Produtos getInstance() {
        return myInstance;
    }

    public Produtos() {
        myInstance = this;
    }
    public String idLoja(){
        String idLj="1";
        return idLj;
    }
    public String getToken(){
        return this.strToken;
    }

    public void setToken(String stken){
        this.strToken = stken;
    }
    public String getPreço(String sProd)
    {
        if (sProd.equals("588968759433")) { //Hamburguer
            strPreço = preços[0];
            Log.i("DEMO T2Mini","getPreço preencheu: "+sProd+" | " +strPreço);
        } else if (sProd.equals("588968759436")) { //Fritas
            strPreço = preços[1];
            Log.i("DEMO T2Mini","getPreço preencheu: "+sProd+" | " +strPreço);
        } else if (sProd.equals("588968759513")) { //Self-Servce
            strPreço = preços[2];
            Log.i("DEMO T2Mini","getPreço preencheu: "+sProd+" | " +strPreço);
        } else if (sProd.equals("588968759438")) {// Refrigerante
            strPreço = preços[3];
            Log.i("DEMO T2Mini","getPreço preencheu: "+sProd+" | " +strPreço);
        } else if (sProd.equals("588968759512")) { //Suco
            strPreço = preços[4];
            Log.i("DEMO T2Mini","getPreço preencheu: "+sProd+" | " +strPreço);
        } else if (sProd.equals("588968759475")) { //Sorvete
            strPreço = preços[5];
            Log.i("DEMO T2Mini","getPreço preencheu: "+sProd+" | " +strPreço);
        } else if (sProd.equals("588968759514")) { //Sobremesa
            strPreço = preços[6];
            Log.i("DEMO T2Mini","getPreço preencheu: "+sProd+" | " +strPreço);
        } else {
            Log.i("DEMO T2Mini", "getPreço: Não existe produto informado.");
        }
        return strPreço;
    }
    public void setPreco(String sProd, String preco){
        strPreço = preco;
        if (sProd.equals("588968759433")) {
            preços[0] = strPreço;
            Log.i("DEMO T2Mini","setPreço preencheu: "+sProd+" | " +preços[0]);
        } else if (sProd.equals("588968759436")) {
            preços[1] = strPreço;
            Log.i("DEMO T2Mini","setPreço preencheu: "+sProd+" | " +preços[1]);
        } else if (sProd.equals("588968759513")) {
            preços[2] = strPreço;
            Log.i("DEMO T2Mini","setPreço preencheu: "+sProd+" | " +preços[2]);
        } else if (sProd.equals("588968759438")) {
            preços[3] = strPreço;
            Log.i("DEMO T2Mini","setPreço preencheu: "+sProd+" | " +preços[3]);
        } else if (sProd.equals("588968759512")) {
            preços[4] = strPreço;
            Log.i("DEMO T2Mini","setPreço preencheu: "+sProd+" | " +preços[4]);
        } else if (sProd.equals("588968759475")) {
            preços[5] = strPreço;
            Log.i("DEMO T2Mini","setPreço preencheu: "+sProd+" | " +preços[5]);
        } else if (sProd.equals("588968759514")) {
            preços[6] = strPreço;
            Log.i("DEMO T2Mini","setPreço preencheu: "+sProd+" | " +preços[6]);
        } else {
            Log.i("DEMO T2Mini", "setPreço: SEM produto para retornar preço. >" +sProd+"<");
        }
    }
}

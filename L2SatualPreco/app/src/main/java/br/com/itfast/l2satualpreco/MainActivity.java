package br.com.itfast.l2satualpreco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnAtualiza;
    private EditText txtUsuario, txtSenha, txtEtiqueta, txtNovoPreco, txtIdLoja;
    private TextView txNomeProd, txPreco, txView2;
    private ProgressBar load;

    private String strUsuario, strSenha, strToken, codLido, idLoja, strProduto, strNpreco, strResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnAtualiza = findViewById(R.id.btnAtualiza);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtSenha = findViewById(R.id.txtSenha);
        txtEtiqueta = findViewById(R.id.txtEtiqueta);
        txtNovoPreco = findViewById(R.id.txtNovoPreco);
        txtIdLoja = findViewById(R.id.txtIdLoja);

        load = findViewById(R.id.progressBar);


        txNomeProd = findViewById(R.id.txNomeProd);
        txPreco = findViewById(R.id.txPreco);
        txView2 = findViewById(R.id.textView2);

        txtUsuario.requestFocus();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strUsuario = "";
                strSenha  = "";
                strToken = "";
                idLoja = "";
                strProduto = "";
                strUsuario = txtUsuario.getText().toString();
                strSenha = txtSenha.getText().toString();
                idLoja = txtIdLoja.getText().toString();
                if (strUsuario.length()!=0){
                    if (strSenha.length()!=0){
                        if (idLoja.length()!=0){
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("login", strUsuario);
                                jsonObject.put("password", strSenha);
                                String jsonString = jsonObject.toString();
                                new PostDataLogin().execute(jsonString);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }else {
                            Toast.makeText(MainActivity.this, "Insira o ID da LOJA!",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Digite a senha!",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Digite um usuário e senha!",Toast.LENGTH_LONG).show();
                }
            }
        });

        txtEtiqueta.setOnKeyListener( new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    mostrarAtualiza();

                    return false;
                }
                return false;
            }
        });

        txtNovoPreco.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (false ==hasFocus){
                    ((InputMethodManager)  MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            txtNovoPreco.getWindowToken(), 0);
                }
            }
        });

        btnAtualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    strNpreco = txtNovoPreco.getText().toString();
                    if (strNpreco.isEmpty() || Float.parseFloat(strNpreco) < 1) {
                        Toast.makeText(MainActivity.this, "Sem valor para atualização, leia o codigo novamente.", Toast.LENGTH_SHORT).show();
                        escondeAtualiza();
                        txNomeProd.setText("");
                        txPreco.setText("");
                    } else {
                        try {
                            JSONObject jsonObject3 = new JSONObject();
                            jsonObject3.put("product_list", "[{\"id\":\"" + codLido + "\", \"price\":\"" + strNpreco + "\"}]");
                            jsonObject3.put("shop_id", idLoja);
                            String jsonString3 = jsonObject3.toString();
                            Log.d("DEMO Coletor", "ENVIOU >>>>>> " + jsonString3);
                            new PostDataAtualiza().execute(jsonString3);
                            escondeAtualiza();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }catch (Exception e){
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Ocorreu erro, verifique o valor informado.", Toast.LENGTH_LONG).show();
                    });
                }

            }
        });

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
                Log.d("DEMO Coletor", "Falha ao fazer login : " + e.getMessage() );
               // Toast.makeText(MainActivity.this, "Falha ao fazer login : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return strToken;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if (s!=null || s.length()!=0 ){
                    if (!s.contains("err")){
                        strToken = s.replace("\"", "");
                        mostrarEtiqueta();
                    }else {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Erro, verifique o LOGIN e SENHA e tente novamente.", Toast.LENGTH_LONG).show();
                        });
                    }
                }
            }catch (Exception e){
                Log.d("DEMO Coletor", e.getMessage());

            }
            load.setVisibility(View.INVISIBLE);
        }
    }

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

                // url d API para Consulta Produto
                URL url = new URL("https://itfastesl.azurewebsites.net/api/product/getInfo");
              // Abrir conexão
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                // definindo o método post
                client.setRequestMethod("POST");
                // definindo tpo de conteudo envio e resposta.
                client.setRequestProperty("Authorization", strToken);
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
                Log.d("DEMO Coletor", "Erro na consulta: " + e.getMessage() );
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
                        txNomeProd.setText(prod.getString("name").toString());
                        txPreco.setText("R$ "+prod.getString("price").toString());
                        mostrarAtualiza();
                    }else{
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Ocorreu erro: " + s, Toast.LENGTH_LONG).show();
                        });
                        txtEtiqueta.setText("");
                        txtEtiqueta.requestFocus();
                    }

                }
            }catch (JSONException e) {
                Log.d("DEMO Coletor", e.getMessage());
            }
            load.setVisibility(View.GONE);

        }
    }

    //Atualizar Preço de 1 produto
    class PostDataAtualiza extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                // url d API para Consulta Produto
                URL url = new URL("https://itfastesl.azurewebsites.net/api/product/update");
                // Abrir conexão
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                // definindo o método post
                client.setRequestMethod("POST");
                // definindo tipo de conteudo envio e resposta.
                client.setRequestProperty("Authorization", strToken);
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
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), "utf-8"))) {
                    StringBuilder respProd = new StringBuilder();
                    String responseLine = null;

                    // escrevendo a resposta
                    while ((responseLine = br.readLine()) != null) {
                        respProd.append(responseLine.trim());
                    }
                    strResp = respProd.toString();
                }
            } catch (Exception e) {
                Log.d ("DEMO Coletor", e.getMessage());
            }
            return strResp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("DEMO Coletor", "Resp Atualiza: " + strResp);
            try{
                if (s!=null || s.length()!=0){
                    JSONObject jsonResp = new JSONObject(s);
                    String msg = jsonResp.getString("msg").toString();
                    if (msg.equals("succeed")){
                        JSONObject jsonRdata = new JSONObject(jsonResp.getString("data").toString());
                        if(jsonRdata.getString("not_exist_list").equals("[]") && jsonRdata.getString("invalid_list").equals("[]")){
                            txPreco.setText("R$ " + strNpreco);
                            escondeAtualiza();
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "Preço ATUALIZADO com sucesso!", Toast.LENGTH_SHORT).show();
                            });
                        }else{
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "Ocorreu erro: " + jsonRdata.toString(), Toast.LENGTH_LONG).show();
                            });
                            txPreco.setText("");
                            txNomeProd.setText("");
                            escondeAtualiza();
                        }
                    }
                }
            }catch (JSONException e) {
                Log.d("DEMO Coletor", e.getMessage());
            }
            load.setVisibility(View.GONE);
        }
    }
    void mostrarEtiqueta(){
        txtEtiqueta.setVisibility(View.VISIBLE);
        txtEtiqueta.requestFocus();
        btnLogin.setVisibility(View.INVISIBLE);
        txtUsuario.setEnabled(false);
        txView2.setVisibility(View.GONE);
        txtSenha.setVisibility(View.GONE);
        txtIdLoja.setEnabled(false);
    }
    void mostrarAtualiza(){
        txNomeProd.setVisibility(View.VISIBLE);
        txPreco.setVisibility(View.VISIBLE);
        txtNovoPreco.setVisibility(View.VISIBLE);
        btnAtualiza.setVisibility(View.VISIBLE);
        txtNovoPreco.requestFocus();

    }
    void escondeAtualiza(){
        txtNovoPreco.setVisibility(View.INVISIBLE);
        btnAtualiza.setVisibility(View.INVISIBLE);
        txtNovoPreco.setText("");
        txtEtiqueta.setText("");
        txtEtiqueta.requestFocus();
    }

}
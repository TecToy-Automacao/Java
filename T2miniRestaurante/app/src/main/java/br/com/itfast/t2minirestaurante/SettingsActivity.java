package br.com.itfast.t2minirestaurante;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import br.com.daruma.framework.mobile.webservice.TrustedManagerManipulator;


public class SettingsActivity extends AppCompatActivity {

    String idLoja, strResp, hamburguerValue, sorveteValue, fritasValue, refrigeranteValue, sucoValue, sobremesaValue, sserviceValue;

    Button btnVoltar, btnAtualizar;

    EditText txtHamburguer, txtFritas, txtSelfService, txtRefrigerante, txtSuco, txtSobremesa, txtSorvete;
    ProgressBar load2;

    Produtos prod;
    int count,cAtual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prod = Produtos.getInstance();
        idLoja = prod.idLoja();

        load2 = findViewById(R.id.progressBar2);
        txtHamburguer = (EditText) findViewById(R.id.txtHamburguer);
        txtFritas = (EditText) findViewById(R.id.txtFritas);
        txtSelfService = (EditText) findViewById(R.id.txtSelfService);
        txtRefrigerante = (EditText) findViewById(R.id.txtRefrigerante);
        txtSuco = (EditText) findViewById(R.id.txtSuco);
        txtSobremesa = (EditText) findViewById(R.id.txtSorvete);
        txtSorvete = (EditText) findViewById(R.id.txtSobremesa);
        btnAtualizar = findViewById(R.id.btnConfigurar);
        btnVoltar = findViewById(R.id.btnVoltar);

        hamburguerValue = prod.getPreço("588968759433");
        fritasValue = prod.getPreço("588968759436");
        sserviceValue = prod.getPreço("588968759513");
        refrigeranteValue = prod.getPreço("588968759438");
        sucoValue = prod.getPreço("588968759512");
        sorveteValue = prod.getPreço("588968759514");
        sobremesaValue = prod.getPreço("588968759475") ;

        txtHamburguer.setText(hamburguerValue);
        txtFritas.setText(fritasValue);
        txtSelfService.setText(sserviceValue);
        txtRefrigerante.setText(refrigeranteValue);
        txtSuco.setText(sucoValue);
        txtSobremesa.setText(sobremesaValue);
        txtSorvete.setText(sorveteValue);

        count=0;
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cAtual=0;
                if(String.valueOf(txtHamburguer.getText()).equals(hamburguerValue)){
                    Log.i("DEMO T2MINI Atualiza","Nada a fazer com o hamburguer");
                }else{
                    atualiza("588968759433",String.valueOf(txtHamburguer.getText()));
                    cAtual++;

                }

                if(String.valueOf(txtFritas.getText()).equals(fritasValue)){
                    Log.i("TESTE","Nada a fazer com as fritas");
                }else{
                   atualiza("588968759436", String.valueOf(txtFritas.getText()));
                    cAtual++;
                }

                if(String.valueOf(txtSelfService.getText()).equals(sserviceValue)){
                    Log.i("TESTE","Nada a fazer com as nuggets");
                }else{
                    atualiza( "588968759513", String.valueOf(txtSelfService.getText()));
                    cAtual++;
                }

                if(String.valueOf(txtRefrigerante.getText()).equals(refrigeranteValue)){
                    Log.i("TESTE","Nada a fazer com as refrigerante");
                }else{
                    atualiza( "588968759438", String.valueOf(txtRefrigerante.getText()));
                    cAtual++;
                }

                if(String.valueOf(txtSuco.getText()).equals(sucoValue)){
                    Log.i("TESTE","Nada a fazer com as suco");
                }else{
                    atualiza( "588968759512", String.valueOf(txtSuco.getText()));
                    cAtual++;
                }

                if(String.valueOf(txtSorvete.getText()).equals(sorveteValue)){
                    Log.i("TESTE","Nada a fazer com o sorvete");
                }else{
                    atualiza("588968759475", String.valueOf(txtSorvete.getText()));
                    cAtual++;
                }
                if(String.valueOf(txtSobremesa.getText()).equals(sobremesaValue)){
                    Log.i("TESTE","Nada a fazer com as tortinha");
                }else{
                  atualiza("588968759514", String.valueOf(txtSobremesa.getText()));
                  cAtual++;
                }
            }
        });
    }
void atualiza(String sProd, String sNpreco){
    try {
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("product_list", "[{\"id\":\"" + sProd + "\", \"price\":\"" + sNpreco + "\"}]");
        jsonObject3.put("shop_id", idLoja);
        String jsonString3 = jsonObject3.toString();
        new PostDataAtualiza().execute(jsonString3);
    } catch (JSONException e) {
        throw new RuntimeException(e);
    }
}

    //Atualizar Preço de 1 produto
    class PostDataAtualiza extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load2.setVisibility(View.VISIBLE);
        }
        private TrustManager[] trustManagers;
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
                URL url = new URL("https://itfastesl.azurewebsites.net/api/product/update");
                // Abrir conexão
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                // definindo o método post
                client.setRequestMethod("POST");
                // definindo tipo de conteudo envio e resposta.
                client.setRequestProperty("Authorization", prod.getToken());
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
                            runOnUiThread(() -> {
                                Toast.makeText(SettingsActivity.this, "Preço ATUALIZADO com sucesso!", Toast.LENGTH_SHORT).show();
                            });
                        }else{
                            runOnUiThread(() -> {
                                Toast.makeText(SettingsActivity.this, "Ocorreu erro: " + jsonRdata.toString(), Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                }
            }catch (JSONException e) {
                Log.d("DEMO T2MINI", e.getMessage());
            }
            count ++;
            if (count >= cAtual){
                load2.setVisibility(View.GONE);
                finish();
            }
        }
    }

}
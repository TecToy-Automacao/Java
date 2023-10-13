package com.example.exemploclassetectoy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.com.daruma.framework.mobile.exception.DarumaException;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.StatusImpressora;
import br.com.itfast.tectoy.TecToy;
import br.com.itfast.tectoy.TecToyCameraProfundidadeCallback;
import br.com.itfast.tectoy.TecToyNfcCallback;
import br.com.itfast.tectoy.TecToyScannerCallback;
import br.com.itfast.tectoy.TectoyBalancaCallback;

public class MainActivity extends AppCompatActivity {
    public static TecToy tectoy;
    private PendingIntent pendingIntent;
    private boolean NFCIniciado = false;
    private Button btnStatusImpressora;
    private Button btnImprimir;
    private Button btnStatusGaveta;
    private Button btnImprimirImagem;
    private Button btnAbrirGaveta;
    private Button btnAcionarGuilhotina;
    private Button btnPosicionarEtiqueta;
    private Button btnImprimirQrCode;
    private Button btnPosicionarFinalEtiqueta;
    private Button btnLimparDisplay;
    private Button btnEscreverDisplay;
    private Button btnQrCodeDisplay;
    private Button btnBmpDisplay;
    private Button btnEncerrarScanner;
    private Button btnIniciarScanner;
    private Button btnIniciarNFC;
    private Button btnLerPesoBalanca;
    private Button btnEscreverNFC;
    private Button btnIniciarCameraProfundidade;
    private Button btnDesligarLedIndicacao;
    private Button btnLigarLedIndicacao;
    private TextView txtDistancia;
    TecToyNfcCallback nfcCallbackK2 = s -> this.runOnUiThread(() -> {
        Log.i ("TECTOY", s) ;
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    });

  TecToyNfcCallback nfcCallback = new TecToyNfcCallback() {
      @Override
      public void retornarValor(String strValor) {
              txtDistancia.setText(strValor);
      }
  };

    TectoyBalancaCallback balancaCallback = map -> Toast.makeText(getApplicationContext(), String.valueOf(map.get("peso")), Toast.LENGTH_LONG).show();
    TecToyScannerCallback scannerCallback = s -> Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    TecToyCameraProfundidadeCallback profundidadeCallback = i -> this.runOnUiThread(() -> txtDistancia.setText(String.valueOf(i) + " cm"));

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

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        btnStatusImpressora = findViewById(R.id.btnStatusImpressora);
        btnImprimir = findViewById(R.id.btnImprimir);
        btnStatusGaveta = findViewById(R.id.btnStatusGaveta);
        btnImprimirImagem = findViewById(R.id.btnImprimirImagem);
        btnAbrirGaveta = findViewById(R.id.btnAbrirGaveta);
        btnAcionarGuilhotina = findViewById(R.id.btnAcionarGuilhotina);
        btnPosicionarEtiqueta = findViewById(R.id.btnPosicionarEtiqueta);
        btnImprimirQrCode = findViewById(R.id.btnImprimirQrCode);
        btnPosicionarFinalEtiqueta = findViewById(R.id.btnPosicionarFinalEtiqueta);
        btnLimparDisplay = findViewById(R.id.btnLimparDisplay);
        btnEscreverDisplay = findViewById(R.id.btnEscreverDisplay);
        btnQrCodeDisplay = findViewById(R.id.btnQrCodeDisplay);
        btnBmpDisplay = findViewById(R.id.btnBmpDisplay);
        btnEncerrarScanner = findViewById(R.id.btnEncerrarScanner);
        btnIniciarScanner = findViewById(R.id.btnIniciarScanner);
        btnIniciarNFC = findViewById(R.id.btnIniciarNFC);
        btnLerPesoBalanca = findViewById(R.id.btnLerPesoBalanca);
        btnEscreverNFC = findViewById(R.id.btnEscreverNFC);
        btnIniciarCameraProfundidade = findViewById(R.id.btnIniciarCameraProfundidade);
        txtDistancia = findViewById(R.id.txtDistancia);
        btnDesligarLedIndicacao = findViewById(R.id.btnDesligarLedIndicacao);
        btnLigarLedIndicacao = findViewById(R.id.btnLigarLedIndicacao);
        Button btnIniciar = findViewById(R.id.btnIniciar);
        Spinner spinnerDispositivos = findViewById(R.id.spinnerDispositivos);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dispositivos, R.layout.spinner_item);
        spinnerDispositivos.setAdapter(adapter);
        btnIniciar.setOnClickListener(v -> {
            switch (spinnerDispositivos.getSelectedItem().toString()) {
                case "T2_MINI":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.T2_MINI, getApplicationContext());
                    btnStatusGaveta.setVisibility(View.VISIBLE);
                    btnStatusImpressora.setVisibility(View.VISIBLE);
                    btnImprimir.setVisibility(View.VISIBLE);
                    btnImprimirImagem.setVisibility(View.VISIBLE);
                    btnAbrirGaveta.setVisibility(View.VISIBLE);
                    btnAcionarGuilhotina.setVisibility(View.VISIBLE);
                    btnLimparDisplay.setVisibility(View.VISIBLE);
                    btnBmpDisplay.setVisibility(View.VISIBLE);
                    btnEscreverDisplay.setVisibility(View.VISIBLE);
                    btnQrCodeDisplay.setVisibility(View.VISIBLE);
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    btnImprimirQrCode.setVisibility(View.VISIBLE);
                    btnLerPesoBalanca.setVisibility(View.VISIBLE);
                    break;
                case "D2_MINI":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.D2_MINI, getApplicationContext());
                    btnStatusGaveta.setVisibility(View.VISIBLE);
                    btnStatusImpressora.setVisibility(View.VISIBLE);
                    btnImprimir.setVisibility(View.VISIBLE);
                    btnImprimirImagem.setVisibility(View.VISIBLE);
                    btnAbrirGaveta.setVisibility(View.VISIBLE);
                    btnAcionarGuilhotina.setVisibility(View.VISIBLE);
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    btnImprimirQrCode.setVisibility(View.VISIBLE);
                    btnLerPesoBalanca.setVisibility(View.VISIBLE);
                    break;
                case "D2S":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.D2S, getApplicationContext());
                    btnStatusGaveta.setVisibility(View.VISIBLE);
                    btnAbrirGaveta.setVisibility(View.VISIBLE);
                    btnStatusImpressora.setVisibility(View.VISIBLE);
                    btnImprimir.setVisibility(View.VISIBLE);
                    btnImprimirImagem.setVisibility(View.VISIBLE);
                    btnImprimirQrCode.setVisibility(View.VISIBLE);
                    btnLerPesoBalanca.setVisibility(View.VISIBLE);
                    break;
                case "V2":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.V2, getApplicationContext());
                    btnStatusImpressora.setVisibility(View.VISIBLE);
                    btnImprimir.setVisibility(View.VISIBLE);
                    btnImprimirImagem.setVisibility(View.VISIBLE);
                    btnImprimirQrCode.setVisibility(View.VISIBLE);
                    break;
                case "V2_PRO":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.V2_PRO, getApplicationContext());
                    btnImprimir.setVisibility(View.VISIBLE);
                    btnImprimirImagem.setVisibility(View.VISIBLE);
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    btnImprimirQrCode.setVisibility(View.VISIBLE);
                    btnPosicionarEtiqueta.setVisibility(View.VISIBLE);
                    btnPosicionarFinalEtiqueta.setVisibility(View.VISIBLE);
                    break;
                case "K2":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.K2, getApplicationContext());
                    btnStatusImpressora.setVisibility(View.VISIBLE);
                    btnImprimir.setVisibility(View.VISIBLE);
                    btnImprimirImagem.setVisibility(View.VISIBLE);
                    btnAcionarGuilhotina.setVisibility(View.VISIBLE);
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    btnLerPesoBalanca.setVisibility(View.VISIBLE);
                    btnIniciarCameraProfundidade.setVisibility(View.VISIBLE);
                    btnDesligarLedIndicacao.setVisibility(View.VISIBLE);
                    btnLigarLedIndicacao.setVisibility(View.VISIBLE);
                    btnIniciarScanner.setVisibility(View.VISIBLE);
                    break;
                case "K2_MINI":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.K2_MINI, getApplicationContext());
                    btnStatusGaveta.setVisibility(View.VISIBLE);
                    btnStatusImpressora.setVisibility(View.VISIBLE);
                    btnImprimir.setVisibility(View.VISIBLE);
                    btnImprimirImagem.setVisibility(View.VISIBLE);
                    btnAbrirGaveta.setVisibility(View.VISIBLE);
                    btnAcionarGuilhotina.setVisibility(View.VISIBLE);
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    btnImprimirQrCode.setVisibility(View.VISIBLE);
                    btnLerPesoBalanca.setVisibility(View.VISIBLE);
                    btnDesligarLedIndicacao.setVisibility(View.VISIBLE);
                    btnLigarLedIndicacao.setVisibility(View.VISIBLE);
                    break;
                case "T2S":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.T2S, getApplicationContext());
                    btnStatusGaveta.setVisibility(View.VISIBLE);
                    btnStatusImpressora.setVisibility(View.VISIBLE);
                    btnImprimir.setVisibility(View.VISIBLE);
                    btnImprimirImagem.setVisibility(View.VISIBLE);
                    btnAbrirGaveta.setVisibility(View.VISIBLE);
                    btnAcionarGuilhotina.setVisibility(View.VISIBLE);
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    btnImprimirQrCode.setVisibility(View.VISIBLE);
                    btnLerPesoBalanca.setVisibility(View.VISIBLE);
                    break;
                case "L2Ks":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.L2Ks, getApplicationContext());
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    btnIniciarScanner.setVisibility(View.VISIBLE);
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    break;
                case "L2s":
                    goneButtons();
                    tectoy = new TecToy(Dispositivo.L2s, getApplicationContext());
                    btnIniciarNFC.setVisibility(View.VISIBLE);
                    btnIniciarScanner.setVisibility(View.VISIBLE);
                    btnEscreverNFC.setVisibility(View.VISIBLE);
                    break;
            }
        });

        btnStatusImpressora.setOnClickListener(v -> {
            try{
                Toast.makeText(getApplicationContext(),StatusImpressora.TextoStatus(tectoy.statusImpressora().obtemStatus()), Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnImprimir.setOnClickListener(v -> {
            try{
                this.startActivity(new Intent(this, ActivityImprimir.class));
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnStatusGaveta.setOnClickListener(v -> {
            try{
                boolean bStatusGaveta = tectoy.gavetaAberta();
                if(bStatusGaveta) {
                    Toast.makeText(getApplicationContext(), "GAVETA ABERTA", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "GAVETA FECHADA", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnImprimirImagem.setOnClickListener(v -> {
            try{
                this.startActivity(new Intent(this, ActivityImprimirImagem.class));
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnAbrirGaveta.setOnClickListener(v -> {
            try{
                tectoy.abrirGaveta();
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnAcionarGuilhotina.setOnClickListener(v -> {
            try{
                tectoy.acionarGuilhotina();
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnPosicionarEtiqueta.setOnClickListener(v -> {
            try{
                tectoy.posicionarEtiqueta();
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnImprimirQrCode.setOnClickListener(v -> {
            try{
                this.startActivity(new Intent(this, ActivityImprimirQrCode.class));
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnPosicionarFinalEtiqueta.setOnClickListener(v -> {
            try{
                tectoy.finalEtiqueta();
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnLimparDisplay.setOnClickListener(v -> {
            try{
                tectoy.limparDisplay();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnEscreverDisplay.setOnClickListener(v -> {
            try{
                this.startActivity(new Intent(this, ActivityEscreverDisplay.class));
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnQrCodeDisplay.setOnClickListener(v -> {
            try{
                this.startActivity(new Intent(this, ActivityQrCodeDisplay.class));
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnBmpDisplay.setOnClickListener(v -> {
            try{
                this.startActivity(new Intent(this, ActivityBMPDisplay.class));
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnEncerrarScanner.setOnClickListener(v -> {
            try{
                tectoy.pararScanner();
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnIniciarScanner.setOnClickListener(v -> {
            try{
                tectoy.iniciarScanner(scannerCallback);
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnIniciarNFC.setOnClickListener(v -> {
           String dispositivo = spinnerDispositivos.getSelectedItem().toString();
            if (dispositivo.equals("K2"))
            {
                try{
                    tectoy.iniciarNFC(getIntent(), nfcCallbackK2);
                    NFCIniciado = true;
                    Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
                }catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                try{
                    tectoy.iniciarNFC(getIntent(), nfcCallback);
                    NFCIniciado = true;
                    Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
                }catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLerPesoBalanca.setOnClickListener(v -> {
            try{
                tectoy.lerPeso(balancaCallback);
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnEscreverNFC.setOnClickListener(v -> {
            try{
                this.startActivity(new Intent(this, ActivityEscreverNFC.class));
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnIniciarCameraProfundidade.setOnClickListener(v -> {
            try{
                Thread thrCamProf;
                thrCamProf = new Thread(iniciarCamProfundidade);
                thrCamProf.start();
                thrCamProf.join();
                this.runOnUiThread(() -> Toast.makeText(MainActivity.this, "Câmera iniciada com sucesso", Toast.LENGTH_SHORT).show());

            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnDesligarLedIndicacao.setOnClickListener(view -> {
            try{
                tectoy.desligarLedStatus();
                Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnLigarLedIndicacao.setOnClickListener(view -> {
            try{
                this.startActivity(new Intent(this, ActivityLedIndicacao.class));
            }catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
            tectoy.onNewIntentNFC(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(this.NFCIniciado == true) {
            tectoy.onPauseNFC(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.NFCIniciado == true) {
            tectoy.onResumeNFC(this, pendingIntent);
        }
    }

    private Runnable iniciarCamProfundidade = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                try {
                    tectoy.iniciarCameraProfundidade(profundidadeCallback);
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(e.getMessage()).setNeutralButton("OK", null);
                    builder.show();
                }

            } catch (DarumaException de) {
                throw de;
            }
        }
    };

    private void goneButtons() {
        btnStatusImpressora.setVisibility(View.GONE);
        btnImprimir.setVisibility(View.GONE);
        btnStatusGaveta.setVisibility(View.GONE);
        btnImprimirImagem.setVisibility(View.GONE);
        btnAbrirGaveta.setVisibility(View.GONE);
        btnAcionarGuilhotina.setVisibility(View.GONE);
        btnPosicionarEtiqueta.setVisibility(View.GONE);
        btnImprimirQrCode.setVisibility(View.GONE);
        btnPosicionarFinalEtiqueta.setVisibility(View.GONE);
        btnLimparDisplay.setVisibility(View.GONE);
        btnEscreverDisplay.setVisibility(View.GONE);
        btnQrCodeDisplay.setVisibility(View.GONE);
        btnBmpDisplay.setVisibility(View.GONE);
        btnEncerrarScanner.setVisibility(View.GONE);
        btnIniciarScanner.setVisibility(View.GONE);
        btnIniciarNFC.setVisibility(View.GONE);
        btnLerPesoBalanca.setVisibility(View.GONE);
        btnEscreverNFC.setVisibility(View.GONE);
        btnIniciarCameraProfundidade.setVisibility(View.GONE);
        btnDesligarLedIndicacao.setVisibility(View.GONE);
        btnLigarLedIndicacao.setVisibility(View.GONE);
    }
}
package br.com.itfast.buscapreco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    private ImageView imgProd;
    private TextView txtNome, txtPreco, txtDesc, txtDtVal, txtCProd, txtUnid;
    private ToggleButton tgSom;
    private int flagSom=1;//som ligado
    public static VideoView video;
    private MediaPlayer m;
    private MediaController mediaController;
    private boolean videoOK = false;
    public static boolean videoPaused = false;
    // para manipular telas
    private ScreenManager screenManager = ScreenManager.getInstance();
    private tela2 tlDisplayCliente = null;
    private MainActivity mainActivity = null;
    public static boolean isVertical = false;

    private EditText txtCodBarra;
    String codLido, nProd, pProd, dtProd, dProd, unidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tgSom = findViewById(R.id.toggleButton);
        txtCodBarra = findViewById(R.id.txtCodBar);
        imgProd = findViewById(R.id.imgProd);
        txtNome = findViewById(R.id.txtNome);
        txtDesc = findViewById(R.id.txtDesc);
        txtPreco = findViewById(R.id.txtPreco);
        txtDtVal = findViewById(R.id.txtValidade);
        txtCProd = findViewById(R.id.txtCprod);
        txtUnid = findViewById(R.id.txtUND);
        video = findViewById(R.id.videoView);
        mediaController = new MediaController(getApplicationContext());
        video.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videoinst));
        mediaController.setAnchorView(video);
        video.setMediaController(mediaController);

//PAra manipular telas
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        isVertical = height > width;
        initData();


        txtCodBarra.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (false == hasFocus) {
                    ((InputMethodManager) MainActivity.this.getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(txtCodBarra.getWindowToken(), 0);
                }
            }
        });

        txtCodBarra.setOnKeyListener( new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    codLido = String.valueOf(txtCodBarra.getText());
                    Log.i("DEMO D2Mini", "Código Lido: "+codLido);
                    if (codLido.length() > 11) {
                        consultaPreço(codLido);
                    }
                    txtCodBarra.requestFocus();
                    myHandler.sendEmptyMessage(0);
                    return false;
                }

                return false;
            }
        });


        tgSom.setOnCheckedChangeListener((tgButton, isChecked) -> {
            if (isChecked) {
                    m.setVolume(1, 1);//ligando som
                flagSom=1;// som ligado

            } else {
                    m.setVolume(0, 0); // desligou
                flagSom=0;//som desligado
            }
        });

        ocultarProduto();
        myHandler.sendEmptyMessage(0);
        videoPlay();
        if(!tlDisplayCliente.isShow){
            tlDisplayCliente.show();
        }

    }

    //mostrando outro activity no display cliente.
    private void initData() {
        mainActivity = (MainActivity) this;
        screenManager.init(this);
        Display[] displays = screenManager.getDisplays();
        Log.e("DEMO D2MINI", "Displays identificados: " + displays.length);
        for (int i = 0; i < displays.length; i++) {
            Log.e("DEMO D2MINI", "Caracteristicas: " + displays[i]);
        }
        Display display = screenManager.getPresentationDisplays();
        if (display != null && !isVertical){
            tlDisplayCliente = new tela2(this, display);
        }
    }

    void consultaPreço(String codigo){
        videoStop();
        Log.i("DEMO D2MINI", "COD Barras: " + codigo);
            switch (codigo) {
                case "7891098038371": // camomila
                    nProd = "Chá de Camomila Leão Fuze Caixa 25g 25 Unidades";
                    pProd = "7,99";
                    dtProd ="14/04/2026";
                    unidade = "Caixa 25gr";
                    dProd = "Está nervoso? Então, para relaxar, não deixe de tomar o Chá de Camomila Fuze, da Leão. Esse produto consiste em uma caixa com 15 sachês, possui coloração amarelo claro, sabor e aroma delicados. A camomila diminui a hiperatividade, alivia o estresse, enjoos e cólicas menstruais, auxilia no tratamento da ansiedade, trata inflamações, principalmente úlceras no estômago, e remove as impurezas da pele.";
                    imgProd.setImageResource(R.drawable.img7891098038371);
                    mostrarProduto();
                    break;
                case "7891098001504": //erva doce
                    nProd = "CHÁ DE ERVA DOCE CHÁ LEÃO 16G 10 UNIDADES";
                    pProd = "6,99";
                    dtProd ="12/09/2027";
                    unidade = "Caixa 16gr";
                    dProd = "O Chá Leão Erva Doce é uma opção deliciosa e natural para quem busca uma bebida quente e saudável. Cada embalagem contém 10 sachês com 16g de erva doce selecionada e cuidadosamente embalada para preservar seu sabor e aroma. A erva doce é conhecida por suas propriedades calmantes, sendo uma excelente opção para relaxar após um dia agitado ou para ajudar a dormir melhor durante a noite. Além disso, ela também pode auxiliar na digestão e na redução de gases, sendo uma opção natural e eficaz para problemas gastrointestinais.";
                    imgProd.setImageResource(R.drawable.img7891098001504);
                    mostrarProduto();
                    break;
                case "7894900530001": // água mineral
                    nProd = "Água Mineral Natural Crystal Sem Gás com 500ml";
                    pProd = "1,79";
                    dtProd ="28/05/2025";
                    unidade = "Pet 500ml";
                    dProd = "A Água Mineral Crystal Sem Gás traz diversos benefícios para o corpo, além de ser pura e leve.";
                    imgProd.setImageResource(R.drawable.img7894900530001);
                    mostrarProduto();
                    break;
                case "7894900531008": // água com gás
                    nProd = "Água Mineral Crystal 500Ml Com Gás";
                    pProd = "1,99";
                    dtProd ="25/06/2025";
                    unidade = "Pet 500ml";
                    dProd = "A Água Mineral Crystal 500ml com Gás é obtida diretamente de fontes naturais com sais minerais, Crystal é a água mineral perfeita para refrescar seu corpo.";
                    imgProd.setImageResource(R.drawable.img7894900531008);
                    mostrarProduto();
                    break;
                default:
                    Toast.makeText(MainActivity.this, "PRODUTO NÃO ENCONTRADO/ CÓDIGO INVÁLIDO...", Toast.LENGTH_LONG).show();
                    videoPlay();
                    break;
            }
            txtCodBarra.setText("");
            txtCodBarra.requestFocus();
        }

    void mostrarProduto(){
        txtCProd.setText("Cód.Barras: "+codLido);
        txtNome.setText(""+ nProd);
        txtDesc.setText(""+dProd);
        txtPreco.setText("R$ "+pProd);
        txtDtVal.setText("Validade: "+ dtProd);
        txtUnid.setText(""+ unidade);

        imgProd.setVisibility(View.VISIBLE);
        txtCProd.setVisibility(View.VISIBLE);
        txtNome.setVisibility(View.VISIBLE);
        txtDesc.setVisibility(View.VISIBLE);
        txtPreco.setVisibility(View.VISIBLE);
        txtDtVal.setVisibility(View.VISIBLE);
        txtUnid.setVisibility(View.VISIBLE);

        Thread thrTemp;
        try {
            thrTemp = new Thread(tempo);
            thrTemp.start();

        } catch (Exception e) {
          Toast.makeText(MainActivity.this, "Ocorreu erro: [" + e.getMessage() + "  ]", Toast.LENGTH_SHORT).show();
        }
    }
    void ocultarProduto(){
        imgProd.setVisibility(View.INVISIBLE);
        txtCProd.setVisibility(View.INVISIBLE);
        txtNome.setVisibility(View.INVISIBLE);
        txtDesc.setVisibility(View.INVISIBLE);
        txtPreco.setVisibility(View.INVISIBLE);
        txtDtVal.setVisibility(View.INVISIBLE);
        txtUnid.setVisibility(View.INVISIBLE);
        myHandler.sendEmptyMessage(0);
    }
    final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.i("DEMO D2MINI", "handleMessage::recebendo msg " + msg.what);
            txtCodBarra.setText("");
            txtCodBarra.requestFocus(); //voltar o foco para o produto
        }
    };
    private Runnable tempo = new Runnable() {
        @Override
        public void run() {
            try{
                Thread.sleep(7000);
               runOnUiThread(() ->{ videoPlay();});
            }catch (Exception e){
                Log.e("DEMO D2Mini", "erro Runnable tempo "+ e.getMessage() );
            }
        }
    };

    void videoPlay(){
        video.setVisibility(View.VISIBLE);
        tgSom.setVisibility(View.VISIBLE);
        ocultarProduto();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                m = mediaPlayer;
                m.setLooping(true);
                if (m.isPlaying()==false){
                    m.start();
                }else{
                    m.pause();
                }
                if (flagSom==1){
                    m.setVolume(1,1);
                }else {
                    m.setVolume(0,0);
                }
                videoOK = true;
            }
        });
    }

    void videoStop(){
        try{
            if (m.isPlaying()){
                m.stop();
                video.setVisibility(View.INVISIBLE);
                tgSom.setVisibility(View.INVISIBLE);
            }
            }catch (Exception e){
            Log.e("DEMO D2MINI", "erro videoStop: "+ e.getMessage());
        }

    }



}
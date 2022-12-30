package br.com.itfast.example.balaca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.com.daruma.framework.mobile.exception.DarumaException;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;
import br.com.itfast.tectoy.TecToyException;

public class MainActivity extends AppCompatActivity {

    private TextView txtDataHora;
    private TextView txtMAC;
    private Button btnDataHora;
    private Button btnMAC;
    private Button btnCadProduto;
    private TecToy tectoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDataHora = (TextView) findViewById(R.id.txtDataHora);
        btnDataHora = (Button) findViewById(R.id.btnDataHora);
        txtMAC = (TextView) findViewById(R.id.txtMac);
        btnMAC = (Button) findViewById(R.id.btnMac);
        btnCadProduto = (Button) findViewById(R.id.btnCadProdPLU);

        tectoy = new TecToy(Dispositivo.D2_MINI, this);
        Thread thrIniciaBalanca;

        try {
            thrIniciaBalanca = new Thread(iniciarBalanca);
            thrIniciaBalanca.start();
            thrIniciaBalanca.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        btnDataHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Thread thrDataHora = new Thread(lerDataHora);
                    thrDataHora.start();
                    thrDataHora.join();
                }catch(TecToyException | InterruptedException tex){
                    Toast.makeText(MainActivity.this, tex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnMAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Thread thrMAC = new Thread(lerMAC);
                    thrMAC.start();
                    thrMAC.join();
                }catch(TecToyException | InterruptedException tex){
                    Toast.makeText(MainActivity.this, tex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCadProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Thread thrCad = new Thread(cadProduto);
                    thrCad.start();
                    thrCad.join();
                }catch(TecToyException | InterruptedException tex){
                    Toast.makeText(MainActivity.this, tex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Runnable lerDataHora = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                try {
                    String ret = tectoy.dataHoraBalanca();
                    txtDataHora.setText(ret);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } catch (DarumaException de) {
                throw de;
            }
        }
    };

    private Runnable lerMAC = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                try {
                    String ret = tectoy.macBalanca();
                    txtMAC.setText(ret);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } catch (DarumaException de) {
                throw de;
            }
        }
    };

    private Runnable cadProduto = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                try {
                     /*
                    Funcionamento do método:
                    strUni aceita os valores 2, 3 e 8. Onde: 2 = pçs, 3 = kg e 8 = 100g;
                    strValor deve ser informado sempre com o separado de decimal ",";
                     */
                    tectoy.cadProdutoBalanca("15", "Prod tectoy", "L01", "15", "", "3", "36,0");
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Produto cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (DarumaException de) {
                throw de;
            }
        }
    };

    private Runnable iniciarBalanca = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                try {
                    /*
                    Funcionamento do método:
                    strip -> Endereço IP em que a balança esta;
                    strPortaTCP -> Porta TCP client configurada na balança, o valor padrão que vem na balança é ""33581;
                     */
                    tectoy.iniciarBalanca("192.168.0.100", "33581");
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } catch (DarumaException de) {
                throw de;
            }
        }
    };
}
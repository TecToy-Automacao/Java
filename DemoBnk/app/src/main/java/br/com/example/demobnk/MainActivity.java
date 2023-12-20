package br.com.example.demobnk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;

public class MainActivity extends AppCompatActivity {
    private static final Object T2S=null;
    private TecToy t2s;
    Intent inttTela;
    Button btnImp, btnSaldo, btnExtrato, btnSair;
    String centro = "" + ((char) 0x1B) + ((char) 0x61) + ((char) 0x31);
    String deslCentro = "" + ((char) 0x1B) + ((char) 0x61) + ((char) 0x30);
    String extra = "" + ((char) 0x1B) + ((char) 0x21) + ((char) 0x30);
    String deslExtra = "" + ((char) 0x1B) + ((char) 0x21) + ((char) 0x00);
    String negrito = "" + ((char) 0x1B) + ((char) 0x45) + ((char) 0x31);
    String deslNegrito = "" + ((char) 0x1B) + ((char) 0x45) + ((char) 0x30);
    String titulo, buffer, strImp, strTela, dataHora;
    TextView txtTela;
    Date date;
    int iValor, flag=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t2s = new TecToy(Dispositivo.T2S, this);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        btnImp = findViewById(R.id.btnImprimi);
        btnExtrato = findViewById(R.id.btnExtrato);
        btnSaldo = findViewById(R.id.btnSaldo);
        btnSair = findViewById(R.id.btnExit);
        txtTela = findViewById(R.id.txtTela);

        txtTela.setMovementMethod(new ScrollingMovementMethod());
        btnSaldo.setEnabled(true);
        btnExtrato.setEnabled(true);
        btnImp.setEnabled(false);


        btnSair.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(inttTela == null){
                    inttTela = new Intent(MainActivity.this, MainActivity2.class);
                }
                txtTela.setText("Bem vindo(a) ao Banking,  clique na opção desejada...");
                startActivity(inttTela);
            }
        });
        btnSaldo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                titulo = null;
                titulo = "SALDO";
                date = new Date();
                dataHora = dateFormat.format(date);
                buffer = null;
                montaBufferSALDO();
                txtTela.setText(strTela);
                btnExtrato.setEnabled(true);
                btnImp.setEnabled(true);
                btnSaldo.setEnabled(false);

            }
        });

        btnExtrato.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                titulo = null;
                titulo = "EXTRATO";
                date = new Date();
                dataHora = dateFormat.format(date);
                buffer = null;
                montaBufferExtrato();
                txtTela.setText(strTela);
                btnSaldo.setEnabled(true);
                btnImp.setEnabled(true);
                btnExtrato.setEnabled(false);
            }
        });

        btnImp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thrImp;
                btnSaldo.setEnabled(true);
                btnImp.setEnabled(false);
                btnExtrato.setEnabled(true);
                try {
                    flag++;
                    thrImp = new Thread(imprBuffer);
                    thrImp.start();
                    thrImp.join();
                    txtTela.setText("Agradecemos sua presença!\n\nEscolha uma das opções para nova consulta, ou clique em 'SAIR'.");
                }catch (Exception e) {
                    mensagem("Impressão retornou erro:"+ e.getMessage());
                }
            };
        });

    }

    void mensagem(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    void montaBufferSALDO(){
        if (flag==0) flag=1;
        String strCab = "TECTOY AUTOMACAO - Terminal T2s\nSaiba mais sobre os equipamentos em\nhttps://tectoyautomacao.com.br/\n------------------------------------------\n\n";
        strImp = negrito + strCab+ deslNegrito;
        strTela = strCab;
            buffer= " ";
            if (flag==1){
                iValor=500;
            }
            else if (flag==2){
                iValor=900;
            }
            else if (flag==3){
                iValor=750;
                flag=0;
            }
            buffer = "\nSeu saldo em: " + dataHora + "\n";
            ////para tela
            strImp += extra + centro + ((char) 0x1D) + ((char) 0x42) + ((char) 0x31) + "* * * " + titulo + " * * *\n" + ((char) 0x1D) + ((char) 0x42) + ((char) 0x30) + deslCentro + deslExtra;
            strImp += buffer+extra + centro + "R$ "+iValor+",00\n\n" + deslCentro + deslExtra + "Sem lancamentos futuros.\n\n\n\n";
            //// para impressora
            strTela += "         * * * "+ titulo +" * * *\n";
            strTela +=  buffer +  "R$ "+iValor+",00\n\n" ;
            strTela += "Sem lançamentos futuros.";

    }

    void montaBufferExtrato(){
        if (flag==0) flag=1;
        String strCab = "TECTOY AUTOMACAO - Terminal T2s\nSaiba mais sobre os equipamentos em\nhttps://tectoyautomacao.com.br/\n------------------------------------------------\n\n";
        strImp = negrito + strCab+ deslNegrito;
        strTela = strCab;
        buffer = "";
            if (flag==1){
                iValor=500;
                buffer += "------------------------------------------------";
                buffer += "Ultimos 10 lancamentos:\n";
                buffer += "C. Debito                           -15,00\n";
                buffer += "C. Debito                           -13,00\n";
                buffer += "Deposito                           +150,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                buffer += "C. Debito                           -27,00\n";
                buffer += "PIX enviado                         -75,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                buffer += "C. Debito                           -27,00\n";
                buffer += "PIX recebido                       +100,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
            }
            else if (flag==2){
                iValor=900;
                buffer += "------------------------------------------------";
                buffer += "Ultimos 20 lancamentos:\n";
                buffer += "C. Debito                           -15,00\n";
                buffer += "C. Debito                           -13,00\n";
                buffer += "Deposito                           +150,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                buffer += "C. Debito                           -99,00\n";
                buffer += "PIX enviado                         -75,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                buffer += "C. Debito                           -27,00\n";
                buffer += "PIX recebido                       +100,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                buffer += "C. Debito                           -89,00\n";
                buffer += "PIX enviado                         -75,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                buffer += "C. Debito                           -27,00\n";
                buffer += "PIX recebido                       +100,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                buffer += "C. Debito                           -33,00\n";
                buffer += "PIX recebido                       +100,00\n";
                buffer += "PIX recebido                       +120,00\n";
            }
            else if (flag==3){
                iValor=750;
                buffer += "------------------------------------------------";
                buffer += "Ultimos 10 lancamentos:\n";
                buffer += "C. Debito                           -35,00\n";
                buffer += "C. Debito                           -18,00\n";
                buffer += "Deposito                           +150,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                buffer += "C. Debito                           -27,00\n";
                buffer += "PIX enviado                         -75,00\n";
                buffer += "C. Debito                           -33,00\n";
                buffer += "PIX recebido                       +100,00\n";
                buffer += "PIX recebido                       +120,00\n";
                buffer += "Saque Cx Eletr.                    -100,00\n";
                flag=0;
            }

            /// para impressora
            strImp += extra + centro + ((char) 0x1D) + ((char) 0x42) + ((char) 0x31) + "* * * " + titulo + " * * *\n" + ((char) 0x1D) + ((char) 0x42) + ((char) 0x30) + deslCentro + deslExtra;
            strImp += "\nData consulta: " + dataHora + "\n";
            strImp += centro + "Saldo atual\n" + deslCentro
                    + centro + negrito +"R$ "+iValor+",00\n\n" +deslNegrito +deslCentro;
            strImp += buffer +"Sem lancamentos futuros.\n\n\n\n";
            //// para tela
            strTela += "         * * * "+ titulo +" * * *\n";
            strTela += "\nData consulta: " + dataHora + "\n";
            strTela += "Saldo atual R$ "+iValor+",00\n\n";
            strTela +=  buffer;
            strTela += "Sem lançamentos futuros.";



    }

    private Runnable imprBuffer = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                try {
                    t2s.imprimir(strImp);
                    //Thread.sleep(150);
                    //t2s.imprimir("\n\n\n\n");
                    //Thread.sleep(100);
                    t2s.acionarGuilhotina();
                }catch (Exception e){
                    mensagem("Erro na impressao: "+ e.getMessage());
                }
            }catch (Exception de){
                throw de;
            }
        }
    };

}
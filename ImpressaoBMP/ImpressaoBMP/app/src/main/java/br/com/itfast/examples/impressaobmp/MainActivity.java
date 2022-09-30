package br.com.itfast.examples.impressaobmp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Button;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

import br.com.daruma.framework.mobile.DarumaMobile;

public class MainActivity extends AppCompatActivity {

    private Button btnImprimir;
    private DarumaMobile dmf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dmf = DarumaMobile.inicializar(MainActivity.this,
                "@FRAMEWORK(TRATAEXCECAO=TRUE;SATNATIVO=FALSE);@BLUETOOTH(ADDRESS=00:11:22:33:44:55;ATTEMPTS=100;TIMEOUT=10000)");

        btnImprimir  = findViewById(R.id.btnImprimir);
        btnImprimir.setOnClickListener(v ->{
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/bmp");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, 2);
            }catch(Exception ex){
                new AlertDialog.Builder(this)
                        .setTitle("Erro")
                        .setMessage("Erro ao selecionar arquivo. Erro: " + ex.getMessage())
                        .setPositiveButton(android.R.string.yes, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 2 || resultCode != RESULT_OK) {
            return;
        }
        dmf.iniciarComunicacao();
        String fullFilePath = UriUtils.getPathFromUri(this,data.getData());

        dmf.eImprimirBMPTecToy(fullFilePath); //METODO RESPONSAVEL POR FAZER IMPPRESSAO DO BITMAP, RECEBE COMO PARAMETRO STRING COM PATH DO ARQUIVO
        dmf.enviarComando(""+((char) 0x1B)+((char) 0x61)+((char) 0x01)+((char) 0x1B)+((char) 0x45)+((char) 0x01)+"PRODUTO TESTE\n"+((char) 0x1B)+((char) 0x45)+((char) 0x30)+"prod. teste cod. 112233443321\n"+((char) 0x1D)+((char) 0x6B)+((char) 0x02)+"123456789012"+((char) 0x00)+"\n\n\n");
        dmf.fecharComunicacao();
        new AlertDialog.Builder(this)
                        .setTitle("OK")
                        .setMessage("Impressao realizada")
                        .setPositiveButton(android.R.string.yes, null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
        } catch (Exception ex) {
            new AlertDialog.Builder(this)
                    .setTitle("Erro")
                    .setMessage("Erro ao enviar impressao. Erro: " + ex.getMessage())
                    .setPositiveButton(android.R.string.yes, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
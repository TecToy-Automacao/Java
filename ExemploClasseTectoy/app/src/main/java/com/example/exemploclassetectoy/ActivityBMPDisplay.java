package com.example.exemploclassetectoy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivityBMPDisplay extends AppCompatActivity {

    private final int CHOOSE_IMAGE_FROM_DEVICE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmpdisplay);

        Button btnEscolherImagemDisplay = findViewById(R.id.btnEscolherImagemDisplay);
        btnEscolherImagemDisplay.setOnClickListener(v -> callChoseFile());
    }

    private void callChoseFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_IMAGE_FROM_DEVICE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if(requestCode == CHOOSE_IMAGE_FROM_DEVICE && resultCode == RESULT_OK) {
            if(resultData != null) {
                try {
                    String fullFilePath = UriUtils.getPathFromUri(this, resultData.getData());
                    Bitmap bmp = BitmapFactory.decodeFile(fullFilePath);
                    MainActivity.tectoy.bmpDisplay(bmp);
                    Toast.makeText(getApplicationContext(), "Comando enviado com sucesso", Toast.LENGTH_SHORT).show();
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
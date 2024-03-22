package com.example.camapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            Toast.makeText(getApplicationContext(),"Camera localizada",Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // no camera on this device
            Toast.makeText(getApplicationContext(),"NÃO ACHEI A CAMERA",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            int numC = Camera.getNumberOfCameras();
            c = Camera.open();
            // Se equipamento for K2, virar 90graus a imagem
            if(getDeviceName().equals("K2")){
                c.setDisplayOrientation(90);
            }
        }
        catch (Exception e){
            Log.i("CAM", "Erro ao abrir camera: "+e.getMessage());
        }
        return c;
    }

    /** A basic Camera preview class */
    public static class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d("CAM", "Erro ao definir a visualização da câmera:: " + e.getMessage());
            }
        }
        public void surfaceDestroyed(SurfaceHolder holder) {
            // vazio. Cuide de liberar a visualização da câmera em sua atividade.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            if (mHolder.getSurface() == null){
                return;
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                Log.e("CAM", e.getMessage());
            }

            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d("CAM", "Erro ao iniciar a visualização da câmera:: " + e.getMessage());
            }
        }
    }

    private static final int STORAGE_PERMISSION_CODE = 101;
    private Camera mc;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permissão de armazenado concedida", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permissão de armazenado negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
    }
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
            Date date = new Date();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
            File arquivoImagem = new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera/", dateFormat.format(date)+"foto.png");
            try (FileOutputStream out = new FileOutputStream(arquivoImagem)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 75, out); //bmp é sua instância de bitmap
                // PNG é um formato sem perdas, o fator de compressão (100) é ignorado
                Toast.makeText(MainActivity.this, "Foto salva com sucesso",Toast.LENGTH_SHORT).show();
                mc.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }

            //use sua imagem
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        mc = getCameraInstance();
        Button btnTakePicture = (Button) findViewById(R.id.btnTakePicture);

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mc.takePicture(null, null, mPicture);

            }
        });
        CameraPreview mPreview = new CameraPreview(getApplicationContext(), mc);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
 
    public static String getDeviceName() {
        String model = Build.MODEL; //recebe o modelo do equipamento.
        return model;
    }

}
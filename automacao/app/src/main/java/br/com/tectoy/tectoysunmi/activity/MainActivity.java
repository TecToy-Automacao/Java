package br.com.tectoy.tectoysunmi.activity;

import static br.com.tectoy.tectoysunmi.R.drawable.test;
import static br.com.tectoy.tectoysunmi.R.drawable.test1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.threadhelp.ThreadPoolManageer;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.HintOneBtnDialog;

public class MainActivity extends AppCompatActivity {

    HintOneBtnDialog mHintOneBtnDialog;
    boolean run;
    public static boolean isK1 = false;
    public static boolean isVertical = false;


    private final DemoDetails[] demos = {
            new DemoDetails(R.string.function_all, R.drawable.function_all,
                    null),
            new DemoDetails(R.string.function_qrcode, R.drawable.function_qr,
                    QrActivity.class),
            new DemoDetails(R.string.function_barcode, R.drawable.function_barcode,
                    BarCodeActivity.class),
            new DemoDetails(R.string.function_text, R.drawable.function_text,
                    TextActivity.class),
            new DemoDetails(R.string.function_tab, R.drawable.function_tab,
                    TableActivity.class),
            new DemoDetails(R.string.function_pic, R.drawable.function_pic,
                    BitmapActivity.class),
            new DemoDetails(R.string.function_threeline, R.drawable.function_threeline,
                    null),
            new DemoDetails(R.string.function_cash, R.drawable.function_cash,
                    null),
            new DemoDetails(R.string.function_lcd, R.drawable.function_lcd,
                    LcdActivity.class),
            new DemoDetails(R.string.function_status, R.drawable.function_status,
                    null),
            new DemoDetails(R.string.function_blackline, R.drawable.function_blackline,
                    BlackLabelActivity.class),
            new DemoDetails(R.string.function_label, R.drawable.function_label,
                    LabelActivity.class),
            new DemoDetails(R.string.cut_paper, R.drawable.function_cortar, null),
            new DemoDetails(R.string.function_scanner, R.drawable.function_scanner, ScannerActivity.class),
            new DemoDetails(R.string.function_led, R.drawable.function_led, LedActivity.class),
            new DemoDetails(R.string.function_paygo, R.drawable.function_payment, MSitef.class)

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupRecyclerView();
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// Largura da tela
        int height = dm.heightPixels;// Largura da tela
        isVertical = height > width;
        isK1 = isHaveCamera() && isVertical;

    }
    public boolean isHaveCamera() {
        HashMap<String, UsbDevice> deviceHashMap = ((UsbManager) getSystemService(Activity.USB_SERVICE)).getDeviceList();
        for (Map.Entry entry : deviceHashMap.entrySet()) {
            UsbDevice usbDevice = (UsbDevice) entry.getValue();
            if (!TextUtils.isEmpty(usbDevice.getInterface(0).getName()) && usbDevice.getInterface(0).getName().contains("Orb")) {
                return true;
            }
            if (!TextUtils.isEmpty(usbDevice.getInterface(0).getName()) && usbDevice.getInterface(0).getName().contains("Astra")) {
                return true;
            }
        }
        return false;
    }
    private void setupRecyclerView() {
        final GridLayoutManager layoutManage = new GridLayoutManager(this, 2);
        RecyclerView mRecyclerView = findViewById(R.id.worklist);
        mRecyclerView.setLayoutManager(layoutManage);
        mRecyclerView.setAdapter(new WorkTogetherAdapter());
    }

    class WorkTogetherAdapter extends RecyclerView.Adapter<WorkTogetherAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.demoDetails = demos[position];
            holder.tv.setText(demos[position].titleId);
            holder.tv.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(demos[position].iconResID), null, null);
        }

        @Override
        public int getItemCount() {
            return demos.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;
            DemoDetails demoDetails;

            MyViewHolder(View v) {
                super(v);
                tv = v.findViewById(R.id.worktext);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(demoDetails == null){
                            return;
                        }
                        if(demoDetails.activityClass != null){
                            startActivity(new Intent(MainActivity.this, demoDetails.activityClass));
                        }
                        if(demoDetails.titleId == R.string.function_all){
                               // TesteCompleto();
if (isK1){
    System.out.println("true");
}else {
    System.out.println("false");
}

                        }
                        if(demoDetails.titleId == R.string.cut_paper){
                            TectoySunmiPrint.getInstance().cutpaper();
                        }
                        if(demoDetails.titleId == R.string.function_threeline){
                            TectoySunmiPrint.getInstance().print3Line();
                        }
                        if(demoDetails.titleId == R.string.function_cash){
                            TectoySunmiPrint.getInstance().openCashBox();
                        }
                        if(demoDetails.titleId == R.string.function_status){
                            TectoySunmiPrint.getInstance().showPrinterStatus(MainActivity.this);
                        }
                        if(demoDetails.titleId == R.string.function_multi){
                            if(mHintOneBtnDialog  == null){
                                mHintOneBtnDialog = DialogCreater.createHintOneBtnDialog(MainActivity.this, null, getResources().getString(R.string.multithread), getResources().getString(R.string.multithread_stop), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        run = false;
                                        mHintOneBtnDialog.cancel();
                                    }
                                });
                            }
                            mHintOneBtnDialog.show();
                            run = true;
                            multiPrint();
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.function, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void TesteCompleto() {


        TectoySunmiPrint.getInstance().initPrinter();
        TectoySunmiPrint.getInstance().setSize(24);

        // Alinhamento do texto

        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Alinhamento\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");

        // Formas de impressão
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Formas de Impressão\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");
        TectoySunmiPrint.getInstance().setSize(28);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printStyleBold(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleAntiWhite(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleDoubleHeight(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleDoubleWidth(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleInvert(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleItalic(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleStrikethRough(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().printStyleUnderLine(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n");
        TectoySunmiPrint.getInstance().printStyleReset();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printTextWithSize("TecToy Automação\n", 35);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printTextWithSize("TecToy Automação\n", 28);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printTextWithSize("TecToy Automação\n",50);
       // TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().setSize(24);


        // Impressão de BarCode
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Imprime BarCode\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
                TectoySunmiPrint.BarCodeTextPosition_INFORME_UM_TEXTO);
        TectoySunmiPrint.getInstance().printAdvanceLines(2);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
                TectoySunmiPrint.BarCodeTextPosition_ABAIXO_DO_CODIGO_DE_BARRAS);
        TectoySunmiPrint.getInstance().printAdvanceLines(2);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
                TectoySunmiPrint.BarCodeTextPosition_ACIMA_DO_CODIGO_DE_BARRAS_BARCODE);
        TectoySunmiPrint.getInstance().printAdvanceLines(2);
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
                TectoySunmiPrint.BarCodeTextPosition_ACIMA_E_ABAIXO_DO_CODIGO_DE_BARRAS);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().print3Line();
        // Impressão de BarCode
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Imprime QrCode\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 8, 1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 8, 1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 8, 1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printDoubleQRCode("www.tectoysunmi.com.br","tectoysunmi", 7, 1);
        //TectoySunmiPrint.getInstance().feedPaper();


        // Impresão Imagem
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Imprime Imagem\n");
        TectoySunmiPrint.getInstance().printText("-------------------------------\n");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 160;
        options.inDensity = 160;
        Bitmap bitmap1 = null;
        Bitmap bitmap = null;
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), test, options);
        }
        if (bitmap1 == null) {
            bitmap1 = BitmapFactory.decodeResource(getResources(), test1, options);
            bitmap1 = scaleImage(bitmap1);
        }
        TectoySunmiPrint.getInstance().printBitmap(bitmap1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().print3Line();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT);
        TectoySunmiPrint.getInstance().printBitmap(bitmap1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().print3Line();
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH);
        TectoySunmiPrint.getInstance().printBitmap(bitmap1);
        //TectoySunmiPrint.getInstance().feedPaper();
        TectoySunmiPrint.getInstance().print3Line();

        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printText("Imprime Tabela\n");
        TectoySunmiPrint.getInstance().printText("--------------------------------\n");

        String[] prod = new String[3];
        int[] width = new int[3];
        int[] align = new int[3];

        width[0] = 100;
        width[1] = 50;
        width[2] = 50;

        align[0] = TectoySunmiPrint.Alignment_LEFT;
        align[1] = TectoySunmiPrint.Alignment_CENTER;
        align[2] = TectoySunmiPrint.Alignment_RIGTH;

        prod[0] = "Produto 001";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 002";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 003";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 004";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 005";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        prod[0] = "Produto 006";
        prod[1] = "10 und";
        prod[2] = "3,98";
        TectoySunmiPrint.getInstance().printTable(prod, width, align);

        TectoySunmiPrint.getInstance().print3Line();
        TectoySunmiPrint.getInstance().openCashBox();
        TectoySunmiPrint.getInstance().cutpaper();

    }
    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 设置想要的大小
        int newWidth = (width / 8 + 1) * 8;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        // 得到新的图片
        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
    }



    private void multiPrint(){
        ThreadPoolManageer.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                while(run){
//                    TectoySunmiPrint.getInstance().sendRawData(BytesUtil.getBaiduTestBytes());
                    TesteCompleto();
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }


            }
        });

    }

    private class DemoDetails {
        @StringRes
        private final int titleId;
        @DrawableRes
        private final int iconResID;
        private final Class<? extends Activity> activityClass;

        private DemoDetails(@StringRes int titleId, @DrawableRes int descriptionId,
                            Class<? extends Activity> activityClass) {
            super();
            this.titleId = titleId;
            this.iconResID = descriptionId;
            this.activityClass = activityClass;
        }
    }
}
package br.com.tectoy.tectoysunmi.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sunmi.extprinterservice.ExtPrinterService;

import java.util.HashMap;
import java.util.Map;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil;
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.EditTextDialog;
import sunmi.sunmiui.dialog.ListDialog;


/**
 * Exemplo de impressão de um código QR
 *
 * @author Geovani Santos
 */
public class QrActivity extends BaseActivity {
    private ImageView mImageView;
    private TextView mTextView1, mTextView2, mTextView3, mTextView4, mTextView5, mTextView6;
    private int print_num = 0;
    private int print_size = 8;
    private int error_level = 3;
    public static boolean isK1 = false;
    public static boolean isVertical = false;
    private ExtPrinterService extPrinterService = null;
    public static KTectoySunmiPrinter kPrinterPresenter;
    int height= 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        setMyTitle(R.string.qr_title);
        setBack();
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// Largura da tela
        height = dm.heightPixels;// Largura da tela
        isVertical = height > width;
        isK1 = isHaveCamera() && isVertical;

        if (isK1 = true && height > 1856){
            connectKPrintService();
        }
        mImageView = findViewById(R.id.qr_image);
        mTextView1 = findViewById(R.id.qr_text_content);
        mTextView2 = findViewById(R.id.qr_text_num);
        mTextView3 = findViewById(R.id.qr_text_size);
        mTextView4 = findViewById(R.id.qr_text_el);
        mTextView5 = findViewById(R.id.qr_align_info);
        mTextView6 = findViewById(R.id.cut_paper_info);

        findViewById(R.id.qr_content).setOnClickListener(new View.OnClickListener() {
            EditTextDialog mDialog;

            @Override
            public void onClick(View v) {
                mDialog = DialogCreater.createEditTextDialog(QrActivity.this, getResources().getString(R.string.cancel), getResources().getString(R.string.confirm), getResources().getString(R.string.input_qrcode), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTextView1.setText(mDialog.getEditText().getText());
                        mDialog.cancel();
                    }
                }, null);
                mDialog.setHintText("www.tectoysunmi.com.br");
                mDialog.show();
            }
        });

        findViewById(R.id.qr_num).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] mStrings = new String[]{getResources().getString(R.string.single), getResources().getString(R.string.twice)};
                final ListDialog listDialog = DialogCreater.createListDialog(QrActivity.this, getResources().getString(R.string.array_qrcode), getResources().getString(R.string.cancel), mStrings);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        if (!BluetoothUtil.isBlueToothPrinter) {
                            Toast.makeText(QrActivity.this, R.string.toast_7, Toast.LENGTH_LONG).show();
                            position = 0;
                        } else {
                            mTextView3.setText("7");
                            print_size = 7;
                        }
                        mTextView2.setText(mStrings[position]);
                        print_num = position;
                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });

        findViewById(R.id.qr_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListDialog listDialog = DialogCreater.createListDialog(QrActivity.this, getResources().getString(R.string.size_qrcode), getResources().getString(R.string.cancel), new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        position += 1;
                        if (print_num == 1 && Integer.parseInt(mTextView3.getText().toString()) > 7) {
                            Toast.makeText(QrActivity.this, R.string.toast_8, Toast.LENGTH_LONG).show();
                            position = 7;
                        }
                        mTextView3.setText("" + position);
                        print_size = position;
                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });

        findViewById(R.id.qr_el).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] el = new String[]{"Correção L (7%)", "Correção M (15%)", "Correção Q (25%)", "Correção H (30%)"};
                final ListDialog listDialog = DialogCreater.createListDialog(QrActivity.this, getResources().getString(R.string.error_qrcode), getResources().getString(R.string.cancel), el);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        mTextView4.setText(el[position]);
                        error_level = position;
                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });
        findViewById(R.id.cut_paper_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] cut = new String[]{"Sim","Não"};
                final ListDialog listDialog = DialogCreater.createListDialog(QrActivity.this, getResources().getString(R.string.error_qrcode), getResources().getString(R.string.cancel), cut);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        mTextView6.setText(cut[position]);
                        error_level = position;
                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });

        findViewById(R.id.qr_align).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] pos = new String[]{getResources().getString(R.string.align_left), getResources().getString(R.string.align_mid), getResources().getString(R.string.align_right)};
                final ListDialog listDialog = DialogCreater.createListDialog(QrActivity.this, getResources().getString(R.string.align_form), getResources().getString(R.string.cancel), pos);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        mTextView5.setText(pos[position]);

                        listDialog.cancel();
                    }
                });
                listDialog.show();
            }
        });
    }

    public void onClick(View view) {
        Bitmap bitmap = BitmapUtil.generateBitmap(mTextView1.getText().toString(), 9, 700, 700);
        if (bitmap != null) {
            mImageView.setImageDrawable(new BitmapDrawable(bitmap));
        }
        if (mTextView6.getText().toString() == "Não") {
            if (isK1 = true && height > 1856){
                kPrinterPresenter.aling(1);
                kPrinterPresenter.text("QrCode\n");
                kPrinterPresenter.text("--------------------------------\n");
                kPrinterPresenter.qrCode(mTextView1.getText().toString(), print_size, error_level);
                kPrinterPresenter.printline(5);
                kPrinterPresenter.cut();
            }else {
                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                TectoySunmiPrint.getInstance().printText("QrCode\n");
                TectoySunmiPrint.getInstance().printText("--------------------------------\n");
                TectoySunmiPrint.getInstance().printQr(mTextView1.getText().toString(), print_size, error_level);
                TectoySunmiPrint.getInstance().print3Line();

            }
        }else {
            if (isK1 = true && height > 1856){
                kPrinterPresenter.aling(1);
                kPrinterPresenter.text("QrCode\n");
                kPrinterPresenter.text("--------------------------------\n");
                kPrinterPresenter.qrCode(mTextView1.getText().toString(), print_size, error_level);
                kPrinterPresenter.printline(5);
                kPrinterPresenter.cut();
            }else {
                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                TectoySunmiPrint.getInstance().printText("QrCode\n");
                TectoySunmiPrint.getInstance().printText("--------------------------------\n");
                TectoySunmiPrint.getInstance().printQr(mTextView1.getText().toString(), print_size, error_level);
                TectoySunmiPrint.getInstance().print3Line();
                TectoySunmiPrint.getInstance().cutpaper();
            }
        }
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
    private void connectKPrintService() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.extprinterservice");
        intent.setAction("com.sunmi.extprinterservice.PrinterService");
        bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            extPrinterService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            extPrinterService = ExtPrinterService.Stub.asInterface(service);
            kPrinterPresenter = new KTectoySunmiPrinter(QrActivity.this, extPrinterService);
        }
    };

}

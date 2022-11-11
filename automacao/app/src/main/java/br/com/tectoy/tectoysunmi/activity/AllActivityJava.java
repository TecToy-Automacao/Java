package br.com.tectoy.tectoysunmi.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;


import java.io.UnsupportedEncodingException;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil;
import br.com.tectoy.tectoysunmi.utils.BytesUtil;
import br.com.tectoy.tectoysunmi.utils.ESCUtil;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;

/**
 *
 * Imprimir todo o conteúdo por esc cmd
 *   @author Geovani Santos
 */
public class AllActivityJava extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        setMyTitle(R.string.all_title);
        setBack();

        findViewById(R.id.multi_one).setOnClickListener(this);
        findViewById(R.id.multi_two).setOnClickListener(this);
        findViewById(R.id.multi_three).setOnClickListener(this);
        findViewById(R.id.multi_four).setOnClickListener(this);
        findViewById(R.id.multi_five).setOnClickListener(this);
        findViewById(R.id.multi_six).setOnClickListener(this);
    }

    /**
     *  Imprimir todos os comandos ESC suportados pela impressora sunmi
     *  Para verificar se a impressora funciona bem
     */
    public void onPrint(View view) {
        byte[] rv = BytesUtil.customData();
        Bitmap head = BitmapFactory.decodeResource(getResources(), R.drawable.sunmi);
        //
        //Imprimir bitmap raster(print raster bitmap ——normal)
        rv = BytesUtil.byteMerger(rv, ESCUtil.printBitmap(head, 0));
        //
        //Imprimir bitmap raster  倍宽  (print raster bitmap ——double width)
        rv = BytesUtil.byteMerger(rv,ESCUtil.printBitmap(head, 1));
        //打印光栅位图  倍高    (print raster bitmap ——double height)
        rv = BytesUtil.byteMerger(rv,ESCUtil.printBitmap(head, 2));
        //打印光栅位图  倍宽倍高   (print raster bitmap ——double width and height)
        rv = BytesUtil.byteMerger(rv,ESCUtil.printBitmap(head, 3));
        //Selecione o comando de bitmap  8点单密度 （print Bitmap mode)
        rv = BytesUtil.byteMerger(rv,ESCUtil.selectBitmap(head, 0));
        //选择位图指令  8点双密度
        rv = BytesUtil.byteMerger(rv,ESCUtil.selectBitmap(head, 1));
        //选择位图指令  24点单密度
        rv = BytesUtil.byteMerger(rv,ESCUtil.selectBitmap(head, 32));
        //选择位图指令  24点双密度
        rv = BytesUtil.byteMerger(rv,ESCUtil.selectBitmap(head, 33));
        //之后将输出可显示的ascii码及制表符
        rv = BytesUtil.byteMerger(rv,BytesUtil.wordData());
        byte[] ascii = new byte[96];
        for(int i = 0; i < 95; i++){
            ascii[i] = (byte) (0x20+i);
        }

        ascii[95] = 0x0A;
        rv = BytesUtil.byteMerger(rv, ascii);
        char[] tabs = new char[116];
        for(int i = 0; i < 116; i++){
            tabs[i] = (char) (0x2500 + i);
        }
        String test = new String(tabs);
        try {
            rv = BytesUtil.byteMerger(rv, test.getBytes("gb18030"));
            rv = BytesUtil.byteMerger(rv, new byte[]{0x0A});
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(BluetoothUtil.isBlueToothPrinter){
            BluetoothUtil.sendData(rv);
        }else{
            TectoySunmiPrint.getInstance().sendRawData(rv);
        }
    }

    @Override
    public void onClick(View v) {
        byte[] rv = null;
        switch (v.getId()){
            case R.id.multi_one:
                rv = BytesUtil.getBaiduTestBytes();
                break;
            case R.id.multi_two:
                rv = BytesUtil.getMeituanBill();
                break;
            case R.id.multi_three:
                rv = BytesUtil.getErlmoData();
                break;
            case R.id.multi_four:
                rv = BytesUtil.getKoubeiData();
                break;
            case R.id.multi_five:
                rv = ESCUtil.printBitmap(BytesUtil.initBlackBlock(384));
                break;
            case R.id.multi_six:
                rv = ESCUtil.printBitmap(BytesUtil.initBlackBlock(800,384));
                break;
            default:
                break;
        }
        if(BluetoothUtil.isBlueToothPrinter){
            BluetoothUtil.sendData(rv);
        }else{
            TectoySunmiPrint.getInstance().sendRawData(rv);
        }
    }
}

package br.com.tectoy.tectoysunmi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.RemoteException;

import androidx.appcompat.app.AppCompatActivity;

import com.sunmi.extprinterservice.ExtPrinterService;
import com.sunmi.peripheral.printer.SunmiPrinterService;


public class KTectoySunmiPrinter extends AppCompatActivity {

    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;
    private Context context;
    private static final String TAG = "KPrinterPresenter";
    private ExtPrinterService mPrinter;
    String unic = "GBK";
    private static final byte ESC = 0x1B;
    // Alinhamento
    public static int Alignment_LEFT = 0;
    public static int Alignment_CENTER = 1;
    public static int Alignment_RIGTH = 2;
    // BarCode
    public static int BarCodeModels_UPC_A = 0;
    public static int BarCodeModels_UPC_E = 1;
    public static int BarCodeModels_EAN13 = 2;
    public static int BarCodeModels_EAN8 = 3;
    public static int BarCodeModels_CODE39 = 4;
    public static int BarCodeModels_ITF = 5;
    public static int BarCodeModels_CODABAR = 6;
    public static int BarCodeModels_CODE93 = 7;
    public static int BarCodeModels_CODE128 = 8;

    // Text Position
    public static int BarCodeTextPosition_NAO_IMPRIMIR = 0;
    public static int BarCodeTextPosition_ACIMA_DO_CODIGO_DE_BARRAS_BARCODE = 1;
    public static int BarCodeTextPosition_ABAIXO_DO_CODIGO_DE_BARRAS = 2;
    public static int BarCodeTextPosition_ACIMA_E_ABAIXO_DO_CODIGO_DE_BARRAS = 3;

    // Cutter’s paper cutting
    public static int FULL_CUTTING = 0;
    public static int HALF_CUTTING = 1;
    public static int CUTTING_PAPER_FEED = 2;


    public int sunmiPrinter = CheckSunmiPrinter;
    private SunmiPrinterService sunmiPrinterService;
    private ExtPrinterService extPrinterService = null;


    public KTectoySunmiPrinter(Context context, ExtPrinterService printerService) {
        this.context = context;
        this.mPrinter = printerService;
    }


    // Status
    public int getStatus() {

        int res = -1;
        try {
            res = mPrinter.getPrinterStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return res;

    }
    public void setSize(){
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // BarCode
    public void barcode(String texto, int tipo, int weight, int height, int hripos) {
        try {
            mPrinter.printBarCode(texto, tipo, weight, height, hripos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Alinhamento
    public void setAlign(int aling) {
        try {
            mPrinter.setAlignMode(aling);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void printBitmap(Bitmap bitmap, int mode) {

        try {
            mPrinter.printBitmap(bitmap, mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Texto
    public void text(String texto) {
        try {
            mPrinter.printText(texto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void printBarcode(String code, int type, int width, int height, int hriPos){
        try {
          //  mPrinter.printBarCode(code, type, width, height, hriPos);
            byte[] barcode = ESCUtil.getPrintBarCode(code, type, width, height, hriPos);
            mPrinter.sendRawData(barcode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // Avança Linha
    public void print3Line() {
        try {
            mPrinter.lineWrap(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cortar Papel
    public void cutpaper(int cutter_mode, int advance_lines) {
        try {
            mPrinter.cutPaper(cutter_mode, advance_lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Negrito
    public void printStyleBold(boolean boo) {
        try {
            if (boo) {
                mPrinter.sendRawData(boldOn());
            } else {
                mPrinter.sendRawData(boldOff());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // QrCode
    public void printQr(String texto, int size, int error) {
        try {
            mPrinter.printQrCode(texto, size, error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void printDoubleQRCode(String texto1, String texto2, int modulesize, int errorlevel) {
        try {
            byte[] qrCode = ESCUtil.getPrintDoubleQRCode(texto1, texto2, modulesize, errorlevel);
            mPrinter.sendRawData(qrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void printTable(String[] colsTextArr, int[] colsWidthArr, int[] colsAlign){
        try {
            mPrinter.printColumnsText(colsTextArr, colsWidthArr, colsAlign);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String traduzStatusImpressora(int status) {

        String result = "Interface é muito baixa para implementar ";

        switch (status) {
            case 0:
                result = "Impressora está funcionando";
                break;
            case 1:
                result = "A impressora está com a tampa aberta";
                break;
            case 2:
                result = "Impressora sem papel";
                break;
            case 3:
                result = "A impressora vai ficar sem papel";
                break;
            case 4:
                result = "Impressora está superaquecendo";
                break;
            default:
                result = "A impressora está offline ou o serviço de impressão não foi conectado ao impressora";
                break;
        }

        return result;
    }

    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();

        int newWidth = (width / 8 + 1) * 8;
        float scaleWidth = ((float) newWidth) / width;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);

        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
    }


    private byte[] boldOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0xF;
        return result;
    }

    private byte[] boldOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0;
        return result;
    }


}

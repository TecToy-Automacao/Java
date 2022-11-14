package br.com.tectoy.tectoysunmi.activity

import br.com.tectoy.tectoysunmi.R.drawable.test
import br.com.tectoy.tectoysunmi.R.drawable.test1
import android.app.Activity
import android.app.Presentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.MediaRouter
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.os.RemoteException
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.Display
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunmi.extprinterservice.ExtPrinterService
import java.util.HashMap
import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.activity.ExemploNFCIdRW.NfcExemplo
import br.com.tectoy.tectoysunmi.threadhelp.ThreadPoolManageer
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint
import sunmi.sunmiui.dialog.DialogCreater
import sunmi.sunmiui.dialog.HintOneBtnDialog

class MainActivity : AppCompatActivity(){
    var height = 0
    lateinit var mHintOneBtnDialog:HintOneBtnDialog
    var run:Boolean = false
    var isK1 = false
    var isVertical = false
    private var extPrinterService:ExtPrinterService? = null
    lateinit var kPrinterPresenter:KTectoySunmiPrinter

    private val demos = arrayOf(DemoDetails(R.string.function_all,R.drawable.function_all,null),
                                DemoDetails(R.string.function_qrcode, R.drawable.function_qr, null),
                                DemoDetails(R.string.function_barcode, R.drawable.function_barcode, null),
                                DemoDetails(R.string.function_text, R.drawable.function_text, null),
                                DemoDetails(R.string.function_tab, R.drawable.function_tab, null),
                                DemoDetails(R.string.function_pic, R.drawable.function_pic, null),
                                DemoDetails(R.string.function_threeline, R.drawable.function_threeline, null),
                                DemoDetails(R.string.function_cash, R.drawable.function_cash, null),
                                DemoDetails(R.string.function_lcd, R.drawable.function_lcd, null),
                                DemoDetails(R.string.function_status, R.drawable.function_status, null),
                                DemoDetails(R.string.function_blackline, R.drawable.function_blackline, null),
                                DemoDetails(R.string.function_label, R.drawable.function_label, null),
                                DemoDetails(R.string.cut_paper, R.drawable.function_cortar, null),
                                DemoDetails(R.string.function_scanner, R.drawable.function_scanner, null),
                                DemoDetails(R.string.function_led, R.drawable.function_led, null),
                                DemoDetails(R.string.function_paygo, R.drawable.function_payment, Paygo::class.java),
                                DemoDetails(R.string.function_scan, R.drawable.function_scanner, null),
                                DemoDetails(R.string.function_nfc, R.drawable.function_nfc, NfcExemplo::class.java),
                                DemoDetails(R.string.function_m_Sitef, R.drawable.function_payment, MSitef::class.java),
                                DemoDetails(R.string.display, R.drawable.telas, DisplayActivity::class.java)
    )

    private fun scaleImage(bitmap : Bitmap) : Bitmap{
        val width = bitmap.width
        val height = bitmap.height
        // 设置想要的大小
        val newWidth = (width / 8 + 1) * 8
        // 计算缩放比例
        val scaleWidth = newWidth.toFloat() / width
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, 1f)
        // 得到新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    fun KTesteCompleto (){

        // Alinhamento
        kPrinterPresenter.printStyleBold(false)
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER)
        kPrinterPresenter.text("Alinhamento\n")
        kPrinterPresenter.text("--------------------------------\n")
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT)
        kPrinterPresenter.text("TecToy Automação\n")
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER)
        kPrinterPresenter.text("TecToy Automação\n")
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_RIGTH)
        kPrinterPresenter.text("TecToy Automação\n")
        kPrinterPresenter.print3Line()

        // Formas de impressão

        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER)
        kPrinterPresenter.text("Formas de Impressão\n")
        kPrinterPresenter.text("--------------------------------\n")
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT)
        kPrinterPresenter.printStyleBold(true)
        kPrinterPresenter.text("TecToy Automação\n")


        // Barcode

        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER)
        kPrinterPresenter.text("BarCode\n")
        kPrinterPresenter.text("--------------------------------\n")
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT)
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 0)
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER)
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 2)
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_RIGTH)
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 1)
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER)
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 3)
        kPrinterPresenter.print3Line()
        // QrCode

        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER)
        kPrinterPresenter.text("QrCode\n")
        kPrinterPresenter.text("--------------------------------\n")
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER)
        kPrinterPresenter.printQr("www.tectoyautomacao.com.br", 8, 0)
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT)
        kPrinterPresenter.printQr("www.tectoyautomacao.com.br", 8, 0)
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_RIGTH)
        kPrinterPresenter.printQr("www.tectoyautomacao.com.br", 8, 0);
        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_LEFT)
        kPrinterPresenter.printDoubleQRCode("www.tectoyautomacao.com.br","tectoy", 7, 1)0
        // Imagem


        kPrinterPresenter.setAlign(kPrinterPresenter.Alignment_CENTER)
        kPrinterPresenter.text("Imagem\n")
        kPrinterPresenter.text("--------------------------------\n")
    }

    // Teste Completo dos Demais Devices
    fun TesteCompleto(){
        TectoySunmiPrint.getInstance().initPrinter()
        TectoySunmiPrint.getInstance().setSize(24)

    // Alinhamento do texto
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printText("Alinhamento\n")
        TectoySunmiPrint.getInstance().printText("--------------------------------\n")
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")

    // Formas de impressão
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printText("Formas de Impressão\n")
        TectoySunmiPrint.getInstance().printText("--------------------------------\n")
        TectoySunmiPrint.getInstance().setSize(28)
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT)
        TectoySunmiPrint.getInstance().printStyleBold(true)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().printStyleReset()
        TectoySunmiPrint.getInstance().printStyleAntiWhite(true)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().printStyleReset()
        TectoySunmiPrint.getInstance().printStyleDoubleHeight(true)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().printStyleReset()
        TectoySunmiPrint.getInstance().printStyleDoubleWidth(true)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().printStyleReset()
        TectoySunmiPrint.getInstance().printStyleInvert(true)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().printStyleReset()
        TectoySunmiPrint.getInstance().printStyleItalic(true);
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().printStyleReset()
        TectoySunmiPrint.getInstance().printStyleStrikethRough(true)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().printStyleReset()
        TectoySunmiPrint.getInstance().printStyleUnderLine(true)
        TectoySunmiPrint.getInstance().printText("TecToy Automação\n")
        TectoySunmiPrint.getInstance().printStyleReset()
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT)
        TectoySunmiPrint.getInstance().printTextWithSize("TecToy Automação\n", 35F)
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printTextWithSize("TecToy Automação\n", 28F)
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH)
        TectoySunmiPrint.getInstance().printTextWithSize("TecToy Automação\n", 50F)
        // TectoySunmiPrint.getInstance().feedPaper()
        TectoySunmiPrint.getInstance().setSize(24)

        // Impressão de BarCode
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printText("Imprime BarCode\n")
        TectoySunmiPrint.getInstance().printText("--------------------------------\n")
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT)
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
            TectoySunmiPrint.BarCodeTextPosition_INFORME_UM_TEXTO)
        TectoySunmiPrint.getInstance().printAdvanceLines(2)
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
            TectoySunmiPrint.BarCodeTextPosition_ABAIXO_DO_CODIGO_DE_BARRAS)
        TectoySunmiPrint.getInstance().printAdvanceLines(2)
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH)
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
            TectoySunmiPrint.BarCodeTextPosition_ACIMA_DO_CODIGO_DE_BARRAS_BARCODE)
        TectoySunmiPrint.getInstance().printAdvanceLines(2)
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printBarCode("7894900700046", TectoySunmiPrint.BarCodeModels_EAN13, 162, 2,
            TectoySunmiPrint.BarCodeTextPosition_ACIMA_E_ABAIXO_DO_CODIGO_DE_BARRAS)
        //TectoySunmiPrint.getInstance().feedPaper()
        TectoySunmiPrint.getInstance().print3Line()

        // Impressão de BarCode
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printText("Imprime QrCode\n")
        TectoySunmiPrint.getInstance().printText("--------------------------------\n")
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT)
        TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 8, 1)
        //TectoySunmiPrint.getInstance().feedPaper()
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
        TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 8, 1)
        //TectoySunmiPrint.getInstance().feedPaper()
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH)
        TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 8, 1)
        //TectoySunmiPrint.getInstance().feedPaper()
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT)
        TectoySunmiPrint.getInstance().printDoubleQRCode("www.tectoysunmi.com.br","tectoysunmi", 7, 1)
        //TectoySunmiPrint.getInstance().feedPaper()

        // Impresão Imagem
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printText("Imprime Imagem\n")
        TectoySunmiPrint.getInstance().printText("-------------------------------\n")
        val options:BitmapFactory.Options =BitmapFactory.Options();
        160.also { options.inTargetDensity = it }
        160.also { options.inDensity = it }
        var bitmap1: Bitmap? = null
        var bitmap: Bitmap? = null

        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), test, options)
        }
        if (bitmap1 == null) {
            bitmap1 = BitmapFactory.decodeResource(getResources(), test1, options)
            bitmap1 = scaleImage(bitmap1)
        }

        TectoySunmiPrint.getInstance().printBitmap(bitmap1)
        //TectoySunmiPrint.getInstance().feedPaper()
        TectoySunmiPrint.getInstance().print3Line()
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_LEFT)
        TectoySunmiPrint.getInstance().printBitmap(bitmap1)
        //TectoySunmiPrint.getInstance().feedPaper()
        TectoySunmiPrint.getInstance().print3Line()
        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_RIGTH)
        TectoySunmiPrint.getInstance().printBitmap(bitmap1)
        //TectoySunmiPrint.getInstance().feedPaper()
        TectoySunmiPrint.getInstance().print3Line()

        TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
        TectoySunmiPrint.getInstance().printText("Imprime Tabela\n")
        TectoySunmiPrint.getInstance().printText("--------------------------------\n")
        val prod=Array<String>(3){""}
        val width=IntArray(3)
        val align=IntArray(3)

        width[0] = 100
        width[1] = 50
        width[2] = 50

        align[0] = TectoySunmiPrint.Alignment_LEFT
        align[1] = TectoySunmiPrint.Alignment_CENTER
        align[2] = TectoySunmiPrint.Alignment_RIGTH

        prod[0] = "Produto 001"
        prod[1] = "10 und"
        prod[2] = "3,98"
        TectoySunmiPrint.getInstance().printTable(prod, width, align)

        prod[0] = "Produto 002"
        prod[1] = "10 und"
        prod[2] = "3,98"
        TectoySunmiPrint.getInstance().printTable(prod, width, align)

        prod[0] = "Produto 003"
        prod[1] = "10 und"
        prod[2] = "3,98"
        TectoySunmiPrint.getInstance().printTable(prod, width, align)

        prod[0] = "Produto 004"
        prod[1] = "10 und"
        prod[2] = "3,98"
        TectoySunmiPrint.getInstance().printTable(prod, width, align)

        prod[0] = "Produto 005"
        prod[1] = "10 und"
        prod[2] = "3,98"
        TectoySunmiPrint.getInstance().printTable(prod, width, align)

        prod[0] = "Produto 006"
        prod[1] = "10 und"
        prod[2] = "3,98"
        TectoySunmiPrint.getInstance().printTable(prod, width, align)

        TectoySunmiPrint.getInstance().print3Line()
        TectoySunmiPrint.getInstance().openCashBox()
        TectoySunmiPrint.getInstance().cutpaper()
    }



    private fun multiPrint() {
        ThreadPoolManageer.getInstance().executeTask {
            while(run){
//                    TectoySunmiPrint.getInstance().sendRawData(BytesUtil.getBaiduTestBytes())
                TesteCompleto();
                try {
                    Thread.sleep(4000);
                } catch (_:InterruptedException) {
                }
            }
        }
    }



    private class DemoDetails{
        @StringRes   private val titleId:Int
        @DrawableRes private val iconResID:Int
        private val activityClass:Activity?
        constructor(@StringRes titleId:Int,
                    @DrawableRes descriptionId:Int,
                    activityClass:Activity?){
            this.titleId       = titleId
            this.iconResID     = descriptionId
            this.activityClass = activityClass
        }
    }

}
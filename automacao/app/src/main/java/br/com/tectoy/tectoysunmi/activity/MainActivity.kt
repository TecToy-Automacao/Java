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
import android.os.*
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
import br.com.tectoy.tectoysunmi.databinding.ActivityMainBinding
import br.com.tectoy.tectoysunmi.threadhelp.ThreadPoolManageer
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint
import sunmi.sunmiui.dialog.DialogCreater
import sunmi.sunmiui.dialog.HintOneBtnDialog

open class MainActivity : AppCompatActivity(){
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
    private var videoDisplay:VideoDisplay? = null
    private var screenManager:ScreenManager = ScreenManager.getInstance()

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        val dm = DisplayMetrics()
        window.windowManager.defaultDisplay.getMetrics(dm)
        val width:Int = dm.widthPixels   //Largura da Tela
        height = dm.heightPixels  //Altura da tela
        isVertical =  height > width
        isK1 = isHaveCamera() && isVertical
        var deviceName:String = getDeviceName()
        if(isK1 && (height > 1856)){
            connectKPrintService()
        }
        val mediaRouter:MediaRouter = this.getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
        val route:MediaRouter.RouteInfo? = mediaRouter.getSelectedRoute(1)

        if (route!=null){
            val presentationDisplay:Display? = route.presentationDisplay
            if(presentationDisplay != null){
                val presentation:Presentation = VideoDisplay(this, presentationDisplay, Environment.getExternalStorageDirectory().path+"/video_01.mp4")
                presentation.show()
            }
        }
    }
    // Conexão Impressão K2
    private fun connectKPrintService() {
        val intent:Intent = Intent()
        intent.`package` = "com.sunmi.extprinterservice"
        intent.action = "com.sunmi.extprinterservice.PrinterService"
        bindService(intent,connService, Context.BIND_AUTO_CREATE)
    }
    private var connService:ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            extPrinterService = ExtPrinterService.Stub.asInterface(service)
            kPrinterPresenter = KTectoySunmiPrinter(this@MainActivity, extPrinterService)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            extPrinterService = null
        }
    }

    private fun getDeviceName():String{
        val manufacturer:String = Build.MANUFACTURER
        val model:String = Build.MODEL
        if (model.startsWith(manufacturer)){
            return capitalize(model)
        }
        return "${capitalize(manufacturer)} $model"
    }

    private fun capitalize(str:String):String{
        if (TextUtils.isEmpty(str)) {
            return str
        }
        var arr:CharArray = str.toCharArray()
        var capitalizeNext:Boolean = true
        var phrase:StringBuilder = StringBuilder()
        for(c:Char in arr){
            if (capitalizeNext && Character.isLetter(c)){
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if(Character.isWhitespace(c)){
                capitalizeNext = true
            }
            phrase.toString()
        }
        return phrase.toString()
    }

    private fun isHaveCamera() : Boolean{
        val deviceHashMap : HashMap<String, UsbDevice> = (getSystemService(Activity.USB_SERVICE) as UsbManager).deviceList
        for(entry in deviceHashMap.entries){
            var usbDevice = entry.value
            if(!TextUtils.isEmpty(usbDevice.deviceName) && usbDevice.deviceName == "Orb"){
                return true
            }
            if(!TextUtils.isEmpty(usbDevice.deviceName) && usbDevice.deviceName == "Astra"){
                return true
            }
        }
        return false
    }

    private fun setupRecyclerView() {
        val layoutManage:GridLayoutManager = GridLayoutManager(this,2)
        var mRecyclerView:RecyclerView = binding.worklist
        mRecyclerView.layoutManager = layoutManage
        mRecyclerView.adapter= WorkTogetherAdapter()
    }

    inner class WorkTogetherAdapter: RecyclerView.Adapter<WorkTogetherAdapter.MyViewHolder>() {
        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            lateinit var tv:TextView
            lateinit var demoDetails:DemoDetails
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v:View = LayoutInflater.from(parent.context).inflate(R.layout.work_item, parent,false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.demoDetails = demos[position]
            holder.tv.text = demos[position].titleId.toString()
            holder.tv.setCompoundDrawablesWithIntrinsicBounds(null,getDrawable(demos[position].iconResID),null,null)
        }

        override fun getItemCount(): Int {
            return demos.size
        }
    }

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
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.text("Alinhamento\n")
        kPrinterPresenter.text("--------------------------------\n")
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_LEFT)
        kPrinterPresenter.text("TecToy Automação\n")
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.text("TecToy Automação\n")
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_RIGTH)
        kPrinterPresenter.text("TecToy Automação\n")
        kPrinterPresenter.print3Line()

        // Formas de impressão

        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.text("Formas de Impressão\n")
        kPrinterPresenter.text("--------------------------------\n")
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_LEFT)
        kPrinterPresenter.printStyleBold(true)
        kPrinterPresenter.text("TecToy Automação\n")


        // Barcode

        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.text("BarCode\n")
        kPrinterPresenter.text("--------------------------------\n")
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_LEFT)
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 0)
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 2)
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_RIGTH)
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 1)
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.printBarcode("7891098010575", 2, 162, 2, 3)
        kPrinterPresenter.print3Line()
        // QrCode

        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.text("QrCode\n")
        kPrinterPresenter.text("--------------------------------\n")
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.printQr("www.tectoyautomacao.com.br", 8, 0)
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_LEFT)
        kPrinterPresenter.printQr("www.tectoyautomacao.com.br", 8, 0)
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_RIGTH)
        kPrinterPresenter.printQr("www.tectoyautomacao.com.br", 8, 0);
        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_LEFT)
        kPrinterPresenter.printDoubleQRCode("www.tectoyautomacao.com.br","tectoy", 7, 1)
        // Imagem


        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.text("Imagem\n")
        kPrinterPresenter.text("--------------------------------\n")
        val options:BitmapFactory.Options =BitmapFactory.Options();
        160.also { options.inTargetDensity = it }
        160.also { options.inDensity = it }
        var bitmap1: Bitmap? = null
        if (bitmap1 == null) {
            bitmap1 = BitmapFactory.decodeResource(getResources(), test1, options)
            bitmap1 = scaleImage(bitmap1);
        }
        kPrinterPresenter.printBitmap(bitmap1,  0)
        kPrinterPresenter.setAlign(0);
        kPrinterPresenter.printBitmap(bitmap1,  0)
        kPrinterPresenter.setAlign(2);
        kPrinterPresenter.printBitmap(bitmap1,  0)

        // Tabelas

        kPrinterPresenter.setAlign(KTectoySunmiPrinter.Alignment_CENTER)
        kPrinterPresenter.text("Tabelas\n")
        kPrinterPresenter.text("--------------------------------\n")

        val prod=Array<String>(3){""}
        val width=IntArray(3)
        val align=IntArray(3)

        width[0] = 100
        width[1] = 50
        width[2] = 50

        align[0] = KTectoySunmiPrinter.Alignment_LEFT
        align[1] = KTectoySunmiPrinter.Alignment_CENTER
        align[2] = KTectoySunmiPrinter.Alignment_RIGTH

        prod[0] = "Produto 001"
        prod[1] = "10 und"
        prod[2] = "3,98"
        kPrinterPresenter.printTable(prod, width, align)

        prod[0] = "Produto 002"
        prod[1] = "10 und"
        prod[2] = "3,98"
        kPrinterPresenter.printTable(prod, width, align)


        prod[0] = "Produto 003"
        prod[1] = "10 und"
        prod[2] = "3,98"
        kPrinterPresenter.printTable(prod, width, align)


        prod[0] = "Produto 004"
        prod[1] = "10 und"
        prod[2] = "3,98"
        kPrinterPresenter.printTable(prod, width, align)


        prod[0] = "Produto 005"
        prod[1] = "10 und"
        prod[2] = "3,98"
        kPrinterPresenter.printTable(prod, width, align)

        prod[0] = "Produto 006"
        prod[1] = "10 und"
        prod[2] = "3,98"
        kPrinterPresenter.printTable(prod, width, align)


        kPrinterPresenter.print3Line();
        kPrinterPresenter.cutpaper(KTectoySunmiPrinter.HALF_CUTTING, 10)
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
    //https://medium.com/android-dev-br/generics-e-variance-em-kotlin-in-out-t-ca5ca07c9fc5
    class DemoDetails(
        @StringRes   val titleId: Int,
        @DrawableRes val descriptionId: Int,
        val activityClass: Class<out Activity>?
    ) {
        @DrawableRes val iconResID:Int = descriptionId
    }

}


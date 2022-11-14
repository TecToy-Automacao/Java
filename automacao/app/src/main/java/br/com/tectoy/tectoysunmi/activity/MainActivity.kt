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
            holder.tv.text = demos[position].titleId // dúvida sobre tipo e referência
        }

        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }
    }
    //https://medium.com/android-dev-br/generics-e-variance-em-kotlin-in-out-t-ca5ca07c9fc5
    class DemoDetails(
        @StringRes   val titleId: Int,
        @DrawableRes val descriptionId: Int,
        val activityClass: Class<out Activity>?
    ) {
        @DrawableRes val iconResID:Int = descriptionId
        var empty = Array<String>(3) { "it = $it" }
    }

}
package br.com.tectoy.tectoysunmi.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog

import com.sunmi.extprinterservice.ExtPrinterService

import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityBarcodeBinding
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint
import sunmi.sunmiui.dialog.DialogCreater
import sunmi.sunmiui.dialog.EditTextDialog
import java.util.*

/**
 * Exemplo de impressão de um código de barras
 *
 * código em java
 * @author Geovani Santos
 *
 * código em kotlin
 * @author Felipe Peres
 * @author Paulo Cativo
 */

class BarCodeActivity : BaseActivity()  {
    private var encode : Int = 8
    private var position : Int = 0
    private var height : Int = 0
    private var width : Int = 0
    private var isK1 : Boolean = false
    private var isVertical : Boolean = false
    private var extPrinterService : ExtPrinterService? = null
    lateinit var kPrinterPresenter : KTectoySunmiPrinter

    private lateinit var binding : ActivityBarcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle(R.string.barcode_title)
        setBack()
        val dm = baseContext.resources.displayMetrics
        width = dm.widthPixels//largura da tela em pixels
        height = dm.heightPixels //altura da tela em pixels
        isVertical = height > width
        isK1 = isHaveCamera() && isVertical
        if(isK1 && height > 1856){
            connectKPrintService()
        }
        //Barcode
        binding.bcContent.setOnClickListener {
            lateinit var mDialog: EditTextDialog
            mDialog = DialogCreater.createEditTextDialog(this@BarCodeActivity,
                resources.getString(R.string.cancel),
                resources.getString(R.string.confirm),
                resources.getString(R.string.input_barcode),
                {
                    mDialog.cancel()
                },
                {
                    binding.bcTextContent.text = mDialog.editText.text
                    mDialog.cancel()
                },
                null)
            mDialog.show()
        }
        //Modelos de barcorde
        binding.bcEncode.setOnClickListener {
            val list = arrayOf("UPC-A", "UPC-E", "EAN13", "EAN8", "CODE39", "ITF", "CODABAR", "CODE93", "CODE128A", "CODE128B", "CODE128C")
            val listDialog = DialogCreater.createListDialog(this@BarCodeActivity,
                resources.getString(R.string.encode_barcode),
                resources.getString(R.string.cancel),
                list)
            listDialog.setItemClickListener {
                binding.bcTextEncode.text = list[it]
                encode = it
                print("posição selecionada: $it")
                listDialog.cancel()
            }
            listDialog.show()
        }
        //HRI posição
        binding.bcPosition.setOnClickListener{
            val list = arrayOf(resources.getString(R.string.no_print), resources.getString(R.string.barcode_up), resources.getString(R.string.barcode_down), resources.getString(R.string.barcode_updown))
            val listDialog = DialogCreater.createListDialog(this@BarCodeActivity,
                resources.getString(R.string.text_position),
                resources.getString(R.string.cancel),
                list)
            listDialog.setItemClickListener {
                binding.bcTextPosition.text = list[it]
                position = it
                listDialog.cancel()
            }
            listDialog.show()
        }
        //Altura
        binding.bcHeight.setOnClickListener{
            showSeekBarDialog(this@BarCodeActivity,
                1,
                resources.getString(R.string.barcode_height),
                255,
                binding.bcTextHeight)
        }
        //Largura
        binding.bcWidth.setOnClickListener {
            showSeekBarDialog(this@BarCodeActivity,
                1,
                resources.getString(R.string.barcode_width),
                255,
                binding.bcTextWidth)
        }
        //Cortar Papel
        binding.cutPaperInfo.setOnClickListener {
            val cut = arrayOf("Sim", "Não")
            val listDialog = DialogCreater.createListDialog(this@BarCodeActivity,
                resources.getString(R.string.error_qrcode),
                resources.getString(R.string.cancel),
                cut)
            listDialog.setItemClickListener {
                binding.cutPaperInfo.text = cut[it]
                listDialog.cancel()
            }
            listDialog.show()
        }
    }

    //Imprimir
    fun onClick(v:View?){
        val text:String = binding.bcTextContent.text.toString()
        var symbology : Int
        if(encode>7){
            symbology = 8
        }else{
            symbology = encode
        }
        //Preciso criar um bitmap e colocar a imagem dele o texto de mTextView
        //Solução em Kotlin : https://stackoverflow.com/questions/9978884/bitmapdrawable-deprecated-alternative
        val bitmap:Bitmap = BitmapUtil.generateBitmap(text,symbology, 700, 400)
        if(bitmap!=null){
            binding.bcImage.setImageDrawable(BitmapDrawable(resources, bitmap))
        }else{
            Toast.makeText(this@BarCodeActivity, R.string.toast_9, Toast.LENGTH_LONG).show()
        }
        //Solução em Kotlin
        val height:Int = binding.bcHeight.height
        val width:Int = binding.bcWidth.width

        if(binding.cutPaperInfo.text == "Não"){
            if(isK1 && height > 1856){
                kPrinterPresenter.setAlign(1)
                kPrinterPresenter.text("BarCode\n")
                kPrinterPresenter.text("--------------------------------\n")
                kPrinterPresenter.printBarcode(text, encode, height, width, position)
                TectoySunmiPrint.getInstance().print3Line()

            } else {
                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
                TectoySunmiPrint.getInstance().printText("BarCode\n")
                TectoySunmiPrint.getInstance().printText("--------------------------------\n")
                TectoySunmiPrint.getInstance().printBarCode(text, encode,height,width, position)
                TectoySunmiPrint.getInstance().print3Line()
            }
        } else {
            if (isK1 && height > 1856){
                kPrinterPresenter.setAlign(1)
                kPrinterPresenter.text("BarCode\n")
                kPrinterPresenter.text("--------------------------------\n")
                kPrinterPresenter.printBarcode(text, encode,height,height, position)
                kPrinterPresenter.cutpaper(KTectoySunmiPrinter.HALF_CUTTING, 10)
                TectoySunmiPrint.getInstance().print3Line()
            } else {
                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                TectoySunmiPrint.getInstance().printText("BarrCode\n");
                TectoySunmiPrint.getInstance().printText("--------------------------------\n");
                TectoySunmiPrint.getInstance().printBarCode(text, encode,height, width, position)
                TectoySunmiPrint.getInstance().print3Line()
                TectoySunmiPrint.getInstance().cutpaper()
            }
        }
    }

    /**
     * custom seekbar dialog
     */
    private fun showSeekBarDialog(context:Context, min:Int ,title:String, max:Int, set:TextView){
        val builder:AlertDialog.Builder= AlertDialog.Builder(context)
        val view:View=LayoutInflater.from(context).inflate(R.layout.widget_seekbar, null)
        builder.setView(view)
        builder.setCancelable(false)
        val dialog:AlertDialog=builder.create()
        val tv_title:TextView=(view.findViewById(R.id.sb_title) as TextView)
        val tv_start:TextView=(view.findViewById(R.id.sb_start) as TextView)
        val tv_end:TextView=(view.findViewById(R.id.sb_end) as TextView)
        val tv_result:TextView=(view.findViewById(R.id.sb_result) as TextView)
        val tv_ok:TextView=(view.findViewById(R.id.sb_ok) as TextView)
        val tv_cancel:TextView=(view.findViewById(R.id.sb_cancel) as TextView)
        val sb:SeekBar=(view.findViewById(R.id.sb_seekbar) as SeekBar)
        tv_title.text   = title
        tv_start.text   = min.toString()+""
        tv_end.text     = max.toString()+""
        tv_result.text  = set.text

        tv_cancel.setOnClickListener {
            dialog.cancel()
        }
        tv_ok.setOnClickListener(){
            set.text=tv_result.text
            dialog.cancel()
        }
        sb.max=max-min
        sb.setProgress(set.text.toString().toInt()-min)
        sb.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rs:Int=min+progress
                tv_result.text = rs.toString()+""
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(SeekBar: SeekBar?) {
            }
        })
        dialog.show()
    }

    private fun isHaveCamera() : Boolean{
        val deviceHashMap : HashMap<String, UsbDevice> = (getSystemService(Activity.USB_SERVICE) as UsbManager).deviceList
        for(entry in deviceHashMap.entries){
            val usbDevice = entry.value
            if(!TextUtils.isEmpty(usbDevice.deviceName) && usbDevice.deviceName.contains("Orb")){
                return true
            }
            if(!TextUtils.isEmpty(usbDevice.deviceName) && usbDevice.deviceName.contains("Astra")){
                return true
            }
        }
        return false
    }

    private fun connectKPrintService(){
        val intent = Intent()
        intent.`package`=("com.summi.extprinterservice")
        intent.action=("com.summi.extprinterservice.PrinterService")
        bindService(intent, connService, Context.BIND_AUTO_CREATE)
    }

    private var connService:ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            extPrinterService = ExtPrinterService.Stub.asInterface(service)
            kPrinterPresenter = KTectoySunmiPrinter(this@BarCodeActivity, extPrinterService)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            extPrinterService = null
        }
    }

}


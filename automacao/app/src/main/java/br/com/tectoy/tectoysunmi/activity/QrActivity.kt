package br.com.tectoy.tectoysunmi.activity

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
import br.com.tectoy.tectoysunmi.databinding.ActivityQrBinding
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil;
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.EditTextDialog;
import sunmi.sunmiui.dialog.ListDialog;

/**
 * Exemplo de impressão de um código QR em Kotlin
 *
 * @author adevan-neves-santos
 */
class QrActivity : BaseActivity(){
    private lateinit var mImageView:ImageView
    private lateinit var mTextView1:TextView
    private lateinit var mTextView2:TextView
    private lateinit var mTextView3:TextView
    private lateinit var mTextView4:TextView
    private lateinit var mTextView5:TextView
    private lateinit var mTextView6:TextView
    private var print_num   = 0
    private var print_size  = 8
    private var error_level = 3
    var isK1 = false                         //dúvida sobre variáveis estáticos
    var isVertical = false                   //dúvida sobre variáveis estáticos
    private var extPrinterService:ExtPrinterService? = null
    lateinit var kPrinterPresenter:KTectoySunmiPrinter    //dúvida sobre variáveis estáticos
    var height:Int = 0

    private lateinit var binding: ActivityQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle(R.string.qr_title)
        setBack()
        val dm = DisplayMetrics()
        window.windowManager.defaultDisplay.getRealMetrics(dm)
        val width:Int = dm.widthPixels   //Largura da Tela
        height = dm.heightPixels  //Altura da tela
        isVertical =  height > width
        isK1 = isHaveCamera() && isVertical

        if(isK1 && (height > 1856)){
            connectKPrintService()
        }

        mImageView = binding.qrImage
        mTextView1 = binding.qrTextContent
        mTextView2 = binding.qrTextNum
        mTextView3 = binding.qrTextSize
        mTextView4 = binding.qrTextEl
        mTextView5 = binding.qrAlignInfo
        mTextView6 = binding.cutPaperInfo

        binding.qrContent.setOnClickListener {
            lateinit var mDialog:EditTextDialog
            mDialog = DialogCreater.createEditTextDialog(this@QrActivity,
                resources.getString(R.string.cancel),
                resources.getString(R.string.confirm) ,
                resources.getString(R.string.input_qrcode),
                { mDialog.cancel() }, {
                    mTextView1.text = mDialog.editText.text
                    mDialog.cancel()
                }, null)
            mDialog.setHintText("www.tectoysunmi.com.br")
            mDialog.show()
        }

        binding.qrNum.setOnClickListener {
            val mStrings:Array<String> = arrayOf<String>(resources.getString(R.string.single),resources.getString(R.string.twice))
            val listDialog:ListDialog = DialogCreater.createListDialog(this@QrActivity,
                resources.getString(R.string.array_qrcode),
                resources.getString(R.string.cancel),
                mStrings)
            listDialog.setItemClickListener { position ->
                if(!BluetoothUtil.isBlueToothPrinter){
                    Toast.makeText(this@QrActivity, R.string.toast_7, Toast.LENGTH_LONG).show()
                }  else {
                    mTextView3.text = "7"
                    print_size = 7
                }
                mTextView2.text = mStrings[position]
                print_num = position
                listDialog.cancel()
            }
            listDialog.show()
        }


        binding.qrSize.setOnClickListener {
            val listDialog: ListDialog = DialogCreater.createListDialog(
                this@QrActivity,
                resources.getString(R.string.size_qrcode),
                resources.getString(R.string.cancel),
                arrayOf<String>("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
            )
            listDialog.setItemClickListener(object : ListDialog.ItemClickListener {
                var pt: Int = 0
                override fun OnItemClick(position: Int) {
                    pt = position
                    pt += 1
                    if (print_num == 1 && mTextView3.text.toString().toInt() > 7) {
                        Toast.makeText(this@QrActivity, R.string.toast_8, Toast.LENGTH_LONG).show()
                        pt = 7
                    }
                    mTextView3.text = "" + pt
                    print_size = position
                    listDialog.cancel()
                }
            })
            listDialog.show()
        }

        binding.qrEl.setOnClickListener {
            val el: Array<String> = arrayOf(
                "Correção L (7%)",
                "Correção M (15%)",
                "Correção Q (25%)",
                "Correção H (30%)"
            )
            val listDialog: ListDialog = DialogCreater.createListDialog(
                this@QrActivity,
                resources.getString(R.string.error_qrcode),
                resources.getString(R.string.cancel), el
            )
            listDialog.setItemClickListener { position ->
                mTextView4.text = el[position]
                error_level = position
                listDialog.cancel()
            }
            listDialog.show()
        }

        binding.cutPaperInfo.setOnClickListener {
            val cut: Array<String> = arrayOf("Sim", "Não")
            val listDialog: ListDialog = DialogCreater.createListDialog(
                this@QrActivity,
                resources.getString(R.string.error_qrcode),
                resources.getString(R.string.cancel), cut
            )
            listDialog.setItemClickListener(object : ListDialog.ItemClickListener {
                override fun OnItemClick(position: Int) {
                    mTextView6.text = cut[position]
                    error_level = position
                    listDialog.cancel()
                }
            })
            listDialog.show()
        }

        binding.qrAlign.setOnClickListener {
            val pos: Array<String> = arrayOf(
                resources.getString(R.string.align_left),
                resources.getString(R.string.align_mid),
                resources.getString(R.string.align_right)
            )
            val listDialog: ListDialog = DialogCreater.createListDialog(
                this@QrActivity,
                resources.getString(R.string.align_form),
                resources.getString(R.string.cancel), pos
            )
            listDialog.setItemClickListener { position ->
                mTextView5.text = pos[position]
                listDialog.cancel()
            }
            listDialog.show()
        }
    }

    fun onClick(v:View?){
        //Preciso criar um bitmap e colocar a imagem dele o texto de mTextView
        //Solução em Kotlin : https://stackoverflow.com/questions/9978884/bitmapdrawable-deprecated-alternative
        val bitmap:Bitmap = BitmapUtil.generateBitmap(mTextView1.text.toString(), 9, 700, 700)
        if(bitmap!=null){
            mImageView.setImageDrawable(BitmapDrawable(this.resources,bitmap))
        }
        //Solução em Kotlin

        if(mTextView6.text.toString() == "Não"){
            if(isK1 && height > 1856){
                kPrinterPresenter.setAlign(1)
                kPrinterPresenter.text("QrCode\n")
                kPrinterPresenter.text("--------------------------------\n")
                kPrinterPresenter.printQr(mTextView1.text.toString(), print_size, error_level)
                kPrinterPresenter.print3Line()
                kPrinterPresenter.cutpaper(KTectoySunmiPrinter.HALF_CUTTING, 10)
            } else {
                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
                TectoySunmiPrint.getInstance().printText("QrCode\n")
                TectoySunmiPrint.getInstance().printText("--------------------------------\n")
                TectoySunmiPrint.getInstance().printQr(mTextView1.text.toString(), print_size, error_level)
                TectoySunmiPrint.getInstance().print3Line()
            }
        } else {
            if (isK1 && height > 1856){
                kPrinterPresenter.setAlign(1)
                kPrinterPresenter.text("QrCode\n")
                kPrinterPresenter.text("--------------------------------\n")
                kPrinterPresenter.printQr(mTextView1.text.toString(), print_size, error_level)
                kPrinterPresenter.print3Line()
                kPrinterPresenter.cutpaper(KTectoySunmiPrinter.HALF_CUTTING, 10)
            } else {
                TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER);
                TectoySunmiPrint.getInstance().printText("QrCode\n");
                TectoySunmiPrint.getInstance().printText("--------------------------------\n");
                TectoySunmiPrint.getInstance().printQr(mTextView1.text.toString(), print_size, error_level)
                TectoySunmiPrint.getInstance().print3Line()
                TectoySunmiPrint.getInstance().cutpaper()
            }
        }
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

    private fun connectKPrintService(){
        var intent:Intent = Intent()
        intent.`package` = "com.sunmi.extprinterservice"
        intent.action = "com.sunmi.extprinterservice.PrinterService"
        bindService(intent,connService, Context.BIND_AUTO_CREATE)
    }
    private var connService:ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            extPrinterService = ExtPrinterService.Stub.asInterface(service)
            kPrinterPresenter = KTectoySunmiPrinter(this@QrActivity, extPrinterService)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            extPrinterService = null
        }
    }

}

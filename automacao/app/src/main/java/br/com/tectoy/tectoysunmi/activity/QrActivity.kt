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
import kotlinx.android.synthetic.main.activity_qr.*
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.EditTextDialog;
import sunmi.sunmiui.dialog.ListDialog;

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

        /*Versão com callbacks
        binding.qrContent.setOnClickListener (object : View.OnClickListener{
            lateinit var mDialog:EditTextDialog
            override fun onClick(view: View?) {
                mDialog = DialogCreater.createEditTextDialog(this@QrActivity,
                resources.getString(R.string.cancel),
                resources.getString(R.string.confirm) ,
                resources.getString(R.string.input_qrcode), object: View.OnClickListener {
                    override fun onClick(v: View?) {
                        mDialog.cancel() }
                    }, object: View.OnClickListener {
                    override fun onClick(v: View?) {
                    mTextView1.text = mDialog.editText.text
                    mDialog.cancel() }
                    }, null)

            }
        }
        )*/

        binding.qrContent.setOnClickListener (object : View.OnClickListener{
                lateinit var mDialog:EditTextDialog
                override fun onClick(view: View?) {
                    mDialog = DialogCreater.createEditTextDialog(this@QrActivity,
                    resources.getString(R.string.cancel),
                    resources.getString(R.string.confirm) ,
                    resources.getString(R.string.input_qrcode),
                    { mDialog.cancel() }, {
                        mTextView1.text = mDialog.editText.text
                        mDialog.cancel()
                    }, null)
                }
            }
        )

        binding.qrNum.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val mStrings = listOf(resources.getString())
            }
        }
        )

    }

    //Mocados
    fun isHaveCamera():Boolean{
        return true
    }

    fun connectKPrintService(){

    }
    //Mocados

}

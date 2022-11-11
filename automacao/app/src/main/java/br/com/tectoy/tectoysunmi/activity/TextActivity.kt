package br.com.tectoy.tectoysunmi.activity

import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Rect
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog


import com.sunmi.extprinterservice.ExtPrinterService

import java.io.IOException


import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityTextBinding
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil
import br.com.tectoy.tectoysunmi.utils.ESCUtil
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint
import sunmi.sunmiui.dialog.DialogCreater
import sunmi.sunmiui.dialog.ListDialog

import java.util.*

class TextActivity: BaseActivity(), CompoundButton.OnCheckedChangeListener{
    private lateinit var mTextView1: TextView
    private lateinit var mTextView2:TextView
    private lateinit var mCheckBox1: CheckBox
    private lateinit var mCheckBox2: CheckBox
    private lateinit var mEditText: EditText
    private lateinit var mLayout: LinearLayout
    private lateinit var mLinearLayout: LinearLayout
    private var record: Int = 0
    private var isBold: Boolean = false
    private var isUnderLine: Boolean = false
    private var isK1: Boolean = false
    private var height: Int = 0
    private var isVertical: Boolean = false
    private var extPrinterService: ExtPrinterService? = null
    lateinit var kPrinterPresenter: KTectoySunmiPrinter

    private lateinit var binding: ActivityTextBinding

    private var mStrings = arrayOf<String>("CP437", "CP850", "CP860", "CP863", "CP865", "CP857", "CP737", "Windows-1252", "CP866", "CP852", "CP858", "CP874", "CP855", "CP862", "CP864", "GB18030", "BIG5", "KSC5601", "utf-8")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle(R.string.text_title)
        setBack()
        var dm: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        var width: Int = dm.widthPixels
        var height: Int = dm.heightPixels
        isVertical = height > width
        isK1 = isHaveCamera() && isVertical

        if (isK1 && height > 1856){
            connectKPrintService()
        }

        record = 17
        isBold = false
        isUnderLine = false
        mTextView1 = findViewById(R.id.text_text_character)
        mTextView2 = findViewById(R.id.text_text_size)
        mCheckBox1 = findViewById(R.id.text_bold)
        mCheckBox2 = findViewById(R.id.text_underline)
        mEditText = findViewById(R.id.text_text)

        mLinearLayout = findViewById(R.id.text_all)
        mLayout = findViewById(R.id.text_set)

        mLinearLayout.viewTreeObserver.addOnGlobalLayoutListener(object: OnGlobalLayoutListener{
            override fun onGlobalLayout(){
                var r: Rect = Rect()
                mLinearLayout.getWindowVisibleDisplayFrame(r)
                if (r.bottom < 800){
                    mLayout.setVisibility(View.GONE)
                } else {
                    mLayout.setVisibility(View.VISIBLE)
                }
            }
        })

        mCheckBox1.setOnCheckedChangeListener(this)
        mCheckBox2.setOnCheckedChangeListener(this)

        binding.textCharacter.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?){
                var listDialog: ListDialog = DialogCreater.createListDialog(this@TextActivity, getResources().getString(R.string.characterset), getResources().getString(R.string.cancel), mStrings)
                listDialog.setItemClickListener(object : ListDialog.ItemClickListener{
                    override fun OnItemClick(position: Int) {
                        mTextView1.setText(mStrings[position])
                        record = position
                        listDialog.cancel()
                    }
                })

                listDialog.show()
            }
        })

        binding.textSize.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                showSeekBarDialog(this@TextActivity, getResources().getString(R.string.size_text), 12, 36, mTextView2)
            }
        })
    }

    fun onClick(p0: View?){
        var content: String = mEditText.getText().toString()

        var size: Float = mTextView2.text.toString().toFloat()

        if (isK1 && height > 1856){
            kPrinterPresenter.printStyleBold(isBold)
            kPrinterPresenter.text(content)
            kPrinterPresenter.cutpaper(KTectoySunmiPrinter.HALF_CUTTING, 10)
        }else{
            TectoySunmiPrint.getInstance().printStyleBold(isBold)
            TectoySunmiPrint.getInstance().printStyleUnderLine(isUnderLine)
            TectoySunmiPrint.getInstance().printTextWithSize(content, size)
            TectoySunmiPrint.getInstance().print3Line()
            TectoySunmiPrint.getInstance().cutpaper()
        }
    }

    fun printByBlutooth(content: String){
        try{
            if(isBold){
                BluetoothUtil.sendData(ESCUtil.boldOn())
            }else{
                BluetoothUtil.sendData(ESCUtil.boldOff())
            }

            if(isUnderLine){
                BluetoothUtil.sendData(ESCUtil.underlineWithOneDotWidthOn())
            } else{
                BluetoothUtil.sendData(ESCUtil.underlineOff())
            }

            if(record < 17){
                BluetoothUtil.sendData(ESCUtil.singleByte())
                BluetoothUtil.sendData(ESCUtil.setCodeSystemSingle(codeParse(record)))
            } else{
                BluetoothUtil.sendData(ESCUtil.singleByteOff())
                BluetoothUtil.sendData(ESCUtil.setCodeSystemSingle(codeParse(record)))
            }

            BluetoothUtil.sendData((mStrings[record].toByteArray()))
            BluetoothUtil.sendData((ESCUtil.nextLine(3)))
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun codeParse(value: Int): Byte{
        var res: Byte = 0x00
        when(value){
            0 -> res = 0x00
            1, 2, 3, 4 -> res =  (value + 1) as Byte
            5, 6, 7, 8, 9, 10, 11 -> res = (value + 8) as Byte
            12 -> res = 21
            13 -> res = 33
            14 -> res = 34
            15 -> res = 36
            16 -> res = 37
            17, 18, 19 -> res = (value - 17) as Byte
            20 -> res = 0xff as Byte
        }
        return res as Byte
    }

    fun showSeekBarDialog(context: Context, title: String, min: Int, max: Int, set: TextView){
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        var view: View = LayoutInflater.from(context).inflate(R.layout.widget_seekbar, null)
        builder.setView(view)
        builder.setCancelable(false)
        var dialog: AlertDialog = builder.create()
        var tv_title: TextView = view.findViewById(R.id.sb_title)
        var tv_start: TextView = view.findViewById(R.id.sb_start)
        var tv_end: TextView = view.findViewById(R.id.sb_end)
        var tv_result: TextView = view.findViewById(R.id.sb_result)
        var tv_ok: TextView = view.findViewById(R.id.sb_ok)
        var tv_cancel: TextView = view.findViewById(R.id.sb_cancel)
        var sb: SeekBar = view.findViewById(R.id.sb_seekbar)
        tv_title.setText(title)
        tv_start.setText(min.toString() + "")
        tv_end.setText(max.toString() + "")
        tv_result.setText(set.getText())
        tv_cancel.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                dialog.cancel()
            }
        })
        tv_ok.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                set.setText(tv_result.getText())
                dialog.cancel()
            }
        })
        sb.setMax(max - min)
        sb.setProgress(Integer.parseInt(set.getText().toString()) - min)
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rs: Int = min + progress
                tv_result.setText(rs.toString() + "")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        dialog.show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView){
            binding.textBold ->{
                isBold = isChecked
            }
            binding.textBold ->{
                isUnderLine = isChecked
            }
        }
    }

    fun isHaveCamera(): Boolean{
        var deviceHashMap: HashMap<String, UsbDevice> = (getSystemService(Activity.USB_SERVICE) as UsbManager).getDeviceList()
        for (entry in deviceHashMap.entries) {
            var usbDevice: UsbDevice = entry.value
            if (!TextUtils.isEmpty(
                    usbDevice.getInterface(0).getName()
                ) && usbDevice.deviceName.contains("Orb")
            ) {
                return true
            }
            if (!TextUtils.isEmpty(
                    usbDevice.getInterface(0).getName()
                ) && usbDevice.deviceName.contains("Astra")
            ) {
                return true
            }
        }
        return false
    }

    fun connectKPrintService() {
        var intent: Intent = Intent()
        intent.`package` = "com.sunmi.extprinterservice"
        intent.action = "com.sunmi.extprinterservice.PrinterService"
        bindService(intent, connService, Context.BIND_AUTO_CREATE)
    }

    private var connService: ServiceConnection = (object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            extPrinterService = ExtPrinterService.Stub.asInterface(service)
            kPrinterPresenter = KTectoySunmiPrinter(this@TextActivity, extPrinterService)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            extPrinterService = null
        }
    })
}
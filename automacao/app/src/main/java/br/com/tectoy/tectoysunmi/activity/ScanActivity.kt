package br.com.tectoy.tectoysunmi.activity

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sunmi.scanner.IScanInterface;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.databinding.ActivityScanBinding
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.ListDialog;
import sunmi.sunmiui.dialog.ListDialog.ItemClickListener

class ScanActivity: BaseActivity() {
    private lateinit var mBtn: TextView
    private lateinit var txt_tipo_papel: TextView
    private lateinit var charcter_set_content: TextView
    private lateinit var prompt_mode_scan: TextView
    private lateinit var scan_mode_content: TextView
    private lateinit var scan_trigger_content: TextView
    private var scanInterface: IScanInterface? = null
    private lateinit var tvNote: TextView
    private lateinit var scrollView: ScrollView
    private var error_level: Int = 0
    private var txt: String = "Cupom"
    private lateinit var sunmiScanner: SunmiScanner
    lateinit var teste: String

    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle(R.string.qr_title)
        bindScannerService()
        mBtn = binding.btnScan
        scrollView = binding.scrollView
        tvNote = binding.tvNote
        charcter_set_content = binding.charcterSetContent
        prompt_mode_scan = binding.promptModeScan
        scan_mode_content = binding.scanModeContent
        scan_trigger_content = binding.scanTriggerContent
        txt_tipo_papel = binding.txtTipoPapel

        binding.tipoPapel.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                val el = arrayOf<String>("Cupom", "Etiqueta")
                val listDialog: ListDialog = DialogCreater.createListDialog(this@ScanActivity,
                "Nível de correção", "Cancelar", el)
                listDialog.setItemClickListener ( object : ListDialog.ItemClickListener {
                    override fun OnItemClick(position: Int) {
                        txt_tipo_papel.setText(el[position])
                        txt = el[position]
                        error_level = position
                        listDialog.cancel()
                    }
                })
                listDialog.show()
            }
        })

        binding.charcterSet.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                val el = arrayOf<String>("UTF-8", "GBK", "ISO-8859-1", "SHIFT-JLS", "Compatibility")
                val listDialog: ListDialog = DialogCreater.createListDialog(this@ScanActivity,
                    "Nível de correção", "Cancelar", el)
                listDialog.setItemClickListener(object : ItemClickListener{
                    override fun OnItemClick(position: Int) {
                        charcter_set_content.setText(el[position])
                        error_level = position
                        listDialog.cancel()
                    }
                })
                listDialog.show()
            }
        })

        binding.promptMode.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                val el = arrayOf<String>("Beep+Vibrate", "Beep", "Vibrate")
                val listDialog: ListDialog = DialogCreater.createListDialog(this@ScanActivity,
                    "Nível de correção", "Cancelar", el)
                listDialog.setItemClickListener(object : ItemClickListener{
                    override fun OnItemClick(position: Int) {
                        charcter_set_content.text = el[position]
                        error_level = position
                        listDialog.cancel()
                    }
                })
                listDialog.show()
            }
        })

        binding.scanMode.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                val el = arrayOf<String>("Trigger mode", "Continuos mode", "Pulse mode")
                val listDialog: ListDialog = DialogCreater.createListDialog(this@ScanActivity,
                    "Nível de correção", "Cancelar", el)
                listDialog.setItemClickListener(object : ItemClickListener{
                    override fun OnItemClick(position: Int) {
                        charcter_set_content.text = el[position]
                        error_level = position
                        listDialog.cancel()
                    }
                })
                listDialog.show()
            }
        })

        binding.scanTrigger.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                val el = arrayOf<String>("none", "On-screen Button")
                val listDialog: ListDialog = DialogCreater.createListDialog(this@ScanActivity,
                    "Nível de correção", "Cancelar", el)
                listDialog.setItemClickListener(object : ItemClickListener{
                    override fun OnItemClick(position: Int) {
                        charcter_set_content.text = el[position]
                        error_level = position
                        listDialog.cancel()
                    }
                })
                listDialog.show()
            }
        })

        mBtn.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                try {
                    scanInterface?.scan()
                } catch (e: RemoteException){
                    e.printStackTrace()
                }
            }
        })

        initScanner()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean{
        val action: Int = event.action

        when(action){
            KeyEvent.ACTION_DOWN ->{
                if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP
                    || event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                    || event.keyCode == KeyEvent.KEYCODE_BACK
                    || event.keyCode == KeyEvent.KEYCODE_MENU
                    || event.keyCode == KeyEvent.KEYCODE_HOME
                    || event.keyCode == KeyEvent.KEYCODE_POWER)
                   return super.dispatchKeyEvent(event)
                if (sunmiScanner != null)
                    sunmiScanner.analysisKeyEvent(event)
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    fun initScanner() {
        val sunmiScanner: SunmiScanner = SunmiScanner(applicationContext)
        sunmiScanner.analysisBroadcast()

        sunmiScanner.setScannerListener(object : SunmiScanner.OnScannerListener {
            override fun onScanData(data: String?, type: SunmiScanner.DATA_DISCRIBUTE_TYPE?) {
                append("Tipo de Dado:" + type + "\nCodigo:" + data + "\n")

                if (txt.equals("Cupom")) {
                    TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
                    TectoySunmiPrint.getInstance().printBarCode(
                        data, TectoySunmiPrint.BarCodeModels_EAN13,
                        162, 2, TectoySunmiPrint.BarCodeTextPosition_ABAIXO_DO_CODIGO_DE_BARRAS
                    )
                    TectoySunmiPrint.getInstance().print3Line()
                } else {
                    TectoySunmiPrint.getInstance().printOneLabel()
                }
            }

            override fun onResponseData(data: String?, type: SunmiScanner.DATA_DISCRIBUTE_TYPE?) {

            }

            override fun onResponseTimeout() {

            }
        })

    }

    fun append(message: String){
        this.runOnUiThread(object : Runnable{
            override fun run() {
                tvNote.append(message)
                scrollView.post(object : Runnable{
                    override fun run() {
                        scrollView.smoothScrollTo(0, tvNote.bottom)
                    }
                })
            }
        })
    }

    val conn: ServiceConnection = (object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            scanInterface = IScanInterface.Stub.asInterface(service)
            Log.i("setting", "Scanner Service Connected!")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("setting", "Scanner Service Disconnected!")
            scanInterface = null
        }
    })
    fun bindScannerService(){
        val intent = Intent()
        intent.`package` = "com.sunmi.scanner"
        intent.action = "com.sunmi.scanner.IScanInterface"
        bindService(intent, conn, Service.BIND_AUTO_CREATE  )
    }

}
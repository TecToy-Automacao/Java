package br.com.tectoy.tectoysunmi.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.os.RemoteException
import android.text.TextUtils
import android.util.DisplayMetrics
import android.widget.Button
import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityBarcodeBinding
import br.com.tectoy.tectoysunmi.databinding.ActivityLedBinding
import br.com.tectoy.tectoysunmi.databinding.ActivityLedkBinding
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter
import com.sunmi.extprinterservice.ExtPrinterService
import com.sunmi.statuslampmanager.IStateLamp
import java.util.HashMap

class LedActivity:BaseActivity() {
    private  var mService:IStateLamp?=null
    var r:Long=12
    private var isK1 : Boolean = false
    private var isVertical : Boolean = false

    private lateinit var binding : ActivityLedBinding
    private lateinit var bindingK : ActivityLedkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLedBinding.inflate(layoutInflater)
        bindingK = ActivityLedkBinding.inflate(layoutInflater)
        setMyTitle(R.string.function_led)
        setBack()

        connectService()

        var dm: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        var width: Int = dm.widthPixels
        var height: Int = dm.heightPixels

        isVertical = height > width
        isK1 = isHaveCamera() && isVertical

        if(isK1 && height > 1856){
            setContentView(R.layout.activity_ledk)

            bindingK.btnAzulclaro.setOnClickListener {
                try {
                    mService?.closeAllLamp()
                    mService?.controlLamp(0,"Led-3")
                    mService?.controlLampForLoop(0,500000,100,"Led-2")
                }catch (e:RemoteException){
                    e.printStackTrace()
                }
            }

            bindingK.btnAmarelo.setOnClickListener {
                try {
                    mService?.closeAllLamp()
                    mService?.controlLamp(0,"Led-2")
                    mService?.controlLampForLoop(0,500000,100,"Led-1")
                }catch (e:RemoteException){
                    e.printStackTrace()
                }
            }

            bindingK.btnRoxo.setOnClickListener {
                try {
                    mService?.controlLamp(0,"Led-3")
                    mService?.controlLampForLoop(0,500000,100,"Led-1")
                }catch (e:RemoteException){
                    e.printStackTrace()
                }
            }

            bindingK.btnAzul.setOnClickListener {
                try {
                    mService?.closeAllLamp();
                    mService?.controlLamp(0,"Led-3")

                }catch (e:RemoteException){
                    e.printStackTrace()
                }
            }

            bindingK.btnVerde.setOnClickListener {
                try {
                    mService?.closeAllLamp();
                    mService?.controlLamp(0,"Led-2")

                }catch (e:RemoteException){
                    e.printStackTrace()
                }
            }
        }else{
            setContentView(R.layout.activity_led)
        }
        binding.btnVermelho.setOnClickListener {
            try {
                mService?.closeAllLamp();
                mService?.controlLamp(0,"Led-1")

            }catch (e:RemoteException){
                e.printStackTrace()
            }
        }

        binding.btnDesligar.setOnClickListener {
            try {
                mService?.closeAllLamp();

            }catch (e:RemoteException){
                e.printStackTrace()
            }
        }
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
    private var con: ServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = IStateLamp.Stub.asInterface(service)

        }
        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
        }
    }

    private fun connectService(){
        val intent = Intent()
        intent.`package`=("com.summi.statuslampanager")
        intent.action=("com.summi.statusamp.Service")
        bindService(intent, con, Context.BIND_AUTO_CREATE)
    }
}
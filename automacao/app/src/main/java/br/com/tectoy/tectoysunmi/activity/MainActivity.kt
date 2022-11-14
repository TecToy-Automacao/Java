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
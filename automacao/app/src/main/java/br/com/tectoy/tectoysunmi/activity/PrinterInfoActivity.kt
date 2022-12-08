package br.com.tectoy.tectoysunmi.activity

import android.content.Context
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sunmi.peripheral.printer.InnerResultCallback;

import br.com.tectoy.tectoysunmi.BuildConfig;
import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.databinding.ActivityInfoBinding
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;

/**
 *
 * Página de exibição de informações da impressora
 */

open class PrinterInfoActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        setMyTitle("Informação da impressora")
        setBack()
    }

    protected fun OnResume(){
        super.onResume()
        updateInfo()
    }

    fun updateInfo(){
        findViewById<TextView>(R.id.info1).text = TectoySunmiPrint.getInstance().printerSerialNo
        findViewById<TextView>(R.id.info2).text = TectoySunmiPrint.getInstance().deviceModel
        findViewById<TextView>(R.id.info3).text = TectoySunmiPrint.getInstance().printerVersion
        findViewById<TextView>(R.id.info4).text = TectoySunmiPrint.getInstance().printerPaper
        TectoySunmiPrint.getInstance().getPrinterDistance(object : InnerResultCallback(){
            override fun onRunResult(isSuccess: Boolean) {
                throw RemoteException()
            }

            override fun onReturnString(result: String?) {
                findViewById<TextView>(R.id.info5).text = result + "mm"
                throw RemoteException()
            }

            override fun onRaiseException(code: Int, msg: String?) {
                throw RemoteException()
            }

            override fun onPrintResult(code: Int, msg: String?) {
                throw RemoteException()
            }
        })

        TectoySunmiPrint.getInstance().getPrinterHead(object : InnerResultCallback(){
            override fun onRunResult(isSuccess: Boolean) {
                throw RemoteException()
            }

            override fun onReturnString(result: String?) {
                findViewById<TextView>(R.id.info7).text = result
                throw RemoteException()
            }

            override fun onRaiseException(code: Int, msg: String?) {
                throw RemoteException()
            }

            override fun onPrintResult(code: Int, msg: String?) {
                throw RemoteException()
            }
        })

        getServiceVersion()
        findViewById<TextView>(R.id.info9).text = BuildConfig.VERSION_NAME
    }

    fun getServiceVersion(){
        val packageManager: PackageManager = packageManager
        try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo("woyou.aidlservice.jiuiv5", 0)
            if(packageInfo != null){
                findViewById<TextView>(R.id.info6).setText(packageInfo.versionName)
                findViewById<TextView>(R.id.info8).setText(packageInfo.versionCode.toString()+"")
            }
        }catch (e: PackageManager.NameNotFoundException){
            e.printStackTrace()
        }
    }

}
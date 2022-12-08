package br.com.tectoy.tectoysunmi.activity

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint
import sunmi.sunmiui.dialog.EditTextDialog

abstract class BaseActivity : AppCompatActivity() {
    var handler:Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler= Handler()
        initPrinterStyle()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
    }

    /**
     * Initialize the printer
     * All style settings will be restored to default
     */
    private fun initPrinterStyle(){
        TectoySunmiPrint.getInstance().initPrinter()
    }

    /**
     * set title
     *
     * @param title title name
     */
    fun setMyTitle(title:String){
        var actionBar:ActionBar? = supportActionBar
        actionBar?.title = title
    }

    /**
     * set title
     *
     * @param title title res
     */

    fun setMyTitle(@StringRes title:Int){
        var actionBar:ActionBar? = supportActionBar
        actionBar?.setTitle(title)
        setSubTitle()
    }

    fun setSubTitle(){
        var actionBar:ActionBar? = supportActionBar
        if(actionBar!=null){
            if (TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.NoSunmiPrinter) {
                actionBar.subtitle = "Sem Impressora"
            } else if (TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.CheckSunmiPrinter) {
                actionBar.subtitle = "Conectando Impressora"
                //Dúvida sobre esse método postDelayed do java
                handler?.postDelayed({ setSubTitle() }, 2000)
            } else if (TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.FoundSunmiPrinter) {
                actionBar.subtitle = "Impressora Conectada"
            } else {
                TectoySunmiPrint.getInstance().initSunmiPrinterService(this)
            }
        }
    }

    /**
     * set back
     */
    fun setBack(){
        var actionBar:ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.back);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true;
    }

    lateinit var mEditTextDialog: EditTextDialog

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_print -> true
            else -> return super.onOptionsItemSelected(item)
        }

    }

}
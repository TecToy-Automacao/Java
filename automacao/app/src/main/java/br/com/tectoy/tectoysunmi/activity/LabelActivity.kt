package br.com.tectoy.tectoysunmi.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityLabelBinding
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint

class LabelActivity:BaseActivity() {
    private lateinit var binding: ActivityLabelBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLabelBinding.inflate(layoutInflater)
        setMyTitle("Label Teste")
        setBack()
    }
    fun testOne(view:View){
        if(!TectoySunmiPrint.getInstance().isLabelMode){
            Toast.makeText(this@LabelActivity, R.string.toast_12, Toast.LENGTH_LONG).show();
            return
        }

        TectoySunmiPrint.getInstance().printOneLabel();
    }

    fun testMore(view: View){
        if(!TectoySunmiPrint.getInstance().isLabelMode){
            Toast.makeText(this, R.string.toast_12, Toast.LENGTH_LONG).show();
            return
        }
        TectoySunmiPrint.getInstance().printMultiLabel(5);
    }
}
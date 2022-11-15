package br.com.tectoy.tectoysunmi.activity

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.databinding.ActivityLcdBinding
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;

class LcdActivity: BaseActivity() {

    private lateinit var binding: ActivityLcdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLcdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle("Lcd Teste")
        setBack()

        TectoySunmiPrint.getInstance().controlLcd(1)
        TectoySunmiPrint.getInstance().controlLcd(2)
        TectoySunmiPrint.getInstance().controlLcd(4)
    }

    fun text(view: View){
        TectoySunmiPrint.getInstance().sendTextToLcd()
    }

    fun texts(view: View){
        TectoySunmiPrint.getInstance().sendTextToLcd()
    }

    fun pic(view: View){
        val options: BitmapFactory.Options = BitmapFactory.Options()

        options.inScaled = false

        options.inDensity = resources.displayMetrics.densityDpi

        TectoySunmiPrint.getInstance().sendPicToLcd(BitmapFactory.decodeResource(resources,
            R.drawable.tectoy, options))
    }
}
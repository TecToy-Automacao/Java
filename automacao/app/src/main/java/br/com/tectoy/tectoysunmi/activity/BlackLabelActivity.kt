package br.com.tectoy.tectoysunmi.activity

import android.os.Bundle
import android.widget.Toast
import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityBlacklabelBinding
import br.com.tectoy.tectoysunmi.utils.ESCUtil
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint

class BlackLabelActivity:BaseActivity() {
    private lateinit var binding: ActivityBlacklabelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlacklabelBinding.inflate(layoutInflater)
        setMyTitle("Black Label")
        setBack()
        initView()
    }

    private fun initView(){
        setContentView(binding.root)
        binding.blSetting.setOnClickListener {
            Toast.makeText(this@BlackLabelActivity, R.string.toast_11, Toast.LENGTH_LONG).show()
        }

        binding.blCheck.setOnClickListener {
            if(TectoySunmiPrint.getInstance().isBlackLabelMode()){
                TectoySunmiPrint.getInstance().sendRawData(ESCUtil.gogogo());
            }else{
                Toast.makeText(this@BlackLabelActivity, R.string.toast_10, Toast.LENGTH_LONG).show()
            }
        }

        binding.blSample.setOnClickListener {
            if(!TectoySunmiPrint.getInstance().isBlackLabelMode()){
                Toast.makeText(this@BlackLabelActivity, R.string.toast_10, Toast.LENGTH_LONG).show();
            }else{
                TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 6, 3);
                TectoySunmiPrint.getInstance().print3Line();
                TectoySunmiPrint.getInstance().sendRawData(ESCUtil.gogogo());
                TectoySunmiPrint.getInstance().cutpaper();
            }
        }
    }
}
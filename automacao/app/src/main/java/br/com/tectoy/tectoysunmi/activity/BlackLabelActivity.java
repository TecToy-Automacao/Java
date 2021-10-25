package br.com.tectoy.tectoysunmi.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.ESCUtil;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;

public class BlackLabelActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklabel);
        setMyTitle(R.string.blackline_title);
        setBack();
        initView();
    }

    private void initView() {
        findViewById(R.id.bl_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BlackLabelActivity.this, R.string.toast_11, Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.bl_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TectoySunmiPrint.getInstance().isBlackLabelMode()){
                    TectoySunmiPrint.getInstance().sendRawData(ESCUtil.gogogo());
                }else{
                    Toast.makeText(BlackLabelActivity.this, R.string.toast_10, Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.bl_sample).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TectoySunmiPrint.getInstance().isBlackLabelMode()){
                    Toast.makeText(BlackLabelActivity.this, R.string.toast_10, Toast.LENGTH_LONG).show();
                }else{
                    TectoySunmiPrint.getInstance().printQr("www.tectoysunmi.com.br", 6, 3);
                    TectoySunmiPrint.getInstance().print3Line();
                    TectoySunmiPrint.getInstance().sendRawData(ESCUtil.gogogo());
                    TectoySunmiPrint.getInstance().cutpaper();
                }
            }
        });
    }
}

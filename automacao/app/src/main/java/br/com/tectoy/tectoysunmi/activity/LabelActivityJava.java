package br.com.tectoy.tectoysunmi.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;

public class LabelActivityJava extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        setMyTitle(R.string.label_title);
        setBack();
    }

    public void testOne(View view) {
        if(!TectoySunmiPrint.getInstance().isLabelMode()){
            Toast.makeText(this, R.string.toast_12, Toast.LENGTH_LONG).show();
            return;
        }

        TectoySunmiPrint.getInstance().printOneLabel();
    }


    public void testMore(View view) {
        if(!TectoySunmiPrint.getInstance().isLabelMode()){
            Toast.makeText(this, R.string.toast_12, Toast.LENGTH_LONG).show();
            return;
        }
        TectoySunmiPrint.getInstance().printMultiLabel(5);
    }
}

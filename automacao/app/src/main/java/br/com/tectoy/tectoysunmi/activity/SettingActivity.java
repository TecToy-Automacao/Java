package br.com.tectoy.tectoysunmi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;


import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.ListDialog;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    String[] method = new String[]{"API"};

    private TextView mTextView1, mTextView2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setMyTitle(R.string.setting_title);
        setBack();

        findViewById(R.id.setting_connect).setOnClickListener(this);
        findViewById(R.id.setting_info).setOnClickListener(this);

        mTextView1 = findViewById(R.id.setting_conected);
        mTextView2 = findViewById(R.id.setting_disconected);
        mTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TectoySunmiPrint.getInstance().initSunmiPrinterService(SettingActivity.this);
                setService();
            }
        });
        mTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TectoySunmiPrint.getInstance().deInitSunmiPrinterService(SettingActivity.this);
                setService();
            }
        });

            ((TextView)findViewById(R.id.setting_textview1)).setText("API");

        setService();
    }

    @Override
    public void onClick(View v) {
        final ListDialog listDialog;
        switch (v.getId()){
            case R.id.setting_connect:
                listDialog = DialogCreater.createListDialog(this, getResources().getString(R.string.connect_method), getResources().getString(R.string.cancel), method);
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        ((TextView)findViewById(R.id.setting_textview1)).setText(method[position]);
                        setMyTitle(R.string.setting_title);
                        listDialog.cancel();
                    }
                });
                listDialog.show();
                break;
            case R.id.setting_info:
                startActivity(new Intent(SettingActivity.this, PrinterInfoActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     *  Set print service connection status
     */
    private void setService(){
        if(TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.FoundSunmiPrinter){
            mTextView1.setTextColor(getResources().getColor(R.color.white1));
            mTextView1.setEnabled(false);
            mTextView2.setTextColor(getResources().getColor(R.color.white));
            mTextView2.setEnabled(true);
        }else if(TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.CheckSunmiPrinter){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setService();
                }
            }, 2000);
        }else if(TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.LostSunmiPrinter){
            mTextView1.setTextColor(getResources().getColor(R.color.white));
            mTextView1.setEnabled(true);
            mTextView2.setTextColor(getResources().getColor(R.color.white1));
            mTextView2.setEnabled(false);
        } else{
            mTextView1.setTextColor(getResources().getColor(R.color.white1));
            mTextView1.setEnabled(true);
            mTextView2.setTextColor(getResources().getColor(R.color.white1));
            mTextView2.setEnabled(false);
        }
    }
}

package br.com.tectoy.tectoysunmi.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil;
import br.com.tectoy.tectoysunmi.utils.BytesUtil;
import br.com.tectoy.tectoysunmi.utils.ESCUtil;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import sunmi.sunmiui.dialog.EditTextDialog;

public abstract class BaseActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        initPrinterStyle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Initialize the printer
     * All style settings will be restored to default
     */
    private void initPrinterStyle() {
        TectoySunmiPrint.getInstance().initPrinter();

    }

    /**
     * set title
     *
     * @param title title name
     */
    void setMyTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    /**
     * set title
     *
     * @param title title res
     */
    void setMyTitle(@StringRes int title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
        setSubTitle();
    }

    /**
     * set sub title
     */
    void setSubTitle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            if (TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.NoSunmiPrinter) {
                actionBar.setSubtitle("Sem Impressora");
            } else if (TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.CheckSunmiPrinter) {
                actionBar.setSubtitle("Conectando Impressora");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setSubTitle();
                    }
                }, 2000);
            } else if (TectoySunmiPrint.getInstance().sunmiPrinter == TectoySunmiPrint.FoundSunmiPrinter) {
                actionBar.setSubtitle("Impressora Conectada");
            } else {
                TectoySunmiPrint.getInstance().initSunmiPrinterService(this);
            }
        }
    }

    /**
     * set back
     */
    void setBack() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    EditTextDialog mEditTextDialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_print:
                // TODO
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

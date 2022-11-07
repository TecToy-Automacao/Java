package br.com.tectoy.tectoysunmi.activity

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sunmi.extprinterservice.ExtPrinterService;

import java.util.HashMap;
import java.util.Map;

import br.com.tectoy.tectoysunmi.R;
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil;
import br.com.tectoy.tectoysunmi.utils.KTectoySunmiPrinter;
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint;
import kotlinx.android.synthetic.main.activity_qr.*
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.EditTextDialog;
import sunmi.sunmiui.dialog.ListDialog;

class QrActivity : BaseActivity(){

}
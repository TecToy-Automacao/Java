package br.com.tectoy.tectoysunmi.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by mayflower on 2019.11.07
 * get me at jiangli@sunmi.com
 * 接口调用顺序：实例化 -> analysisBroadcast() -> setOnBroadcastListener() ->然后就可以为所欲为了
 * 最后注意在onDestroy()中调用closeBroadcast()
 * 注意：这个类只是Demo，可以根据自己需求任意修改，请随意调教它
 **/

public class SunmiScanner {
    private static final String TAG = SunmiScanner.class.getSimpleName();
    private Context context;

    public SunmiScanner(Context context){
        this.context = context;
    }

    /**
     * 开启广播数据处理
     */
    public void analysisBroadcast(){
        Log.i(TAG, "analysisBroadcast");
        if (this.isBroadcastOpened){
            Log.e(TAG, "analysisBroadcast:Broadcast is already opened");
            return;
        }
        if (this.context == null){
            Log.e(TAG, "analysisBroadcast:context is null");
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED");
        this.context.registerReceiver(broadcastReceiver, filter);
        isBroadcastOpened = true;
    }

    private boolean isBroadcastOpened = false;
    //等待命令的返回中,主要给串口扫码器用,现在只有它有指令返回值
    private static boolean isWaittingResponse = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            if (null != data && data.length() > 0) {
                if (isResponseData(data, DATA_DISCRIBUTE_TYPE.TYPE_BROADCAST)){
                    Log.i(TAG, "BroadcastReceiver onResponseData:" + data);
                    isWaittingResponse = false;
                    for (Map.Entry<String, OnScannerListener> entry : scannerListenerHashMap.entrySet())
                        entry.getValue().onResponseData(data, DATA_DISCRIBUTE_TYPE.TYPE_BROADCAST);
                }
                else {
                    Log.i(TAG, "BroadcastReceiver onScanData:" + data);
                    for (Map.Entry<String, OnScannerListener> entry : scannerListenerHashMap.entrySet())
                        entry.getValue().onScanData(data, DATA_DISCRIBUTE_TYPE.TYPE_BROADCAST);
                }
            }
        }
    };

    private boolean isResponseData(String data, DATA_DISCRIBUTE_TYPE type){
        Log.i(TAG, "isResponseData[hex]:" + data + "[" + ByteUtils.str2HexString(data)+ "]");
        switch (type){
            case TYPE_KEYBOARD: //键值，开头会缺少2字节，这俩是不可见字符：<STX><SOH>
                data = ByteUtils.hexStr2Str(SerialCmd.RES_PREFIX_HEX).substring(0,2) + data;
                break;
            case TYPE_BROADCAST: //广播
                break;
            default:
                break;
        }
        return (data.length() > 6 && data.substring(0,6).equals(ByteUtils.hexStr2Str(SerialCmd.RES_PREFIX_HEX)));
    }

    /**
     * 开启键值数据处理，注意：必须在当前活跃的activity或dialog中，才能够收得到KeyEvent
     * demo这里用的是间隔来判断一次扫码，如果想用结束符判断也是可以的,但要注意：结束符有3种：0D，0A，0D0A
     * @param keyEvent
     */
    public void analysisKeyEvent(KeyEvent keyEvent){
        Log.i(TAG, "analysisKeyEvent:" + keyEvent.toString());
        if (keyEvent == null){
            Log.e(TAG, "analysisKeyEvent:keyEvent is null.");
            return;
        }
        int unicodeChar = keyEvent.getUnicodeChar();
        sb.append((char) unicodeChar);
        curLen++;
        if (!isKeyEventScanning){
            isKeyEventScanning = true;
            timerScanCal();
        }
    }

    private static final int SAME_CODE_DELAY_TIME = 200;     //一次扫码判断策略: SAME_CODE_DELAY_TIME时间内没有再收到别的数据
    private StringBuilder sb = new StringBuilder();
    private Handler handler = new Handler();
    private int oldLen = 0;
    private int curLen = 0;
    private boolean isKeyEventScanning = false;

    private void timerScanCal(){
        oldLen = curLen;
        //正常扫码器扫码间隔都是200ms以上,而且人为扫码时,一般动作间隔800ms以上(也可以用KEYCODE_ENTER判,但麻烦,因为各种扫码器后缀不一定)
        handler.postDelayed(scan, SAME_CODE_DELAY_TIME);
    }

    private Runnable scan = new Runnable() {
        @Override
        public void run() {
            if (oldLen != curLen){
                timerScanCal();
                return;
            }
            oldLen = curLen = 0;
//            String data = sb.toString().replaceAll("\\p{C}", "");     //清除所有不可见字符
//            String data = sb.toString().replaceAll("[\u0000-\u0009\u000B\u000C\u000E-\u001F\u007F]", "");     //保留0D和0A，清除别的不可见字符
            String data = sb.toString();
            if (data.length() > 0){
                isKeyEventScanning = false;
                if (isResponseData(data, DATA_DISCRIBUTE_TYPE.TYPE_KEYBOARD)){
                    Log.i(TAG, "KeyEvent onResponseData:" + data);
                    for (Map.Entry<String, OnScannerListener> entry : scannerListenerHashMap.entrySet())
                        entry.getValue().onResponseData(data, DATA_DISCRIBUTE_TYPE.TYPE_KEYBOARD);
                }
                else {
                    Log.i(TAG, "KeyEvent onScanData:" + data);
                    for (Map.Entry<String, OnScannerListener> entry : scannerListenerHashMap.entrySet())
                        entry.getValue().onScanData(data, DATA_DISCRIBUTE_TYPE.TYPE_KEYBOARD);
                }
                sb.setLength(0);
            }
        }
    };

    /**
     * 退出程序时释放资源
     */
    public void destory(){
        Log.i(TAG, "destory");
        if (this.context == null || !isBroadcastOpened)
            return;
        try {
            this.context.unregisterReceiver(broadcastReceiver);
            isBroadcastOpened = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置扫码器数据监听
     * 注意：这里是支持多个回调的，OnScannerListener实例化多个的话，每个都能收到数据
     * @param listener
     */
    public void setScannerListener(OnScannerListener listener){
        Log.i(TAG, "setScannerListener:" + listener);
        if (listener == null){
            Log.e(TAG, "setScannerListener:listener is null.");
            return;
        }
        scannerListenerHashMap.put(listener.toString(), listener);
    }

    /**
     * 关闭单个listener
     * @param listener
     */
    public void closeScannerListener(OnScannerListener listener){
        String key = listener.toString();
        if (scannerListenerHashMap.containsKey(key))
            scannerListenerHashMap.remove(key);
    }

    /**
     * 关闭所有listener
     */
    public void closeScannerListener(){
        scannerListenerHashMap = new ConcurrentHashMap<>();
    }

    public interface OnScannerListener{
        /**
         * @param data 数据
         * @param type 数据来源，键值 or 广播
         */
        void onScanData(String data, DATA_DISCRIBUTE_TYPE type);     //扫码数据
        void onResponseData(String data, DATA_DISCRIBUTE_TYPE type); //指令返回值,给串口扫码器预留
        void onResponseTimeout();           //指令返回超时,给串口扫码器预留
    }
    private ConcurrentHashMap<String, OnScannerListener> scannerListenerHashMap = new ConcurrentHashMap<>();

    /**
     * 设置数据分发方式
     * USB扫码器默认是TYPE_KEYBOARD，且不支持TYPE_KEYBOARD_AND_BROADCAST
     * 串口扫码器默认是TYPE_KEYBOARD_AND_BROADCAST，支持所有参数
     * @param type 键值？ 广播？ 键值+广播？ decide！
     */
    public void setDataDiscributeType(DATA_DISCRIBUTE_TYPE type){
        Log.i(TAG, "setDataDiscributeType:" + type.toString());
        if (this.context == null) {
            Log.e(TAG, "setDataDiscributeType:context is null");
            return;
        }
        //type处理下
        int usbType = 0;    //usb扫码器默认键值
        boolean bSerialKeyboard = true;     //串口扫码器默认键值
        boolean bSerialBroadcast = true;    //串口扫码器默认键值
        switch (type){
            case TYPE_KEYBOARD:
                usbType = 0;
                bSerialKeyboard = true;
                bSerialBroadcast = false;
                break;
            case TYPE_BROADCAST:
                usbType = 2;
                bSerialKeyboard = false;
                bSerialBroadcast = true;
                break;
            case TYPE_KEYBOARD_AND_BROADCAST:
                usbType = 0;
                bSerialKeyboard = true;
                bSerialBroadcast = true;
                break;
            default:
                break;
        }

        //1.USB扫码器
        int pid, vid;
        HashMap<String,UsbDevice> deviceHashMap = ((UsbManager)context.getSystemService(Activity.USB_SERVICE)).getDeviceList();
        for (Map.Entry<String, UsbDevice> entry : deviceHashMap.entrySet()){
            pid = entry.getValue().getProductId();
            vid = entry.getValue().getVendorId();
            if (pidVidList.contains(pid + "," + vid)){
                Intent intent = new Intent();
                intent.setAction("com.sunmi.scanner.ACTION_BAR_DEVICES_SETTING");
                intent.putExtra("name",entry.getValue().getDeviceName()); //deviceName可以为空
                intent.putExtra("pid",pid);
                intent.putExtra("vid",vid);
                intent.putExtra("type",usbType); //0键盘(默认) 1键值 2广播(推荐) 3直接填充输出
                intent.putExtra("toast",false); //toast信息框的不要
                this.context.sendBroadcast(intent);
            }
        }
        //2.串口扫码器
        Intent intent = new Intent();
        intent.setAction("com.sunmi.scanner.ACTION_SCANNER_SERIAL_SETTING");
        intent.putExtra("analog_key", bSerialKeyboard);   //true为开启键盘keyboard
        intent.putExtra("broadcast", bSerialBroadcast);   //true为开启广播broadcast
        intent.putExtra("toast",false);
        this.context.sendBroadcast(intent);
    }

    public enum  DATA_DISCRIBUTE_TYPE{ TYPE_KEYBOARD, TYPE_BROADCAST, TYPE_KEYBOARD_AND_BROADCAST }

    /**
     * 这就是所有USB扫码器的pid,vid，若有外接别的，在这里添加即可
     */
    private static final List<String> pidVidList = new ArrayList<>(Arrays.asList(
            "4608,1504",
            "9492,1529",
            "34,12879",
            "193,12879"
    ));


    /**
     * 串口扫码器支持指令控制，有特殊需求的话可以控制它，想干嘛都可以
     * 注意：发指令有风险，正常扫码不需要发任何指令
     * @param command 完整指令
     */
    public void sendCommand(String command){
        Log.i(TAG,"sendCmdByBroadcast[hex]:" + command + "[" + ByteUtils.str2HexString(command) + "]");
        if(this.context == null || command == null || command.isEmpty()) {
            Log.e(TAG, "sendCmdByBroadcast:context or cmd is null!");
            return;
        }
        if (singleThreadExecutor == null)
            singleThreadExecutor = Executors.newSingleThreadExecutor(); //注意：线程过多则不可使用这种初始化，详见百度

        SendThread sendThread = new SendThread(command);
        singleThreadExecutor.execute(sendThread);
    }

    /**
     * ~<SOH>0000系列指令封装示例
     * @param simpleCmd ~<SOH>0000系列指令，见指令集文档
     */
    public void sendSimpleCmd(String simpleCmd){
        if (this.context == null || simpleCmd == null || simpleCmd.isEmpty()) {
            Log.e(TAG, "sendSimpleCmd:context or cmd is null!");
            return;
        }
        //PREFIX_HEX和SUFFIX_HEX拿出来是因为：有的指令并没有这俩货
        String cmd = ByteUtils.hexStr2Str(SerialCmd.PREFIX_HEX + ByteUtils.str2HexString(simpleCmd) + SerialCmd.SUFFIX_HEX);
        sendCommand(cmd);
    }

    //线程池，发送命令间隔控制
    private ExecutorService singleThreadExecutor = null;    //命令的线程池，一个一个发
    private boolean isHaveResponse = false;                 //~<SOH>0000系列有返回值(旧版NLS系列被服务屏蔽掉了)

    private class SendThread implements Runnable{
        private String strCmd;      //必须放这里哦,每个线程中的cmd都不一样
        private SendThread(String cmd){ strCmd = cmd; }

        @Override
        public void run(){
            if (strCmd.length() > 6)
                isHaveResponse = strCmd.substring(0, 6).equals(ByteUtils.hexStr2Str(SerialCmd.PREFIX_HEX));
            else
                isHaveResponse = false;
            //置true是因为：需要等返回值或MAX_RESPONSE_TIME
            isWaittingResponse = true;

            byte[] bytes = strCmd.getBytes();
            byte[] cmd = new byte[bytes.length + 2];
            System.arraycopy(bytes,0,cmd,0,bytes.length);
            lrcCheckSum(cmd);
            Intent intent = new Intent("com.sunmi.scanner.Setting_cmd");
            intent.putExtra("cmd_data", cmd);
            //是线程池,必须判
            if (SunmiScanner.this.context == null)
                return;
            SunmiScanner.this.context.sendBroadcast(intent);

            long curTime = System.currentTimeMillis();
            while (isWaittingResponse){
                //没有返回值的命令要等50ms，才能够发送下一条指令
                if (!isHaveResponse){
                    if (System.currentTimeMillis() - curTime > SerialCmd.MIN_SEND_TIME){
                        Log.i(TAG, "SendThread:NLS cmd has no response");
                        return;
                    }
                }
                else if (System.currentTimeMillis() - curTime > SerialCmd.MAX_RESPONSE_TIME){
                    Log.i(TAG,"SendThread:response timeout");
                    for (Map.Entry<String, OnScannerListener> entry : scannerListenerHashMap.entrySet())
                        entry.getValue().onResponseTimeout();
                    return;
                }
            }
            Log.i(TAG,"SendThread:response suc");
        }
    }

    //校验和
    private void lrcCheckSum(byte[] content) {
        int len = content.length;
        int crc = 0;
        for (int l = 0; l < len - 2; l++) {
            crc += content[l] & 0xFF;
        }
        crc = ~crc + 1;
        content[len - 2] = (byte) ((crc >> 8) & 0xFF);
        content[len - 1] = (byte) (crc & 0xFF);
    }
}
package com.acbr.sunmi_printer;

//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class Printer {


    private IWoyouService woyouService;

    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };

    ICallback callback = new ICallback.Stub() {

        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {
            if(!isSuccess){
                Log.d("Printer", "Callback Error");
            }
        }

        @Override
        public void onReturnString(final String value) throws RemoteException {
            String retV = value;
        }

        @Override
        public void onRaiseException(int code, final String msg){
                String err = msg;
                //throws RemoteException {
        }

        @Override
        public void onPrintResult(int code, String msg) throws RemoteException {
            String retM = msg;
        }
    };

    public Printer(Context context) {

        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        context.startService(intent);
        context.bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    public void PrintTeste() {

        try {
            String chaveExemploCodigoBarras = "35220211111111111111591234567890014465643660";
            String qrCodeExemplo = "CFe35220211111111111111591234567890014465643660|20220217122532|9.00||QZMi8t0Jvvz23apPJ/X/ToR469DUZOa2yJ+t3h/rMjIlG5iaW/8c1ZN6IVPHskVRcET1myzMzAp9Gb1qTLkK7caOgHiaeFt7c5X5oiiPv+5c04lMBrp0CvvI93yhUGCalUhrywacCV3zwc7riL1/4//siufYETGVL0UdcTtWnZp4SMDrWHvPyqdR5Ep5f8LI66unzNq7ZHyWaZXOEgqa/VRrG78P1xqVV1PGj/enidxgRisIeJM7cFZp8JNhP1tdTr2kJTuOOHvr3xK3t4ibd1h5WLVeoxkUluOwKrQPEbSFEFRdQzgl/JMv+H/n5UWHfLyQbewhHI1bOXVAM790HQ==";
            woyouService.printText("QRCODE 01 \n\n", callback);
            woyouService.printQRCode(qrCodeExemplo, 4, 1, callback);
            woyouService.printText("\n\n\n", callback);
            woyouService.printText("QRCODE 02 \n\n", callback);
            woyouService.printQRCode(chaveExemploCodigoBarras, 4, 1, callback);
            woyouService.printText("\n\n\n\n", callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public String getServiceVersion() {
        try {
            return woyouService.getServiceVersion();
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            return "";
            }
    }

    public void printerInit() {
        try {
            woyouService.printerInit(callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void printerSelfChecking() {
        try {
            woyouService.printerSelfChecking(callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public String getPrinterSerialNo() {
        try {
            return woyouService.getPrinterSerialNo();
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            return "";
            }
    }

    public String getPrinterVersion() {
        try {
            return woyouService.getPrinterVersion();
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            return "";
            }
    }

    public String getPrinterModal() {
        try {
            return woyouService.getPrinterModal();
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            return "";
            }
    }

    public void getPrintedLength() {
        try {
            woyouService.getPrintedLength();
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void lineWrap(int n) {
        try {
            woyouService.lineWrap(n, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void sendRAWData(byte[] data) {
        try {
            woyouService.sendRAWData(data, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void setAlignment(int alignment) {
        try {
            woyouService.setAlignment(alignment, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void setFontName(String typeface) {
        try {
            woyouService.setFontName(typeface, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void setFontSize(float fontsize) {
        try {
            woyouService.setFontSize(fontsize, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void printText(String text) {
        try {
            woyouService.printText(text, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void printTextLF(String text) {
        try {
            woyouService.printText(text+"\n", callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void printTextWithFont(java.lang.String text, java.lang.String typeface, float fontsize) {
        try {
            woyouService.printTextWithFont(text, typeface, fontsize, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void printColumnsText(java.lang.String[] colsTextArr, int[] colsWidthArr, int[] colsAlign) {
        try {
            woyouService.printColumnsText(colsTextArr, colsWidthArr, colsAlign, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void printBitmap(android.graphics.Bitmap bitmap) {
        try {
            woyouService.printBitmap(bitmap, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void printBarCode(java.lang.String data, int symbology, int height, int width, int textposition) {
        try {
            woyouService.printBarCode(data, symbology, height, width, textposition, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void printQRCode(java.lang.String data, int modulesize, int errorlevel) {
        try {
            woyouService.printQRCode(data, modulesize, errorlevel, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void printOriginalText(java.lang.String text) {
        try {
            woyouService.printOriginalText(text, callback);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void commitPrinterBuffer() {
        try {
            woyouService.commitPrinterBuffer();
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void enterPrinterBuffer(boolean clean) {
        try {
            woyouService.enterPrinterBuffer(clean);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void exitPrinterBuffer(boolean commit) {
        try {
            woyouService.exitPrinterBuffer(commit);
            }
        catch(Exception ex){
            Log.d("Printer", "Error "+ ex.getMessage());
            }
    }

    public void cutPaper() throws RemoteException {
        woyouService.cutPaper(callback);
    }

    public int getCutPaperTimes() throws RemoteException {
        return woyouService.getCutPaperTimes();
    }

    public void openDrawer() throws RemoteException {
        woyouService.openDrawer(callback);
    }

    public int getOpenDrawerTimes() throws RemoteException {
        return woyouService.getOpenDrawerTimes();
    }

    public int getPrinterMode() throws RemoteException {
        return woyouService.getPrinterMode();
    }

    public int getPrinterBBMDistance() throws RemoteException {
        return woyouService.getPrinterBBMDistance();
    }

    public int updatePrinterState() throws RemoteException {
        return woyouService.updatePrinterState();
    }

    public boolean getDrawerStatus() throws RemoteException {
        return woyouService.getDrawerStatus();
    }
}
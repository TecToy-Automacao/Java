// IScanInterface.aidl
package com.sunmi.scanner;

// Declare quaisquer tipos não padrão aqui com instruções de importação

interface IScanInterface {
 /**
     * Gatilho para iniciar e parar a digitalização do código
     * key.getAction()==KeyEvent.ACTION_UP Gatilho para ler o código
     * key.getAction()==KeyEvent.ACTION_DWON Gatilho para interromper a digitalização do código
     */
    void sendKeyEvent(in KeyEvent key);
    /**
     *  Gatilho para ler o código
     */
    void scan();
    /**
     * Gatilho para interromper a digitalização do código
     */
    void stop();
    /**
     * Obtenha o tipo de doca de varredura
     * 100-->NONE
     * 101-->P2Lite
     * 102-->l2-newland
     * 103-->l2-zabra
     */
    int getScannerModel();
}

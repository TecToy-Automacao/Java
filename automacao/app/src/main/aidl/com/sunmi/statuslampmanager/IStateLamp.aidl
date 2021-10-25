// IStateLamp.aidl
package com.sunmi.statuslampmanager;

// Declare any non-default types here with import statements
import java.lang.String;

interface IStateLamp {
    /**
     * Função: controlar uma única luz de alarme
     * parâmetro：
     * [in]status      Estado，0 brilhante，1 off
     * [in]lamp         Luz LED，parâmetro：
     *                      "Led-1" Vermelha
     *                      "Led-2" Verde
     *                      "Led-3" Azul
     *                      "Led-4" Nada
     *                      "Led-5" Nada
     *                      "Led-6" Nada
     * valor de retorno：null
     */
    void controlLamp(in int status, in String lamp);
    /**
     * 功能：控制单个报警灯循环 显示
     * 参数：
     * [in]status       状态，0开始循环，1停止循环
     * [in]lightTime    报警灯亮时间，单位：毫秒(ms)
         * [in]putoutTime   报警灯灭时间，单位：毫秒(ms)
     * [in]lamp         Led灯，参数：
     *                      "Led-1"
     *                      "Led-2"
     *                      "Led-3"
     *                      "Led-4"
     *                      "Led-5"
     *                      "Led-6"
     * 返回值：无
     */
    void controlLampForLoop(in int status, in long lightTime, in long putoutTime, in String lamp);

    /**
     * 功能：关闭所有报警灯
     */
     void closeAllLamp();
}

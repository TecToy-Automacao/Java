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
     * 功能：Controlar uma única exibição de ciclo da lâmpada de alarme
     * 参数：
     * [in]status       Estado, 0 inicia o ciclo, 1 interrompe o ciclo
     * [in]lightTime    Tempo aceso da luz do alarme, unidade: milissegundos (ms)
         * [in]putoutTime   Tempo de desligamento da lâmpada do alarme, unidade: milissegundos (ms)
     * [in]lamp         Led灯，parâmetro：
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

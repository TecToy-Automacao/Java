package br.com.tectoy.tectoysunmi.activity;

/**
 * created by mayflower on 2020.6.23
 * get me at jiangli@sunmi.com
 * 这里封装了基本的语法和常用指令，别的详见指令集文档
 */

public class SerialCmd {
    //命令格式：Prefix Storage Tag SubTag {Data} [, SubTag {Data}] [; Tag SubTag {Data}] […] ; Suffix
    public static final String PREFIX_HEX = "7E0130303030"; //~<SOH>0000
    public static final String STORAGE_EVER_HEX = "40";    //@ 是设置永久有效
    public static final String STORAGE_TEMP_HEX = "23";    //# 则是临时设置，断电后失效
    //tag、subtag、data，这三个合起来就是具体的命令；多个命令可以合起来发，也可以挨个发（需间隔50ms或收到返回值后发）
    public static final String SUFFIX_HEX = "3B03";         //;<ETX>

    //查询命令，属于subtag
    public static final String SUBTAG_QUERY_CURRENT_HEX = "2A";     //查询当前设置
    public static final String SUBTAG_QUERY_FACTORY_HEX = "26";     //查询出厂默认设置
    public static final String SUBTAG_QUERY_RANGE_HEX = "5E";       //查询取值范围

    //应答数据格式：<STX><SOH>0000 Storage cmd [Data]
    public static final String RES_PREFIX_HEX = "020130303030";   //<STX><SOH>0000
    public static final String RES_ACK_HEX = "06";     //操作成功
    public static final String RES_NAK_HEX = "15";     //数据的值不在支持范围
    public static final String RES_ENQ_HEX = "05";     //设置类别或功能项不存在

    //发送和应答间隔
    public static final int MIN_SEND_TIME = 50;        //每个命令之间最小间隔：50ms
    public static final int MAX_RESPONSE_TIME = 5000;   //每条指令正常应答时间为500ms,但多条指令组合会导致应答时间翻倍,这里就用5000ms吧

    //具体指令们
    public static final String NLS_SETUPE1 = "#SETUPE1";    //进入配置模式
    public static final String NLS_SETUPE0 = "#SETUPE0";    //退出配置模式

    public static final String NLS_KEY_DOWN = "#SCNTRG1";   //模拟按键按下，开启扫码
    public static final String NLS_KEY_UP = "#SCNTRG0";     //模拟按键松开，关闭扫码

    public static final String NLS_RESTORE = "@FACDEF"; //恢复出厂(慎用)

    /**
     * 设置识读偏好,出厂默认是扫屏模式(曝光等级低,LV5),可以改成纸质模式(曝光等级高,LV0)
     */
    public static String setExposure(int level){
        return "@EXPLVL" + level;
    }

    /**
     * 以下是其他常用指令，本扫码器独有
     * 设置扫码模式
     * @param mode 0 电平触发模式，手动读码（发1B31才读一次码）
     *             1 自动触发模式，自动读码
     * @param wait 0 亮灯后等待时间默认值，手动读码模式默认60000ms，自动读码模式默认1000ms
     *             $ 等待时间 ms
     * @param delay 0 同码间隔时间默认值(异码间隔默认200，一般不需要改)，自动读码默认1000ms，只对自动读码有效
     *              $ 扫码间隔 ms
     */
    public static String setScanMode(int mode, int wait, int delay){
        switch (mode) {
            case 0:
                return ("@SCNMOD0;ORTSET"+(wait>0 ? wait : 60000));
            case 1:
                return ("@SCNMOD2;ORTSET"+(wait>0 ? wait : 1000)+";" + "RRDDUR"+(delay>=200 ? delay : 1000));
            default:
                return null;
        }
    }

    /**
     * 查询当前配置
     * @return
     */
    public static String queryScanMode(){
        return "#SCNMOD*;ORTSET*;RRDDUR*";
    }
}

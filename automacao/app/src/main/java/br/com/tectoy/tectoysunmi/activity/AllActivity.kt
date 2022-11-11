package br.com.tectoy.tectoysunmi.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View

import java.io.UnsupportedEncodingException

import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityAllBinding
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil
import br.com.tectoy.tectoysunmi.utils.BytesUtil
import br.com.tectoy.tectoysunmi.utils.ESCUtil
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint


/**
 *
 * Imprimir todo o conteúdo por esc cmd
 * Código em java
 *   @author Geovani Santos
 *   
 * Código em Kotlin
 *   @author Felipe Peres
 */
class AllActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding : ActivityAllBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle(R.string.all_title)
        setBack()

        binding.multiOne.setOnClickListener{this}
        binding.multiTwo.setOnClickListener{this}
        binding.multiThree.setOnClickListener{this}
        binding.multiFour.setOnClickListener{this}
        binding.multiFive.setOnClickListener{this}
        binding.multiSix.setOnClickListener{this}
    }

    /**
     *  Imprimir todos os comandos ESC suportados pela impressora sunmi
     *  Para verificar se a impressora funciona bem
     */
    fun onPrint(){
        var rv : ByteArray? = BytesUtil.customData()
        val head = BitmapFactory.decodeResource(resources, R.drawable.sunmi)
        //
        //Imprimir bitmap raster(print raster bitmap ——normal)
        rv = BytesUtil.byteMerger(rv, ESCUtil.printBitmap(head, 0))
        //
        //Imprimir bitmap raster  倍宽  (print raster bitmap ——double width)
        rv = BytesUtil.byteMerger(rv, ESCUtil.printBitmap(head, 1))
        //
        //打印光栅位图  倍高    (print raster bitmap ——double height)
        rv = BytesUtil.byteMerger(rv, ESCUtil.printBitmap(head, 2))
        //
        //打印光栅位图  倍宽倍高   (print raster bitmap ——double width and height)
        rv = BytesUtil.byteMerger(rv, ESCUtil.printBitmap(head, 3))
        //
        //Selecione o comando de bitmap  8点单密度 （print Bitmap mode)
        rv = BytesUtil.byteMerger(rv, ESCUtil.selectBitmap(head, 0))
        //
        //选择位图指令  8点双密度
        rv = BytesUtil.byteMerger(rv, ESCUtil.selectBitmap(head, 1))
        //
        //选择位图指令  24点单密度
        rv = BytesUtil.byteMerger(rv, ESCUtil.selectBitmap(head, 32))
        //
        //选择位图指令  24点双密度
        rv = BytesUtil.byteMerger(rv, ESCUtil.selectBitmap(head, 33))
        //
        //之后将输出可显示的ascii码及制表符
        rv = BytesUtil.byteMerger(rv, BytesUtil.wordData())

        val ascii = ByteArray(96)
        ascii.forEach { 0x20 + it }

        ascii[95] = 0x0A
        rv = BytesUtil.byteMerger(rv, ascii)
        val tabs = CharArray(116)
        tabs.forEach { 0x2500 + it.code.toByte() }
        val test = String(tabs)

        try{
            rv = BytesUtil.byteMerger(rv, test.toByteArray(charset("gb18030")))
            rv = BytesUtil.byteMerger(rv, ByteArray(1){0x0A})
        }catch(e : UnsupportedEncodingException){
            e.printStackTrace()
        }

        if(BluetoothUtil.isBlueToothPrinter) {
            BluetoothUtil.sendData(rv)
        }
        else{
            TectoySunmiPrint.getInstance().sendRawData(rv)
        }
    }

    override fun onClick(v : View?){
        lateinit var rv : ByteArray
        when(v?.id){
            binding.multiOne.id   -> rv = BytesUtil.getBaiduTestBytes()
            binding.multiTwo.id   -> rv = BytesUtil.getMeituanBill()
            binding.multiThree.id -> rv = BytesUtil.getErlmoData()
            binding.multiFour.id  -> rv = BytesUtil.getKoubeiData()
            binding.multiFive.id  -> rv = ESCUtil.printBitmap(BytesUtil.initBlackBlock(384))
            binding.multiSix.id   -> rv = ESCUtil.printBitmap(BytesUtil.initBlackBlock(800, 384))
        }
        if(BluetoothUtil.isBlueToothPrinter){
            BluetoothUtil.sendData(rv)
        }
        else{
            TectoySunmiPrint.getInstance().sendRawData(rv)
        }
    }

}


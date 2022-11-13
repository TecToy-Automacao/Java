package br.com.tectoy.tectoysunmi.activity


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View

import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityBitmapBinding
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint
import sunmi.sunmiui.dialog.DialogCreater

/**
 * Exemplo de impressão de uma imagem
 *
 * @author Felipe Peres
 */
class BitmapActivity : BaseActivity(){
    var bitmap: Bitmap? = null
    lateinit var binding: ActivityBitmapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle(R.string.bitmap_title)
        setBack()

        binding.picAlign.setOnClickListener{
            val pos = arrayOf<String>(resources.getString(R.string.align_left), resources.getString(R.string.align_mid), resources.getString(R.string.align_right))
            val listDialog = DialogCreater.createListDialog(this@BitmapActivity,
                resources.getString(R.string.align_form),
                resources.getString(R.string.cancel),
                pos)
            listDialog.setItemClickListener {
                binding.picAlignInfo.text = pos[it]
                TectoySunmiPrint.getInstance().setAlign(it)
                listDialog.cancel()
            }
            listDialog.show()
        }

        binding.cutPaperInfo.setOnClickListener{
            val cut = arrayOf<String>("Sim", "não")
            val listDialog = DialogCreater.createListDialog(this@BitmapActivity,
                resources.getString(R.string.error_qrcode),
                resources.getString(R.string.cancel),
                cut)
            listDialog.setItemClickListener {
                binding.cutPaperInfo.text = cut[it]
                listDialog.cancel()
            }
            listDialog.show()
        }

        val options = BitmapFactory.Options()
        options.inTargetDensity = 160
        options.inDensity = 160
        if(bitmap == null){
            bitmap = scaleImage(BitmapFactory.decodeResource(resources, R.drawable.test, options))
        }

        binding.bitmapImageview.setImageDrawable(BitmapDrawable(bitmap))

        binding.picStyle.visibility = View.GONE
    }

    /**
     * Scaled image width is an integer multiple of 8 and can be ignored
     */
    private fun scaleImage(bitmap : Bitmap) : Bitmap{
        val width = bitmap.width
        val height = bitmap.height
        // 设置想要的大小
        val newWidth = (width / 8 + 1) * 8
        // 计算缩放比例
        val scaleWidth = newWidth.toFloat() / width
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, 1f)
        // 得到新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    fun onClick(v : View?){
        if(binding.cutPaperInfo.text == "Não"){
            TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
            TectoySunmiPrint.getInstance().printText("Imagem\n")
            TectoySunmiPrint.getInstance().printText("--------------------------------\n")
            TectoySunmiPrint.getInstance().printBitmap(bitmap)
            TectoySunmiPrint.getInstance().print3Line()
        }else{
            TectoySunmiPrint.getInstance().setAlign(TectoySunmiPrint.Alignment_CENTER)
            TectoySunmiPrint.getInstance().printText("Imagem\n")
            TectoySunmiPrint.getInstance().printText("--------------------------------\n")
            TectoySunmiPrint.getInstance().printBitmap(bitmap)
            TectoySunmiPrint.getInstance().print3Line()
            TectoySunmiPrint.getInstance().cutpaper()
        }
    }
}
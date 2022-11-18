@file:Suppress("UNREACHABLE_CODE")

package br.com.tectoy.tectoysunmi.activity

import android.os.Bundle
import android.os.RemoteException
import android.view.Menu
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityBufferBinding
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint
import com.sunmi.peripheral.printer.InnerResultCallback

/**
 * Exemplo de impressão de transação
 * A impressão de transações permite que os desenvolvedores obtenham os resultados reais da impressão,
 * Portanto, ele só pode ser chamado por meio da API não suportada.
 *
 * Atualmente, apenas o modelo V1 não é compatível com a impressão de transações
 * @author Geovani Santos
 */

class BufferActivity: BaseActivity() {
    private var mark: Boolean = false
    lateinit var mTextView: TextView

    private lateinit var binding: ActivityBufferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBufferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle("Transaction")
        setBack()
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    fun initView(){
        mTextView = findViewById(R.id.buffer_info)

        binding.bufferSet.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                if(mark){
                    mark = false
                    v?.setBackgroundColor(resources.getColor(R.color.text))
                    (v as TextView).setText(R.string.enter_work)

                }else{
                    mark = true
                    v?.setBackgroundColor(resources.getColor(R.color.gray))
                    (v as TextView).setText(R.string.exit_work)
                }
            }
        })

        binding.bufferPrint.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                if (mark){
                    mTextView.setText(R.string.start_work)
                    TectoySunmiPrint.getInstance().printTrans(applicationContext, mCallBack)
                }else{
                    mTextView.setText(R.string.start_work_low)
                    TectoySunmiPrint.getInstance().printExample(applicationContext)
                }
            }
        })
    }

    var mCallBack: InnerResultCallback =  (object : InnerResultCallback() {
        override fun onRunResult(isSuccess: Boolean) {
            throw RemoteException()
        }

        override fun onReturnString(result: String?) {
            throw RemoteException()
        }

        override fun onRaiseException(code: Int, msg: String?) {
            throw RemoteException()
        }

        override fun onPrintResult(code: Int, msg: String?) {
            throw RemoteException()
            val res: Int = code
            runOnUiThread(object : Runnable{
                override fun run() {
                    if (res == 0){
                        mTextView.setText(R.string.over_work)
                        //TODO Follow-up after successful
                    }else{
                        mTextView.setText(R.string.error_work)
                        //TODO Follow-up after failed, such as reprint
                    }
                }
            })
        }
    })
}
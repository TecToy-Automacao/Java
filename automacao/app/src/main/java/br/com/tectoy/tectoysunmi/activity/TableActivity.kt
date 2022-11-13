package br.com.tectoy.tectoysunmi.activity;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner


import java.util.LinkedList;

import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityTableBinding
import br.com.tectoy.tectoysunmi.utils.BluetoothUtil
import br.com.tectoy.tectoysunmi.utils.BytesUtil
import br.com.tectoy.tectoysunmi.utils.ESCUtil
import br.com.tectoy.tectoysunmi.utils.TectoySunmiPrint
import sunmi.sunmiui.button.ButtonRectangular

class TableActivity : BaseActivity(){
    lateinit var mListView:ListView
    lateinit var mTextView:TextView
    lateinit var footView:ButtonRectangular
    lateinit var ta:TableAdapter
    protected lateinit var datalist:LinkedList<TableItem>

    private lateinit var binding: ActivityTableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setMyTitle(R.string.tab_title)
        setBack()

        mListView = binding.tableList
        mTextView = binding.tableTips

        initListView()
    }

    private fun initListView() {
        footView = ButtonRectangular(this)
        footView.setTitleText(resources.getString(R.string.add_line))
        footView.setTextColorEnabled(R.color.black)
        footView.setOnClickListener {
            addOneData(datalist)
            ta.notifyDataSetChanged()
        }
        mListView.addFooterView(footView)
        datalist = LinkedList()
        addOneData(datalist)
        ta = TableAdapter()
        mListView.adapter = ta
    }

    fun onClick(v:View?){
        for (tableItem:TableItem in datalist){
            TectoySunmiPrint.getInstance().printTable(tableItem.text,
                tableItem.width, tableItem.align)
        }
        TectoySunmiPrint.getInstance().feedPaper()
    }

    private fun addOneData(data: LinkedList<TableItem>) {
        val ti = TableItem();
        data.add(ti)
    }

    class ViewHolder : View.OnFocusChangeListener, AdapterView.OnItemSelectedListener{

        lateinit var mText:TextView

        lateinit var mText1:EditText
        lateinit var mText2:EditText
        lateinit var mText3:EditText

        lateinit var width1:EditText
        lateinit var width2:EditText
        lateinit var width3:EditText

        lateinit var align1:AppCompatSpinner
        lateinit var align2:AppCompatSpinner
        lateinit var align3:AppCompatSpinner

        lateinit var view:EditText
        var line:Int = 0

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            Log.d("Geovani",v?.id.toString()+">>"+hasFocus)
            if(hasFocus){
                view = v as EditText
                return
            }
            val ti:TableItem =
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            TODO("Not yet implemented")
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("Not yet implemented")
        }

    }

    //Mocados
    class TableAdapter : BaseAdapter(){
        override fun getCount(): Int {
            TODO("Not yet implemented")
        }

        override fun getItem(p0: Int): Any {
            TODO("Not yet implemented")
        }

        override fun getItemId(p0: Int): Long {
            TODO("Not yet implemented")
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            TODO("Not yet implemented")
        }
    }
    //https://kotlinlang.org/docs/arrays.html#primitive-type-arrays
    protected data class TableItem(var text:Array<String> = arrayOf("test", "test", "test"),
                                 var width:IntArray = intArrayOf(1, 1, 1),
                                 var align:IntArray = intArrayOf(0, 0, 0)) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TableItem

            if (!text.contentEquals(other.text)) return false
            if (!width.contentEquals(other.width)) return false
            if (!align.contentEquals(other.align)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = text.contentHashCode()
            result = 31 * result + width.contentHashCode()
            result = 31 * result + align.contentHashCode()
            return result
        }

    }

    //Mocados
}
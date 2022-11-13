package br.com.tectoy.tectoysunmi.activity;

import android.content.Context
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

import androidx.appcompat.widget.AppCompatSpinner


import java.util.LinkedList;

import br.com.tectoy.tectoysunmi.R
import br.com.tectoy.tectoysunmi.databinding.ActivityTableBinding
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
        ta = TableAdapter(this)
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

    //https://kotlinlang.org/docs/nested-classes.html#inner-classes
    inner class TableAdapter(context: Context) : BaseAdapter(){
        private var mInflator: LayoutInflater
            get() {
                return mInflator
            }

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        inner class ViewHolder() : View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {
            lateinit var mText:TextView

            var mText1:EditText? = null
            var mText2:EditText? = null
            var mText3:EditText? = null

            var width1:EditText? = null
            var width2:EditText? = null
            var width3:EditText? = null

            var align1:AppCompatSpinner? = null
            var align2:AppCompatSpinner? = null
            var align3:AppCompatSpinner? = null

            lateinit var view:EditText
            var line:Int = 0


            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                Log.d("Geovani",v?.id.toString()+">>"+hasFocus)
                if(hasFocus){
                    view = v as EditText
                    return
                }
                val ti:TableItem = datalist[line]
                when(v?.id){
                    R.id.it_text3  -> {  (ti.text)[2] = ((v as EditText).text.toString())  }
                    R.id.it_text2  -> {  (ti.text)[1] = ((v as EditText).text.toString())  }
                    R.id.it_text1  -> {  (ti.text)[0] = ((v as EditText).text.toString())  }

                    R.id.it_width3 -> {  (ti.width)[2] = ((v as EditText).text.toString()).toInt()  }
                    R.id.it_width2 -> {  (ti.width)[1] = ((v as EditText).text.toString()).toInt()  }
                    R.id.it_width1 -> {  (ti.width)[0] = ((v as EditText).text.toString()).toInt()  }
                    else           -> return
                }
            }

            fun setCallback(){
                if (   mText1 == null || mText2 == null || mText3 == null
                    || width1 == null || width2 == null || width3 == null
                    || align1 == null || align2 == null || align3 == null) {
                    return
                }
                mText1?.onFocusChangeListener = this
                mText2?.onFocusChangeListener = this
                mText3?.onFocusChangeListener = this

                width1?.onFocusChangeListener = this
                width2?.onFocusChangeListener = this
                width3?.onFocusChangeListener = this

                align1?.onFocusChangeListener = this
                align2?.onFocusChangeListener = this
                align3?.onFocusChangeListener = this
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var ti:TableItem = datalist[line]
                when(parent?.id){
                    R.id.it_align3 -> {(ti.align)[2] = position}
                    R.id.it_align2 -> {(ti.align)[1] = position}
                    R.id.it_align1 -> {(ti.align)[0] = position}
                    else           -> return
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        override fun getCount(): Int {
            return datalist.size
        }

        override fun getItem(position: Int): Int {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        /*
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val vh: ListRowHolder
        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.list_row, parent, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        vh.label.text = sList[position]
        return view
    }
         */

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var _position = position
            val view: View?
            val vh: ViewHolder
            if(convertView == null){
                view = this.mInflator.inflate(R.layout.item_table, null)
                vh = ViewHolder()
                if(convertView!=null){
                    vh.mText = convertView.findViewById(R.id.it_title)

                    vh.mText1 = convertView.findViewById(R.id.it_text1)
                    vh.mText2 = convertView.findViewById(R.id.it_text2)
                    vh.mText3 = convertView.findViewById(R.id.it_text3)

                    vh.width1 = convertView.findViewById(R.id.it_width1)
                    vh.width2 = convertView.findViewById(R.id.it_width2)
                    vh.width3 = convertView.findViewById(R.id.it_width3)

                    vh.align1 = convertView.findViewById(R.id.it_align1)
                    vh.align2 = convertView.findViewById(R.id.it_align2)
                    vh.align3 = convertView.findViewById(R.id.it_align3)

                    vh.setCallback()
                    convertView.tag = vh
                }

            } else {
                vh = convertView.tag as ViewHolder
            }
            vh.line = _position
            vh.mText.text = "Row." + (++_position)
            vh.view.requestFocus()
            return convertView
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
}
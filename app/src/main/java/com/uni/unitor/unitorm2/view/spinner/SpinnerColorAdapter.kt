package com.uni.unitor.unitorm2.view.spinner

import com.uni.unitor.unitorm2.R
import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter


class SpinnerColordapter(internal var context: Context, internal var data: List<String>?, internal var coclor: List<Int>?) : BaseAdapter() {
    internal var inflater: LayoutInflater

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.color_spinner_normar, parent, false)
        }

        if (data != null && coclor != null) {
            //데이터세팅
            val text = data!![position]
            val color = coclor!![position]
            (convertView!!.findViewById(R.id.txt_color_name) as TextView).text = text
            (convertView!!.findViewById(R.id.color_frame) as FrameLayout).setBackgroundColor(color)
        }

        return convertView
    }

    override fun getCount(): Int {
        return if (data != null) data!!.size else 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.color_spinner_down, parent, false)
        }

        //데이터세팅
        val text = data!![position]
        val color = coclor!![position]
        (convertView!!.findViewById(R.id.txt_color_name) as TextView).text = text
        (convertView!!.findViewById(R.id.color_frame) as FrameLayout).setBackgroundColor(color)

        return convertView
    }

    override fun getItem(position: Int): Array<Any> {
        return arrayOf(data!![position], coclor!![position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}
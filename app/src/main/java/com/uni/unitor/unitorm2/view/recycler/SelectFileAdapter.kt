package com.uni.unitor.unitorm2.view.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.uni.unitor.unitorm2.R
import android.graphics.drawable.Drawable
import android.widget.TextView
import android.widget.BaseAdapter
import com.uni.unitor.unitorm2.layout.LayoutKey


class SelectFileAdapter(type:String) : BaseAdapter() {

    private val listViewList = ArrayList<SelectFileItem>()
    private val type = type
    private var title: String? = null
    private var icon: Drawable? = null
    private var path: String? = null

    override fun getCount(): Int {
        return listViewList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val context = parent.context

        if (convertView == null) {
            when (type) {
                LayoutKey.DIALOG_FILEEX_SOUND -> {
                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    convertView = inflater.inflate(R.layout.item_list_select_f, parent, false)
                }
                LayoutKey.DIALOG_FILEEX_UNIPACK -> {
                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    convertView = inflater.inflate(R.layout.item_list_select_u, parent, false)
                }
            }
        }

        val nameView = convertView!!.findViewById(R.id.FragDial_file_name_select) as TextView
        val pathView = convertView.findViewById(R.id.FragDial_file_path_select) as TextView
        val icoView = convertView.findViewById(R.id.FragDial_file_Image_select) as ImageView

        val listItem = listViewList[position]

        title = listItem.titlef
        path = listItem.pathf
        icon = listItem.iconf

        nameView.text = title
        pathView.text = path
        icoView.setImageDrawable(icon)

        return convertView
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): SelectFileItem {
        return listViewList[position]
    }

    fun addItem(listItem: SelectFileItem) {
        listViewList.add(listItem)
        DataChange()
    }

    fun remove(position: Int) {
        listViewList.removeAt(position)
        DataChange()
    }

    fun clearItem() {
        listViewList.clear()
        DataChange()
    }

    fun DataChange() {
        this.notifyDataSetChanged()
    }
}
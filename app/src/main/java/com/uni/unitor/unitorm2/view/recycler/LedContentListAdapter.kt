package com.uni.unitor.unitorm2.view.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uni.unitor.unitorm2.R

class LedContentListAdapter : RecyclerView.Adapter<LedContentListAdapter.ViewHolder>() {

    private val contentItems: MutableList<LedContentListItem>

    init {
        contentItems = ArrayList<LedContentListItem>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_source, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = contentItems[position]
        holder.textLine.setText(item.line)
        holder.textCon.setText(item.contents)
    }

    override fun getItemCount(): Int {
        return contentItems.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var textLine: TextView
        var textCon: TextView

        init {
            textLine = view.findViewById(R.id.list_file_title)
            textCon = view.findViewById(R.id.list_file_path)
        }
    }

    fun removeItem(position: Int) {
        try {
            contentItems.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }

    fun addItem(item: LedContentListItem) {
        contentItems.add(item)
        notifyItemInserted(contentItems.size)
    }

    fun getAllItem(): MutableList<LedContentListItem> {
        return contentItems
    }

    fun getItem(position: Int): LedContentListItem {
        return contentItems[position]
    }

    fun clearItem() {
        val count = contentItems.size
        contentItems.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun dataChanged(p:Int) {
        notifyItemChanged(p)
    }
}
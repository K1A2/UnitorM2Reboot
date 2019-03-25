package com.uni.unitor.unitorm2.view.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uni.unitor.unitorm2.R

/**파일 목록 보여줄때 사용하는 리사이클러뷰 어댑터**/

class FileListAdapter : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    private val fileItems: MutableList<FileListItem>

    init {
        fileItems = ArrayList<FileListItem>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_source, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = fileItems[position]
        holder.textName.setText(item.fname)
        holder.textPath.setText(item.fpath)
    }

    override fun getItemCount(): Int {
        return fileItems.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var textName: TextView
        var textPath: TextView

        init {
            textName = view.findViewById(R.id.list_file_title)
            textPath = view.findViewById(R.id.list_file_path)
        }
    }

    fun removeItem(position: Int) {
        try {
            fileItems.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }

    fun addItem(item: FileListItem) {
        fileItems.add(item)
        notifyItemInserted(fileItems.size)
    }

    fun getAllItem(): MutableList<FileListItem> {
        return fileItems
    }

    fun getItem(position: Int): FileListItem {
        return fileItems[position]
    }

    fun clearItem() {
        val count = fileItems.size
        fileItems.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun dataChanged(p:Int) {
        notifyItemChanged(p)
    }
}
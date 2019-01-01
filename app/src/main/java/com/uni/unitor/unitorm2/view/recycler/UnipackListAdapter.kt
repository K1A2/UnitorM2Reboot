package com.uni.unitor.unitorm2.view.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uni.unitor.unitorm2.R

class UnipackListAdapter : RecyclerView.Adapter<UnipackListAdapter.ViewHolder>() {

    private val unipackExplorerItems: MutableList<UnipackListItem>

    init {
        unipackExplorerItems = ArrayList<UnipackListItem>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_unipack, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = unipackExplorerItems[position]
        holder.textproducer.setText(item.fproducer)
        holder.textName.setText(item.fname)
        holder.textPath.setText(item.fpath)
        holder.textChain.setText(item.fchain)
    }

    override fun getItemCount(): Int {
        return unipackExplorerItems.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var textproducer: TextView
        var textName: TextView
        var textPath: TextView
        var textChain: TextView

        init {

            textproducer = view.findViewById(R.id.list_unipack_producer)
            textName = view.findViewById(R.id.list_unipack_title)
            textPath = view.findViewById(R.id.list_unipack_path)
            textChain = view.findViewById(R.id.list_unipack_chain)
        }
    }

    fun removeItem(position: Int) {
        try {
            unipackExplorerItems.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }

    fun addItem(item: UnipackListItem) {
        unipackExplorerItems.add(item)
        notifyItemInserted(unipackExplorerItems.size)
    }

    fun getItem(position: Int): UnipackListItem {
        return unipackExplorerItems[position]
    }

    fun clearItem() {
        val count = unipackExplorerItems.size
        unipackExplorerItems.clear()
        notifyItemRangeRemoved(0, count)
    }
}
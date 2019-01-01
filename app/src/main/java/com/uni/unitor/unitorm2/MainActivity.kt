package com.uni.unitor.unitorm2

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.getbase.floatingactionbutton.FloatingActionButton
import com.uni.unitor.unitorm2.File.UnipackIO
import com.uni.unitor.unitorm2.view.recycler.UnipackListAdapter
import com.uni.unitor.unitorm2.view.recycler.UnipackListItem
import com.uni.unitor.unitorm2.view.recycler.listener.RecyclerItemClickListener

class MainActivity : AppCompatActivity() {

    private lateinit var recycler_unipack:RecyclerView
    private lateinit var floating_new: FloatingActionButton
    private lateinit var floating_import: FloatingActionButton
    private lateinit var floating_setting: FloatingActionButton

    private val unipackAdapter:UnipackListAdapter = UnipackListAdapter()
    private lateinit var unipackIO:UnipackIO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        if (supportActionBar != null) supportActionBar!!.hide()

        unipackIO = UnipackIO(this)

        recycler_unipack = findViewById(R.id.recycle_main_unipack) as RecyclerView
        floating_new = findViewById(R.id.fab_new) as FloatingActionButton
        floating_import = findViewById(R.id.fab_import) as FloatingActionButton
        floating_setting = findViewById(R.id.fab_setting) as FloatingActionButton

        recycler_unipack.layoutManager = LinearLayoutManager(this)
        recycler_unipack.itemAnimator = DefaultItemAnimator()

        recycler_unipack.adapter = unipackAdapter
        showUnipack()

        recycler_unipack.addOnItemTouchListener(RecyclerItemClickListener(this, recycler_unipack, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClicked(view: View, position: Int) {
                val item: UnipackListItem = unipackAdapter.getItem(position)
                //startEdit(item.fname!!, item.fproducer!!, item.fchain!!, item.fpath!!)
            }

            override fun onLongItemClicked(view: View?, position: Int) {
                val layout: RelativeLayout = View.inflate(this@MainActivity, R.layout.dialog_delete, null) as RelativeLayout
                val unipackListItem = unipackAdapter.getItem(position)
                val delete = AlertDialog.Builder(this@MainActivity)
                delete.setView(layout)
                layout.findViewById<TextView>(R.id.dialog_delete_title).text = String.format(getString(R.string.alert_title_dunipack), unipackListItem.fname)
                layout.findViewById<TextView>(R.id.dialog_delete_sub).text = String.format(getString(R.string.alert_message_dunipack), unipackListItem.fname)
//                delete.setNegativeButton(getString(R.string.alert_button_dcancel), null)
//                delete.setPositiveButton(getString(R.string.alert_button_dok)) { dialogInterface, i ->
//                    val deleteFile = DeleteFile(this@MainActivity)
//                    deleteFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FileKey.KEY_DELETE_UNIPACK, unipackListItem.fpath, String.format(getString(R.string.asynk_delete_title), unipackListItem.fname), recycler_Unipack, recyclerAdapter, position)
//                }
                delete.show()
            }
        }))
    }

    private fun showUnipack() {
        unipackAdapter.clearItem()
        val arrayUnipack = unipackIO.getUnipacks()
        if (arrayUnipack != null) {
            for (unipackInfo in arrayUnipack) {
                if (unipackInfo != null) {
                    val uni: UnipackListItem = UnipackListItem()
                    uni.fname = unipackInfo[0]
                    uni.fproducer = unipackInfo[1]
                    uni.fchain = unipackInfo[2]
                    uni.fpath = unipackInfo[3]
                    unipackAdapter.addItem(uni)
                }
            }
        }
        recycler_unipack.adapter = unipackAdapter
    }

    override fun onResume() {
        super.onResume()
        showUnipack()
    }
}
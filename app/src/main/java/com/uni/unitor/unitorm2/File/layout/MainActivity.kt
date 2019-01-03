package com.uni.unitor.unitorm2.File.layout

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.getbase.floatingactionbutton.FloatingActionButton
import com.uni.unitor.unitorm2.File.UnipackIO
import com.uni.unitor.unitorm2.File.sharedpreference.PreferenceKey
import com.uni.unitor.unitorm2.File.sharedpreference.SharedPreferenceIO
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.view.recycler.UnipackListAdapter
import com.uni.unitor.unitorm2.view.recycler.UnipackListItem
import com.uni.unitor.unitorm2.view.recycler.listener.RecyclerItemClickListener

class MainActivity : AppCompatActivity() {

    private lateinit var recycler_unipack:RecyclerView
    private lateinit var floating_new: FloatingActionButton
    private lateinit var floating_import: FloatingActionButton
    private lateinit var floating_setting: FloatingActionButton

    private val unipackAdapter:UnipackListAdapter = UnipackListAdapter()
    private lateinit var sharedPreferenceIO:SharedPreferenceIO;
    private lateinit var unipackIO:UnipackIO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        if (supportActionBar != null) supportActionBar!!.hide()

        unipackIO = UnipackIO(this)
        sharedPreferenceIO = SharedPreferenceIO(this)

        recycler_unipack = findViewById<RecyclerView>(R.id.recycle_main_unipack)
        floating_new = findViewById<FloatingActionButton>(R.id.fab_new)
        floating_import = findViewById<FloatingActionButton>(R.id.fab_import)
        floating_setting = findViewById<FloatingActionButton>(R.id.fab_setting)

        recycler_unipack.layoutManager = LinearLayoutManager(this)
        recycler_unipack.itemAnimator = DefaultItemAnimator()

        recycler_unipack.adapter = unipackAdapter
        showUnipack()

        //유니팩 리스트 리스너
        recycler_unipack.addOnItemTouchListener(RecyclerItemClickListener(this, recycler_unipack, object : RecyclerItemClickListener.OnItemClickListener {
            //클릭=편집시작
            override fun onItemClicked(view: View, position: Int) {
                val item: UnipackListItem = unipackAdapter.getItem(position)
                startEdit(item.fname!!, item.fproducer!!, item.fchain!!, item.fpath!!)
            }

            //롱클릭=삭제
            override fun onLongItemClicked(view: View?, position: Int) {
                val layout: RelativeLayout = View.inflate(this@MainActivity, R.layout.dialog_delete, null) as RelativeLayout
                val unipackListItem = unipackAdapter.getItem(position)
                val delete = AlertDialog.Builder(this@MainActivity)
                var dialog:AlertDialog? = null
                delete.setView(layout)
                layout.findViewById<TextView>(R.id.dialog_delete_title).text = String.format(getString(R.string.alert_title_dunipack), unipackListItem.fname)
                layout.findViewById<TextView>(R.id.dialog_delete_sub).text = String.format(getString(R.string.alert_message_dunipack), unipackListItem.fname)
                layout.findViewById<Button>(R.id.button_delete_ok).setOnClickListener { dialog!!.dismiss() }
                layout.findViewById<Button>(R.id.button_delete_cancle).setOnClickListener { dialog!!.dismiss() }
//                delete.setNegativeButton(getString(R.string.alert_button_dcancel), null)
//                delete.setPositiveButton(getString(R.string.alert_button_dok)) { dialogInterface, i ->
//                    val deleteFile = DeleteFile(this@MainActivity)
//                    deleteFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, FileKey.KEY_DELETE_UNIPACK, unipackListItem.fpath, String.format(getString(R.string.asynk_delete_title), unipackListItem.fname), recycler_Unipack, recyclerAdapter, position)
//                }
                dialog = delete.create()
                dialog.show()
            }
        }))
    }

    //유니팩 리스트 초기화
    private fun showUnipack() {
        unipackAdapter.clearItem()
        val arrayUnipack = unipackIO.getUnipacks()
        if (arrayUnipack != null) {
            for (unipackInfo in arrayUnipack) {
                if (unipackInfo != null) {
                    val uni = UnipackListItem()
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

    //유니팩 편집 시작전 SharedPreference에 info전보 저장
    private fun startEdit(string_title: String, string_producer: String, string_chain: String, string_path: String) {
        sharedPreferenceIO = SharedPreferenceIO(this@MainActivity, PreferenceKey.KEY_REPOSITORY_INFO)
        sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_TITLE, string_title)
        sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PRODUCER, string_producer)
        sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_CHAIN, string_chain)
        sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PATH, string_path)
        val intent = Intent(this@MainActivity, TabHostActivity::class.java)
        intent.putExtra(PreferenceKey.KEY_KILL_DIED, false)
        startActivity(intent)
        finish()
    }

    //액티비티가 다시 포그라운드로 돌아오면 리스트 다시 초기화
    override fun onResume() {
        super.onResume()
        showUnipack()
    }
}
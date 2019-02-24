package com.uni.unitor.unitorm2.layout

import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.android.gms.ads.*
import com.uni.unitor.unitorm2.File.FileIO
import com.uni.unitor.unitorm2.File.FileKey
import com.uni.unitor.unitorm2.File.UnipackIO
import com.uni.unitor.unitorm2.File.sharedpreference.PreferenceKey
import com.uni.unitor.unitorm2.File.sharedpreference.SharedPreferenceIO
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.fragment.dialog.FileExplorerdDialog
import com.uni.unitor.unitorm2.preference.PreferenceActivity
import com.uni.unitor.unitorm2.view.recycler.UnipackListAdapter
import com.uni.unitor.unitorm2.view.recycler.UnipackListItem
import com.uni.unitor.unitorm2.view.recycler.listener.RecyclerItemClickListener
import java.io.File

class MainActivity : AppCompatActivity(), FileExplorerdDialog.OnUnipackSelectListener {

    private lateinit var recycler_unipack:RecyclerView
    private lateinit var floating_new: FloatingActionButton
    private lateinit var floating_import: FloatingActionButton
    private lateinit var floating_setting: FloatingActionButton
    private lateinit var adView:AdView
    private lateinit var interAd:InterstitialAd

    private lateinit var kill:SharedPreferenceIO
    private val unipackAdapter:UnipackListAdapter = UnipackListAdapter()
    private lateinit var sharedPreferenceIO:SharedPreferenceIO;
    private lateinit var unipackIO:UnipackIO
    private lateinit var fileio:FileIO
    private val fileexDialog = FileExplorerdDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        if (supportActionBar != null) supportActionBar!!.hide()

        MobileAds.initialize(this, "ca-app-pub-7873521316289922~7967347251")
        adView = findViewById(R.id.admob)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        interAd = InterstitialAd(this)
        interAd.adUnitId = "ca-app-pub-7873521316289922/9694048818"
        interAd.loadAd(AdRequest.Builder().build())

        unipackIO = UnipackIO(this)
        sharedPreferenceIO = SharedPreferenceIO(this@MainActivity, PreferenceKey.KEY_REPOSITORY_INFO)
        kill = SharedPreferenceIO(this, PreferenceKey.KEY_REPOSITORY_KILL)
        fileio = FileIO(this)

        recycler_unipack = findViewById<RecyclerView>(R.id.recycle_main_unipack)
        floating_new = findViewById<FloatingActionButton>(R.id.fab_new)
        floating_import = findViewById<FloatingActionButton>(R.id.fab_import)
        floating_setting = findViewById<FloatingActionButton>(R.id.fab_setting)

        recycler_unipack.layoutManager = LinearLayoutManager(this)
        recycler_unipack.itemAnimator = DefaultItemAnimator()

        val killed = kill.getBoolean(PreferenceKey.KEY_KILL_DIED, true)
        if (!killed) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle(String.format(getString(R.string.alert_restore), sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, "")))
            dialog.setMessage(String.format(getString(R.string.alert_restore_msg), sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, "")))
            dialog.setPositiveButton(getString(R.string.alert_restore_ok), DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(this@MainActivity, TabHostActivity::class.java)
                intent.putExtra(PreferenceKey.KEY_KILL_DIED, false)
                startActivity(intent)
                finish()
            })
            dialog.setNegativeButton(getString(R.string.alert_restore_no), null)
            dialog.show()
        }

        recycler_unipack.adapter = unipackAdapter
        showUnipack()

        floating_import.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(LayoutKey.DIALOG_FILEEX_TAG, LayoutKey.DIALOG_FILEEX_UNIPACK)
            fileexDialog.arguments = bundle
            fileexDialog.show(supportFragmentManager, LayoutKey.DIALOG_FILEEX_TAG)
        }

        floating_new.setOnClickListener {
            val layout: LinearLayout = View.inflate(this@MainActivity, R.layout.dialog_newpack, null) as LinearLayout
            val p: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            p.setView(layout)
            p.setTitle(getString(R.string.dialog_title_newUnipack))
            p.setPositiveButton(getString(R.string.dialog_Make)) { dialog, which ->
                val edit_title: EditText  = layout.findViewById(R.id.Edit_Title_new) as EditText
                val edit_producer: EditText  = layout.findViewById(R.id.Edit_Producer_new) as EditText
                val edit_chain: EditText  = layout.findViewById(R.id.Edit_Chain_new) as EditText

                val string_title: String = edit_title.text.toString()
                val string_producer: String  = edit_producer.text.toString()
                val string_chain: String = edit_chain.text.toString()
                val string_path: String = fileio.getDefaultPath() + "unipackProject/" + string_title +
                        File(fileio.getDefaultPath() + "unipackProject/").listFiles { file -> file.getName().startsWith(string_title)}.size + "/"

                if ((string_title.isEmpty() || string_title == "")||(string_producer.isEmpty() || string_producer == "")||(string_chain.isEmpty() || string_chain == "")) {
                    Toast.makeText(this@MainActivity, getString(R.string.toast_newUnipack_null), Toast.LENGTH_LONG).show()
                } else {
                    try {
                        unipackIO.mkNewUnipack(string_title, string_producer, string_chain, string_path)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (e.message != null) fileio.showErr(e.message!!)
                    }
                    startEdit(string_title, string_producer, string_chain, string_path)
                }
            }
            p.show()
        }

        floating_setting.setOnClickListener {
            startActivity(Intent(this@MainActivity, PreferenceActivity::class.java))
        }

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
                layout.findViewById<Button>(R.id.button_delete_ok).setOnClickListener {
                    dialog!!.dismiss()
                    FileIO.DeleteFile(this@MainActivity, FileKey.KEY_UNIPACKP_DELETE, unipackListItem.fpath!!, unipackListItem.fname!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                }
                layout.findViewById<Button>(R.id.button_delete_cancle).setOnClickListener { dialog!!.dismiss() }
                dialog = delete.create()
                dialog.show()
            }
        }))
    }

    //유니팩 리스트 초기화
    fun showUnipack() {
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
        if (interAd.isLoaded) {
            interAd.show()
            interAd.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    // Code to be executed when an ad request fails.
                }

                override fun onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                override fun onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                override fun onAdClosed() {
                    sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_TITLE, string_title)
                    sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PRODUCER, string_producer)
                    sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_CHAIN, string_chain)
                    sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PATH, string_path)
                    kill.setBoolean(PreferenceKey.KEY_KILL_DIED, false)
                    kill.setBoolean(PreferenceKey.KEY_SOUND_INIT, true)
                    val intent = Intent(this@MainActivity, TabHostActivity::class.java)
                    intent.putExtra(PreferenceKey.KEY_KILL_DIED, false)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_TITLE, string_title)
            sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PRODUCER, string_producer)
            sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_CHAIN, string_chain)
            sharedPreferenceIO.setString(PreferenceKey.KEY_INFO_PATH, string_path)
            kill.setBoolean(PreferenceKey.KEY_KILL_DIED, false)
            kill.setBoolean(PreferenceKey.KEY_SOUND_INIT, true)
            val intent = Intent(this@MainActivity, TabHostActivity::class.java)
            intent.putExtra(PreferenceKey.KEY_KILL_DIED, false)
            startActivity(intent)
            finish()
        }

    }

    //selected unipack
    override fun onUnipackSelect(name: String, path: String) {
        fileexDialog.dismiss()
        Toast.makeText(this, name + "\n" + path, Toast.LENGTH_SHORT).show()
        val string_path: String = fileio.getDefaultPath() + "unipackProject/" + name +
                File(fileio.getDefaultPath() + "unipackProject/").listFiles { file -> file.getName().startsWith(name)}.size + "/"
        UnipackIO.UnzipUnipack(this, name, path, string_path).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    //액티비티가 다시 포그라운드로 돌아오면 리스트 다시 초기화
    override fun onResume() {
        super.onResume()
        showUnipack()
    }
}
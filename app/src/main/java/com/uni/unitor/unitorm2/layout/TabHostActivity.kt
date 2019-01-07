package com.uni.unitor.unitorm2.layout

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.uni.unitor.unitorm2.File.FileKey
import com.uni.unitor.unitorm2.File.InfoIO
import com.uni.unitor.unitorm2.File.KeySoundIO
import com.uni.unitor.unitorm2.fragment.InfoFragment
import com.uni.unitor.unitorm2.fragment.KeyLEDFragment
import com.uni.unitor.unitorm2.fragment.KeySoundFragment
import com.uni.unitor.unitorm2.fragment.ListenerKey
import com.uni.unitor.unitorm2.File.sharedpreference.PreferenceKey
import com.uni.unitor.unitorm2.File.sharedpreference.SharedPreferenceIO
import com.uni.unitor.unitorm2.R
import java.lang.Exception

class TabHostActivity : AppCompatActivity(), InfoFragment.OnInfoChangeListener, KeySoundFragment.OnKeySoundRequestListener {

    private lateinit var toolbarV: Toolbar
    private lateinit var tablayout:TabLayout
    private lateinit var viewPager:ViewPager

    private lateinit var keySoundIO: KeySoundIO
    private lateinit var infoIO: InfoIO
    private lateinit var menu_save:MenuItem
    private lateinit var sharedPreferenceIO: SharedPreferenceIO
    private var keysoundList:ArrayList<String>? = ArrayList()
    private var soundList:ArrayList<Array<String>> = ArrayList()
    private val infoFragment:InfoFragment = InfoFragment()
    private val keySoundFragment:KeySoundFragment = KeySoundFragment()
    private val keyLEDFragment:KeyLEDFragment = KeyLEDFragment()
    private var backKeyPress: Long = 0
    private var keysoundInit:Boolean = true
    private var soundLoaded:ArrayList<Array<Any>> = ArrayList()
    private lateinit var loadSound: LoadSound
    private lateinit var soundPool:SoundPool
    private var isUnload:Boolean = false
    private var chain:String = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_tabhost)
        if (supportActionBar != null) supportActionBar!!.hide()

        keySoundIO = KeySoundIO(this)
        infoIO = InfoIO(this)
        sharedPreferenceIO = SharedPreferenceIO(this, PreferenceKey.KEY_REPOSITORY_INFO)
        toolbarV = findViewById<Toolbar>(R.id.toolbar)
        tablayout = findViewById<TabLayout>(R.id.tabs)
        viewPager = findViewById<ViewPager>(R.id.container)

        toolbarV.title =""
        setSupportActionBar(toolbarV)

        //탭 추가
        val infoTab = tablayout.newTab()
        infoTab.text = "Info"
        tablayout.addTab(infoTab)
        val soundTab = tablayout.newTab()
        soundTab.text = "KeySound"
        tablayout.addTab(soundTab)
        val ledTab = tablayout.newTab()
        ledTab.text = "KeyLED"
        tablayout.addTab(ledTab)

        //뷰페이저 스와이프 할때마다 탭 바뀌게 리스너/탭 리스너 추가
        val tabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        viewPager.adapter = tabPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                if (menu_save != null) {
                    when (position) {
                        0 -> menu_save.title = getString(R.string.save_info)

                        1 -> menu_save.title = getString(R.string.save_keySound)

                        2 -> menu_save.title = getString(R.string.save_KeyLED)
                    }
                }
                viewPager.currentItem = position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        if (savedInstanceState != null) {
            keysoundInit = savedInstanceState.getBoolean(LayoutKey.TABHOST_KEYSOUND_INIT)
            when (savedInstanceState.getInt(LayoutKey.TABHOST_TAB_SELECTED)) {
                0 -> {infoTab.select()}
                1 -> {soundTab.select()}
                2 -> {ledTab.select()}
            }
            keysoundList = savedInstanceState.getStringArrayList(LayoutKey.TABHOST_KEYSOUND_LIST)
        }

        //keysound초기화
        initKeySound()
    }

    //메뉴생성
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_actionbar, menu)
        menu_save = menu!!.findItem(R.id.menu_save)
        return super.onCreateOptionsMenu(menu)
    }

    //메뉴 클릭 리스너
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.menu_save -> {
                    when (tablayout.selectedTabPosition) {
                        0 -> {
                            saveInfo()
                        }
                        1 -> {
                            //onSaveListener2.onSave(ActivityKey.KEY_INT_SOUND)
                        }
                        2 -> {
                            //onSaveListener3.onSave(ActivityKey.KEY_INT_LED)
                        }
                    }
                    return true
                }
                R.id.menu_saveall -> {
                    return true
                }
                else -> {
                    return super.onOptionsItemSelected(item)
                }
            }
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    //keysound 초기화
    private fun initKeySound() {//TODO: keysound초기화
        if (keysoundInit) {
            keySoundIO.mkKeySoundWork()
            KeySoundIO.DupliSaveSound(this, FileKey.KEY_SOUND_WORK_DUPLICATE,
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            keysoundInit = false
        }
        soundList = keySoundIO.getSoundFile(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!)
    }

    fun setKeysoundWork() {
        keysoundList = keySoundIO.getKeySoundWork()
        keySoundFragment.soundloadFinish()
    }

    //info저장
    private fun saveInfo() {
        try {
            infoIO.mkInfo(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, ""),
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PRODUCER, ""),
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, ""),
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "") + "info")
            Toast.makeText(this, getString(R.string.toast_save_succeed), Toast.LENGTH_SHORT).show()
        } catch (e:Exception) {
            Toast.makeText(this, getString(R.string.toast_save_fail), Toast.LENGTH_SHORT).show()
        }
    }

    //infofragment 이밴트(info)
    override fun onInfoChaged(type:String, content:String) {
        //최초요청시 전달
        if (type.equals(ListenerKey.KEY_INFO_START)) {
            infoFragment.requestInfo(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, ""), sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PRODUCER, ""), sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, ""))
        } else {//일반적인경우(edittext편집)
            sharedPreferenceIO.setString(type, content)
        }
    }

    //(keysound)
    override fun onRequest(type:String, content:String) {
        when (type) {
            ListenerKey.KEY_SOUND_CHAIN -> {
                keySoundFragment.setChain(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, "1"))
            }
            ListenerKey.KEY_SOUND_FILE -> {
                if (keysoundList != null) keySoundFragment.setButton(keysoundList!!)
            }
            ListenerKey.KEY_SOUND_CHAIN_T -> {
                chain = content
            }
        }
    }

    //play
    fun play(name:String, repeat:String, whole:String) {
        play(name, repeat)
    }//play
    fun play(name:String, repeat:String) {
        for (sound in soundLoaded) {
            if (sound[0].toString().equals(name)) {
                soundPool.play(sound[1] as Int, 1f, 1f, 0, 0, 1f)
            }
        }
    }

    //뷰페이져에 프래그먼트 붙이기
    private inner class TabPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> {
                    return infoFragment
                }

                1 -> {
                    return keySoundFragment
                }

                2 -> {
                    return keyLEDFragment
                }

                else -> return null
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

    //정보 일시저장
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putBoolean(LayoutKey.TABHOST_KEYSOUND_INIT, keysoundInit)
        outState!!.putInt(LayoutKey.TABHOST_TAB_SELECTED, tablayout.selectedTabPosition)
        outState!!.putStringArrayList(LayoutKey.TABHOST_KEYSOUND_LIST, keysoundList)
    }

    //화면 돌아오면 사운드 로딩
    override fun onResume() {
        super.onResume()
        if (!isUnload) {
            loadSound = LoadSound(this, soundList)
            loadSound.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    //백그라운드로 돌아갈시 언로딩
    override fun onStop() {
        super.onStop()
        while (loadSound.status == AsyncTask.Status.RUNNING) {

        }
        soundUnLoad()
    }

    //사운드 언로딘
    fun soundUnLoad() {
        if (soundPool != null) {
            isUnload = true
            for (o in soundLoaded) {
                soundPool!!.unload(o[1] as Int)
            }
            soundPool!!.release()
            soundLoaded.clear()
            isUnload = false
        }
    }

    //뒤로가기
    override fun onBackPressed() {
        if (System.currentTimeMillis() - backKeyPress < 2000) {
//            soundUnLoad()
//            isKilledSelf = true//스스로 종료시
//            sharedPKill.setBoolean(PreferenceKey.KEY_KILL_DIED, PreferenceKey.KEY_KILL_SELF)
            startActivity(Intent(this@TabHostActivity, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this@TabHostActivity, getString(R.string.toast_back), Toast.LENGTH_SHORT).show()
            backKeyPress = System.currentTimeMillis()
        }
    }

    //로딩된 리스트 가져옴
    fun setLoad(sl:ArrayList<Array<Any>>, sound: SoundPool) {
        soundLoaded = sl
        soundPool = sound
    }


    //사운드 로딩
    class LoadSound(context: Context, sounds:ArrayList<Array<String>>) : AsyncTask<String, String, Boolean>() {

        private lateinit var progressDialog:ProgressDialog
        private val context:Context = context
        private val sounds = sounds
        private var count = 0
        private var soundPool:SoundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        private var soundLoaded:ArrayList<Array<Any>> = ArrayList()

        override fun onPreExecute() {
            progressDialog = ProgressDialog(context)
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setTitle(context.getString(R.string.async_load_sound_title))
            progressDialog.show()
            progressDialog.max = sounds.size
            soundPool = SoundPool(sounds.size, AudioManager.STREAM_MUSIC, 0)
        }

        override fun doInBackground(vararg params: String?): Boolean {
            try {
                for (s in sounds) {
                    val pool = soundPool.load(s[1], 0)
                    val name = s[0]
                    publishProgress(s[1])
                    soundLoaded.add(arrayOf(name, pool))
                    count++
                }
                return true
            } catch (e:Exception) {
                return false
            }
        }

        override fun onProgressUpdate(vararg values: String?) {
            progressDialog.setMessage(values[0])
            progressDialog.progress = count
        }

        override fun onPostExecute(result: Boolean?) {
            progressDialog.dismiss()
            if (result!!) {
                (context as TabHostActivity).setLoad(soundLoaded, soundPool)
            } else {

            }
        }
    }
}
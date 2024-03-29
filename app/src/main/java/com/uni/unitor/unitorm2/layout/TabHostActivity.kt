package com.uni.unitor.unitorm2.layout

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.PowerManager
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.uni.unitor.unitorm2.File.*
import com.uni.unitor.unitorm2.fragment.InfoFragment
import com.uni.unitor.unitorm2.fragment.KeyLEDFragment
import com.uni.unitor.unitorm2.fragment.KeySoundFragment
import com.uni.unitor.unitorm2.fragment.ListenerKey
import com.uni.unitor.unitorm2.File.sharedpreference.PreferenceKey
import com.uni.unitor.unitorm2.File.sharedpreference.SharedPreferenceIO
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.fragment.dialog.FileExplorerdDialog
import java.lang.Exception
import java.net.URL
import java.security.Key
import kotlin.RuntimeException

/**InfoFragment, KeySoundFragment, KeyLEDFragment를 하나로 이어주는 액티비티**/

class TabHostActivity : AppCompatActivity(), InfoFragment.OnInfoChangeListener, KeySoundFragment.OnKeySoundRequestListener, FileExplorerdDialog.OnFileSelectListener, KeyLEDFragment.OnKeyLEDRequestListener {

    private lateinit var toolbarV: Toolbar
    private lateinit var tablayout:TabLayout
    private lateinit var viewPager:ViewPager

    private var infoFragment:InfoFragment? = InfoFragment()
    private var keySoundFragment:KeySoundFragment? = KeySoundFragment()
    private var keyLEDFragment:KeyLEDFragment? = KeyLEDFragment()
    private val fileexDialog:FileExplorerdDialog = FileExplorerdDialog()
    private var tabPagerAdapter:TabPagerAdapter? = null

    private lateinit var keyLEDIO: KeyLEDIO
    private lateinit var keySoundIO: KeySoundIO
    private lateinit var infoIO: InfoIO
    private var menu_save:MenuItem? = null
    private lateinit var sharedPreferenceIO: SharedPreferenceIO
    private var keysoundList:ArrayList<String>? = ArrayList()
    private var soundList:ArrayList<Array<String>> = ArrayList()
    private var backKeyPress: Long = 0
    private var keysoundInit:Boolean = true
    private var soundLoaded:ArrayList<Array<Any>> = ArrayList()
    private lateinit var loadSound: LoadSound
    private var soundPool:SoundPool? = null
    private var isUnload:Boolean = false
    private var chain:String = "1"
    private lateinit var preKill:SharedPreferenceIO
    private var isKilled = false
    private lateinit var isOn:SharedPreferences
    private var isKeepOn = false
    private var wakeLock: PowerManager.WakeLock? = null

    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_tabhost)
        if (supportActionBar != null) supportActionBar!!.hide()

        isOn = PreferenceManager.getDefaultSharedPreferences(this)
        preKill = SharedPreferenceIO(this, PreferenceKey.KEY_REPOSITORY_KILL)
        isKilled = preKill.getBoolean(PreferenceKey.KEY_KILL_DIED, false)
        keysoundInit = preKill.getBoolean(PreferenceKey.KEY_SOUND_INIT, true)

        //화면 유지 여부
        isKeepOn = isOn.getBoolean("setting_screen", false)
        if (isKeepOn) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ScreenOn")
            wakeLock!!.acquire()
        }

        keyLEDIO = KeyLEDIO(this)
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

        setPageAdapter()

        //임시저장 정보 받아옴
        if (savedInstanceState != null) {
            //keysoundInit = savedInstanceState.getBoolean(LayoutKey.TABHOST_KEYSOUND_INIT)
            when (savedInstanceState.getInt(LayoutKey.TABHOST_TAB_SELECTED)) {
                0 -> {infoTab.select()}
                1 -> {soundTab.select()}
                2 -> {ledTab.select()}
            }
            keysoundList = savedInstanceState.getStringArrayList(LayoutKey.TABHOST_KEYSOUND_LIST)
        }

//        //keysound초기화
        //initKeySound()
    }

    /**메뉴처리**/
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
                            saveKeySound()
                        }
                        2 -> {
                            savKeyLED(false)
                        }
                    }
                    return true
                }

                R.id.menu_saveall -> {
                    saveInfo()
                    saveKeySound()
                    savKeyLED(false)
                    return true
                }

                R.id.menu_export -> {
                    saveInfo()
                    saveKeySound()
                    savKeyLED(true)
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

    //내보내기 처리
    fun zipUnipack() {
        FileIO.Export(this, sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE,"")!!
                , sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun zipFinish(result: Boolean, out:String) {
        val dialog = AlertDialog.Builder(this)
        if (result) {
            dialog.setTitle(getString(R.string.dialog_export_finish_suc))
            dialog.setMessage(String.format(getString(R.string.dialog_export_mes_suc), out))
//            dialog.setPositiveButton(getString(R.string.dialog_export_button_go), DialogInterface.OnClickListener { dialog, which ->
//                val intent = Intent("android.intent.action.VIEW");
//                intent.setData(Uri.EMPTY); // uri of directory from FileProvider, DocumentProvider or uri with file scheme, can be empty.
//                intent.putExtra("org.openintents.extra.ABSOLUTE_PATH", out); // String
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
//            })
        } else {
            dialog.setTitle(getString(R.string.dialog_export_finish_fail))
        }
        dialog.setNegativeButton(getString(R.string.dialog_export_button_ok), null)
        dialog.show()
    }

    /**info, sound, led 가져옴**/
    override fun setInfoContext(infoFragment: InfoFragment) {
        this.infoFragment = infoFragment
        //if (tabPagerAdapter == null) setPageAdapter()
    }

    override fun setKeySoundContext(keySoundFragment: KeySoundFragment) {
        this.keySoundFragment = keySoundFragment

        //if (tabPagerAdapter == null) setPageAdapter()
    }

    override fun setKeyLEDContext(keyLEDFragment: KeyLEDFragment) {
        this.keyLEDFragment = keyLEDFragment
        //if (tabPagerAdapter == null) setPageAdapter()
    }

    private fun setPageAdapter() {
        //뷰페이저 스와이프 할때마다 탭 바뀌게 리스너/탭 리스너 추가
        tabPagerAdapter = TabPagerAdapter(supportFragmentManager)
        viewPager.adapter = tabPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                if (menu_save != null) {
                    when (position) {
                        0 -> menu_save!!.title = getString(R.string.save_info)

                        1 -> menu_save!!.title = getString(R.string.save_keySound)

                        2 -> menu_save!!.title = getString(R.string.save_KeyLED)
                    }
                }
                viewPager.currentItem = position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    /**Info 처리**/
    //info저장
    private fun saveInfo() {
        try {
            infoIO.mkInfo(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, ""),
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PRODUCER, ""),
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, ""),
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "") + "info")
            Toast.makeText(this, getString(R.string.toast_save_succeed) + ": INFO", Toast.LENGTH_SHORT).show()
        } catch (e:Exception) {
            Toast.makeText(this, getString(R.string.toast_save_fail) + ": INFO", Toast.LENGTH_SHORT).show()
        }
    }

    //infofragment 이밴트(info)
    override fun onInfoChaged(type:String, content:String) {
        //최초요청시 전달
        if (type.equals(ListenerKey.KEY_INFO_START)) {
            infoFragment!!.requestInfo(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, ""),
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PRODUCER, ""),
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, ""))
        } else {//일반적인경우(edittext편집)
            sharedPreferenceIO.setString(type, content)
            if (type.equals(PreferenceKey.KEY_INFO_CHAIN)) {
                if (keySoundFragment != null) keySoundFragment!!.setChain(content)
                if (keyLEDFragment != null) keyLEDFragment!!.setChain(content)
            }
        }
    }

    /**KeySound처리**/
    //keysound 초기화
    private fun initKeySound() {//keysound초기화
        if (keysoundInit&&!isKilled) {
            keySoundIO.mkKeySoundWork()
            KeySoundIO.DupliSaveSound(this,
                    sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            setKeysoundWork()
        }
    }

    //keysound에서 복제가 끝나면 work폴더에서 가져옴
    fun setKeysoundWork() {
        soundList = keySoundIO.getSoundFile(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!)
        keysoundList = keySoundIO.getKeySoundWork()
        keySoundFragment!!.soundloadFinish()
        if (!isUnload) {
            loadSound = LoadSound(this, soundList)
            loadSound.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    //(keysound)
    override fun onRequest(type:String, content:String) {
        when (type) {
            ListenerKey.KEY_SOUND_CHAIN -> {
                keySoundFragment!!.setChain(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, "1"))
            }
            ListenerKey.KEY_SOUND_FILE -> {
                if (keysoundList != null) keySoundFragment!!.setButton(keysoundList!!)
            }
            ListenerKey.KEY_SOUND_CHAIN_T -> {
                chain = content
            }
            ListenerKey.KEY_SOUND_WAVFILE -> {
                keySoundFragment!!.receiveWavs(soundList)
            }
            ListenerKey.KEY_SOUND_PLAY -> {
                play(content, "1")
            }
            ListenerKey.KEY_SOUND_ADD -> {
                keysoundList!!.add(content)
                keySoundIO.saveKeySoundWork(keysoundList)
            }
            ListenerKey.KEY_SOUND_REMOVE -> {
                val s = content.split("\\s+".toRegex())
                var int:Int? = null
                for (i in 0..keysoundList!!.size - 1) {
                    val content = keysoundList!!.get(i)
                    if (s.size == 5) {
                        if (content.equals(s[0] + " " + s[1] + " " + s[2] + " " + s[3])||content.equals(s[0] + " " + s[1] + " " + s[2] + " " + s[3] + " " + s[4])) {
                            int = i
                            break
                        }
                    } else {
                        if (content.equals(s[0] + " " + s[1] + " " + s[2] + " " + s[3] + " " + s[4] + " " + s[5])) {
                            int = i
                            break
                        }
                    }
                }
                if (int != null) {
                    keysoundList!!.removeAt(int)
                    keySoundIO.saveKeySoundWork(keysoundList)
                }
            }
            ListenerKey.KEY_SOUND_DELETE -> {
                FileIO.DeleteFile(this, FileKey.KEY_FILE_DELETE_SOUND, content, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
            ListenerKey.KEY_SOUND_ADD_FILE -> {
                val bundle = Bundle()
                bundle.putString(LayoutKey.DIALOG_FILEEX_TAG, LayoutKey.DIALOG_FILEEX_SOUND)
                fileexDialog.arguments = bundle
                fileexDialog.show(supportFragmentManager, LayoutKey.DIALOG_FILEEX_TAG)
            }
            ListenerKey.KEY_SOUND_LOAD -> {
                //keysound초기화
                initKeySound()
            }
        }
    }
    override fun onRequest(type:String, content1:String, content2: String) {
        when (type) {
            ListenerKey.KEY_SOUND_CHANGE -> {
                if (keysoundList != null) {
                    val s = content1.split("\\s+".toRegex())
                    for (i in 0..keysoundList!!.size - 1) {
                        val a = keysoundList!!.get(i)
                        if (s.size == 5) {
                            if (a.equals(s[0] + " " + s[1] + " " + s[2] + " " + s[3])||a.equals(s[0] + " " + s[1] + " " + s[2] + " " + s[3] + " " + s[4])) {
                                keysoundList!!.set(i, content2)
                                break
                            }
                        } else {
                            if (a.equals(s[0] + " " + s[1] + " " + s[2] + " " + s[3] + " " + s[4] + " " + s[5])) {
                                keysoundList!!.set(i, content2)
                                break
                            }
                        }
                    }
                    keySoundIO.saveKeySoundWork(keysoundList)
                }
            }
        }
    }

    //play
    fun play(name:String, repeat:String, whole:String) {
        play(name, repeat)
        keySoundFragment!!.setWormwhole(whole)
    }

    //play
    fun play(name:String, repeat:String) {
        for (sound in soundLoaded) {
            if (sound[0].toString().equals(name)) {
                if (repeat.toInt()-1==-1) {
                    soundPool!!.play(sound[1] as Int, 1f, 1f, 0, 0, 1f)
                    break
                } else {
                    soundPool!!.play(sound[1] as Int, 1f, 1f, 0, repeat.toInt() - 1, 1f)
                    break
                }
            }
        }
    }

    //로딩된 리스트 가져옴
    fun setLoad(sl:ArrayList<Array<Any>>, sound: SoundPool) {
        soundLoaded = sl
        soundPool = sound
        keySoundFragment!!.loadFinish()
        if (keysoundInit&&!isKilled) {
            KeyLEDIO.getLedWork(this, sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            keysoundInit = false
            preKill.setBoolean(PreferenceKey.KEY_SOUND_INIT, false)
        }
    }

    //sound저장
    private fun saveKeySound() {
        try {
            keySoundIO.saveKeySound(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!)
            Toast.makeText(this, getString(R.string.toast_save_succeed) + ": KEYSOUND", Toast.LENGTH_SHORT).show()
        } catch (e:Exception) {
            Toast.makeText(this, getString(R.string.toast_save_fail) + ": KEYSOUND", Toast.LENGTH_SHORT).show()
        }
    }

    //사운드 파일에 변화(삭제,추기) 있을때 처리
    fun soundsChange(isAdd:Boolean) {
        soundUnLoad()
        soundList = ArrayList()
        soundList = keySoundIO.getSoundFile(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!)
        loadSound = LoadSound(this, soundList)
        loadSound.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
//        if (isAdd) {
//            keySoundFragment!!.addSound(soundList)
//        }
    }

    /**동시처리**/
    fun isButtonClicked(activity:String, tag:String, list:ArrayList<Array<String>>) {
        when (activity) {
            LayoutKey.PLAYBTN_LAYOUT_SOUND -> {//sound버튼이 클릭
                keySoundFragment!!.isPlayButtonClicked(tag, list)
            }
            LayoutKey.PLAYBTN_LAYOUT_LED -> {//led버튼이 클릭

            }
        }
    }

    /**KeyLED 처리**///TODO: Keyled request 처리
    fun savKeyLED(isZip:Boolean) {
        KeyLEDIO.SaveLED(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!, this, isZip).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    override fun onRequestLED(type:String?, content:String?) {
        when (type) {
            ListenerKey.KEY_LED_CHAIN -> {
                keyLEDFragment!!.setChain(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, "1"))
            }
            ListenerKey.KEY_LED_FILE -> {
                KeyLEDIO.GetKeySoundContent(this, sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
            ListenerKey.KEY_LED_FILE_NEW -> {
                keyLEDIO.makeNewLED(content!!)
            }
            ListenerKey.KEY_LED_FILE_DELETE -> {
                keyLEDIO.deleteLedFile(content!!, sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!)
            }
        }
    }

    override fun onRequestLED(type:String?, content1:String?, content2: String?) {
        when (type) {
            ListenerKey.KEY_LED_MODIFIDE -> {
                keyLEDIO.saveLedWork(content1!!, content2!!)
            }
        }
    }

    //led rename finished
    fun ledRenameFinished(ic:ArrayList<Array<String>>) {
        keyLEDFragment!!.ledDeleteFinished(ic)
    }

    //LED 가져
    fun recieveLED(ledlist:ArrayList<Array<String>>, clicked:String) {
        keyLEDFragment!!.setLEDList(ledlist, clicked)
    }

    //led 편집
    fun editLED(id:String) {
        keyLEDFragment!!.receiveEdit(id)
    }

    //LED 내용 가져오기 (원본 파일)
    fun getLEDContentFinish(content : ArrayList<Array<String>>?) {
        keyLEDFragment!!.setLEDFile(content)
    }

    //LED 초기화 끝나고
    fun setLedInitFinish() {

    }

    /**액티비티가 백그라운드로 전환될때 처리
     * 사운드 언로딩, 정보 일시저장**/
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
//        if (!isUnload) {
//            loadSound = LoadSound(this, soundList)
//            loadSound.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
//        }
    }

    //다이얼로그 보일시 강제로 비활
    override fun onPause() {
        super.onPause()
        if (fileexDialog.dialog != null&&fileexDialog.dialog.isShowing&&!fileexDialog.isRemoving) {
            fileexDialog.dismiss()
        }
        if (isKeepOn&&wakeLock != null) {
            try {
                if (wakeLock!!.isHeld) wakeLock!!.release()
            } catch (e:RuntimeException) {

            } catch (e:java.lang.RuntimeException) {

            }
        }
//        preKill.setBoolean(PreferenceKey.KEY_SOUND_INIT, keysoundInit)
//        preKill.setBoolean(PreferenceKey.KEY_KILL_DIED, PreferenceKey.KEY_KILL_FORCE)
    }

    //백그라운드로 돌아갈시 언로딩
    override fun onStop() {
        super.onStop()
        if (loadSound.status == AsyncTask.Status.RUNNING) {
            loadSound.cancel(true)
        } else {
            soundUnLoad()
        }
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
            isKilled = true//스스로 종료시
            preKill.setBoolean(PreferenceKey.KEY_KILL_DIED, true)
            preKill.setBoolean(PreferenceKey.KEY_SOUND_INIT, true)
            startActivity(Intent(this@TabHostActivity, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this@TabHostActivity, getString(R.string.toast_back), Toast.LENGTH_SHORT).show()
            backKeyPress = System.currentTimeMillis()
        }
    }

    //사운파일선택리스너
    override fun onFileSelect(files: ArrayList<Array<String>>) {
        fileexDialog.dismiss()
        KeySoundIO.GetSoundsFile(this, files, sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "")!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }



    //뷰페이져에 프래그먼트 붙이기
    private inner class TabPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> {
                    if (infoFragment != null) return infoFragment else return  null
                }

                1 -> {
                    if (keySoundFragment != null) return keySoundFragment else return  null
                }

                2 -> {
                    if (keyLEDFragment != null) return keyLEDFragment else return  null
                }

                else -> return null
            }
        }

        override fun getCount(): Int {
            return 3
        }
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
                    if (isCancelled) {
                        return true
                    }
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

        override fun onCancelled(result: Boolean?) {
            progressDialog.dismiss()
            Toast.makeText(context, "Load Cancelled", Toast.LENGTH_SHORT).show()
            if (result!!) {
                (context as TabHostActivity).setLoad(soundLoaded, soundPool)
            } else {

            }
            (context as TabHostActivity).soundUnLoad()
        }

        override fun onPostExecute(result: Boolean?) {
            progressDialog.dismiss()
            Toast.makeText(context, "Load Finished", Toast.LENGTH_SHORT).show()
            if (result!!) {
                (context as TabHostActivity).setLoad(soundLoaded, soundPool)
            } else {

            }
        }
    }
}
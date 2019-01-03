package com.uni.unitor.unitorm2.File.layout

import android.content.Intent
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
import com.uni.unitor.unitorm2.File.InfoIO
import com.uni.unitor.unitorm2.File.fragment.InfoFragment
import com.uni.unitor.unitorm2.File.fragment.KeyLEDFragment
import com.uni.unitor.unitorm2.File.fragment.KeySoundFragment
import com.uni.unitor.unitorm2.File.fragment.ListenerKey
import com.uni.unitor.unitorm2.File.sharedpreference.PreferenceKey
import com.uni.unitor.unitorm2.File.sharedpreference.SharedPreferenceIO
import com.uni.unitor.unitorm2.R
import java.lang.Exception

class TabHostActivity : AppCompatActivity(), InfoFragment.OnInfoChangeListener {

    private lateinit var toolbarV: Toolbar
    private lateinit var tablayout:TabLayout
    private lateinit var viewPager:ViewPager

    private lateinit var infoIO: InfoIO
    private lateinit var menu_save:MenuItem
    private lateinit var sharedPreferenceIO: SharedPreferenceIO
    private val infoFragment:InfoFragment = InfoFragment()
    private val keySoundFragment:KeySoundFragment = KeySoundFragment()
    private val keyLEDFragment:KeyLEDFragment = KeyLEDFragment()
    private var backKeyPress: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_tabhost)
        if (supportActionBar != null) supportActionBar!!.hide()

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

    //info저장
    private fun saveInfo() {
        try {
            infoIO.mkInfo(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, ""), sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PRODUCER, ""), sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, ""), sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PATH, "") + "info")
            Toast.makeText(this, getString(R.string.toast_save_succeed), Toast.LENGTH_SHORT).show()
        } catch (e:Exception) {
            Toast.makeText(this, getString(R.string.toast_save_fail), Toast.LENGTH_SHORT).show()
        }
    }

    //infofragment 이밴트
    override fun onInfoChaged(type:String, content:String) {
        //최초요청시 전달
        if (type.equals(ListenerKey.KEY_INFO_START)) {
            infoFragment.requestInfo(sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_TITLE, ""), sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_PRODUCER, ""), sharedPreferenceIO.getString(PreferenceKey.KEY_INFO_CHAIN, ""))
        } else {
            sharedPreferenceIO.setString(type, content)
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
}
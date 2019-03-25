package com.uni.unitor.unitorm2.view.buttons

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.fragment.KeySoundFragment
import com.uni.unitor.unitorm2.layout.TabHostActivity
import android.os.Bundle
import android.widget.RelativeLayout
import com.uni.unitor.unitorm2.fragment.KeyLEDFragment
import com.uni.unitor.unitorm2.layout.LayoutKey
import java.lang.Exception

/**연주하는 네모난 64개의 버튼
 * led, sound에 따라서 다르게 처리하는 클릭 리스너 구현**/

class PlayButton : RelativeLayout, KeyLEDFragment.OnPlayLED {

    lateinit var textView: TextView
    lateinit var viewIn:View
    private lateinit var viewLED:View
    private lateinit var con: Context

    private lateinit var activity: String
    private var currenrchain:String = "1"
    private var chain:String = "1"

    private var soundlist:ArrayList<Array<String>> = ArrayList()
    private var multilist:ArrayList<Array<Int>> = ArrayList()
    private var isPlay:Boolean = false
    private var isEdit:Boolean = false

    private var ledlist:ArrayList<Array<String>> = ArrayList()
    private var ledmulti:ArrayList<Array<Int>> = ArrayList()

    constructor(context: Context) : super(context) {
        iniView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        iniView(context)
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        iniView(context)
        getAttrs(attrs, defStyleAttr)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        iniView(context)
        getAttrs(attrs, defStyleAttr, defStyleRes)
    }

    private fun iniView(context: Context) {
        val infService = Context.LAYOUT_INFLATER_SERVICE
        val layoutInflater = getContext().getSystemService(infService) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_button_play, this@PlayButton, false)
        textView = view.findViewById(R.id.View_Play_text)
        viewIn = view.findViewById<View>(R.id.view_play_showin)
        viewLED = view.findViewById(R.id.view_play_led)

        this.isClickable = true
        textView.isClickable = true

        con = context

        addView(view)
        multilist.add(arrayOf(1, 0, 0, 0))//chain current allcount forcount

//        val receiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                when (intent.action) {
//                    LayoutKey.BROAD_ACTION_ON -> {
//                        Thread(Runnable {
//                            val list = intent.getStringArrayListExtra("on")
//                            for (i in list) {
//                                val o = i.split("\\n".toRegex())
//                                if (o[0].startsWith(textView.text.toString())) {
//                                    (con as TabHostActivity).runOnUiThread {
//                                        onLED(o[1].toInt())
//                                    }
//                                }
//                            }
//                        }).start()
//                    }
//                    LayoutKey.BROAD_ACTION_OFF -> {
//                        Thread(Runnable {
//                            val list = intent.getStringArrayListExtra("off")
//                            for (i in list) {
//                                val o = i.split("\\n".toRegex())
//                                if (o[0].startsWith(textView.text.toString())) {
//                                    (con as TabHostActivity).runOnUiThread {
//                                        offLED()
//                                    }
//                                }
//                            }
//                        }).start()
//
//                    }
//                }
//            }
//        }
//        val filter = IntentFilter()
//        filter.addAction(LayoutKey.BROAD_ACTION_ON)
//        filter.addAction(LayoutKey.BROAD_ACTION_OFF)
//        con.registerReceiver(receiver, filter)

        textView.setOnClickListener {
            when(activity) {
                //sound일때
                LayoutKey.PLAYBTN_LAYOUT_SOUND -> {
                    if (isPlay) {//play mode
                        for (list in 0 until soundlist.size) {
                            val s = soundlist.get(list)

                            if (s[1].equals(currenrchain)) {
                                val count = multilist.get(s[1].toInt() - 1)//TODO: Err check
                                if (count[1] > count[3]) {//넘길횟수 체크
                                    multilist.set(s[1].toInt() - 1, arrayOf(count[0], count[1], count[2], count[3] + 1))
                                    continue
                                }
                                if (count[1] <= count[2]) {//총 카운트랑 현재 카운트가 작거나 같을시 재생
                                    if (s.size == 5) {
                                        (con as TabHostActivity).play(s[2], s[3], s[4])
                                        soundlist.set(list, arrayOf("0", s[1], s[2], s[3], s[4]))
                                    } else {
                                        (con as TabHostActivity).play(s[2], s[3])
                                        soundlist.set(list, arrayOf("0", s[1], s[2], s[3]))
                                    }
                                }
                                if (count[1] == count[2]) {//총 카운트랑 현재 카운트가 같을시 초기화
                                    multilist.set(s[1].toInt() - 1, arrayOf(count[0], 1, count[2], 1))
                                } else {//아니면 현재 카운트+1
                                    multilist.set(s[1].toInt() - 1, arrayOf(count[0], count[1] + 1, count[2], 1))
                                }
                                break
                            }
                        }
                    } else {//edit mode
                        val list:ArrayList<Array<String>> = ArrayList()
                        for (s in soundlist) {
                            if (s[1].equals(currenrchain)) {
                                if (s.size == 5) {

                                    list.add(arrayOf(s[2], s[3], s[4]))
                                } else {
                                    list.add(arrayOf(s[2], s[3]))
                                }
                            }
                        }
                        (con as TabHostActivity).isButtonClicked(activity, textView.text.toString(), list)
                    }
                }

                //LED일때
                LayoutKey.PLAYBTN_LAYOUT_LED -> {
                    if (isEdit) {//편집모드
                        (con as TabHostActivity).editLED(textView.text.toString())
                    } else {//전체모드
                        val ledGet:ArrayList<Array<String>> = ArrayList()
                        for (i in ledlist) {
                            if (currenrchain.equals(i[2])) {
                                when (i.size) {
                                    3 -> {
                                        ledGet.add(arrayOf(i[0], i[1], i[2]))
                                    }
                                    4 -> {
                                        ledGet.add(arrayOf(i[0], i[1], i[2], i[3]))
                                    }
                                }
                            }
                        }
                        (con as TabHostActivity).recieveLED(ledGet, textView.text.toString())
                    }
                }
            }
        }
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayButton)
        setTypeArray(typedArray)
    }


    private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayButton, defStyle, 0)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyle: Int, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayButton, defStyle, defStyleAttr)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typeArray: TypedArray) {
        val text_string = typeArray.getString(R.styleable.PlayButton_name)
        textView.text = text_string
        typeArray.recycle()
    }



    /**sound 액티비티일때**/
    //whole있을때
    fun addSound(chain:String, name:String, rpeat:String, whole:String) {
        soundlist.add(arrayOf("0", chain, name, rpeat, whole))
        setMulti()
    }

    //whole없을떄
    fun addSound(chain:String, name:String, rpeat:String) {
        soundlist.add(arrayOf("0", chain, name, rpeat))
        setMulti()
    }

    //multilist초기화
    private fun initList(): Boolean {
        multilist.clear()
        try {
            for (a in 1.. chain.toInt()) {
                multilist.add(arrayOf(a, 0, 0, 0))
            }
            return true
        } catch (e:Exception) {
            return false
        } catch (e:kotlin.Exception) {
            return false
        }
    }

    //multilist세팅
    fun setMulti() {
        if (initList()) {
            for (s in soundlist) {
                val chain = s[1]

                var isIn = false
                for (m in 0..multilist.size - 1) {
                    val sin = multilist.get(m)
                    if (chain.toInt() == sin[0]) {
                        multilist.set(m, arrayOf(sin[0], 1, sin[2] + 1, 1))//chain current allcount forcount
                        isIn = true
                        break
                    }
                }
                if (!isIn) {
                    multilist.add(arrayOf(chain.toInt(), 1, 1, 1))
                }
            }
            setButtonisIn()
        }
    }

    //sound 반복/웜홀 변경
    fun changeSound(p:String, chain:String, name:String, rpeat:String, whole:String) {
        soundlist.set(findSound(p), arrayOf("0", chain, name, rpeat, whole))
    }

    //sound 반복 변경
    fun changeSound(p:String, chain:String, name:String, rpeat:String) {
        soundlist.set(findSound(p), arrayOf("0", chain, name, rpeat))
    }

    private fun findSound(p:String): Int {
        val spl = p.split("\\s+".toRegex())
        if (spl.size == 6) {
            for (a in 0..soundlist.size-1) {
                val i = soundlist.get(a)
                if (i[1].equals(spl[0])&&i[2].equals(spl[3])&&i[3].equals(spl[4])&&i[4].equals(spl[5])) {
                    return a
                }
            }
        } else {
            for (a in 0..soundlist.size-1) {
                val i = soundlist.get(a)
                if (i[1].equals(spl[0])&&i[2].equals(spl[3])&&i[3].equals(spl[4])) {
                    return a
                }
            }
        }
        return 0
    }

    fun removeSound(p:String) {
        soundlist.removeAt(findSound(p))
        setMulti()
    }


    /**LED**/
    fun addLED(name:String, content:String) {
        val names = name.split("\\s+".toRegex())
        when (names.size) {
            4 -> {//content name chain
                ledlist.add(arrayOf(content, name, names[0]))
            }
            5 -> {//content name chain multi
                ledlist.add(arrayOf(content, name, names[0], names[4]))
            }
        }
        setButtonisInLed()
    }

    fun changeLed(con:String, name:String) {
        var count = 0
        for (i in ledlist) {
            if (i[1].equals(name)) {
                break
            } else {
                count++
            }
        }
        val names = name.split("\\s+".toRegex())
        when (names.size) {
            4 -> {//content name chain
                ledlist.set(count, arrayOf(con, name, names[0]))
            }
            5 -> {//content name chain multi
                ledlist.set(count, arrayOf(con, name, names[0], names[4]))
            }
        }
    }

    //led멀티 설정
    fun initLED() {
        ledmulti.clear()
        for (a in 1.. chain.toInt()) {
            ledmulti.add(arrayOf(a, 0, 0, 0))
        }
    }

    fun setMultiLED() {

    }

    fun onLED(color:Int) {
        viewLED.visibility = View.VISIBLE
        viewLED.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    fun onLEDHtml(color:Int) {
        viewLED.visibility = View.VISIBLE
        viewLED.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    fun offLED() {
        viewLED.visibility = View.INVISIBLE
    }


    /**그밖의 처리**/
    //파일이 있는지 시각화
    private fun setButtonisIn() {
        var count = 0
        for (s in soundlist) {
            val chain = s[1]

            if (chain.equals(currenrchain)) {
                count++
            }
        }
        if (count == 0) {
            viewIn.visibility = View.INVISIBLE
        } else {
            viewIn.visibility = View.VISIBLE
        }
    }

    //파일이 있는지 시각화
    fun setButtonisInLed() {
        var count = 0
        for (s in ledlist) {
            val chain = s[2]

            if (chain==currenrchain) {
                count++
            }
        }
        if (count == 0) {
            viewIn.visibility = View.INVISIBLE
        } else {
            viewIn.visibility = View.VISIBLE
        }
    }

    fun disableShow() {
        viewIn.visibility = View.INVISIBLE
    }

    //체인의 전체갯수
    fun setChain(c:String) {
        chain = c
    }

    //현재 체인 업데이트
    fun setcurrentChain(c:String) {
        currenrchain = c
        setButtonisIn()
    }

    //play모드인지 아닌지 판별하는것 sound
    fun setIsPlay(isplay:Boolean) {
        isPlay = isplay
    }

    //edit모드인지 아닌지 판별하는것 led
    fun setIsEdit(isedit:Boolean) {
        isEdit = isedit
    }

    //리스트 전체 리셋
    fun resetSound() {
        soundlist.clear()
        multilist.clear()
    }

    fun resetLed() {
        ledlist.clear()
    }

    //현제 액티비티가 뭔지 판별
    fun setActivity(activity: String) {
        this.activity = activity
    }


    //LED On/Off
    override fun onLEDOn(on: ArrayList<Array<String>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLEDOff(off: ArrayList<Array<String>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
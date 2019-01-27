package com.uni.unitor.unitorm2.view.buttons

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
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
import com.uni.unitor.unitorm2.layout.LayoutKey


class PlayButton : RelativeLayout {

    lateinit var textView: TextView
    lateinit var viewIn:View
    private lateinit var con: Context
    private var soundlist:ArrayList<Array<String>> = ArrayList()
    private var multilist:ArrayList<Array<Int>> = ArrayList()
    private lateinit var activity: String
    private var isPlay:Boolean = false
    private var currenrchain:String = "1"
    private var chain:String = "1"

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

        this.isClickable = true
        textView.isClickable = true

        con = context

        addView(view)
        multilist.add(arrayOf(1, 0, 0, 0))//chain current allcount forcount
        textView.setOnClickListener {
            //TODO: sounds와 keyled 구
            when(activity) {
                LayoutKey.PLAYBTN_LAYOUT_SOUND -> {
                    if (isPlay) {//play mode
                        for (list in 0 until soundlist.size) {
                            val s = soundlist.get(list)

                            if (s[1].equals(currenrchain)) {
                                val count = multilist.get(s[1].toInt() - 1)
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

                LayoutKey.PLAYBTN_LAYOUT_LED -> {

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
    private fun initList() {
        multilist.clear()
        for (a in 1.. chain.toInt()) {
            multilist.add(arrayOf(a, 0, 0, 0))
        }
    }

    //multilist세팅
    private fun setMulti() {
        initList()
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

    //파일이 있는지 시각화
    private fun setButtonisIn() {
        var count = 0
        for (s in soundlist) {
            val chain = s[1]

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

    //체인의 전체갯수
    fun setChain(c:String) {
        chain = c
    }

    //현재 체인 업데이트
    fun setcurrentChain(c:String) {
        currenrchain = c
        setButtonisIn()
    }

    //play모드인지 아닌지 판별하는것
    fun setIsPlay(isplay:Boolean) {
        isPlay = isplay
    }

    //리스트 전체 리셋
    fun resetSound() {
        soundlist.clear()
        multilist.clear()
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

    //현제 액티비티가 뭔지 판별
    fun setActivity(activity: String) {
        this.activity = activity
    }

//    override fun onClick(v: View?) {
//        for (list in 0..soundlist.size - 1) {
//            val s = soundlist.get(list)
//
//            if (s[1].equals(chain)) {
//                val count = multilist.get(s[1].toInt())
//                if (s[1] > s[3]) {//넘길횟수 체크
//                    multilist.set(s[1].toInt(), arrayOf(count[0], count[1], count[2], count[3] + 1))
//                    continue
//                }
//                if (count[1] <= count[2]) {//총 카운트랑 현재 카운트가 작거나 같을시 재생
//                    if (s.size == 5) {
//                        (con as TabHostActivity).play(s[2], s[3], s[4])
//                        soundlist.set(list, arrayOf("0", s[1], s[2], s[3], s[4]))
//                    } else {
//                        (con as TabHostActivity).play(s[2], s[3])
//                        soundlist.set(list, arrayOf("0", s[1], s[2], s[3]))
//                    }
//                }
//                if (count[1] == count[2]) {//총 카운트랑 현재 카운트가 같을시 초기화
//                    multilist.set(s[1].toInt(), arrayOf(count[0], 1, count[2], 1))
//                } else {//아니면 현재 카운트+1
//                    multilist.set(s[1].toInt(), arrayOf(count[0], count[1] + 1, count[2], 1))
//                }
//                break
//            }
//        }
//    }
}
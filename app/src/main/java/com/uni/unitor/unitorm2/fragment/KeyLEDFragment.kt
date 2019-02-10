package com.uni.unitor.unitorm2.fragment

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import com.uni.unitor.unitorm2.File.PlayLED
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.LayoutKey
import com.uni.unitor.unitorm2.view.buttons.PlayButton
import com.uni.unitor.unitorm2.view.recycler.FileListAdapter
import com.uni.unitor.unitorm2.view.recycler.FileListItem
import com.uni.unitor.unitorm2.view.recycler.LedContentListAdapter
import com.uni.unitor.unitorm2.view.recycler.LedContentListItem
import com.uni.unitor.unitorm2.view.recycler.listener.RecyclerItemClickListener
import java.lang.StringBuilder
import kotlin.Exception



class KeyLEDFragment : Fragment(){

    private lateinit var spinner_chain: Spinner
    private lateinit var linear_buttons: LinearLayout
    private lateinit var layout_ledLeft: LinearLayout
    private lateinit var layout_editLeft: LinearLayout
    private lateinit var layout_ledRight: LinearLayout
    private lateinit var layout_editRight: LinearLayout
    private lateinit var recycler_LED: RecyclerView
    private lateinit var recycler_content: RecyclerView
    private lateinit var text_current_led:TextView
    private lateinit var button_previous:ImageButton
    private lateinit var seekBar_frame:SeekBar
    private lateinit var text_current_con:TextView
    private lateinit var recycler_frame:RecyclerView

    private val ledFrameAapter:LedContentListAdapter = LedContentListAdapter()
    private val ledContentAdapter:LedContentListAdapter = LedContentListAdapter()
    private val ledListAdpater:FileListAdapter = FileListAdapter()
    private lateinit var root:View
    private var bundle: Bundle? = null
    private var chain:String = ""
    private var isEdit: Boolean = false
    private lateinit var onRequestListener:OnKeyLEDRequestListener

    protected lateinit var velocity:IntArray

    //tabhostactivity에 등록된 리스너를 가져옴
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onRequestListener = context as OnKeyLEDRequestListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_keyled, container, false)

        velocity = intArrayOf(resources.getColor(R.color.v_0), resources.getColor(R.color.v_1), resources.getColor(R.color.v_2),
                resources.getColor(R.color.v_3), resources.getColor(R.color.v_4), resources.getColor(R.color.v_5), resources.getColor(R.color.v_6),
                resources.getColor(R.color.v_7), resources.getColor(R.color.v_8), resources.getColor(R.color.v_9), resources.getColor(R.color.v_10),
                resources.getColor(R.color.v_11), resources.getColor(R.color.v_12), resources.getColor(R.color.v_13),
                resources.getColor(R.color.v_14), resources.getColor(R.color.v_15), resources.getColor(R.color.v_16),
                resources.getColor(R.color.v_17), resources.getColor(R.color.v_18), resources.getColor(R.color.v_19),
                resources.getColor(R.color.v_20), resources.getColor(R.color.v_21), resources.getColor(R.color.v_22),
                resources.getColor(R.color.v_23), resources.getColor(R.color.v_24), resources.getColor(R.color.v_25),
                resources.getColor(R.color.v_26), resources.getColor(R.color.v_27), resources.getColor(R.color.v_28),
                resources.getColor(R.color.v_29), resources.getColor(R.color.v_30), resources.getColor(R.color.v_31),
                resources.getColor(R.color.v_32), resources.getColor(R.color.v_33), resources.getColor(R.color.v_34),
                resources.getColor(R.color.v_35), resources.getColor(R.color.v_36), resources.getColor(R.color.v_37),
                resources.getColor(R.color.v_38), resources.getColor(R.color.v_39), resources.getColor(R.color.v_40),
                resources.getColor(R.color.v_41), resources.getColor(R.color.v_42), resources.getColor(R.color.v_43),
                resources.getColor(R.color.v_44), resources.getColor(R.color.v_45), resources.getColor(R.color.v_46),
                resources.getColor(R.color.v_47), resources.getColor(R.color.v_48), resources.getColor(R.color.v_49),
                resources.getColor(R.color.v_50), resources.getColor(R.color.v_51), resources.getColor(R.color.v_52),
                resources.getColor(R.color.v_53), resources.getColor(R.color.v_54), resources.getColor(R.color.v_55),
                resources.getColor(R.color.v_56), resources.getColor(R.color.v_57), resources.getColor(R.color.v_58),
                resources.getColor(R.color.v_59), resources.getColor(R.color.v_60), resources.getColor(R.color.v_61),
                resources.getColor(R.color.v_62), resources.getColor(R.color.v_63), resources.getColor(R.color.v_64),
                resources.getColor(R.color.v_65), resources.getColor(R.color.v_66), resources.getColor(R.color.v_67),
                resources.getColor(R.color.v_68), resources.getColor(R.color.v_69), resources.getColor(R.color.v_70),
                resources.getColor(R.color.v_71), resources.getColor(R.color.v_72), resources.getColor(R.color.v_73),
                resources.getColor(R.color.v_74), resources.getColor(R.color.v_75), resources.getColor(R.color.v_76),
                resources.getColor(R.color.v_77), resources.getColor(R.color.v_78), resources.getColor(R.color.v_79),
                resources.getColor(R.color.v_80), resources.getColor(R.color.v_81), resources.getColor(R.color.v_82),
                resources.getColor(R.color.v_83), resources.getColor(R.color.v_84), resources.getColor(R.color.v_85),
                resources.getColor(R.color.v_86), resources.getColor(R.color.v_87), resources.getColor(R.color.v_88),
                resources.getColor(R.color.v_89), resources.getColor(R.color.v_90), resources.getColor(R.color.v_91),
                resources.getColor(R.color.v_92), resources.getColor(R.color.v_93), resources.getColor(R.color.v_94),
                resources.getColor(R.color.v_95), resources.getColor(R.color.v_96), resources.getColor(R.color.v_97),
                resources.getColor(R.color.v_98), resources.getColor(R.color.v_99), resources.getColor(R.color.v_100),
                resources.getColor(R.color.v_101), resources.getColor(R.color.v_102), resources.getColor(R.color.v_103),
                resources.getColor(R.color.v_104), resources.getColor(R.color.v_105), resources.getColor(R.color.v_106),
                resources.getColor(R.color.v_107), resources.getColor(R.color.v_108), resources.getColor(R.color.v_109),
                resources.getColor(R.color.v_110), resources.getColor(R.color.v_111), resources.getColor(R.color.v_112),
                resources.getColor(R.color.v_113), resources.getColor(R.color.v_114), resources.getColor(R.color.v_115),
                resources.getColor(R.color.v_116), resources.getColor(R.color.v_117), resources.getColor(R.color.v_118),
                resources.getColor(R.color.v_119), resources.getColor(R.color.v_120), resources.getColor(R.color.v_121),
                resources.getColor(R.color.v_122), resources.getColor(R.color.v_123), resources.getColor(R.color.v_124),
                resources.getColor(R.color.v_125), resources.getColor(R.color.v_126), resources.getColor(R.color.v_127))


        if (savedInstanceState != null)bundle = savedInstanceState

        onRequestListener.setKeyLEDContext(this)

        linear_buttons = root.findViewById<LinearLayout>(R.id.Layout_Btns)
        layout_ledLeft = root.findViewById(R.id.Layout_LED_ALL_LEFT)
        layout_editLeft = root.findViewById(R.id.Layout_LED_EDIT_LEFT)
        layout_ledRight = root.findViewById(R.id.Layout_LED_ALL_RIGHT)
        layout_editRight = root.findViewById(R.id.Layout_LED_EDIT_RIGHT)
        spinner_chain = root.findViewById(R.id.Spinner_chain_LED) as Spinner
        recycler_LED = root.findViewById(R.id.recycle_files)
        recycler_content = root.findViewById(R.id.recycle_content)
        text_current_led = root.findViewById(R.id.text_current_led)
        button_previous = root.findViewById(R.id.button_edit_previous)
        seekBar_frame = root.findViewById(R.id.seekbar_frame)
        text_current_con = root.findViewById(R.id.text_current_frame)
        recycler_frame = root.findViewById(R.id.recycle_frame)

        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setActivity(LayoutKey.PLAYBTN_LAYOUT_LED)
                if (savedInstanceState != null) {
                    playButton.setcurrentChain(savedInstanceState.getString(LayoutKey.KEYSOUND_BUNDLE_CHAIN))
                } else {
                    playButton.setcurrentChain("1")
                }
            }
        }

        recycler_LED.layoutManager = LinearLayoutManager(activity)
        recycler_LED.itemAnimator = DefaultItemAnimator()
        val dividerItemDecoration = DividerItemDecoration(activity, LinearLayoutManager(activity).orientation)
        recycler_LED.addItemDecoration(dividerItemDecoration)
        recycler_LED.adapter = ledListAdpater

        recycler_content.layoutManager = LinearLayoutManager(activity)
        recycler_content.itemAnimator = DefaultItemAnimator()
        recycler_content.addItemDecoration(dividerItemDecoration)
        recycler_content.adapter = ledContentAdapter

        recycler_frame.layoutManager = LinearLayoutManager(activity)
        recycler_frame.itemAnimator = DefaultItemAnimator()
        recycler_frame.addItemDecoration(dividerItemDecoration)
        recycler_frame.adapter = ledFrameAapter

        //뒤로가기 버튼
        button_previous.setOnClickListener {
            setLayoutMode(false)
            ledContentAdapter.clearItem()
            text_current_led.setText("")
            for (vertical in 1..8) {
                for (horizontal in 1..8) {
                    val playButton = root.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                    playButton.setIsEdit(false)
                }
            }
        }

        //체인 선택 리스너뷰
        spinner_chain.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent!!.childCount != 0) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    try {
                        val chain_selected = (spinner_chain.getItemAtPosition(position) as String).split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        chain = chain_selected
                        setChainButton()
                        //buttonCurrent = ""
                        //text_current.setText(buttonCurrent)
                        //soundPlayListAdapter.clearItem()
                        ledListAdpater.clearItem()
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
            }
        }

        //LED파일 선택 리스너
        recycler_LED.addOnItemTouchListener(RecyclerItemClickListener(container!!.context, recycler_LED, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClicked(view: View, position: Int) {
                val x = ledListAdpater.getItem(position)
                var menu = PopupMenu(context, view)
                menu.menuInflater.inflate(R.menu.menu_led_list, menu.menu)
                menu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                        when (menuItem.itemId) {
                            R.id.menu_led_select -> {//TODO: LED 선택
                                setLayoutMode(true)
                                text_current_led.text = x.fname

                                val builder = StringBuilder()//프레임 단위로 정리
                                for (i in x.fpath!!.split("\\n".toRegex())) {
                                    if (i.startsWith("d")||i.startsWith("delay")) {
                                        builder.append(i + "\n")
                                        val con = LedContentListItem()
                                        con.line = "Frame " + (ledContentAdapter.itemCount + 1).toString()
                                        con.contents = builder.toString()
                                        ledContentAdapter.addItem(con)
                                        builder.clear()
                                    } else {
                                        builder.append(i + "\n")
                                    }
                                }
                                seekBar_frame.max = ledContentAdapter.itemCount - 1
                                seekBar_frame.progress = 0

                                for (vertical in 1..8) {
                                    for (horizontal in 1..8) {
                                        val playButton = root.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                                        playButton.setIsEdit(true)
                                    }
                                }
                            }
                            R.id.menu_led_play -> {//TODO: Play LED
                                //PlayLED(x.fpath!!, root).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                                //com.uni.unitor.unitorm2.File.PlayLED(x.fpath, root, context, velocity.toTypedArray()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                            }
                            R.id.menu_led_delete -> {}
                        }
                        return true
                    }
                })
                menu.show()
            }

            override fun onLongItemClicked(view: View?, position: Int) {

            }
        }))

        recycler_content.addOnItemTouchListener(RecyclerItemClickListener(container!!.context, recycler_content, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClicked(view: View, position: Int) {
                seekBar_frame.progress = position
                setSelectFrame(position)
            }

            override fun onLongItemClicked(view: View?, position: Int) {

            }
        }))

        //드래그로 고르기
        seekBar_frame.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar:SeekBar) {
                setSelectFrame(seekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar:SeekBar) {

            }

            override fun onProgressChanged(seekBar:SeekBar , progress:Int, fromUser:Boolean) {
                recycler_content.layoutManager!!.scrollToPosition(progress)
                val con = ledContentAdapter.getItem(progress)
                text_current_con.setText("Selected: " +  con.line)
            }
        })

        if (savedInstanceState == null) {
            setLayoutMode(false)
            chain = "1"
        } else {
            chain = savedInstanceState.getString(LayoutKey.KEYLED_BUNDLE_CHAIN)
            if (savedInstanceState!!.getBoolean(LayoutKey.KEYLED_BUNDLE_ISPLAY)) {
                setLayoutMode(true)
            } else {
                setLayoutMode(false)
            }
        }

        return root
    }

    private fun setSelectFrame(position:Int) {
        ledFrameAapter.clearItem()

        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.offLED()
            }
        }

        val now = position

        val con = ledContentAdapter.getItem(now)

        for (i in con.contents!!.split("\\n".toRegex())) {
            if (i.startsWith("d")||i.startsWith("delay")) {
                continue
            } else if (!i.isEmpty()) {
                if (i.startsWith("o")||i.startsWith("on")) {
                    try {
                        val c = i.split("\\s+".toRegex())
                        val playButton = root.findViewWithTag(c[1] + " " + c[2]) as PlayButton
                        playButton.onLED(velocity[c[4].toInt()])
                    } catch (e:Exception) {
                        continue
                    }
                } else if (i.startsWith("f")||i.startsWith("off")) {
                    try {
                        val c = i.split("\\s+".toRegex())
                        val playButton = root.findViewWithTag(c[1] + " " + c[2]) as PlayButton
                        playButton.offLED()
                    } catch (e:Exception) {
                        continue
                    }
                }
                val item = LedContentListItem()
                item.line = i
                item.contents = ""
                ledFrameAapter.addItem(item)
            }
        }
    }

    private fun setLayoutMode(mode:Boolean) {
        if (mode) {//true = edit mode
            layout_editRight.visibility = View.VISIBLE
            layout_editLeft.visibility = View.VISIBLE
            layout_ledRight.visibility = View.GONE
            layout_ledLeft.visibility = View.GONE
        } else {//false = view all mode
            layout_editRight.visibility = View.GONE
            layout_editLeft.visibility = View.GONE
            layout_ledRight.visibility = View.VISIBLE
            layout_ledLeft.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        onRequestListener.onRequestLED(ListenerKey.KEY_LED_CHAIN, "")
        onRequestListener.onRequestLED(ListenerKey.KEY_LED_FILE, "")
    }

    //tabhost에 chain갯수 요청 결과 처리
    fun setChain(chain:String?) {
        var adapters:ArrayAdapter<String?>? = null
        try {
            val chain_num = chain!!.toInt()
            val chainlist:Array<String?> = arrayOfNulls<String>(chain_num)
            val list:ArrayList<String?> = ArrayList()
            //val chainlist = arrayOf(chain_num)
            for (i in 0 until chain_num step 1) {
                chainlist[i] = String.format(getString(R.string.spinner_chain), i+1)
                list.add(String.format(getString(R.string.spinner_chain), i+1))
            }
            adapters = ArrayAdapter(activity, android.support.design.R.layout.support_simple_spinner_dropdown_item, list)
            spinner_chain.setAdapter(adapters)
        } catch (e:Exception) {
            val s = arrayOf<String>(String.format(getString(R.string.spinner_chain), "1"))
            adapters = ArrayAdapter(activity, android.support.design.R.layout.support_simple_spinner_dropdown_item, s)
            spinner_chain.adapter = adapters
        }
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setChain(chain!!)
            }
        }
        if (bundle != null) {
            spinner_chain.setSelection(bundle!!.getString(LayoutKey.KEYLED_BUNDLE_CHAIN).toInt() - 1)
        } else {
            spinner_chain.setSelection(0)
        }
    }

    //playbutton에 chain설정
    private fun setChainButton() {
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setcurrentChain(chain)
            }
        }
    }

    //led파일 가져오기
    fun setLEDFile(content : ArrayList<Array<String>>?) {//TODO: led파일 버튼에 처리
        if (content != null) {
             for (i in content) {
                 val name = i[0]
                 val content = i[1]
                 val names = name.split("\\s+".toRegex())
                 if (names.size in 4..5) {
                     try {
                         val playButton = root.findViewWithTag(names[1] + " " + names[2])as PlayButton
                         playButton.addLED(name, content)
                     } catch (e:Exception) {
                         continue
                     }
                 }
             }
        }
    }

    //LED파일 리스트에 등록
    fun setLEDList(ledlist:ArrayList<Array<String>>) {
        ledListAdpater.clearItem()
        for (i in ledlist) {
            val name = i[1]
            val content = i[0]
            val c = FileListItem()
            c.fname = name
            c.fpath = content
            ledListAdpater.addItem(c)
        }
    }


    //버튼 크기조절
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewTreeObserver = linear_buttons.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    linear_buttons.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    linear_buttons.viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
                val height = linear_buttons.height
                val params = RelativeLayout.LayoutParams(height, height)
                params.addRule(RelativeLayout.CENTER_HORIZONTAL)
                linear_buttons.layoutParams = params
            }
        })
    }

    //keyled수정시 tabhost로 데이터 요청/전달하는 리스너 interface 구현
    interface OnKeyLEDRequestListener {
        fun onRequestLED(type:String?, content:String?) {

        }
        fun onRequestLED(type:String?, content1:String?, content2: String?)
        fun setKeyLEDContext(keyLEDFragment: KeyLEDFragment) {

        }
    }

    interface OnPlayLED {
        fun onLEDOn(on:ArrayList<Array<String>>)
        fun onLEDOff(off:ArrayList<Array<String>>)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(LayoutKey.KEYLED_BUNDLE_ISPLAY, isEdit)
        outState.putString(LayoutKey.KEYLED_BUNDLE_CHAIN, chain)
    }


    //LED 재생
    inner class PlayLED(content: String, root: View) : AsyncTask<String, String, Boolean>() {

        private val content = content.split("\\n".toRegex())
        private val root = root
        protected var isPlay = false
        private var play:Play? = null
        override fun onPreExecute() {

        }

        override fun doInBackground(vararg params: String?): Boolean {
            val builder = StringBuilder()
            val list = ArrayList<String>()
            try {
                for (i in content) {
                    val co = i.split("\\s+".toRegex())
                    if (co[0].startsWith("delay")||co[0].startsWith("d")) {
                        list.add(builder.toString())
                        builder.clear()
                        builder.append(i + "\n")
                    } else {
                        builder.append(i + "\n")
                    }
                }

                for (i in list) {
                    publishProgress(i, "start")
                    isPlay = true
                    while (play != null||play!!.state == Thread.State.RUNNABLE) {

                    }
                }
                return true
            } catch (e:Exception) {
                return false
            }
        }

        override fun onProgressUpdate(vararg values: String?) {
            when (values[1]) {
                "start" -> {
                    val content = values[0]!!.split("\\n".toRegex())
                    var time = 0
                    if (content[0].startsWith("delay")||content[0].startsWith("d")) {
                        try {
                            time = content[0].split("\\s+".toRegex())[1].toInt()
                        } catch (e:Exception) {
                            time = 0
                        }
                    }
                    Play(time, values[0]!!).start()
                }

                "play" -> {
                    val con = values[0]!!.split("\\s+".toRegex())
                    when(con[0]) {
                        "o", "on" -> {
                            when (con[3]) {
                                "a", "auto" -> {
                                    val playButton = root.findViewWithTag(con[1] + " " + con[2])as PlayButton
                                    playButton.onLED(velocity[con[4].toInt()])
                                }

                                else -> {
                                    val playButton = root.findViewWithTag(con[1] + " " + con[2])as PlayButton
                                    playButton.onLEDHtml(Color.parseColor("#" + con[4]))
                                }
                            }
                        }

                        "f", "off" -> {
                            val playButton = root.findViewWithTag(con[1] + " " + con[2])as PlayButton
                            playButton.offLED()
                        }
                    }
                }
            }
        }

        inner class Play(time:Int, content: String) : Thread() {

            private val time = time
            private val content = content.split("\\n".toRegex())

            override fun run() {
                sleep(time.toLong())
                for (i in content) {
                    if (i.startsWith("delay")||i.startsWith("d")) {
                        continue
                    } else {
                        publishProgress(i, "play")
                    }
                }
                isPlay = false

                return
            }
        }

        override fun onPostExecute(result: Boolean?) {
//            if (play != null) {
//                if (play!!.state == Thread.State.RUNNABLE) {
//                    play
//                }
//            }
            Toast.makeText(context, "led Finish", Toast.LENGTH_SHORT).show()
        }

    }
}
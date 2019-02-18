package com.uni.unitor.unitorm2.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import com.uni.unitor.unitorm2.File.PlayLED
import com.uni.unitor.unitorm2.File.sharedpreference.PreferenceKey
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.LayoutKey
import com.uni.unitor.unitorm2.view.buttons.PlayButton
import com.uni.unitor.unitorm2.view.recycler.FileListAdapter
import com.uni.unitor.unitorm2.view.recycler.FileListItem
import com.uni.unitor.unitorm2.view.recycler.LedContentListAdapter
import com.uni.unitor.unitorm2.view.recycler.LedContentListItem
import com.uni.unitor.unitorm2.view.recycler.listener.RecyclerItemClickListener
import kotlin.Exception
import kotlin.text.StringBuilder
import com.uni.unitor.unitorm2.view.spinner.SpinnerColordapter
import java.util.*
import java.util.Collections.addAll




class KeyLEDFragment : Fragment(){

    private var spinner_chain: Spinner? = null
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
    private lateinit var button_add:ImageButton
    private lateinit var edit_delay:EditText
    private lateinit var group_command:RadioGroup
    private lateinit var spinner_velocity:Spinner
    private lateinit var button_add_led:ImageButton
    private lateinit var text_currenr_button:TextView

    private val ledFrameAapter:LedContentListAdapter = LedContentListAdapter()
    private val ledContentAdapter:LedContentListAdapter = LedContentListAdapter()
    private val ledListAdpater:FileListAdapter = FileListAdapter()
    private var root:View? = null
    private var bundle: Bundle? = null
    private var chain:String = ""
    private var isEdit: Boolean = false
    private lateinit var onRequestListener:OnKeyLEDRequestListener
    private var clickedButton:String = ""

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

        linear_buttons = root!!.findViewById<LinearLayout>(R.id.Layout_Btns)
        layout_ledLeft = root!!.findViewById(R.id.Layout_LED_ALL_LEFT)
        layout_editLeft = root!!.findViewById(R.id.Layout_LED_EDIT_LEFT)
        layout_ledRight = root!!.findViewById(R.id.Layout_LED_ALL_RIGHT)
        layout_editRight = root!!.findViewById(R.id.Layout_LED_EDIT_RIGHT)
        spinner_chain = root!!.findViewById(R.id.Spinner_chain_LED) as Spinner
        recycler_LED = root!!.findViewById(R.id.recycle_files)
        recycler_content = root!!.findViewById(R.id.recycle_content)
        text_current_led = root!!.findViewById(R.id.text_current_led)
        button_previous = root!!.findViewById(R.id.button_edit_previous)
        seekBar_frame = root!!.findViewById(R.id.seekbar_frame)
        text_current_con = root!!.findViewById(R.id.text_current_frame)
        recycler_frame = root!!.findViewById(R.id.recycle_frame)
        button_add = root!!.findViewById(R.id.button_edit_add)
        edit_delay = root!!.findViewById(R.id.edit_delay)
        group_command = root!!.findViewById(R.id.group_command)
        spinner_velocity = root!!.findViewById(R.id.spinner_velocity)
        button_add_led = root!!.findViewById(R.id.button_edit_add_led)
        text_currenr_button = root!!.findViewById(R.id.text_current_button)

        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setActivity(LayoutKey.PLAYBTN_LAYOUT_LED)
                if (savedInstanceState != null) {
                    playButton.setcurrentChain(savedInstanceState.getString(LayoutKey.KEYSOUND_BUNDLE_CHAIN))
                } else {
                    playButton.setcurrentChain("1")
                }
            }
        }

        val LED_num = ArrayList<String>()
        val color = ArrayList<Int>()
        val v = arrayOfNulls<Int>(velocity.size)
        for (t in 0 until velocity.size) {
            color.add(velocity[t])
        }
        for (i in 0..127) {
            val s = i.toString()
            LED_num.add(s)
        }
        val spinnerColordapter = SpinnerColordapter(activity!!, LED_num, color)
        spinner_velocity.setAdapter(spinnerColordapter)

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
                    val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                    isEdit = false
                    playButton.setIsEdit(isEdit)
                    playButton.offLED()
                    playButton.setButtonisInLed()
                }
            }
            layout_editRight.isEnabled = false
            ledFrameAapter.clearItem()
            edit_delay.setText("")
            text_current_con.setText("")
            text_currenr_button.text = ""
            clickedButton = ""
        }

        //frame 추가 버튼
        button_add.setOnClickListener {
            val con = LedContentListItem()
            con.line = "Frame " + (ledContentAdapter.itemCount + 1)
            con.contents = "d 10\n"
            ledContentAdapter.addItem(con)
            modifiedLed()
            seekBar_frame.max = ledContentAdapter.itemCount - 1
            seekBar_frame.progress = ledContentAdapter.itemCount - 1
        }

        //led파일 추가 버튼
        button_add_led.setOnClickListener {
            if (!clickedButton.isEmpty()) {
                val con = FileListItem()
                val name = chain + " " + clickedButton + " 1 " + ledListAdpater.itemCount.toString()
                val path = Environment.getExternalStorageDirectory().absolutePath + "/unipackProject/work/keyLED/" + name
                con.fname = name
                con.fpath = ""
                onRequestListener.onRequestLED(ListenerKey.KEY_LED_FILE_NEW, path)
                ledListAdpater.addItem(con)

                onRequestListener.onRequestLED(ListenerKey.KEY_LED_FILE, "")
            } else {
                Toast.makeText(context, context!!.getString(R.string.toast_sound_noselect), Toast.LENGTH_SHORT).show()
            }
        }

        //체인 선택 리스너뷰
        spinner_chain!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent!!.childCount != 0) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    try {
                        val chain_selected = (spinner_chain!!.getItemAtPosition(position) as String).split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
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
                                text_currenr_button.text = ""
                                clickedButton = ""
                                text_current_led.text = x.fname

                                var finised = false
                                val builder = StringBuilder()//프레임 단위로 정리
                                for (i in x.fpath!!.split("\\n".toRegex())) {
                                    if (!i.isEmpty()) {
                                        if (i.startsWith("d")||i.startsWith("delay")) {
                                            builder.append(i + "\n")
                                            val con = LedContentListItem()
                                            con.line = "Frame " + (ledContentAdapter.itemCount + 1).toString()
                                            con.contents = builder.toString()
                                            ledContentAdapter.addItem(con)
                                            builder.clear()
                                            finised = true
                                        } else {
                                            builder.append(i + "\n")
                                            finised = false
                                        }
                                    }
                                }
                                if (!finised) {//마지막에 딜레이가 없을때 추가해줌
                                    builder.append("d 10\n")
                                    val con = LedContentListItem()
                                    con.line = "Frame " + (ledContentAdapter.itemCount + 1).toString()
                                    con.contents = builder.toString()
                                    ledContentAdapter.addItem(con)
                                    builder.clear()
                                }
                                if (ledContentAdapter.itemCount != 0)  {
                                    seekBar_frame.max = ledContentAdapter.itemCount - 1
                                    seekBar_frame.progress = 0
                                }
                                for (vertical in 1..8) {
                                    for (horizontal in 1..8) {
                                        val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                                        isEdit = false
                                        playButton.disableShow()
                                    }
                                }
                                ledListAdpater.clearItem()
                            }
                            R.id.menu_led_play -> {//TODO: Play LED
                                //PlayLED(x.fpath!!, root).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                                //com.uni.unitor.unitorm2.File.PlayLED(x.fpath, root, context, velocity.toTypedArray()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                            }
                            R.id.menu_led_delete -> {
                                val x = ledListAdpater.getItem(position)
                                val dialog = AlertDialog.Builder(context)
                                dialog.setTitle(String.format(context!!.getString(R.string.dialog_title_delete_frame), x.fname))
                                dialog.setMessage(context!!.getString(R.string.dialog_delete_led))
                                dialog.setPositiveButton(context!!.getString(R.string.dialog_button_frame_delete)) { dialog, which ->
                                    onRequestListener.onRequestLED(ListenerKey.KEY_LED_FILE_DELETE, x.fname)
                                }
                                dialog.setNegativeButton(context!!.getString(R.string.cancel), null)
                                dialog.show()
                            }
                        }
                        return true
                    }
                })
                menu.show()
            }

            override fun onLongItemClicked(view: View?, position: Int) {

            }
        }))

        //frame select
        recycler_content.addOnItemTouchListener(RecyclerItemClickListener(container!!.context, recycler_content, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClicked(view: View, position: Int) {
                seekBar_frame.progress = position
                setSelectFrame(position)
                layout_editRight.isEnabled = true
            }

            override fun onLongItemClicked(view: View?, position: Int) {
                val x = ledContentAdapter.getItem(position)
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle(String.format(context!!.getString(R.string.dialog_title_delete_frame), x.line))
                dialog.setPositiveButton(context!!.getString(R.string.dialog_button_frame_delete)) { dialog, which ->
                    ledContentAdapter.deleteItem(x.line!!)
                    ledContentAdapter.changeName()
                    modifiedLed()
                    ledFrameAapter.clearItem()
                    text_current_con.text = ""
                }
                dialog.setNegativeButton(context!!.getString(R.string.cancel), null)
                dialog.show()
            }
        }))

        //프레임 리스트뷰
        recycler_frame.addOnItemTouchListener(RecyclerItemClickListener(container!!.context, recycler_frame, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClicked(view: View, position: Int) {
                //delete
                val x = ledFrameAapter.getItem(position)
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle(String.format(context!!.getString(R.string.dialog_title_delete_frame), x.line))
                dialog.setPositiveButton(context!!.getString(R.string.dialog_button_frame_delete)) { dialog, which ->
                    ledFrameAapter.deleteItem(x.line!!)
//                    val s = x.line!!.split("\\s+".toRegex())
//                    (root.findViewWithTag(s[1] + " " + s[2]) as PlayButton).offLED()
//                    for (q in ledFrameAapter.getAllItem().asReversed()) {
//                        if (q.line!!.contains(s[1] + " " + s[2])) {
//                            if (q.line!!.startsWith("o")&&q.line!!.startsWith("on")) {
//                                try {
//                                    (root.findViewWithTag(s[1] + " " + s[2]) as PlayButton).onLED(velocity[q.line!!.split("\\s+".toRegex())[4].toInt()])
//                                } catch (e:Exception) {
//
//                                }
//                            }
//                        }
//                    }
                    var c = 0
                    for (h in ledContentAdapter.getAllItem()) {
                        if (h.line.equals(text_current_con.text.substring(10))) {
                            val builder = StringBuilder()
                            val list = ArrayList<String>()
                            for (m in h.contents!!.split("\\n+".toRegex())) {
                                if (!m.isEmpty()&&!m.equals(x.line)) {
                                    list.add(m)
                                }
                            }
                            for (k in list) {
                                builder.append(k + "\n")
                            }
                            val l = LedContentListItem()
                            l.line = h.line
                            l.contents = builder.toString()
                            ledContentAdapter.changeItem(c, l)
                            modifiedLed()
                            break
                        } else {
                            c++
                        }
                    }
                    setSelectFrame(c)
                }
                dialog.setNegativeButton(context!!.getString(R.string.cancel), null)
                dialog.show()
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
                if (progress <= ledContentAdapter.itemCount - 1) {
                    recycler_content.layoutManager!!.scrollToPosition(progress)
                }
            }
        })

        //edittext바뀔때마다 delay 변경 전달
        edit_delay!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (isEdit&&!text_current_con.text.isEmpty()) {
                    var modifide = "1"
                    if (!editable.toString().isEmpty()) {
                        modifide = editable.toString()
                    }
                    val pos = text_current_con.text.substring(16).toInt() - 1
                    val builder = StringBuilder()
                    for (i in ledContentAdapter.getItem(pos).contents!!.split("\\n".toRegex())) {
                        if (i.startsWith("d")||i.startsWith("delay")) {
                            builder.append("d " + modifide + "\n")
                        } else {
                            builder.append(i + "\n")
                        }
                    }
                    val con = LedContentListItem()
                    con.line = ledContentAdapter.getItem(pos).line
                    con.contents = builder.toString()
                    ledContentAdapter.changeItem(pos, con)
                    modifiedLed()
                }
            }
        })

        if (savedInstanceState == null) {
            setLayoutMode(false)
            chain = "1"
        } else {
            chain = savedInstanceState.getString(LayoutKey.KEYLED_BUNDLE_CHAIN)
//            if (savedInstanceState!!.getBoolean(LayoutKey.KEYLED_BUNDLE_ISPLAY)) {
//                setLayoutMode(true)
//            } else {
//                setLayoutMode(false)
//            }
        }

        return root
    }

    //led 수정
    private fun modifiedLed() {
        val builder = StringBuilder()
        for (i in ledContentAdapter.getAllItem()) {
            builder.append(i.contents)
        }
        onRequestListener.onRequestLED(ListenerKey.KEY_LED_MODIFIDE, builder.toString(), text_current_led.text.toString())
        val s = text_current_led.text.toString().split("\\s+".toRegex())
        val playButton = root!!.findViewWithTag(s[1] + " " + s[2]) as PlayButton
        playButton.changeLed(builder.toString(), text_current_led.text.toString())
    }

    //frame 선택
    private fun setSelectFrame(position:Int) {
        ledFrameAapter.clearItem()

        isEdit = true
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setIsEdit(isEdit)
                playButton.offLED()
            }
        }

        val now = position

        if (position >= 1) {
            val con = ledContentAdapter.getItem(now - 1)
            for (i in con.contents!!.split("\\n".toRegex())) {
                if (!i.isEmpty()) {
                    if (i.startsWith("o")||i.startsWith("on")) {
                        try {
                            val c = i.split("\\s+".toRegex())
                            val playButton = root!!.findViewWithTag(c[1] + " " + c[2]) as PlayButton
                            playButton.onLED(velocity[c[4].toInt()])
                        } catch (e:Exception) {
                            continue
                        }
                    } else if (i.startsWith("f")||i.startsWith("off")) {
                        try {
                            val c = i.split("\\s+".toRegex())
                            val playButton = root!!.findViewWithTag(c[1] + " " + c[2]) as PlayButton
                            playButton.offLED()
                        } catch (e:Exception) {
                            continue
                        }
                    }
                }
            }
        }

        val con = ledContentAdapter.getItem(now)

        text_current_con.setText("Selected: " +  con.line)

        for (i in con.contents!!.split("\\n".toRegex())) {
            if (i.startsWith("d")||i.startsWith("delay")) {
                edit_delay.setText(i.split("\\s+".toRegex())[1])
                continue
            } else if (!i.isEmpty()) {
                if (i.startsWith("o")||i.startsWith("on")) {
                    try {
                        val c = i.split("\\s+".toRegex())
                        val playButton = root!!.findViewWithTag(c[1] + " " + c[2]) as PlayButton
                        playButton.onLED(velocity[c[4].toInt()])
                    } catch (e:Exception) {
                        continue
                    }
                } else if (i.startsWith("f")||i.startsWith("off")) {
                    try {
                        val c = i.split("\\s+".toRegex())
                        val playButton = root!!.findViewWithTag(c[1] + " " + c[2]) as PlayButton
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

    //layout 모드 변경
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

    //receive edit
    fun receiveEdit(id:String) {//TODO: led modifiy
        val playButton = root!!.findViewWithTag(id) as PlayButton
        when (group_command.checkedRadioButtonId) {

            R.id.radio_edit_on -> {
                playButton.onLED(velocity[spinner_velocity.selectedItemPosition])
                val command = "o " + id + " a " + spinner_velocity.selectedItemPosition
                val i = LedContentListItem()
                i.line = command
                i.contents = ""
                ledFrameAapter.addItem(i)
                var c = 0
                for (h in ledContentAdapter.getAllItem()) {
                    if (h.line.equals(text_current_con.text.substring(10))) {
                        val builder = StringBuilder()
                        var count=  0
                        val list = ArrayList<String>()
                        for (m in h.contents!!.split("\\n+".toRegex())) {
                            if (!m.isEmpty()) {
                                list.add(m)
                            }
                        }
                        for (k in list) {
                            if (count+1 == list.size) {
                                builder.append(command + "\n")
                            }
                            builder.append(k + "\n")
                            count++
                        }
                        val l = LedContentListItem()
                        l.line = h.line
                        l.contents = builder.toString()
                        ledContentAdapter.changeItem(c, l)
                        modifiedLed()
                        break
                    } else {
                        c++
                    }
                }
            }

            R.id.radio_edit_off -> {
                playButton.offLED()
                val command = "f " + id
                val i = LedContentListItem()
                i.line = command
                i.contents = ""
                ledFrameAapter.addItem(i)
                var c = 0
                for (h in ledContentAdapter.getAllItem()) {
                    if (h.line.equals(text_current_con.text.substring(10))) {
                        val builder = StringBuilder()
                        var count = 0
                        val list = ArrayList<String>()
                        for (m in h.contents!!.split("\\n+".toRegex())) {
                            if (!m.isEmpty()) {
                                list.add(m)
                            }
                        }
                        for (k in list) {
                            if (count+1 == list.size) {
                                builder.append(command + "\n")
                            }
                            builder.append(k + "\n")
                            count++
                        }
                        val l = LedContentListItem()
                        l.line = h.line
                        l.contents = builder.toString()
                        ledContentAdapter.changeItem(c, l)
                        modifiedLed()
                        break
                    } else {
                        c++
                    }
                }
            }
        }
    }

    //led 삭제 끝
    fun ledDeleteFinished(newlist:ArrayList<Array<String>>) {
        setLEDList(newlist, clickedButton)
    }

    //tabhost에 chain갯수 요청 결과 처리
    fun setChain(chain:String?) {
        if (spinner_chain != null&&root != null&&activity != null) {
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
                spinner_chain!!.setAdapter(adapters)
            } catch (e:Exception) {
                val s = arrayOf<String>(String.format(getString(R.string.spinner_chain), "1"))
                adapters = ArrayAdapter(activity, android.support.design.R.layout.support_simple_spinner_dropdown_item, s)
                spinner_chain!!.adapter = adapters
            }
            for (vertical in 1..8) {
                for (horizontal in 1..8) {
                    val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                    playButton.setChain(chain!!)
                }
            }
            if (bundle != null) {
                spinner_chain!!.setSelection(bundle!!.getString(LayoutKey.KEYLED_BUNDLE_CHAIN).toInt() - 1)
            } else {
                spinner_chain!!.setSelection(0)
            }
        }
    }

    //playbutton에 chain설정
    private fun setChainButton() {
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setcurrentChain(chain)
                playButton.setButtonisInLed()
            }
        }
    }

    //led파일 가져오기
    fun setLEDFile(content : ArrayList<Array<String>>?) {//TODO: led파일 버튼에 처리
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.resetLed()
            }
        }
        if (content != null) {
             for (i in content) {
                 val name = i[0]
                 val content = i[1]
                 val names = name.split("\\s+".toRegex())
                 if (names.size in 4..5) {
                     try {
                         val playButton = root!!.findViewWithTag(names[1] + " " + names[2])as PlayButton
                         playButton.addLED(name, content)
                     } catch (e:Exception) {
                         continue
                     }
                 }
             }
        }
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setButtonisInLed()
            }
        }
    }

    //LED파일 리스트에 등록
    fun setLEDList(ledlist:ArrayList<Array<String>>, clicked:String) {
        clickedButton = clicked
        text_currenr_button.text = chain + " " + clicked
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

    override fun onStop() {
        super.onStop()
        clickedButton = ""
        text_currenr_button.text = ""
        isEdit = false
        ledContentAdapter.clearItem()
        text_current_led.setText("")
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setIsEdit(isEdit)
                playButton.setButtonisInLed()
                playButton.offLED()
            }
        }
        layout_editRight.isEnabled = false
        ledFrameAapter.clearItem()
        edit_delay.setText("")
        text_current_con.setText("")
        setLayoutMode(false)
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
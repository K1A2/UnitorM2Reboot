package com.uni.unitor.unitorm2.fragment

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import com.uni.unitor.unitorm2.R
import java.lang.Exception
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import com.uni.unitor.unitorm2.view.buttons.PlayButton
import android.widget.RadioGroup
import com.uni.unitor.unitorm2.layout.LayoutKey
import com.uni.unitor.unitorm2.view.recycler.FileListAdapter
import com.uni.unitor.unitorm2.view.recycler.FileListItem
import com.uni.unitor.unitorm2.view.recycler.UnipackListItem
import com.uni.unitor.unitorm2.view.recycler.listener.RecyclerItemClickListener
import com.uni.unitor.unitorm2.R.string.text_current
import android.support.v7.widget.DividerItemDecoration
import java.util.*
import kotlin.Comparator

/**keysound 편집 화면**/

class KeySoundFragment : Fragment(){

    private lateinit var linear_buttons:LinearLayout
    private var spinner_chain:Spinner? = null
    private lateinit var radioG_mode:RadioGroup
    private lateinit var list_sounds:RecyclerView
    private lateinit var list_playlist:RecyclerView
    private lateinit var text_current:TextView
    private lateinit var button_add:ImageButton

    private var bundle: Bundle? = null
    private lateinit var onRequestListener:OnKeySoundRequestListener
    private var soundListAdapter:FileListAdapter = FileListAdapter()
    private var soundPlayListAdapter:FileListAdapter = FileListAdapter()
    private var isPlay:Boolean = false
    private var root:View? = null
    private var chain:String = "1"
    private var buttonCurrent:String = ""

    //tabhostactivity에 등록된 리스너를 가져옴
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onRequestListener = context as OnKeySoundRequestListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_keysound, container, false)

        if (savedInstanceState != null) bundle = savedInstanceState

        onRequestListener.setKeySoundContext(this)

        linear_buttons = root!!.findViewById<LinearLayout>(R.id.Layout_Btns)
        spinner_chain = root!!.findViewById<Spinner>(R.id.Spinner_chain)
        radioG_mode = root!!.findViewById<RadioGroup>(R.id.RadioG_mode)
        list_sounds = root!!.findViewById<RecyclerView>(R.id.List_KeySound)
        list_playlist = root!!.findViewById<RecyclerView>(R.id.list_sound_playlist)
        text_current = root!!.findViewById<TextView>(R.id.Text_current_sound)
        button_add = root!!.findViewById<ImageButton>(R.id.Button_Image_add)

        //sound files
        list_sounds.layoutManager = LinearLayoutManager(activity)
        list_sounds.itemAnimator = DefaultItemAnimator()
        list_sounds.adapter = soundListAdapter

        //sound playlist
        list_playlist.layoutManager = LinearLayoutManager(activity)
        list_playlist.itemAnimator = DefaultItemAnimator()
        val dividerItemDecoration = DividerItemDecoration(activity, LinearLayoutManager(activity).orientation)
        list_playlist.addItemDecoration(dividerItemDecoration)
        list_playlist.adapter = soundPlayListAdapter

        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setActivity(LayoutKey.PLAYBTN_LAYOUT_SOUND)
                if (savedInstanceState != null) {
                    playButton.setcurrentChain(savedInstanceState.getString(LayoutKey.KEYSOUND_BUNDLE_CHAIN))
                } else {
                    playButton.setcurrentChain("1")
                }
            }
        }

        //체인 선택 리스너뷰
        spinner_chain!!.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent!!.childCount != 0) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                    try {
                        val chain_selected = (spinner_chain!!.getItemAtPosition(position) as String).split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        chain = chain_selected
                        setChainButton()
                        buttonCurrent = ""
                        text_current.setText(buttonCurrent)
                        soundPlayListAdapter.clearItem()
                    } catch (e: Exception) {

                    }
                } else {
                    if (bundle != null) {
                        spinner_chain!!.setSelection(bundle!!.getString(LayoutKey.KEYLED_BUNDLE_CHAIN).toInt() - 1)
                    } else {
                        spinner_chain!!.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
            }
        }

        //모드변경 라스너
        radioG_mode.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.Radio_Edit//편집
                -> {
                    isPlay = false
                    setIsPlayButton()
                }

                R.id.Radio_Test//테스트
                -> {
                    isPlay = true
                    soundPlayListAdapter.clearItem()
                    text_current.setText("")
                    buttonCurrent = ""
                    setIsPlayButton()
                }
            }
        }

        //파일 선택 리스너
        list_sounds.addOnItemTouchListener(RecyclerItemClickListener(container!!.context, list_sounds, object : RecyclerItemClickListener.OnItemClickListener {
            //클릭=
            override fun onItemClicked(view: View, position: Int) {
                val popupMenu = PopupMenu(context, view)
                popupMenu.menuInflater.inflate(R.menu.menu_sound, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                        val item = soundListAdapter.getItem(position)
                        when (menuItem.itemId) {
                            R.id.menu_sound_select -> {//선택
                                if (!buttonCurrent.equals("")) {
                                    val playButton = root!!.findViewWithTag(buttonCurrent) as PlayButton
                                    playButton.addSound(chain, item.fname!!, "1")
                                    val s = FileListItem()
                                    s.fname = chain + " " + buttonCurrent + " " + item.fname + " 1"
                                    s.fpath = item.fname
                                    soundPlayListAdapter.addItem(s)
                                    onRequestListener.onRequest(ListenerKey.KEY_SOUND_ADD, chain + " " + buttonCurrent + " " + item.fname + " 1")
                                } else {
                                    Toast.makeText(activity, getString(R.string.toast_sound_noselect), Toast.LENGTH_SHORT).show()
                                }
                            }

                            R.id.menu_sound_play -> {//재생
                                onRequestListener.onRequest(ListenerKey.KEY_SOUND_PLAY, item.fname!!)
                            }

                            R.id.menu_sound_delete -> {//삭제
                                soundListAdapter.removeItem(position)
                                onRequestListener.onRequest(ListenerKey.KEY_SOUND_DELETE, item.fpath!!)
                            }
                        }
                        return true
                    }
                })
                popupMenu.show()
            }

            override fun onLongItemClicked(view: View?, position: Int) {

            }
        }))

        //playlist 클릭처리
        list_playlist.addOnItemTouchListener(RecyclerItemClickListener(container!!.context, list_playlist, object : RecyclerItemClickListener.OnItemClickListener {
            //클릭시 수정창 띄우기
            override fun onItemClicked(view: View, position: Int) {
                val item = soundPlayListAdapter.getItem(position)
                val s = item.fname!!.split("\\s+".toRegex())

                val layout: LinearLayout = View.inflate(activity, R.layout.dialog_editplay, null) as LinearLayout
                val delete = AlertDialog.Builder(activity!!)
                delete.setView(layout)
                val reapet = layout.findViewById<EditText>(R.id.Edit_Repeattime)
                val worwhole = layout.findViewById<EditText>(R.id.Edit_Whomwhole)
                reapet.setText(s[4])
                if (s.size == 6) {
                    worwhole.setText(s[5])//배열이 6개일경우 웜홀도 추가
                }
                delete.setPositiveButton(getString(R.string.dialog_sound_add)) { dialog, which ->
                    val list = soundPlayListAdapter.getAllItem()
                    var slist = s[0] + " " + s[1] + " " + s[2] + " " + s[3]

                    if (!reapet.text.toString().isEmpty()) {
                        slist  += " " + reapet.text.toString()
                    } else {
                        slist += " 1"

                    }

                    if (!worwhole.text.toString().isEmpty()) {
                        if (!worwhole.text.toString().equals("0")) {
                            slist += " " + worwhole.text.toString()
                        }
                    }

                    onRequestListener.onRequest(ListenerKey.KEY_SOUND_CHANGE, item.fname!!, slist)

                    val itemn = FileListItem()
                    itemn.fname = slist
                    itemn.fpath = s[3]
                    list.set(position, itemn)
                    soundPlayListAdapter.dataChanged(position)

                    val playButton = root!!.findViewWithTag(buttonCurrent) as PlayButton
                    val a = slist.split("\\s+".toRegex())
                    if (a.size == 6) {
                        playButton.changeSound(item.fname!!, a[0], a[3], a[4], a[5])
                    } else {
                        playButton.changeSound(item.fname!!, a[0], a[3], a[4])
                    }
                }
                delete.setNegativeButton(getString(R.string.cancel), null)
                delete.show()
            }

            override fun onLongItemClicked(view: View?, position: Int) {
                val popupMenu = PopupMenu(context, view)
                popupMenu.menuInflater.inflate(R.menu.menu_sound, popupMenu.menu)
                popupMenu.menu.getItem(0).setVisible(false)
                popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                        val item = soundPlayListAdapter.getItem(position)
                        when (menuItem.itemId) {
                            R.id.menu_sound_play -> {//재생
                                onRequestListener.onRequest(ListenerKey.KEY_SOUND_PLAY, item.fpath!!)
                            }

                            R.id.menu_sound_delete -> {//삭제
                                val item = soundPlayListAdapter.getItem(position)
                                soundPlayListAdapter.removeItem(position)
                                onRequestListener.onRequest(ListenerKey.KEY_SOUND_REMOVE, item.fname!!)
                                val playButton = root!!.findViewWithTag(buttonCurrent) as PlayButton
                                playButton.removeSound(item.fname!!)
                            }
                        }
                        return true
                    }
                })
                popupMenu.show()
            }
        }))

        return root
    }

    override fun onStart() {
        super.onStart()
        onRequestListener.onRequest(ListenerKey.KEY_SOUND_LOAD, "")
        //사운드 파일 추가
        button_add.setOnClickListener {
            onRequestListener.onRequest(ListenerKey.KEY_SOUND_ADD_FILE, "")
        }
        //tabhost에 chain갯수 요청
        onRequestListener.onRequest(ListenerKey.KEY_SOUND_CHAIN, "")
    }

    fun loadFinish() {
        //tabhost에 wav파일 리스트 요철
        onRequestListener.onRequest(ListenerKey.KEY_SOUND_WAVFILE, "")
    }

    /**tabhost에 요총한 결과 처리**/
    //playbutton에 chain설정
    private fun setChainButton() {
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setcurrentChain(chain)
            }
        }
    }

    //platbutton에 isplay설정
    private fun setIsPlayButton() {
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setIsPlay(isPlay)
            }
        }
    }

    //tabhost에 chain갯수 요청 결과 처리
    fun setChain(chain:String?) {
        if (spinner_chain != null&&root != null&&activity != null) {
            var adapter: ArrayAdapter<String?>? = null
            try {
                val chain_num = chain!!.toInt()
                val chainlist = arrayOfNulls<String>(chain_num)
                //val chainlist = arrayOf(chain_num)
                for (i in 0 until chain_num step 1) {
                    chainlist[i] = String.format(getString(R.string.spinner_chain), i + 1)
                }
                adapter = ArrayAdapter(context, android.support.design.R.layout.support_simple_spinner_dropdown_item, chainlist)
                spinner_chain!!.adapter = adapter
            } catch (e: Exception) {
                val s = arrayOf<String>(1.toString())
                s[0] = String.format(getString(R.string.spinner_chain), "1")
                adapter = ArrayAdapter(context, android.support.design.R.layout.support_simple_spinner_dropdown_item, s)

                spinner_chain!!.adapter = adapter
            }
            for (vertical in 1..8) {
                for (horizontal in 1..8) {
                    val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                    playButton.setChain(chain!!)
                    playButton.setMulti()
                }
            }
        }
    }

    //tabhost에 keysound파일 요청 결과처리
    fun setButton(sound:ArrayList<String>) {
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root!!.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.textView.isClickable = true
                playButton.textView.isFocusable = true
                for (s in sound) {
                    val spl = s.split("\\s+".toRegex())//c x y n r w
                    try {
                        if (spl[1].equals(vertical.toString())&&spl[2].equals(horizontal.toString())) {
                            when (spl.size) {
                                4 -> {playButton.addSound(spl[0], spl[3], "1")}
                                5 -> {playButton.addSound(spl[0], spl[3], spl[4])}
                                6 -> {playButton.addSound(spl[0], spl[3], spl[4], spl[5])}
                            }
                        }
                    } catch (e:Exception) {
                        continue
                    }
                }
            }
        }
    }

    //tabhost에 wav파일 목록 요청 결과 처리
    fun receiveWavs(wavs:ArrayList<Array<String>>) {
        soundListAdapter.clearItem()
        for (w in wavs) {
            val s = FileListItem()
            s.fname = w[0]
            s.fpath = w[1]
            soundListAdapter.addItem(s)
        }
    }

    //tabhost에 keysound파일 요청
    fun soundloadFinish() {
        onRequestListener.onRequest(ListenerKey.KEY_SOUND_FILE, "")
    }

    //웜홀 처리
    fun setWormwhole(chain:String) {
        if (chain.toInt() <= spinner_chain!!.count) {
            spinner_chain!!.setSelection(chain.toInt() - 1)
        }
    }

    //playbutton 클릭 처리
    fun isPlayButtonClicked(tag:String, list:ArrayList<Array<String>>) {//TODO: keysound눌렀을때 편집 처리
        soundPlayListAdapter.clearItem()
        text_current.text = "Selected: " + chain + " " + tag
        buttonCurrent = tag
        for (l in list) {
            val s = FileListItem()
            s.fpath = l[0]
            if (l.size == 3) {
                s.fname = chain + " " + tag + " " + l[0] + " " + l[1] + " " + l[2]
                soundPlayListAdapter.addItem(s)
            } else {
                s.fname = chain + " " + tag + " " + l[0] + " " + l[1]
                soundPlayListAdapter.addItem(s)
            }
        }
    }

    //리스트뷰에 사운드 추가
    fun addSound(list: ArrayList<Array<String>>) {
        soundListAdapter.clearItem()
        for (i in list) {
            val ia = FileListItem()
            ia.fname = i[0]
            ia.fpath = i[1]
            soundListAdapter.addItem(ia)
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

    //keysound수정시 tabhost로 데이터 요청/전달하는 리스너 interface 구현
    interface OnKeySoundRequestListener {
        fun onRequest(type:String, content:String) {

        }
        fun onRequest(type:String, content1:String, content2: String)
        fun setKeySoundContext(keySoundFragment: KeySoundFragment) {

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(LayoutKey.KEYSOUND_BUNDLE_CHAIN, chain)
    }
}
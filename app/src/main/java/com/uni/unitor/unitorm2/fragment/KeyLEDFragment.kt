package com.uni.unitor.unitorm2.fragment

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.LayoutKey
import com.uni.unitor.unitorm2.view.buttons.PlayButton
import kotlin.Exception

class KeyLEDFragment : Fragment(){

    private lateinit var spinner_chain: Spinner
    private lateinit var linear_buttons: LinearLayout
    private lateinit var layout_ledLeft: LinearLayout
    private lateinit var layout_editLeft: LinearLayout
    private lateinit var layout_ledRight: LinearLayout
    private lateinit var layout_editRight: LinearLayout

    private lateinit var root:View
    private var chain:String = ""
    private var isEdit: Boolean = false
    private lateinit var onRequestListener:OnKeyLEDRequestListener

    //tabhostactivity에 등록된 리스너를 가져옴
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onRequestListener = context as OnKeyLEDRequestListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_keyled, container, false)

        linear_buttons = root.findViewById<LinearLayout>(R.id.Layout_Btns)
        layout_ledLeft = root.findViewById(R.id.Layout_LED_ALL_LEFT)
        layout_editLeft = root.findViewById(R.id.Layout_LED_EDIT_LEFT)
        layout_ledRight = root.findViewById(R.id.Layout_LED_ALL_RIGHT)
        layout_editRight = root.findViewById(R.id.Layout_LED_EDIT_RIGHT)
        spinner_chain = root.findViewById(R.id.Spinner_chain_LED) as Spinner

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

        //체인 선택 리스너뷰
        spinner_chain.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                try {
                    val chain_selected = (spinner_chain.getItemAtPosition(position) as String).split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    chain = chain_selected
                    //setChainButton()
                    //buttonCurrent = ""
                    //text_current.setText(buttonCurrent)
                    //soundPlayListAdapter.clearItem()
                } catch (e: Exception) {

                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }


//        if (savedInstanceState != null) {
//            spinner_chain.setSelection(savedInstanceState.getString(LayoutKey.KEYLED_BUNDLE_CHAIN).toInt() - 1)
//        }


        if (savedInstanceState == null) {
            layout_editRight.visibility = View.GONE
            layout_editLeft.visibility = View.GONE
            chain = "1"
        } else {
            chain = savedInstanceState.getString(LayoutKey.KEYLED_BUNDLE_CHAIN)
            if (savedInstanceState!!.getBoolean(LayoutKey.KEYLED_BUNDLE_ISPLAY)) {
                layout_editRight.visibility = View.VISIBLE
                layout_editLeft.visibility = View.VISIBLE
                layout_ledRight.visibility = View.GONE
                layout_ledLeft.visibility = View.GONE
            } else {
                layout_editRight.visibility = View.GONE
                layout_editLeft.visibility = View.GONE
                layout_ledRight.visibility = View.VISIBLE
                layout_ledLeft.visibility = View.VISIBLE
            }
        }

        onRequestListener.onRequestLED(ListenerKey.KEY_LED_CHAIN, "")

        return root
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
        } catch (e:Exception) {
            val s = arrayOf<String>(1.toString())
            s[0] = String.format(getString(R.string.spinner_chain), "1")
            adapters = ArrayAdapter(activity, android.support.design.R.layout.support_simple_spinner_dropdown_item, s)
        } finally {
            //spinner_chain.adapter = adapters
        }
        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setChain(chain!!)
            }
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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(LayoutKey.KEYLED_BUNDLE_ISPLAY, isEdit)
        outState.putString(LayoutKey.KEYLED_BUNDLE_CHAIN, chain)
    }
}
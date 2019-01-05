package com.uni.unitor.unitorm2.File.fragment

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
import java.lang.Exception
import android.widget.AdapterView
import android.widget.Toast
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView





class KeySoundFragment : Fragment(){

    private lateinit var linear_buttons:LinearLayout
    private lateinit var spinner_chain:Spinner

    private lateinit var onRequestListener:OnKeySoundRequestListener

    //tabhostactivity에 등록된 리스너를 가져옴
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onRequestListener = context as OnKeySoundRequestListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.fragment_keysound, container, false)

        linear_buttons = root.findViewById<LinearLayout>(R.id.Layout_Btns)
        spinner_chain = root.findViewById<Spinner>(R.id.Spinner_chain)

        onRequestListener.onRequest(ListenerKey.KEY_SOUND_CHAIN, "")

        //체인 선택 리스너뷰
        spinner_chain.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
            }
        }
        return root
    }

    public fun setChain(chain:String?) {
        var adapter:ArrayAdapter<String?>? = null
        try {
            val chain_num = chain!!.toInt()
            val chainlist = arrayOfNulls<String>(chain_num)
            //val chainlist = arrayOf(chain_num)
            for (i in 0 until chain_num step 1) {
                chainlist[i] = String.format(getString(R.string.spinner_chain), i+1)
            }
            adapter = ArrayAdapter(context, android.support.design.R.layout.support_simple_spinner_dropdown_item, chainlist)
        } catch (e:Exception) {
            val s = arrayOf<String>(1.toString())
            s[0] = String.format(getString(R.string.spinner_chain), "1")
            adapter = ArrayAdapter(context, android.support.design.R.layout.support_simple_spinner_dropdown_item, s)
        } finally {
            spinner_chain.adapter = adapter
        }
    }

    //버튼 크기조절
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewTreeObserver = linear_buttons.getViewTreeObserver()
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    linear_buttons.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                } else {
                    linear_buttons.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                }
                val height = linear_buttons.getHeight()
                val params = RelativeLayout.LayoutParams(height, height)
                params.addRule(RelativeLayout.CENTER_HORIZONTAL)
                linear_buttons.setLayoutParams(params)
            }
        })
    }

    //keysound수정시 tabhost로 데이터 요청하는/전달하는 리스너 interface 구현
    public interface OnKeySoundRequestListener {
        fun onRequest(type:String, content:String) {

        }
    }
}
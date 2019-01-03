package com.uni.unitor.unitorm2.File.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.uni.unitor.unitorm2.File.layout.TabHostActivity
import com.uni.unitor.unitorm2.File.sharedpreference.PreferenceKey
import com.uni.unitor.unitorm2.R

class InfoFragment : Fragment() {

    private lateinit var edit_Title:EditText
    private lateinit var edit_Producer:EditText
    private lateinit var edit_Chain:EditText
    private lateinit var text_Info:TextView

    private lateinit var onInfoChangeListener: OnInfoChangeListener

    //tabhostactivity에 등록된 리스너를 가져옴
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onInfoChangeListener = context as OnInfoChangeListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.fragment_info, container, false)

        edit_Title = root.findViewById<EditText>(R.id.Edit_Title)
        edit_Producer = root.findViewById<EditText>(R.id.Edit_Producer)
        edit_Chain = root.findViewById<EditText>(R.id.Edit_Chain)
        text_Info = root.findViewById<TextView>(R.id.Edit_Title)

        //최초 시작시 데이터 요청
        onInfoChangeListener.onInfoChaged(ListenerKey.KEY_INFO_START, "")

        //edittext바뀔때마다 onInfoChangeListener로 tabhost에 내용 전달
        edit_Title.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                onInfoChangeListener.onInfoChaged(PreferenceKey.KEY_INFO_TITLE, editable.toString())
            }
        })
        edit_Producer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                onInfoChangeListener.onInfoChaged(PreferenceKey.KEY_INFO_PRODUCER, editable.toString())
            }
        })
        edit_Chain.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                onInfoChangeListener.onInfoChaged(PreferenceKey.KEY_INFO_CHAIN, editable.toString())
            }
        })
        return root
    }

    //tabhost에 info데이터 요청
    public fun requestInfo(title:String?, producer:String?, chain:String?) {
        edit_Title.setText(title)
        edit_Producer.setText(producer)
        edit_Chain.setText(chain)
    }

    //백그라운드로 들어갈때 데이터 저장
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    //info수정시 tabhost로 데이터 전달하는 리스너 interface 구현
    public interface OnInfoChangeListener {
        fun onInfoChaged(type:String, content:String) {

        }
    }
}
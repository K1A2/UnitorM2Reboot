package com.uni.unitor.unitorm2.fragment

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.LayoutKey
import com.uni.unitor.unitorm2.view.buttons.PlayButton

class KeyLEDFragment : Fragment(){

    private lateinit var linear_buttons: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.fragment_keyled, container, false)

        linear_buttons = root.findViewById<LinearLayout>(R.id.Layout_Btns)

        for (vertical in 1..8) {
            for (horizontal in 1..8) {
                val playButton = root.findViewWithTag(vertical.toString() + " " + horizontal.toString()) as PlayButton
                playButton.setActivity(LayoutKey.PLAYBTN_LAYOUT_LED)
            }
        }

        return root
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
}
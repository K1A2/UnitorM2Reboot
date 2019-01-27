package com.uni.unitor.unitorm2.view.buttons

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.uni.unitor.unitorm2.R

class McButton : LinearLayout, View.OnClickListener {

    lateinit var textView: TextView
    lateinit var con: Context

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
        val view = layoutInflater.inflate(R.layout.view_button_mc, this@McButton, false)
        textView = view.findViewById(R.id.View_Play_text)

        this.isClickable = true
        textView.isClickable = true

        con = context

        addView(view)
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
    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
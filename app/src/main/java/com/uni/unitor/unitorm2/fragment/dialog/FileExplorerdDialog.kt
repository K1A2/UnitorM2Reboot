package com.uni.unitor.unitorm2.fragment.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.uni.unitor.unitorm2.R

class FileExplorerdDialog : DialogFragment() {

    private lateinit var text_path:TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root:View = inflater.inflate(R.layout.dialog_fileex, container, false)
        return root
    }
}
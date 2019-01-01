package com.uni.unitor.unitorm2.File

import android.content.Context
import android.content.ContextWrapper
import android.os.Environment

class KeySoundIO(private val context: Context) : ContextWrapper(context) {
    private val defaultpath: String = Environment.getExternalStorageDirectory().absolutePath + "/"
}
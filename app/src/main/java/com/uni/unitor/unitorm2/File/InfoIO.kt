package com.uni.unitor.unitorm2.File

import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter

class InfoIO(private val context: Context) : ContextWrapper(context) {
    private val defaultpath: String = Environment.getExternalStorageDirectory().absolutePath + "/"

    val fileIO:FileIO = FileIO(context)

    //info내용 가져옴
    @Throws(Exception::class)
    fun getInfo(path: String): ArrayList<String>? {
        var path = path
        path += "info"
        return fileIO.getTextFile(File(path))
    }

    @Throws(Exception::class)
    fun mkInfo(Title: String?, Producer: String?, Chain: String?, path: String?) {
        val file = File(path)
        fileIO.isExists(file, FileKey.KEY_FILE_INT)

        val printWriter = PrintWriter(file)
        printWriter.printf(FileKey.KEY_INFO_CONTENT, Title, Producer, Chain)
        printWriter.close()
    }

}
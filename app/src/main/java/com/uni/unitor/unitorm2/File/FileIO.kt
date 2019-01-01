package com.uni.unitor.unitorm2.File

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import com.uni.unitor.unitorm2.R
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class FileIO(private val context: Context) : ContextWrapper(context) {
    private val defaultpath: String = Environment.getExternalStorageDirectory().absolutePath + "/"

    //파일내용 가져옴
    @Throws(Exception::class)
    fun getTextFile(file: File): ArrayList<String>? {
        if (file.exists()) {
            val arrayFile = ArrayList<String>()
            val bufferedReader = BufferedReader(FileReader(file))
            var line: String?

            do {
                line = bufferedReader.readLine()
                if (line == null) {
                    break
                }
                if (!line.isEmpty()) {
                    arrayFile.add(line)
                }
            } while (true)
            bufferedReader.close()

            return arrayFile
        } else {
            return null
        }
    }

    fun getDefaultPath(): String {
        return defaultpath
    }

    //파일 유무, 없으면 생성
    @Throws(Exception::class)
    fun isExists(file: File, i: Int) {
        if (!file.exists()) {
            if (i == FileKey.KEY_FILE_INT) {
                file.createNewFile()
            } else if (i == FileKey.KEY_DIRECTORY_INT) {
                file.mkdirs()
            }
        }
    }

    //에러출력
    fun showErr(e: String) {
        val alertErr = AlertDialog.Builder(context)
        alertErr.setTitle(getString(R.string.alert_err))
        alertErr.setMessage(e)
        alertErr.setPositiveButton(getString(R.string.alert_ok), null)
        alertErr.show()
    }
}
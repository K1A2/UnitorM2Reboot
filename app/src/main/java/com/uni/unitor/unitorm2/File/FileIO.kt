package com.uni.unitor.unitorm2.File

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.AsyncTask
import android.os.Environment
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.TabHostActivity
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

    class DeleteFile(context: Context, type:String, path:String) : AsyncTask<String, String, Boolean>() {

        private val prograssDialog: ProgressDialog = ProgressDialog(context)

        private val context:Context = context
        private val type:String = type
        private val path:String = path

        override fun onPreExecute() {
            prograssDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            prograssDialog.setCancelable(false)
            prograssDialog.setCanceledOnTouchOutside(false)
            when (type) {
                FileKey.KEY_FILE_DELETE_SOUND -> {
                    prograssDialog.setTitle(context.getString(R.string.async_file_deleteS))
                }
                FileKey.KEY_FILE_DELETE_LED -> {
                    prograssDialog.setTitle(context.getString(R.string.async_file_deleteL))
                }
            }
            prograssDialog.show()
        }

        override fun doInBackground(vararg params: String?): Boolean {
            try {
                delete(path)
                return true
            } catch (e:java.lang.Exception) {
                return false
            }
        }

        override fun onPostExecute(result: Boolean) {
            prograssDialog.dismiss()
            if (result) {
                when (type) {
                    FileKey.KEY_FILE_DELETE_SOUND -> {
                        (context as TabHostActivity).deleteFinish()
                    }
                    FileKey.KEY_FILE_DELETE_LED -> {

                    }
                }
            } else {

            }
        }

        @Throws(Exception::class)
        private fun delete(path: String) {
            val d = File(path)
            if (d.exists()) {
                if (d.isFile) {
                    d.delete()
                } else {
                    val childFileList = d.listFiles()
                    for (childFile in childFileList!!) {
                        if (childFile.isDirectory) {
                            delete(childFile.path)
                        } else {
                            childFile.delete()
                        }
                    }
                    d.delete()
                }
            }
        }
    }
}
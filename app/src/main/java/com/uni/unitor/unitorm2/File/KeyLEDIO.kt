package com.uni.unitor.unitorm2.File

import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.AsyncTask
import android.os.Environment
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.TabHostActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.StringBuilder

class KeyLEDIO(private val context: Context) : ContextWrapper(context) {

    private val fileIO:FileIO = FileIO(context)

    private val defaultpath: String = Environment.getExternalStorageDirectory().absolutePath + "/"

    //LED파일 가져옴
    @Throws(Exception::class)
    fun getKeyLED(path: String): Array<File>? {
        var path = path
        path += "keyLED/"
        return File(path).listFiles { file -> file.isFile}
    }

    //led 파일 내용까지
    @Throws(Exception::class)
    fun getKeyLEDCont(path: String): ArrayList<Array<String>>? {
        val iC = ArrayList<Array<String>>()
        val files = getKeyLED(path)
        if (files != null) {
            for (i in files) {
                val content = fileIO.getTextFile(File(i.path))
                if (content != null) {
                    val stringBuilder : StringBuilder = StringBuilder()
                    for (c in content) {
                        stringBuilder.append(c + "\n")
                    }
                    iC.add(arrayOf(i.name, stringBuilder.toString()))
                } else {
                    iC.add(arrayOf(i.name, ""))
                }
            }
            return iC
        } else {
            return null
        }
    }

    //키사운드 워크파일 내용 가져옴
    @Throws(Exception::class)
    fun getKeyLEDWork(): Array<File>? {
        return File(defaultpath + "unipackProject/work/keyLED").listFiles { file -> file.isFile}
    }

    class GetKeySoundContent(context: Context, path: String) : AsyncTask<String, String, Boolean>() {

        val context = context
        val path = path
        var content : ArrayList<Array<String>>? = null
        val keyLEDIO = KeyLEDIO(context)
        private val prograssDialog: ProgressDialog = ProgressDialog(context)

        override fun onPreExecute() {
            prograssDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            prograssDialog.setCancelable(false)
            prograssDialog.setTitle(context.getString(R.string.async_led_content))
            prograssDialog.setCanceledOnTouchOutside(false)
            prograssDialog.show()
        }

        override fun doInBackground(vararg params: String?): Boolean {
            try {
                content = keyLEDIO.getKeyLEDCont(path)
                return true
            } catch (e:java.lang.Exception) {
                return false
            }
        }

        override fun onPostExecute(result: Boolean?) {
            prograssDialog.dismiss()
            if (result!!) {
                (context as TabHostActivity).getLEDContentFinish(content)
            } else {

            }
        }
    }

    class getLedWork(context: Context, path: String) : AsyncTask<String, String, Boolean>() {

        private val keyLEDIO = KeyLEDIO(context)
        private val context = context
        private val path = path
        private val prograssDialog = ProgressDialog(context)

        override fun onPreExecute() {
            prograssDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            prograssDialog.setCancelable(false)
            prograssDialog.setCanceledOnTouchOutside(false)
            prograssDialog.show()
        }

        override fun doInBackground(vararg params: String?): Boolean {
            try {
                val workpath = Environment.getExternalStorageDirectory().absolutePath + "/unipackProject/work/keyLED"

                if (!File(workpath).exists()) {
                    File(workpath).mkdirs()
                }

                val worklist = keyLEDIO.getKeyLEDWork()
                if (worklist != null) {
                    publishProgress("dS", worklist.size.toString())//삭제 시작
                    var count = 0
                    for (i in worklist) {
                        publishProgress("dU", count.toString(), i.path)//삭제 현황 업댓
                        i.delete()
                        count++
                    }
                    publishProgress("dF")//삭제 끝
                }
                val source = keyLEDIO.getKeyLED(path)
                if (source != null) {
                    publishProgress("LS", source.size.toString())//복사 시작
                    var count = 0
                    for (i in source) {
                        publishProgress("LU", count.toString())
                        val inputStream = FileInputStream(i)
                        val outputStream = FileOutputStream(workpath + "/" + i.name)
                        val fcain = inputStream.channel
                        val fout = outputStream.channel
                        val size = fcain.size()
                        fcain.transferTo(0, size, fout)
                        fout.close()
                        fcain.close()
                        outputStream.close()
                        inputStream.close()
                        count++
                    }
                }
                return true
            } catch (e:Exception) {
                return false
            }
        }

        override fun onProgressUpdate(vararg values: String?) {
            when (values[0]) {
                "dS" -> {
                    prograssDialog.setTitle(context.getString(R.string.async_led_initial_delete))
                    prograssDialog.max = values[1]!!.toInt()
                }
                "dU" -> {
                    prograssDialog.progress = values[1]!!.toInt()
                    prograssDialog.setMessage(values[2])
                }
                "LS" -> {
                    prograssDialog.setTitle(context.getString(R.string.async_led_initial))
                    prograssDialog.max = values[1]!!.toInt()
                }
                "LU" -> {
                    prograssDialog.progress = values[1]!!.toInt()
                }
            }
        }

        override fun onPostExecute(result: Boolean) {
            prograssDialog.dismiss()
            if (result) {
                (context as TabHostActivity).setLedInitFinish()
            } else {

            }
        }
    }
}
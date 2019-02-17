package com.uni.unitor.unitorm2.File

import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.AsyncTask
import android.os.Environment
import android.widget.Toast
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.TabHostActivity
import java.io.*
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

    //led 워크파일 내용 가져옴
    @Throws(Exception::class)
    fun getKeyLEDWork(): Array<File>? {
        return File(defaultpath + "unipackProject/work/keyLED").listFiles { file -> file.isFile}
    }

    //led 워크폴더 저장
    @Throws(Exception::class)
    fun saveLedWork(content:String, name:String) {
        val f = File(defaultpath + "unipackProject/work/keyLED/" + name)
        fileIO.isExists(f, FileKey.KEY_FILE_INT)

        val printWriter = PrintWriter(f)
        printWriter.printf(content)
        printWriter.close()
    }

    @Throws(Exception::class)
    fun makeNewLED(path:String) {
        val workpath = Environment.getExternalStorageDirectory().absolutePath + "/unipackProject/work/keyLED"

        if (!File(workpath).exists()) {
            File(workpath).mkdirs()
        }

        fileIO.isExists(File(path), FileKey.KEY_FILE_INT)
    }

    @Throws(Exception::class)
    fun deleteLedFile(name:String, path:String) {
        val workpath = Environment.getExternalStorageDirectory().absolutePath + "/unipackProject/work/keyLED"
        val s = name.split("\\s+".toRegex())

        if (!File(workpath).exists()) {
            File(workpath).mkdirs()
        }

        rename(workpath, name)
        rename(path + "keyLED/", name)

        val files = File(workpath).listFiles(object : FileFilter {
            override fun accept(file: File): Boolean {
                return file.isFile && file.name.startsWith(s[0] + " " + s[1] + " " + s[2])
            }
        })

        val iC = ArrayList<Array<String>>()
        if (files != null) {
            for (i in files) {
                val content = fileIO.getTextFile(File(i.path))
                if (content != null) {
                    val stringBuilder : StringBuilder = StringBuilder()
                    for (c in content) {
                        stringBuilder.append(c + "\n")
                    }
                    iC.add(arrayOf(stringBuilder.toString(), i.name))
                } else {
                    iC.add(arrayOf("", i.name))
                }
            }
        }
        (context as TabHostActivity).ledRenameFinished(iC)
        GetKeySoundContent(context, defaultpath).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun rename(workpath:String, name:String) {
        val s = name.split("\\s+".toRegex())
        val file = File(workpath).listFiles(object : FileFilter {
            override fun accept(file: File): Boolean {
                return file.isFile && file.name.equals(name)
            }
        })

        for (i in file) {
            i.delete()
        }

        val file2 = File(workpath).listFiles(object : FileFilter {
            override fun accept(file: File): Boolean {
                return file.isFile && file.name.startsWith(s[0] + " " + s[1] + " " + s[2])
            }
        })

        var count = 0
        for (i in file2) {
            val p = i.name.split("\\s+".toRegex())
            i.renameTo(File(workpath + "/" + p[0] + " " + p[1] + " " + p[2] + " " + p[3] + " " + count.toString()))
            count++
        }
    }

    class GetKeySoundContent(context: Context, path: String) : AsyncTask<String, String, Boolean>() {

        val context = context
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
                content = keyLEDIO.getKeyLEDCont(Environment.getExternalStorageDirectory().absolutePath +
                        "/unipackProject/work/")
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

    class SaveLED(path:String, context: Context, isZip:Boolean) : AsyncTask<String, String, Boolean>() {

        val path = path + "/keyLED/"
        val context = context
        val workpath = Environment.getExternalStorageDirectory().absolutePath + "/unipackProject/work/keyLED"
        val isUnzip = isZip

        private val prograssDialog = ProgressDialog(context)

        override fun onPreExecute() {
            prograssDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            prograssDialog.setCancelable(false)
            prograssDialog.setCanceledOnTouchOutside(false)
            prograssDialog.setTitle(context.getString(R.string.async_save_led))
            prograssDialog.max = File(workpath).listFiles { file -> file.isFile}.size
            prograssDialog.show()
        }

        override fun doInBackground(vararg params: String?): Boolean {
            try {
                val works = File(workpath).listFiles { file -> file.isFile}
                var count = 0
                for (i in works) {
                    publishProgress(count.toString())
                    if (!File(path + i.name).exists()) {
                        File(path + i.name).createNewFile()
                    }
                    val inputStream = FileInputStream(i)
                    val outputStream = FileOutputStream(File(path + i.name))
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
                return true
            } catch (e:Exception) {
                return false
            }
        }

        override fun onProgressUpdate(vararg values: String?) {
            prograssDialog.progress = values[0]!!.toInt()
        }

        override fun onPostExecute(result: Boolean?) {
            prograssDialog.dismiss()
            if (result!!) {
                Toast.makeText(context, context.getString(R.string.toast_save_succeed) + ": KEYLED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, context.getString(R.string.toast_save_fail) + ": KEYLED", Toast.LENGTH_SHORT).show()
            }
            if (isUnzip) {
                (context as TabHostActivity).zipUnipack()
            }
        }
    }
}
package com.uni.unitor.unitorm2.File

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.AsyncTask
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.TabHostActivity
import java.io.File
import java.io.FileFilter
import java.io.PrintWriter

class KeySoundIO(private val context: Context) : ContextWrapper(context) {

    private val fileIO:FileIO = FileIO(context)

    private val defaultpath: String = Environment.getExternalStorageDirectory().absolutePath + "/"

    //키사운드 워크파일 생성
    @Throws(Exception::class)
    public fun mkKeySoundWork() {
        fileIO.isExists(File(defaultpath + "unipackProject/work/"), FileKey.KEY_DIRECTORY_INT)
        fileIO.isExists(File(defaultpath + "unipackProject/work/keySound.txt"), FileKey.KEY_FILE_INT)
    }

    //키사운드 가져옴
    @Throws(Exception::class)
    fun getKeySound(path: String): ArrayList<String>? {
        var path = path
        path += "keySound"
        return fileIO.getTextFile(File(path))
    }

    //키사운드 워크파일 내용 가져옴
    @Throws(Exception::class)
    fun getKeySoundWork(): ArrayList<String>? {
        return fileIO.getTextFile(File(defaultpath + "unipackProject/work/keySound.txt"))
    }
    //키사운드 워크파일 내용 가져옴
    @Throws(Exception::class)
    fun deleteKeySoundWork() {
        File(defaultpath + "unipackProject/work/keySound.txt").delete()
    }

    //사운드 파일 가져옴
    @Throws(Exception::class)
    fun getSoundFile(path: String): ArrayList<Array<String>> {
        val path_sound = File(path + "sounds/")
        val sounds = ArrayList<Array<String>>()

        fileIO.isExists(path_sound, FileKey.KEY_DIRECTORY_INT)
        val soundlist = path_sound.listFiles(object : FileFilter {
            override fun accept(file: File): Boolean {
                return file.isFile && (file.name.endsWith(".wav") || file.name.endsWith(".mp3"))
            }
        })

        for (f in soundlist) {
            sounds.add(arrayOf(f.getName(), f.getAbsolutePath()))
        }

        return sounds
    }

    //키사운드 워크폴더에 저장
    @Throws(Exception::class)
    fun saveKeySoundWork(content: ArrayList<String>?) {
        var path = defaultpath + "unipackProject/work/keySound.txt"
        var file = File(path)
        fileIO.isExists(file, FileKey.KEY_FILE_INT)

        val printWriter = PrintWriter(file)
        if (content != null) {
            for (s in content) {
                printWriter.println(s)
            }
        }
        printWriter.close()
    }

    //키사운드 생성 저장
    @Throws(Exception::class)
    fun saveKeySound(path: String) {
        val content = getKeySoundWork()
        val file = File(path)
        fileIO.isExists(file, FileKey.KEY_FILE_INT)

        val printWriter = PrintWriter(file)
        if (content != null) {
            for (s in content) {
                printWriter.println(s)
            }
        }
        printWriter.close()
    }

    class DupliSaveSound(context: Context, type:String, path:String?) : AsyncTask<String, String, Boolean>() {

        private val keysoundIO:KeySoundIO = KeySoundIO(context)
        private val prograssDialog:ProgressDialog = ProgressDialog(context)

//        private val progressDialog:AlertDialog.Builder = AlertDialog.Builder(context)
//        val layout: RelativeLayout = View.inflate(context, R.layout.dialog_progress, null) as RelativeLayout

        private val type:String = type
        private val context:Context = context
        private val path:String? = path
//        private val text_Title:TextView = layout.findViewById<TextView>(R.id.dialog_progress_title)
//        private val text_Sub:TextView = layout.findViewById<TextView>(R.id.dialog_progress_sub)
//        private val progressBar:ProgressBar = layout.findViewById<ProgressBar>(R.id.dialog_progress_bar)
//        private val button_Ok:Button = layout.findViewById<Button>(R.id.button_progress_ok)

        override fun onPreExecute() {
//            progressDialog.setView(layout)
            prograssDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            prograssDialog.setCancelable(false)
            prograssDialog.setCanceledOnTouchOutside(false)
            when (type) {
                FileKey.KEY_SOUND_WORK_DUPLICATE -> {
//                    text_Title.setText(R.string.async_duplicate_sound_title)
//                    text_Sub.visibility = View.GONE
                    prograssDialog.setTitle(context.getString(R.string.async_duplicate_sound_title))
                }
            }
            prograssDialog.show()
        }

        override fun doInBackground(vararg params: String?): Boolean {
            when (type) {
                FileKey.KEY_SOUND_WORK_DUPLICATE -> {
                    try {
                        keysoundIO.saveKeySoundWork(keysoundIO.getKeySound(path!!))
                        return true
                    } catch (e:Exception) {
                        return false
                    }
                } else -> {return false}
            }
        }

        override fun onPostExecute(result: Boolean?) {
            if (result!!) {
                prograssDialog.dismiss()
                (context as TabHostActivity).setKeysoundWork()
            } else {
                prograssDialog.dismiss()
            }
        }
    }
}
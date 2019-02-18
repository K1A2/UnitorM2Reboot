package com.uni.unitor.unitorm2.File

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.AsyncTask
import android.os.Environment
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.MainActivity
import com.uni.unitor.unitorm2.layout.TabHostActivity
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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

    //ㅍㅏ일 삭제
    class DeleteFile(context: Context, type:String, path:String, name:String?) : AsyncTask<String, String, Boolean>() {

        private val prograssDialog: ProgressDialog = ProgressDialog(context)

        private val context:Context = context
        private val type:String = type
        private val path:String = path
        private val name = name

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
                FileKey.KEY_UNIPACKP_DELETE -> {
                    prograssDialog.setTitle(String.format(context.getString(R.string.async_delete_unipack), name!!))
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
                        (context as TabHostActivity).soundsChange(false)
                    }
                    FileKey.KEY_FILE_DELETE_LED -> {

                    }
                    FileKey.KEY_UNIPACKP_DELETE -> {
                        (context as MainActivity).showUnipack()
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

    //ZIP
    class Export(context: Context, title:String, path: String) : AsyncTask<String, String, Boolean>() {

        private val progressDialog = ProgressDialog(context)
        private val title = title
        private val context = context
        private val path = path
        private var out:String = ""


        @Override
        override fun onPreExecute() {
            //progressDialog.setTitle(getString(R.string.Dialog_Zip))
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setCancelable(false)
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog.show()
        }

        override fun doInBackground(vararg params: String?): Boolean {
            try {
                val mk = File(Environment.getExternalStorageDirectory().absolutePath + "/unipackExport/")
                mk.mkdirs()
                out = Environment.getExternalStorageDirectory().absolutePath + "/unipackExport/" + title + File(Environment.getExternalStorageDirectory().absolutePath
                        + "/unipackExport/").listFiles(object : FileFilter { override fun accept(file: File): Boolean {
                    return file.isFile && file.name.startsWith(title) } }).size + ".zip"
                zip(path, out)
                return true
            } catch (e:Exception) {
                e.printStackTrace()
                return false
            }
        }

        private val COMPRESSION_LEVEL = 8
        private val BUFFER_SIZE = 1024 * 2

        @Throws(Exception::class)
        public fun zip(sourcePath:String, output:String) {

            // 압축 대상(sourcePath)이 디렉토리나 파일이 아니면 리턴한다.
            val sourceFile = File(sourcePath)
            if (!sourceFile.isFile() && !sourceFile.isDirectory()) {
                throw Exception("압축 대상의 파일을 찾을 수가 없습니다.")
            }

            var fos:FileOutputStream? = null
            var bos:BufferedOutputStream? = null
            var zos:ZipOutputStream? = null

            try {
                fos = FileOutputStream(output) // FileOutputStream
                bos = BufferedOutputStream(fos) // BufferedStream
                zos = ZipOutputStream(bos) // ZipOutputStream
                zos.setLevel(COMPRESSION_LEVEL) // 압축 레벨 - 최대 압축률은 9, 디폴트 8

                zipEntry(sourceFile, sourcePath, zos) // Zip 파일 생성
                zos.finish() // ZipOutputStream finish
            } finally {
                if (zos != null) {
                    zos.close()
                }
                if (bos != null) {
                    bos.close()
                }
                if (fos != null) {
                    fos.close()
                }
            }
        }

        @Throws(Exception::class)
        private fun zipEntry(sourceFile:File, sourcePath:String, zos:ZipOutputStream) {
            if (sourceFile.isDirectory()) {
                val fileArray = sourceFile.listFiles() // sourceFile 의 하위 파일 리스트
                for (i in fileArray) {
                    zipEntry(i, sourcePath, zos) // 재귀 호출
                }
            } else { // sourcehFile 이 디렉토리가 아닌 경우
                var bis:BufferedInputStream? = null
                try {
                    val sFilePath = sourceFile.getPath()
                    val zipEntryName = sFilePath.substring(sourcePath.length, sFilePath.length)

                    bis = BufferedInputStream(FileInputStream(sourceFile))
                    val zentry = ZipEntry(zipEntryName)
                    zentry.setTime(sourceFile.lastModified())
                    zos.putNextEntry(zentry)

                    val buffer = ByteArray(BUFFER_SIZE)
                    var cnt = 0
                    do {
                        cnt = bis.read(buffer, 0, BUFFER_SIZE)
                        if (cnt == -1) {
                            break
                        }
                        zos.write(buffer, 0, cnt)
                    } while (true)
                    zos.closeEntry()
                } finally {
                    if (bis != null) {
                        bis.close()
                    }
                }
            }
        }

        override fun onPostExecute(result: Boolean?) {
            progressDialog.dismiss()
            (context as TabHostActivity).zipFinish(result!!, out)
        }
    }
}
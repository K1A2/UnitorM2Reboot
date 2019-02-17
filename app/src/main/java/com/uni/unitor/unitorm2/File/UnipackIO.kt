package com.uni.unitor.unitorm2.File

import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.AsyncTask
import android.os.Environment
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.MainActivity
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class UnipackIO(private val context: Context) : ContextWrapper(context) {
    private val defaultpath: String = Environment.getExternalStorageDirectory().absolutePath + "/"

    val infoIO:InfoIO = InfoIO(context)
    val fileIO:FileIO = FileIO(context)

    fun getUnipacks(): ArrayList<Array<String>?>? {
        var unipackProjectF: File = File(defaultpath + "unipackProject/")

        try {
            fileIO.isExists(unipackProjectF, FileKey.KEY_DIRECTORY_INT)
            fileIO.isExists(File(defaultpath + "unipackProject/.nomedia"), FileKey.KEY_FILE_INT)
        } catch (e:Exception) {
            e.printStackTrace()
            if (e.message != null) fileIO.showErr(e.message!!)
            return null
        }

        val unipackList = unipackProjectF.listFiles(FileFilter { file -> file.isDirectory })


        val unipackInfo: ArrayList<Array<String>?>? = ArrayList()

        for (arrayUnipack in unipackList) {
            val path = arrayUnipack.absolutePath
            if (unipackInfo != null) {
                unipackInfo.add(getUnipackInfo(File(path + "/info"), path + "/"))
            }
        }

        return unipackInfo
    }

    //새 유팩생성
    @Throws(Exception::class)
    fun mkNewUnipack(Title: String, Producer: String, Chain: String, path: String) {
        var path = path
        val file = File(path)
        fileIO.isExists(file, FileKey.KEY_DIRECTORY_INT)

        path += "info"
        infoIO.mkInfo(Title, Producer, Chain, path)
    }

    //유니팩 인포 가져옴
    fun getUnipackInfo(unipack: File, path: String): Array<String>? {
        if (unipack.exists()) {
            try {
                val arrayInfo = fileIO.getTextFile(unipack)

                val info: Array<String> = Array(4) { i ->  ""}
                if (arrayInfo != null) {
                    for (`in` in arrayInfo) {
                        when {
                            `in`.startsWith(FileKey.KEY_INFO_TITLE) -> info[0] = `in`.replace(FileKey.KEY_INFO_TITLE, "")
                            `in`.startsWith(FileKey.KEY_INFO_PRODUCER) -> info[1] = `in`.replace(FileKey.KEY_INFO_PRODUCER, "")
                            `in`.startsWith(FileKey.KEY_INFO_CHAIN) -> info[2] = `in`.replace(FileKey.KEY_INFO_CHAIN, "")
                        }
                    }
                }
                info[3] = path

                return info
            } catch (e: Exception) {
                e.printStackTrace()
                if (e.message != null) fileIO.showErr(e.message!!)
                return null
            }

        } else {
            return null
        }
    }

    class UnzipUnipack(private val context: Context, name: String, path: String, target: String) : AsyncTask<Any, String, Boolean>() {
        private var progressDialog: ProgressDialog? = null
        private var name: String = name
        private var path: String = path
        private var target: String = target
        private var finish: String? = null
        //private var adapter: UnipackListAdapter? = null

        override fun onPreExecute() {
            progressDialog = ProgressDialog(context)
            progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
        }

        override fun doInBackground(vararg objects: Any): Boolean? {
            //adapter = objects[3] as UnipackListAdapter
            publishProgress("start", name)

            try {
                val fileInputStream = FileInputStream(path)
                val zipInputStream = ZipInputStream(fileInputStream)
                var zipEntry: ZipEntry? = null

                var targetFile: File? = null
                do {
                    zipEntry = zipInputStream.getNextEntry()
                    if (zipEntry == null) {
                        break
                    }
                    val filenameTounzip = zipEntry!!.getName()
                    targetFile = File(target, filenameTounzip)

                    if (zipEntry!!.isDirectory()) {
                        val pathF = File(targetFile!!.getAbsolutePath())
                        pathF.mkdirs()
                    } else {
                        val pathF = File(targetFile!!.getParent())
                        pathF.mkdirs()
                        Unzip(zipInputStream, targetFile)
                    }
                } while (true)

                fileInputStream.close()
                zipInputStream.close()
                if (targetFile != null) finish = targetFile!!.getAbsolutePath()
                return true
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                publishProgress(e.message)
                return false
            } catch (e: IOException) {
                e.printStackTrace()
                publishProgress(e.message)
                return false
            } catch (e: Exception) {
                e.printStackTrace()
                publishProgress(e.message)
                return false
            }

        }

        override fun onProgressUpdate(vararg values: String) {
            if (values[0] == "start") {
                progressDialog!!.setTitle(context.getString(R.string.async_unzip_title))
                progressDialog!!.setMessage(String.format(context.getString(R.string.async_unzip_message), values[1]))
                progressDialog!!.show()
            } else {
                FileIO(context).showErr(values[0])
            }
        }

        override fun onPostExecute(b: Boolean?) {
            progressDialog!!.dismiss()
            progressDialog = ProgressDialog(context)
            if (b == true) {
                progressDialog!!.setTitle(String.format(context.getString(R.string.dialog_unzip_sucT), name))
                progressDialog!!.setMessage(String.format(context.getString(R.string.dialog_unzip_sucM), name))
//                val s = FileIO(context).getUnipackInfo(File(target!! + "info"), target!!)
//                val item = UnipackListItem()
//                item.fname = s!![0]
//                item.fproducer = s!![1]
//                item.fchain = s!![2]
//                item.fpath = s!![3]
//                adapter!!.addItem(item)
                (context as MainActivity).showUnipack()
            } else {
                progressDialog!!.setTitle(String.format(context.getString(R.string.dialog_unzip_failT), name))
                progressDialog!!.setMessage(String.format(context.getString(R.string.dialog_unzip_failM), name))
            }
            progressDialog!!.setButton(context.getString(R.string.alert_ok)) { dialogInterface, i -> progressDialog!!.dismiss() }
            progressDialog!!.show()
        }

        @Throws(IOException::class)
        private fun Unzip(zipInputStream: ZipInputStream, targetFile: File): File {
            var fileOutputStream: FileOutputStream? = null

            val BUFFER_SIZE = 1024 * 2

            try {
                fileOutputStream = FileOutputStream(targetFile)

                val buffer = ByteArray(BUFFER_SIZE)
                var len = 0
                try {
                    do {
                        len = zipInputStream.read(buffer)
                        if (len == -1) {
                            break
                        }
                        fileOutputStream!!.write(buffer, 0, len)
                    } while (true)
//                while ((len = zipInputStream.read(buffer)) != -1) {
//                    fileOutputStream!!.write(buffer, 0, len)
//                }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    fileOutputStream!!.close()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return targetFile
        }
    }
}
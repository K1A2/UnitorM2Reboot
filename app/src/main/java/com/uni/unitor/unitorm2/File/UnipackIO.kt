package com.uni.unitor.unitorm2.File

import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import java.io.File
import java.io.FileFilter

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
}
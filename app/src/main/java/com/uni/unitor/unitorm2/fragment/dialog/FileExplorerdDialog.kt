package com.uni.unitor.unitorm2.fragment.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.uni.unitor.unitorm2.File.FileIO
import com.uni.unitor.unitorm2.R
import com.uni.unitor.unitorm2.layout.LayoutKey
import com.uni.unitor.unitorm2.view.recycler.SelectFileAdapter
import com.uni.unitor.unitorm2.view.recycler.SelectFileItem
import com.uni.unitor.unitorm2.view.recycler.listener.RecyclerItemClickListener
import java.io.File
import android.widget.AbsListView
import android.widget.Toast
import com.uni.unitor.unitorm2.view.recycler.FileListItem
import android.widget.AdapterView
import java.util.*
import kotlin.collections.ArrayList


class FileExplorerdDialog : DialogFragment() {

    private lateinit var text_path:TextView
    private lateinit var text_order:TextView
    private lateinit var btn_add:Button
    private lateinit var list_files:ListView
    
    private lateinit var selectFileAdapter:SelectFileAdapter

    private var checkSelected:ArrayList<Int> = ArrayList()
    private lateinit var con:Context
    private lateinit var now:String
    private lateinit var fileIO:FileIO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root:View = inflater.inflate(R.layout.dialog_fileex, container, false)

        val bundle = arguments
        if (bundle!=null) {
            now = bundle.getString(LayoutKey.DIALOG_FILEEX_TAG)
            selectFileAdapter = SelectFileAdapter(now)
        } else {
            now = LayoutKey.DIALOG_FILEEX_SOUND
            selectFileAdapter = SelectFileAdapter(now)
        }

        fileIO = FileIO(activity!!)

        text_order = root.findViewById(R.id.text_sum)
        text_path = root.findViewById(R.id.text_file_path)
        btn_add = root.findViewById(R.id.dialog_add)
        list_files = root.findViewById(R.id.recycle_files)

        when (now) {
            LayoutKey.DIALOG_FILEEX_SOUND -> {
                onFileSelectListener = con as OnFileSelectListener
                text_order.setText(getString(R.string.dialog_title_file))
            }
            LayoutKey.DIALOG_FILEEX_UNIPACK -> {
                onUnipackSelectListener = con as OnUnipackSelectListener
                text_order.setText(getString(R.string.dialog_title_unipack))
                btn_add.visibility = View.GONE
            }
        }

        list_files.setOnItemClickListener { adapterView, view, i, l ->
            when (now) {
                LayoutKey.DIALOG_FILEEX_SOUND -> {
                    val fileListItem = selectFileAdapter.getItem(i)

                    //클릭된 파일 경로 가져옴
                    val clicked = File(fileListItem.pathf)

                    //클릭된 항목이 폴더인지 파일인지 구분
                    if (clicked.isDirectory()) {//폴더일때
                        if (clicked.canRead()) {//읽을수 있는지 없는지 확인
                            list_files.choiceMode = AbsListView.CHOICE_MODE_SINGLE
                            showList(fileListItem.pathf!!)//폴더이므로 재귀호출로 하뤼폴더 리스트에 보여줌
                        } else {//못읽으면 토스트로 오류출력
                            //Toast.makeText(con, getString(R.string.DialogFrag_FileS_cant), Toast.LENGTH_LONG).show()
                        }
                        list_files.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE
                    } else if (fileListItem.titlef.equals(".../") && fileListItem.pathf.equals("")) {//상위폴더 이동
                        val parent_path = File(text_path.getText().toString()).getParent()
                        showList(parent_path + "/")//재귀호출로 상위폴더의 파일을 보여줌
                    } else if (clicked.getName().endsWith(".wav") || clicked.getName().endsWith(".mp3")) {
                        var remove:Int? = null
                        for (c in 0..checkSelected.size - 1) {
                            val ch = checkSelected.get(c)
                            if (ch.equals(i)) {
                                remove = c
                                break
                            }
                        }
                        if (remove != null) {
                            checkSelected.removeAt(remove)
                        } else {
                            checkSelected.add(i)
                        }
                    }
                }
                LayoutKey.DIALOG_FILEEX_UNIPACK -> {
                    val fileListItem = selectFileAdapter.getItem(i)

                    //클릭된 파일 경로 가져옴
                    val clicked = File(fileListItem.pathf)

                    //클릭된 항목이 폴더인지 파일인지 구분
                    if (clicked.isDirectory()) {//폴더일때
                        if (clicked.canRead()) {//읽을수 있는지 없는지 확인
                            list_files.choiceMode = AbsListView.CHOICE_MODE_SINGLE
                            showList(fileListItem.pathf!!)//폴더이므로 재귀호출로 하뤼폴더 리스트에 보여줌
                        } else {//못읽으면 토스트로 오류출력
                            //Toast.makeText(con, getString(R.string.DialogFrag_FileS_cant), Toast.LENGTH_LONG).show()
                        }
                        list_files.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE
                    } else if (fileListItem.titlef.equals(".../") && fileListItem.pathf.equals("")) {//상위폴더 이동
                        val parent_path = File(text_path.getText().toString()).getParent()
                        showList(parent_path + "/")//재귀호출로 상위폴더의 파일을 보여줌
                    } else if (clicked.getName().endsWith(".zip")) {
                        onUnipackSelectListener.onUnipackSelect(clicked.name, clicked.absolutePath)
                    }
                }
            }
        }

        //추가 버튼 리스너
        btn_add.setOnClickListener {
            when (now) {
                LayoutKey.DIALOG_FILEEX_SOUND -> {
//                    val checkedId = list_files.checkedItemPositions
//                    val ids = list_files.checkedItemIds
//                    if (ids != null) {
//                        val count = ids.size
//                        val selected = ArrayList<Array<String>>()
//                        for (i in 0..count-1) {
//                                val item = selectFileAdapter.getItem(ids.get(i) as Int)
//                                selected.add(arrayOf(item.titlef!!, item.pathf!!))
//                        }
//                        onFileSelectListener.onFileSelect(selected)
//                    }
                    val selected = ArrayList<Array<String>>()
                    for (i in checkSelected) {
                        val item = selectFileAdapter.getItem(i)
                        selected.add(arrayOf(item.titlef!!, item.pathf!!))
                    }
                    onFileSelectListener.onFileSelect(selected)
                }
                LayoutKey.DIALOG_FILEEX_UNIPACK -> {

                }
            }
        }

        showList(fileIO.getDefaultPath())
        return root
    }

    override fun onStart() {
        super.onStart()
        if (context != null) {
            val dm = context!!.resources.displayMetrics
            val width = dm.widthPixels
            val height = dm.heightPixels
            dialog.window.setLayout(width*95/100, height*95/100);
        }
    }

    private fun showList(path:String) {
        checkSelected = ArrayList()
        var filelist:Array<File>? = null

        text_path.setText(path)
        selectFileAdapter.clearItem()

        when (now) {
            LayoutKey.DIALOG_FILEEX_SOUND -> {
                filelist = File(path).listFiles { file -> file.isDirectory() ||
                        (file.isFile() && (file.getName().endsWith(".wav") ||
                                file.getName().endsWith(".mp3")))  }
            }
            LayoutKey.DIALOG_FILEEX_UNIPACK -> {
                filelist = File(path).listFiles { file -> file.isDirectory() ||
                        (file.isFile() && file.getName().endsWith(".zip"))  }
            }
        }

        Arrays.sort(filelist, object : Comparator<File> {
            override fun compare(file: File, t1: File): Int {
                return file.name.compareTo(t1.name, ignoreCase = true)
            }
        })

        if (path != fileIO.getDefaultPath()) {
            val  a = SelectFileItem()
            a.iconf = context!!.resources.getDrawable(android.support.design.R.drawable.navigation_empty_icon)
            a.pathf = ""
            a.titlef = ".../"
            selectFileAdapter.addItem(a)
        }

        for (file in filelist!!) {
            if (file.isDirectory()) {
                val a = SelectFileItem()
                a.iconf = context!!.resources.getDrawable(R.drawable.round_folder_black_48)
                a.titlef = file.name
                a.pathf = file.absolutePath + "/"
                selectFileAdapter.addItem(a)
            } else {
                if (file.getName().endsWith(".zip")) {
                    val a = SelectFileItem()
                    a.iconf = context!!.resources.getDrawable(R.drawable.round_folder_open_black_48)
                    a.titlef = file.name
                    a.pathf = file.absolutePath + "/"
                    selectFileAdapter.addItem(a)
                } else if (file.getName().endsWith(".wav") || file.getName().endsWith(".mp3")) {
                    val a = SelectFileItem()
                    a.iconf = context!!.resources.getDrawable(R.drawable.round_music_note_black_48)
                    a.titlef = file.name
                    a.pathf = file.absolutePath + "/"
                    selectFileAdapter.addItem(a)
                }
            }
        }
        list_files.adapter = selectFileAdapter
    }

    lateinit var onUnipackSelectListener: OnUnipackSelectListener
    lateinit var onFileSelectListener: OnFileSelectListener

    interface OnUnipackSelectListener {
        fun onUnipackSelect(name: String, path: String)
    }

    interface OnFileSelectListener {
        fun onFileSelect(files: ArrayList<Array<String>>)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        con = context!!
    }
}
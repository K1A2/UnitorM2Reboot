package com.uni.unitor.unitorm2.File.layout

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import com.uni.unitor.unitorm2.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        if (supportActionBar != null) supportActionBar!!.hide()

        //안드 5.0 이상일때 권한검사
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //저장소 읽기/쓰기 권한 요청
            val permisionRequest = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permisionRequest2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

            if (permisionRequest == PackageManager.PERMISSION_DENIED || permisionRequest2 == PackageManager.PERMISSION_DENIED)
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    val permissioCheck = AlertDialog.Builder(this)
                    permissioCheck.setTitle(getString(R.string.per_re_title))
                            .setMessage(getString(R.string.per_re_content))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.agree), DialogInterface.OnClickListener { dialog, which ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which ->
                                Toast.makeText(this, getString(R.string.unable_save), Toast.LENGTH_SHORT).show()
                                finish()
                            })
                            .create()
                            .show()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                }
            else {
                startMain()
            }
        } else {
            startMain()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMain()
                } else {
                    Toast.makeText(this, R.string.unable_save, Toast.LENGTH_SHORT).show()
                    finish()
                }
                return
            }
        }
    }

    private fun startMain() {
        val h = Handler()
        h.postDelayed(Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}
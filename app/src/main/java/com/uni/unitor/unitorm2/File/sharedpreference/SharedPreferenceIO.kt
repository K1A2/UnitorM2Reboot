package com.uni.unitor.unitorm2.File.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SharedPreferenceIO {

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var context: Context? = null

    constructor(context: Context) {
        this.context = context
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        this.editor = sharedPreferences!!.edit()
    }

    constructor(context: Context, repository: String) {
        this.context = context
        this.sharedPreferences = context.getSharedPreferences(repository, Context.MODE_PRIVATE)
        this.editor = sharedPreferences!!.edit()
    }

    fun setNewSharedPreference(repository: String) {
        this.sharedPreferences = context!!.getSharedPreferences(repository, Context.MODE_PRIVATE)
        this.editor = sharedPreferences!!.edit()
    }

    fun getString(name: String, defaultV: String): String? {
        return sharedPreferences!!.getString(name, defaultV)
    }

    fun getBoolean(name: String, defaultV: Boolean): Boolean {
        return sharedPreferences!!.getBoolean(name, defaultV)
    }

    fun setString(name: String, value: String) {
        editor!!.putString(name, value).commit()
    }

    fun setBoolean(name: String, value: Boolean) {
        editor!!.putBoolean(name, value).commit()
    }
}
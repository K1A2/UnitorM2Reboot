package com.uni.unitor.unitorm2.preference

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import com.uni.unitor.unitorm2.R
import android.preference.SwitchPreference



class PreferenceActivity : PreferenceActivity(), Preference.OnPreferenceClickListener {

    private lateinit var switch_display: SwitchPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)

        switch_display = findPreference("setting_screen") as SwitchPreference
        switch_display.onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        if (preference!!.getKey().equals("setting_screen")) {

        }
        return false
    }
}
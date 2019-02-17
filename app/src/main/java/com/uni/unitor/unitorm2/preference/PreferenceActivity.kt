package com.uni.unitor.unitorm2.preference

import android.os.Bundle
import android.preference.PreferenceActivity
import com.uni.unitor.unitorm2.R

class PreferenceActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
    }
}
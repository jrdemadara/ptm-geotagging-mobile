package com.jrdemadara.ptm_geotagging.features.register

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.util.NetworkChecker

class RegisterActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefApp = "pref_app"
    private var prefUserId = "pref_user_id"
    private lateinit var buttonRegister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        localDatabase = LocalDatabase(this)
        sharedPreferences = getSharedPreferences(prefApp, MODE_PRIVATE)
        buttonRegister = findViewById(R.id.buttonRegister)
        val deviceModel = Build.MODEL
        val deviceBrand = Build.MANUFACTURER
        val deviceName = Build.DEVICE
        buttonRegister.setOnClickListener{

        }

    }
}
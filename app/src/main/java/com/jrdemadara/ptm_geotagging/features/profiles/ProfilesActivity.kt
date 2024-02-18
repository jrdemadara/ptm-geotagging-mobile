package com.jrdemadara.ptm_geotagging.features.profiles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.login.LoginActivity
import com.jrdemadara.ptm_geotagging.features.profiling.ProfilingActivity

class ProfilesActivity : AppCompatActivity() {
    private lateinit var floatingButtonAdd: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles)
        floatingButtonAdd = findViewById(R.id.floatingActionButtonAdd)
        floatingButtonAdd.setOnClickListener {
            val intent = Intent(applicationContext, ProfilingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
package com.jrdemadara.ptm_geotagging.features.search

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.admin.AdminActivity
import com.jrdemadara.ptm_geotagging.features.profile_details.ProfileDetailsActivity
import com.jrdemadara.ptm_geotagging.server.LocalDatabase

class PowerSearchActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    //* Search Variables
    private lateinit var recyclerViewProfile: RecyclerView
    private var adapterPowerSearch: PowerSearchAdapter? = null
    private lateinit var editTextPowerSearch: EditText
    private lateinit var buttonViewProfile: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power_search)
        val toolbar: Toolbar = findViewById(R.id.materialToolbarPowerSearch)
        setSupportActionBar(toolbar)

        // Enable the back arrow button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        localDatabase = LocalDatabase(this@PowerSearchActivity)

        // Initialize Power Search
        recyclerViewProfile = findViewById(R.id.recyclerViewPowerSearch)
        recyclerViewProfile.layoutManager = LinearLayoutManager(this@PowerSearchActivity)
        adapterPowerSearch = PowerSearchAdapter()
        recyclerViewProfile.adapter = adapterPowerSearch
        editTextPowerSearch = findViewById(R.id.editTextPowerSearch)
        buttonViewProfile = findViewById(R.id.buttonViewProfile)
        var id: String = ""
        var precinct: String = ""
        var lastname: String = ""
        var firstname: String = ""
        var middlename: String = ""
        var extension: String = ""
        var birthdate: String = ""
        var phone: String = ""
        var occupation: String = ""
        var hasptmid: String = ""


        val profiles = localDatabase.getProfilesPowerSearch()
        adapterPowerSearch?.addItems(profiles)

        editTextPowerSearch.requestFocus()
        editTextPowerSearch.doAfterTextChanged {
            val result = localDatabase.powerSearch(editTextPowerSearch.text.toString())
            adapterPowerSearch?.addItems(result)
        }

        adapterPowerSearch!!.setOnClickItem {
            id = it.id
            precinct = it.precinct
            lastname =  it.lastname
            firstname = it.firstname
            middlename = it.middlename
            extension = it.extension
            birthdate = it.birthdate
            phone = it.phone
            occupation = it.occupation
            hasptmid = it.hasptmid.toString()
        }

        buttonViewProfile.setOnClickListener {
            val intent = Intent(this@PowerSearchActivity, ProfileDetailsActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("precinct", precinct)
            intent.putExtra("lastname", lastname)
            intent.putExtra("firstname", firstname)
            intent.putExtra("middlename", middlename)
            intent.putExtra("extension", extension)
            intent.putExtra("birthdate", birthdate)
            intent.putExtra("phone", phone)
            intent.putExtra("occupation", occupation)
            intent.putExtra("hasptmid", hasptmid)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(applicationContext, AdminActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }
}
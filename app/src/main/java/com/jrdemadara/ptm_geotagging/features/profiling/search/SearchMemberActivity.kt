package com.jrdemadara.ptm_geotagging.features.profiling.search

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.SearchMembers
import com.jrdemadara.ptm_geotagging.features.profiling.ProfilingActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import com.jrdemadara.ptm_geotagging.util.capitalizeWords

class SearchMemberActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences

    //* Search Variables
    private lateinit var recyclerViewSearch: RecyclerView
    private var adapterSearch: SearchAdapter? = null
    private lateinit var editTextSearchMember: EditText
    private lateinit var textViewSelectedName: TextView
    private lateinit var buttonSelectMember: Button
    private lateinit var buttonSearchMember: ImageButton
    private var prefAccessToken = "pref_access_token"
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_member)
        val toolbar: Toolbar = findViewById(R.id.materialToolbarSearch)
        setSupportActionBar(toolbar)

        // Enable the back arrow button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        localDatabase = LocalDatabase(this@SearchMemberActivity)
        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)
        accessToken = sharedPreferences.getString(prefAccessToken, null).toString()
        // Initialize Search
        recyclerViewSearch = findViewById(R.id.recyclerViewSearchMember)
        recyclerViewSearch.layoutManager = LinearLayoutManager(this@SearchMemberActivity)
        adapterSearch = SearchAdapter()
        recyclerViewSearch.adapter = adapterSearch
        editTextSearchMember = findViewById(R.id.editTextSearchMember)
        textViewSelectedName = findViewById(R.id.textViewSelectedName)
        buttonSelectMember = findViewById(R.id.buttonSelectMember)
        buttonSearchMember = findViewById(R.id.buttonSearchMember)
        var qrcode: String = ""
        var precinct: String = ""
        var lastname: String = ""
        var firstname: String = ""
        var middlename: String = ""
        var extension: String = ""
        var birthdate: String = ""
        var contact: String = ""
        var occupation: String = ""
        var purok: String = ""
        var hasptmid: String = ""
        var isMuslim: Boolean = false

        editTextSearchMember.requestFocus()
        buttonSearchMember.setOnClickListener {
            networkChecker = NetworkChecker(application)
            networkChecker.observe(this) { isConnected ->
                if (isConnected) {
                    val retrofit =
                        NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)

                    Thread {
                        try {
                            val response =
                                retrofit.searchMember(editTextSearchMember.text.toString())
                                    .execute()

                            if (response.isSuccessful) {
                                val members: ArrayList<SearchMembers> =
                                    ArrayList(response.body() ?: emptyList())
                                
                                // Update adapter on main thread
                                Handler(Looper.getMainLooper()).post {
                                    adapterSearch?.addItems(members)
                                }

                            } else {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        applicationContext,
                                        "Something went wrong.\nStatusCode: ${response.code()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(
                                    applicationContext,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("SearchMemberActivity", e.message.toString())
                            }
                        }
                    }.start()

                }
            }
        }



        adapterSearch!!.setOnClickItem {
            textViewSelectedName.text = buildString {
                append(it.lastname.capitalizeWords())
                append(", ")
                append(it.firstname.capitalizeWords())
                append(" ")
                append(it.middlename.capitalizeWords())
                append(" ")
                append(it.extension.capitalizeWords())
            }
            qrcode = it.qrcode
            precinct = it.precinct
            lastname = it.lastname
            firstname = it.firstname
            middlename = it.middlename
            extension = it.extension
            birthdate = it.birthdate
            contact = it.phone
            occupation = it.occupation
            purok = it.purok
            hasptmid = it.has_ptmid
            isMuslim = it.is_muslim
        }

        buttonSelectMember.setOnClickListener {
            val intent = Intent(this@SearchMemberActivity, ProfilingActivity::class.java)
            intent.putExtra("qrcode", qrcode)
            intent.putExtra("precinct", precinct)
            intent.putExtra("lastname", lastname)
            intent.putExtra("firstname", firstname)
            intent.putExtra("middlename", middlename)
            intent.putExtra("extension", extension)
            intent.putExtra("birthdate", birthdate)
            intent.putExtra("contact", contact)
            intent.putExtra("occupation", occupation)
            intent.putExtra("purok", purok)
            intent.putExtra("hasptmid", hasptmid)
            intent.putExtra("isMuslim", isMuslim)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(applicationContext, ProfilingActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

}
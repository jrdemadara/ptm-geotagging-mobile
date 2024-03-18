package com.jrdemadara.ptm_geotagging.features.profiling.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.Members
import com.jrdemadara.ptm_geotagging.data.SearchMembers
import com.jrdemadara.ptm_geotagging.features.profiling.ProfilingActivity
import com.jrdemadara.ptm_geotagging.server.LocalDatabase

class SearchMemberActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    //* Search Variables
    private lateinit var recyclerViewSearch: RecyclerView
    private var adapterSearch: SearchAdapter? = null
    private var membersList: ArrayList<SearchMembers> = ArrayList()
    private lateinit var editTextSearchMember: EditText
    private lateinit var textViewSelectedName: TextView
    private lateinit var buttonSelectMember: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_member)
        localDatabase = LocalDatabase(this@SearchMemberActivity)
        // Initialize Search
        recyclerViewSearch = findViewById(R.id.recyclerViewSearchMember)
        recyclerViewSearch.layoutManager = LinearLayoutManager(this@SearchMemberActivity)
        adapterSearch = SearchAdapter()
        recyclerViewSearch.adapter = adapterSearch
        editTextSearchMember = findViewById(R.id.editTextSearchMember)
        textViewSelectedName = findViewById(R.id.textViewSelectedName)
        buttonSelectMember = findViewById(R.id.buttonSelectMember)
        var precinct: String = ""
        var lastname: String = ""
        var firstname: String = ""
        var middlename: String = ""
        var extension: String = ""
        var birthdate: String = ""
        var contact: String = ""
        var occupation: String = ""
        var hasptmid: Int = 0

        editTextSearchMember.requestFocus()
        editTextSearchMember.doAfterTextChanged {
            val result = localDatabase.searchMember(editTextSearchMember.text.toString())
            adapterSearch?.addItems(result)
        }

        adapterSearch!!.setOnClickItem {
            textViewSelectedName.text = buildString {
                append(it.lastname.replaceFirstChar (Char::uppercase))
                append(", ")
                append(it.firstname.replaceFirstChar (Char::uppercase))
                append(" ")
                append(it.middlename.replaceFirstChar (Char::uppercase))
                append(" ")
                append(it.extension.replaceFirstChar (Char::uppercase))
            }
            lastname = it.lastname
            firstname = it.firstname
            middlename = it.middlename
            extension = it.extension
            birthdate = it.birthdate
            contact = it.contact
            occupation = it.occupation
            hasptmid = it.isptmid
        }

        buttonSelectMember.setOnClickListener {
            val intent = Intent(this@SearchMemberActivity, ProfilingActivity::class.java)
            intent.putExtra("lastname", lastname)
            intent.putExtra("firstname", firstname)
            intent.putExtra("middlename", middlename)
            intent.putExtra("extension", extension)
            intent.putExtra("birthdate", birthdate)
            intent.putExtra("contact", contact)
            intent.putExtra("occupation", occupation)
            intent.putExtra("hasptmid", hasptmid)
            startActivity(intent)
        }
    }
}
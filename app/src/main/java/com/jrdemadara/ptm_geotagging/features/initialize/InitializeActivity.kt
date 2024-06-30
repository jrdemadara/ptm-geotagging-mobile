package com.jrdemadara.ptm_geotagging.features.initialize

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.Assistance
import com.jrdemadara.ptm_geotagging.data.Barangay
import com.jrdemadara.ptm_geotagging.data.Members
import com.jrdemadara.ptm_geotagging.features.profiles.ProfilesActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InitializeActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefAccessToken = "pref_access_token"
    private var prefMunicipality = "pref_municipality"
    private lateinit var accessToken: String
    private lateinit var municipality: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initialize)
        localDatabase = LocalDatabase(this)
        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)
        accessToken = sharedPreferences.getString(prefAccessToken, null).toString()
        municipality = sharedPreferences.getString(prefMunicipality, null).toString()
        initializeMember()
        initializeAssistance()
        initializeBarangay()
    }

    private fun initializeMember(){
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)
                val filter = HashMap<String, String>()
                filter["municipality"] = municipality
                retrofit.getMembers(filter).enqueue(object : Callback<List<Members>?> {
                    override fun onResponse(
                        call: Call<List<Members>?>,
                        response: Response<List<Members>?>
                    ){
                        //Update Members
                        val list: List<Members>? = response.body()
                        val membersCount = list?.size
                        var savedCount = 0
                        assert(list != null)
                        if (list != null) {
                            localDatabase.truncateMembers()
                            for (x in list) {
                                savedCount++
                                val precinct = if (x.precinct.isNullOrEmpty()) "" else x.precinct
                                val birthdate = if (x.birthdate.isNullOrEmpty()) "" else x.birthdate
                                val contact = if (x.contact.isNullOrEmpty()) "" else x.contact
                                val occupation = if (x.occupation.isNullOrEmpty()) "" else x.occupation
                                val hasPTMID = if (x.isptmid == "NO") 0 else 1
                                localDatabase.updateMembers(
                                    precinct,
                                    x.lastname.lowercase(),
                                    x.firstname.lowercase(),
                                    x.middlename.lowercase(),
                                    x.extension.lowercase(),
                                    birthdate,
                                    contact,
                                    occupation,
                                    hasPTMID
                                )
                            }
                            if (membersCount == savedCount){
                                val intent = Intent(applicationContext, ProfilesActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<Members>?>, t: Throwable) {
                        Log.e("Request Failure", t.message.toString())

                    }
                })
            }
        }
    }

    private fun initializeBarangay(){
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {

                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)
                val filter = HashMap<String, String>()
                filter["municipality"] = municipality
                retrofit.getBarangays(filter).enqueue(object : Callback<List<Barangay>?> {
                    override fun onResponse(
                        call: Call<List<Barangay>?>,
                        response: Response<List<Barangay>?>
                    ){
                        //Update Barangay
                        val list: List<Barangay>? = response.body()
                        val barangaysCount = list?.size
                        var savedCount = 0
                        assert(list != null)
                        if (list != null) {
                            for (x in list) {
                                savedCount++
                                val barangay = x.name.ifEmpty { "" }
                                localDatabase.updateBarangays(
                                    barangay,
                                )
                            }
                            if (barangaysCount == savedCount){
                                val intent = Intent(applicationContext, ProfilesActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<Barangay>?>, t: Throwable) {
                        Log.e("Request Failure", t.message.toString())

                    }
                })
            }
        }
    }

    private fun initializeAssistance(){
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)
                val filter = HashMap<String, String>()
                filter["assistance"] = municipality
                retrofit.getAssistanceType(filter).enqueue(object : Callback<List<Assistance>?> {
                    override fun onResponse(
                        call: Call<List<Assistance>?>,
                        response: Response<List<Assistance>?>
                    ){
                        //Update Members
                        val list: List<Assistance>? = response.body()
                        assert(list != null)
                        if (list != null) {
                            localDatabase.truncateAssistanceType()
                            for (x in list) {
                                localDatabase.updateAssistanceType(
                                    x.assistance,
                                )
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<Assistance>?>, t: Throwable) {
                    }
                })
            }
        }
    }
}
package com.jrdemadara.ptm_geotagging.features.initialize

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.Assistance
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
                                val hasPTMID = if (x.isptmid == "NO") 0 else 1
                                localDatabase.updateMembers(
                                    x.precinct,
                                    x.lastname,
                                    x.firstname,
                                    x.middlename,
                                    x.extension,
                                    x.birthdate,
                                    x.contact,
                                    x.occupation,
                                    hasPTMID,
                                    x.assistance,
                                    x.amount,
                                    x.dateavailed
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
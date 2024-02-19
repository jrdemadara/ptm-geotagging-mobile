package com.jrdemadara.ptm_geotagging.features.profiles

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.profiling.ProfilingActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilesActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefAccessToken = "pref_access_token"
    private lateinit var accessToken: String
    private lateinit var floatingButtonAdd: FloatingActionButton
    private lateinit var textViewTotalProfile: TextView
    private lateinit var textViewUploaded: TextView
    private lateinit var textViewNotUploaded: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles)
        val toolbar = findViewById<View>(R.id.materialToolbar2) as MaterialToolbar
        localDatabase = LocalDatabase(this@ProfilesActivity)
        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)
        accessToken = sharedPreferences.getString(prefAccessToken, null).toString()
        floatingButtonAdd = findViewById(R.id.floatingActionButtonAdd)
        textViewTotalProfile = findViewById(R.id.textViewTotalProfile)
        textViewUploaded = findViewById(R.id.textViewUploaded)
        textViewNotUploaded = findViewById(R.id.textViewNotUploaded)

        textViewTotalProfile.text = localDatabase.getProfileCount().toString()
        textViewUploaded.text = localDatabase.getUploadedCount().toString()
        textViewNotUploaded.text = localDatabase.getNotUploadedCount().toString()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    println("Logout")
                    true
                }
                R.id.about -> {
                    // Handle click on the menu item
                    true
                }
                R.id.upload -> {
                    uploadProfile()
                }
            }
            false
        }

        floatingButtonAdd.setOnClickListener {
            val intent = Intent(applicationContext, ProfilingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun uploadProfile(){
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)

                val profileWithDetails = localDatabase.getProfilesWithDetails()

                profileWithDetails.forEach { details ->
                    val profile = details.profile

                    val filter = HashMap<String, Any>()
                    filter["lastname"] = profile.lastname
                    filter["firstname"] = profile.firstname
                    filter["middlename"] = profile.middlename
                    filter["extension"] = profile.extension
                    filter["birthdate"] = profile.birthdate
                    filter["occupation"] = profile.occupation
                    filter["phone"] = profile.phone
                    filter["lat"] = profile.lat
                    filter["lon"] = profile.lon

                    // Convert beneficiaries list to a list of maps
                    val beneficiariesList = details.beneficiaries?.map { beneficiary ->
                        mapOf(
                            "precinct" to beneficiary.precinct,
                            "fullname" to beneficiary.fullname,
                            "birthdate" to beneficiary.birthdate
                        )
                    } ?: emptyList()

                    val jsonArray = beneficiariesList.map { beneficiary ->
                        "{ \"precinct\": \"${beneficiary["precinct"]}\", \"fullname\": \"${beneficiary["fullname"]}\", \"birthdate\": \"${beneficiary["birthdate"]}\" }"
                    }.joinToString(", ", "[", "]")

                    filter["beneficiaries"] = jsonArray

                    // Convert skills and livelihoods lists to simple lists
                    val skillsJsonArray = details.skills.joinToString(", ", "{\"skills\": [", "]}") { skill ->
                        "\"${skill}\""
                    }

                    val livelihoodsJsonArray = details.livelihoods.joinToString(", ", "{\"livelihoods\": [", "]}") { livelihood ->
                        "\"${livelihood}\""
                    }

                    filter["skills"] = skillsJsonArray
                    filter["livelihoods"] = livelihoodsJsonArray

                    // Convert photos to strings
                    val photo = details.photo
                    val personalPhoto: String = photo.personalPhoto.decodeToString()
                    val familyPhoto: String = photo.familyPhoto.decodeToString()
                    val livelihoodPhoto: String = photo.livelihoodPhoto.decodeToString()
                    filter["personalPhoto"] = personalPhoto
                    filter["familyPhoto"] = familyPhoto
                    filter["livelihoodPhoto"] = livelihoodPhoto

                    // Make POST request
                    retrofit.uploadProfile(filter).enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                            if (response.code() == 201) {
                                // Data uploaded successfully
                                println("Data uploaded successfully for profile with ID: ${profile.id}")
                            } else {
                                // Handle unsuccessful response
                                val responseBody = response.body()?.string() // Convert response body to string
                                println(responseBody.toString())
                                Toast.makeText(applicationContext, response.code().toString(), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            // Handle failure
                            println("Failed to upload data for profile with ID: ${profile.id}. Error: ${t.message}")
                        }
                    })
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }
}
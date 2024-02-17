package com.jrdemadara.ptm_geotagging

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.jrdemadara.ptm_geotagging.data.Municipality
import com.jrdemadara.ptm_geotagging.features.login.LoginActivity
import com.jrdemadara.ptm_geotagging.features.register.RegisterActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefApp = "pref_app"
    private var prefFirstStart = "pref_first_start"
    private lateinit var buttonGetStarted: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        localDatabase = LocalDatabase(this)
        sharedPreferences = getSharedPreferences(prefApp, MODE_PRIVATE)
        buttonGetStarted = findViewById(R.id.buttonGetStarted)
        updateMunicipalities()
        buttonGetStarted.setOnClickListener{
            checkFirstStart()
        }
    }

    private fun checkFirstStart(){
        val firstStart = sharedPreferences.getString(prefFirstStart, null)
        if (firstStart != null) {
            //* App is already initialized
            //* Proceed to login
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            //* Initialize the app
            //* Proceed to register
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateMunicipalities() {
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
                retrofit.getMunicipalities().enqueue(object : Callback<List<Municipality>?> {
                    override fun onResponse(
                        call: Call<List<Municipality>?>,
                        response: Response<List<Municipality>?>
                    ){
                        val list: List<Municipality>? = response.body()
                        assert(list != null)
                        if (list != null) {
                            localDatabase.truncateTables()
                            for (x in list) {
                                localDatabase.updateMunicipalities(
                                    x.name
                                )
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<Municipality>?>, t: Throwable) {
                    }
                })
            }
        }
    }
}
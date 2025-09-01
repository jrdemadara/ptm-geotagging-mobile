package com.jrdemadara.ptm_geotagging.features.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.initialize.InitializeActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.NodeServer
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var prefAccessToken = "pref_access_token"
    private lateinit var accessToken: String
    private lateinit var buttonLogin: Button
    private lateinit var progressBar: ProgressBar


    private lateinit var editTextLoginEmail: EditText
    private lateinit var editTextLoginPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)
        accessToken = sharedPreferences.getString(prefAccessToken, null).toString()
        buttonLogin = findViewById(R.id.buttonLogin)
        progressBar = findViewById(R.id.buttonSpinner)
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail)
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword)
        buttonLogin.setOnClickListener {
            login()
        }
    }


    private fun login() {
        if (editTextLoginEmail.text.isNotEmpty() && editTextLoginPassword.text.isNotEmpty()) {
            showLoading(true)
            val retrofit =
                NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)
            val filter = HashMap<String, String>()
            filter["email"] = editTextLoginEmail.text.toString()
            filter["password"] = editTextLoginPassword.text.toString()
            filter["device_id"] = Build.ID

            retrofit.loginUser(filter).enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                        val jsonObject = responseBody?.let { JSONObject(it) }
                        val accessToken = jsonObject?.getString("access_token")

                        if (accessToken != null) {
                            saveAccessToken(accessToken)
                            val intent = Intent(applicationContext, InitializeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Login failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    showLoading(false)
                    Log.e("Request Failure", t.message.toString())
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } else {
            Toast.makeText(
                applicationContext,
                "Please fill the required fields.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun showLoading(show: Boolean) {
        if (show) {
            buttonLogin.text = ""
            buttonLogin.isEnabled = false
            progressBar.visibility = View.VISIBLE
        } else {
            buttonLogin.text = "Login"
            buttonLogin.isEnabled = true
            progressBar.visibility = View.GONE
        }
    }

    private fun saveAccessToken(accessToken: String?) {
        getSharedPreferences("pref_app", MODE_PRIVATE)
            .edit()
            .putString(prefAccessToken, accessToken)
            .apply()
    }
}
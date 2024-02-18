package com.jrdemadara.ptm_geotagging.features.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.profiles.ProfilesActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var prefAccessToken= "pref_access_token"
    private lateinit var buttonLogin: Button
    private lateinit var editTextLoginEmail: EditText
    private lateinit var editTextLoginPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)

        buttonLogin = findViewById(R.id.buttonLogin)
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail)
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword)
        buttonLogin.setOnClickListener{
            login()
        }
    }


    private fun login() {
        if (editTextLoginEmail.text.isNotEmpty() && editTextLoginPassword.text.isNotEmpty()){
            val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
            val filter = HashMap<String, String>()
            filter["email"] = editTextLoginEmail.text.toString()
            filter["password"] = editTextLoginPassword.text.toString()
            filter["device_id"] =  Build.ID

            retrofit.loginUser(filter).enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string() // Convert response body to string
                        val jsonObject = responseBody?.let { JSONObject(it) }
                        val accessToken = jsonObject?.getString("access_token")

                        // Now you have the access token, you can use it as needed
                        if (accessToken != null) {
                            saveAccessToken(accessToken)
                            val intent = Intent(applicationContext, ProfilesActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // Handle unsuccessful response
                        Log.e("Response Error", "Unsuccessful response: ${response.code()}")
                    }

                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("Request Failure", t.message.toString())
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(applicationContext, "Please fill the required fields.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAccessToken(accessToken: String?) {
        getSharedPreferences("pref_app", MODE_PRIVATE)
            .edit()
            .putString(prefAccessToken, accessToken)
            .apply()
    }
}
package com.jrdemadara.ptm_geotagging.features.register

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.Municipality
import com.jrdemadara.ptm_geotagging.features.login.LoginActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefApp = "pref_app"
    private lateinit var buttonRegister: Button
    private lateinit var spinnerMunicipality: Spinner
    private lateinit var editTextFullname: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        localDatabase = LocalDatabase(this)
        sharedPreferences = getSharedPreferences(prefApp, MODE_PRIVATE)
        buttonRegister = findViewById(R.id.buttonRegister)
        spinnerMunicipality = findViewById(R.id.spinnerMunicipality)
        editTextFullname = findViewById(R.id.editTextFullname)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        populateSpinner()

        buttonRegister.setOnClickListener{
            register()
        }
    }

    private fun populateSpinner(){
        val adapter = ArrayAdapter(this.applicationContext, android.R.layout.simple_spinner_item, localDatabase.getMunicipalities())
        spinnerMunicipality.adapter = adapter
        spinnerMunicipality.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {}
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun register() {
        if (editTextPassword.text.toString() == editTextConfirmPassword.text.toString()){
            val retrofit = NodeServer.getRetrofitInstance().create(ApiInterface::class.java)
            val filter = HashMap<String, String>()
            filter["name"] = editTextFullname.text.toString()
            filter["email"] = editTextEmail.text.toString()
            filter["municipality"] = spinnerMunicipality.selectedItem.toString()
            filter["password"] = editTextPassword.text.toString()
            filter["device_id"] =  Build.ID
            filter["is_admin"] = false.toString()

            retrofit.registerUser(filter).enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {

                    Toast.makeText(applicationContext, response.headers().toString(), Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("Request Failure", t.message.toString())
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(applicationContext, "Password doesn't match.", Toast.LENGTH_SHORT).show()
        }
    }
}

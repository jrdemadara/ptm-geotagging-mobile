package com.jrdemadara.ptm_geotagging.features.profiles

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.admin.AdminActivity
import com.jrdemadara.ptm_geotagging.features.assistance.AssistanceActivity
import com.jrdemadara.ptm_geotagging.features.login.LoginActivity
import com.jrdemadara.ptm_geotagging.features.profiling.ProfilingActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart

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

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    logout()
                }
                R.id.aid -> {
                    val intent = Intent(applicationContext, AssistanceActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.admin -> {
                    val dialog = showAdminDialog()
                    dialog.show()
                }
            }
            false
        }

        loadData()
        floatingButtonAdd.setOnClickListener {
            val intent = Intent(applicationContext, ProfilingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadData(){
        textViewTotalProfile.text = localDatabase.getProfileCount().toString()
        textViewUploaded.text = localDatabase.getUploadedCount().toString()
        textViewNotUploaded.text = localDatabase.getNotUploadedCount().toString()
    }

    private fun logout(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
            .setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            getSharedPreferences("pref_app", MODE_PRIVATE)
                .edit()
                .putString(prefAccessToken, "")
                .apply()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        // Add "No" button and its action
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showAdminDialog(): Dialog {
        val dialog = Dialog(this@ProfilesActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_admin) // Create a layout file for the dialog

        val editTextSecret = dialog.findViewById<EditText>(R.id.editTextAdminSecret)
        val buttonAccess = dialog.findViewById<Button>(R.id.buttonAccess)

        buttonAccess.setOnClickListener {
            if (editTextSecret.text.toString() == "E=mc2"){
                dialog.dismiss()
                val intent = Intent(applicationContext, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                Toast.makeText(applicationContext, "Incorrect Passphrase.", Toast.LENGTH_SHORT).show()
            }
        }

        // Make the dialog full-screen width
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }
}
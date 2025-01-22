package com.jrdemadara.ptm_geotagging.features.profiles

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.AssistanceRequest
import com.jrdemadara.ptm_geotagging.data.ProfileResponse
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    @RequiresApi(Build.VERSION_CODES.S)
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
                R.id.aidpersonal -> {
                    realtimeValidation.launch(ScanOptions())
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

    @RequiresApi(Build.VERSION_CODES.S)
    private val realtimeValidation = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(applicationContext, "QR Scanner has been cancelled", Toast.LENGTH_SHORT)
                .show()
        } else {

            val qrcode = result.contents

            val loadingDialog = showLoadingDialog()
            if (qrcode.isNotEmpty()) {
                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)
                loadingDialog.show()

                retrofit.validateProfilePersonal(qrcode).enqueue(object : Callback<ProfileResponse> {
                    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                        loadingDialog.dismiss()

                        if (response.isSuccessful && response.body() != null) {
                            val dataResponse = response.body()

                            // Extract the profile data
                            val profile = dataResponse?.profile

                            if (profile != null) {
                                val fullName = "${profile.lastname}, ${profile.firstname} ${profile.middlename} ${profile.extension}".trim()

                                // Show dialog if ID is valid
                                if (profile.id != 0) {
                                    showAVDialog(
                                        profile.id,
                                        profile.solo,
                                        profile.family,
                                        profile.household,
                                        profile.precinct,
                                        fullName,
                                        profile.phone,
                                        profile.purok,
                                        profile.barangay,
                                        profile.status,
                                        profile.lat,
                                        profile.lon,
                                        dataResponse = dataResponse
                                    ).show()
                                }
                            }
                        } else {
                            // Handle case when response is unsuccessful or body is null
                            Log.e("API Response", "Unsuccessful response or null body")
                            alertDialog("Not Found", "Profile does not exist.")
                            vibrate()
                        }
                    }

                    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                        // Log request failure
                        Log.e("Request Failure", t.message.toString())
                        alertDialog("Request Failure", "There was an error connecting to the server.")
                        loadingDialog.dismiss()
                        vibrate()
                    }
                })
            } else {
                alertDialog("Oops!", "INVALID QR CODE")
                vibrate()
            }
        }
    }


    private fun showAVDialog(
        id : Int,
        solo: String,
        family: String,
        livelihood: String,
        precinct: String,
        name: String,
        phone: String,
        purok: String,
        barangay: String,
        status: String,
        lat: String,
        lon: String,
        dataResponse: ProfileResponse
    ): Dialog {
        val dialog = Dialog(this@ProfilesActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_verify)

        val imageViewSolo = dialog.findViewById<ImageView>(R.id.imageViewVSolo)
        val imageViewFamily = dialog.findViewById<ImageView>(R.id.imageViewVFamily)
        val imageViewLivelihood = dialog.findViewById<ImageView>(R.id.imageViewVLivelihood)

        val textViewPrecinct = dialog.findViewById<TextView>(R.id.textViewVPrecinct)
        val textViewName = dialog.findViewById<TextView>(R.id.textViewVName)
        val textViewPhone = dialog.findViewById<TextView>(R.id.textViewVPhone)
        val textViewPurok = dialog.findViewById<TextView>(R.id.textViewVPurok)
        val textViewBarangay = dialog.findViewById<TextView>(R.id.textViewVBarangay)
        val textViewMunicipality = dialog.findViewById<TextView>(R.id.textViewVMunicipality)
        val textViewStatus = dialog.findViewById<TextView>(R.id.textViewVStatus)
        val buttonDone = dialog.findViewById<Button>(R.id.buttonVDone)
        val buttonLocation = dialog.findViewById<Button>(R.id.buttonVLocation)

        val tabLayout: TabLayout = dialog.findViewById<TabLayout>(R.id.tabLayout)

        // Initialize RecyclerView
        val recyclerViewBeneficiary: RecyclerView = dialog.findViewById(R.id.rvBeneficiary)
        val recyclerViewSkill: RecyclerView = dialog.findViewById(R.id.rvSkill)
        val recyclerViewLivelihood: RecyclerView = dialog.findViewById(R.id.rvLivelihood)
        val recyclerViewAssistance: RecyclerView = dialog.findViewById(R.id.rvAssistance)

        recyclerViewBeneficiary.layoutManager = LinearLayoutManager(applicationContext)
        recyclerViewSkill.layoutManager = LinearLayoutManager(applicationContext)
        recyclerViewLivelihood.layoutManager = LinearLayoutManager(applicationContext)
        recyclerViewAssistance.layoutManager = LinearLayoutManager(applicationContext)

        // Declare the adapter as lateinit
        lateinit var adapterBeneficiary: BeneficiaryListAdapter
        lateinit var adapterSkill: SkillsListAdapter
        lateinit var adapterLivelihood: LivelihoodListAdapter
        lateinit var adapterAssistance: AssistanceListAdapter

        // Initialize the adapters
        adapterBeneficiary = BeneficiaryListAdapter(emptyList())
        adapterSkill = SkillsListAdapter(emptyList())
        adapterLivelihood = LivelihoodListAdapter(emptyList())
        adapterAssistance = AssistanceListAdapter(emptyList())

        recyclerViewBeneficiary.adapter = adapterBeneficiary
        recyclerViewSkill.adapter = adapterSkill
        recyclerViewLivelihood.adapter = adapterLivelihood
        recyclerViewAssistance.adapter = adapterAssistance

        // Hide all RecyclerViews initially
        recyclerViewBeneficiary.visibility = View.VISIBLE
        recyclerViewSkill.visibility = View.GONE
        recyclerViewLivelihood.visibility = View.GONE
        recyclerViewAssistance.visibility = View.GONE

        // Update RecyclerViews with data
        dataResponse.beneficiary?.let {
            adapterBeneficiary.updateData(it) // Populate the Beneficiary RecyclerView
        }

        dataResponse.skill?.let {
            adapterSkill.updateData(it) // Populate the Skill RecyclerView
        }

        dataResponse.livelihood?.let {
            adapterLivelihood.updateData(it) // Populate the Livelihood RecyclerView
        }

        dataResponse.assistance?.let {
            adapterAssistance.updateData(it) // Populate the Assistance RecyclerView
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> recyclerViewBeneficiary.visibility = View.VISIBLE
                    1 -> recyclerViewSkill.visibility = View.VISIBLE
                    2 -> recyclerViewLivelihood.visibility = View.VISIBLE
                    3 -> recyclerViewAssistance.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> recyclerViewBeneficiary.visibility = View.GONE
                    1 -> recyclerViewSkill.visibility = View.GONE
                    2 -> recyclerViewLivelihood.visibility = View.GONE
                    3 -> recyclerViewAssistance.visibility = View.GONE
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Optionally handle re-selection logic
            }
        })

        solo?.let {
            val cleanBase64Solo = if (solo.startsWith("data:image")) {
                solo.substringAfter(",")
            } else {
                solo
            }
            val bitmapSolo = decodeBase64ToBitmap(cleanBase64Solo)
            imageViewSolo.setImageBitmap(bitmapSolo ?: BitmapFactory.decodeResource(resources, R.drawable.empty))

        }

        family?.let {
            val cleanBase64Family = if (family.startsWith("data:image")) {
                family.substringAfter(",")
            } else {
                family
            }
            val bitmapFamily = decodeBase64ToBitmap(cleanBase64Family)
            imageViewFamily.setImageBitmap(bitmapFamily ?: BitmapFactory.decodeResource(resources, R.drawable.empty))

        }

        livelihood?.let {
            val cleanBase64Livelihood = if (livelihood.startsWith("data:image")) {
                livelihood.substringAfter(",")
            } else {
                livelihood
            }
            val bitmapLivelihood = decodeBase64ToBitmap(cleanBase64Livelihood)
            imageViewLivelihood.setImageBitmap(bitmapLivelihood ?: BitmapFactory.decodeResource(resources, R.drawable.empty))

        }

        // Set the values for text views
        textViewPrecinct.text = precinct
        textViewName.text = name
        textViewPhone.text = phone
        textViewPurok.text = purok
        textViewBarangay.text = barangay
        textViewMunicipality.text = "ISULAN"
        textViewStatus.text = when (status) {
            "verified" -> {
                textViewStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
                "VERIFIED"
            }
            "disqualified" -> {
                textViewStatus.setTextColor(Color.parseColor("#FF9412"))
                "DISQUALIFIED"
            }
            else -> {
                textViewStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
                "UNVERIFIED"
            }
        }

        buttonDone.setOnClickListener { dialog.dismiss() }

        buttonLocation.setOnClickListener {
            val label = "Your Location"
            val uri = "geo:$lat,$lon?q=$lat,$lon($label)"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun showLoadingDialog(): Dialog {
        val dialog = Dialog(this@ProfilesActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_loading) // Create a layout file for the dialog

        val imageView = dialog.findViewById<ImageView>(R.id.imageViewLoading)
        Glide.with(this@ProfilesActivity).load(R.drawable.loading).apply(
            RequestOptions.diskCacheStrategyOf(
                DiskCacheStrategy.NONE
            )
        ).into(imageView)

        return dialog
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null // Return null if the decoding fails
        }
    }

    private fun alertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
        builder.setNegativeButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun vibrate() {
        val vibratorManager =
            this.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibratorManager.defaultVibrator
        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
    }

}
package com.jrdemadara.ptm_geotagging.features.profile_details

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.Photo
import com.jrdemadara.ptm_geotagging.features.profile_details.beneficiary.ProfileBenefeciaryAdapter
import com.jrdemadara.ptm_geotagging.features.profile_details.livelihood.ProfileLivelihoodAdapter
import com.jrdemadara.ptm_geotagging.features.profile_details.skills.ProfileSkillsAdapter
import com.jrdemadara.ptm_geotagging.features.search.PowerSearchActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import com.jrdemadara.ptm_geotagging.util.capitalizeWords
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileDetailsActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    //* Search Variables
    private lateinit var sharedPreferences: SharedPreferences
    private var prefAccessToken = "pref_access_token"
    private lateinit var accessToken: String

    private lateinit var recyclerViewBeneficiaries: RecyclerView
    private var adapterBeneficiaries: ProfileBenefeciaryAdapter? = null

    private lateinit var recyclerViewSkills: RecyclerView
    private var adapterSkills: ProfileSkillsAdapter? = null

    private lateinit var recyclerViewLivelihood: RecyclerView
    private var adapterLivelihood: ProfileLivelihoodAdapter? = null

    private lateinit var buttonViewBeneficiaries: Button
    private lateinit var buttonViewSkills: Button
    private lateinit var buttonViewLivelihood: Button
    private lateinit var buttonViewImages: Button
    private lateinit var buttonSingleUpload: Button
    private lateinit var textViewPrecinct: TextView
    private lateinit var textViewFullname: TextView
    private lateinit var textViewBirthdate: TextView
    private lateinit var textViewOccupation: TextView
    private lateinit var textViewPhone: TextView
    private lateinit var textViewPTMID: TextView

    private lateinit var imageViewSolo: ImageView
    private lateinit var imageViewFamily: ImageView
    private lateinit var imageViewLivelihood: ImageView
    lateinit var id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile_details)
        val toolbar: Toolbar = findViewById(R.id.materialToolbarProfileDetails)
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)
        accessToken = sharedPreferences.getString(prefAccessToken, null).toString()
        // Enable the back arrow button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        localDatabase = LocalDatabase(this@ProfileDetailsActivity)

        buttonViewBeneficiaries = findViewById(R.id.buttonViewBeneficiaries)
        buttonViewSkills = findViewById(R.id.buttonViewSkills)
        buttonViewLivelihood = findViewById(R.id.buttonViewLivelihood)
        buttonViewImages = findViewById(R.id.buttonViewImages)
        buttonSingleUpload = findViewById(R.id.buttonSingleUpload)

        textViewPrecinct = findViewById(R.id.textViewProfilePrecinct)
        textViewFullname = findViewById(R.id.textViewProfileFullname)
        textViewBirthdate = findViewById(R.id.textViewProfileBirthdate)
        textViewOccupation = findViewById(R.id.textViewProfileOccupation)
        textViewPhone = findViewById(R.id.textViewProfilePhone)
        textViewPTMID = findViewById(R.id.textViewProfilePTMID)

        if (intent.hasExtra("id")){
            id = intent.getStringExtra("id").toString()
        }

        if (intent.hasExtra("precinct")){
            textViewPrecinct.text = intent.getStringExtra("precinct").toString().capitalizeWords()
        }

        if (intent.hasExtra("lastname")){
            textViewFullname.text =  buildString {
                append(intent.getStringExtra("lastname").toString().capitalizeWords().replaceFirstChar (Char::uppercase))
                append(", ")
                append(intent.getStringExtra("firstname").toString().capitalizeWords().replaceFirstChar (Char::uppercase))
                append(" ")
                append(intent.getStringExtra("middlename").toString().capitalizeWords().replaceFirstChar (Char::uppercase))
                append(" ")
                append(intent.getStringExtra("extension").toString().capitalizeWords().replaceFirstChar (Char::uppercase))
            }
        }

        if (intent.hasExtra("birthdate")){
            textViewBirthdate.text = intent.getStringExtra("birthdate").toString().capitalizeWords()
        }

        if (intent.hasExtra("occupation")){
            textViewOccupation.text = intent.getStringExtra("occupation").toString().capitalizeWords()
        }

        if (intent.hasExtra("phone")){
            textViewPhone.text = intent.getStringExtra("phone").toString()
        }

        if (intent.hasExtra("hasptmid")) {
            textViewPTMID.text = if (intent.getStringExtra("hasptmid")?.toIntOrNull() == 1) "YES" else "NO"
        }

        buttonViewBeneficiaries.setOnClickListener {
            val dialog = showBeneficiariesDialog()
            dialog.show()
        }

        buttonViewSkills.setOnClickListener {
            val dialog = showSkillsDialog()
            dialog.show()
        }

        buttonViewLivelihood.setOnClickListener {
            val dialog = showLivelihoodDialog()
            dialog.show()
        }
        buttonViewImages.setOnClickListener {
            val dialog = showPhotosDialog()
            dialog.show()
        }

        buttonSingleUpload.setOnClickListener {
            val dialog = showUploadDialog(id)
            dialog.show()
        }

    }

    private fun uploadProfile(barangay: String?, id: String) {
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                val loadingDialog = showLoadingDialog()
                loadingDialog.show()

                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)
                val profile = localDatabase.getSingleProfiles(id)

                profile.forEach { profileData ->
                    try {
                        val jsonData = JSONObject().apply {
                            put("precinct", profileData.precinct)
                            put("lastname", profileData.lastname)
                            put("firstname", profileData.firstname)
                            put("middlename", profileData.middlename)
                            put("extension", profileData.extension)
                            put("birthdate", profileData.birthdate)
                            put("occupation", profileData.occupation)
                            put("phone", profileData.phone)
                            put("lat", profileData.lat)
                            put("lon", profileData.lon)
                            put("qrcode", profileData.qrcode)
                            put("hasptmid", profileData.hasptmid)
                            put("barangay", barangay)
                        }

                        jsonData.put("beneficiaries", localDatabase.getBeneficiaries(profileData.id).let { beneficiaries ->
                            JSONArray().apply {
                                beneficiaries.forEach {
                                    put(JSONObject().apply {
                                        put("precinct", it.precinct)
                                        put("fullname", it.fullname)
                                        put("birthdate", it.birthdate)
                                    })
                                }
                            }
                        })

                        jsonData.put("skills", localDatabase.getSkills(profileData.id).let { skills ->
                            JSONArray().apply {
                                skills.forEach { put(it.skill) }
                            }
                        })

                        jsonData.put("livelihoods", localDatabase.getLivelihood(profileData.id).let { livelihoods ->
                            JSONArray().apply {
                                livelihoods.forEach { put(it.livelihood) }
                            }
                        })

                        jsonData.put("assistance", localDatabase.getAssistance(profileData.id).let { assistance ->
                            JSONArray().apply {
                                assistance.forEach {
                                    put(JSONObject().apply {
                                        put("assistance", it.assistance)
                                        put("amount", it.amount)
                                        put("released_at", it.releasedAt)
                                    })
                                }
                            }
                        })


                        val photos = localDatabase.getPhotos(id)
                        val personalByteArray = photos.firstOrNull()?.personal ?: byteArrayOf(0)
                        val familyByteArray = photos.firstOrNull()?.family ?: byteArrayOf(0)
                        val livelihoodByteArray = photos.firstOrNull()?.livelihood ?: byteArrayOf(0)

                        fun createPhotoPart(photo: ByteArray, name: String): MultipartBody.Part {
                            val base64String = Base64.encodeToString(photo, Base64.DEFAULT)
                            val requestBody = base64String.toRequestBody("image/jpeg".toMediaTypeOrNull())
                            return MultipartBody.Part.createFormData(name, "$name.jpg", requestBody)
                        }

                        val personalPhoto = createPhotoPart(personalByteArray, "personalPhoto")
                        val familyPhoto = createPhotoPart(familyByteArray, "familyPhoto")
                        val livelihoodPhoto = createPhotoPart(livelihoodByteArray, "livelihoodPhoto")

                        retrofit.uploadProfile(
                            jsonData.toString().toRequestBody("application/json".toMediaTypeOrNull()),
                            personalPhoto,
                            familyPhoto,
                            livelihoodPhoto
                        ).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                if (response.isSuccessful) {
                                    if (response.code() == 201) {
                                        localDatabase.markUploaded(profileData.id)
                                        Toast.makeText(applicationContext, "Successfully uploaded.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(applicationContext, "Failed to save profile.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(applicationContext, "Something went wrong.\nStatusCode: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                                loadingDialog.dismiss()
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                                loadingDialog.dismiss()
                            }
                        })
                    } catch (e: Exception) {
                        Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    }
                }
            }
        }
    }


    private fun showUploadDialog(id: String): Dialog {
        val dialog = Dialog(this@ProfileDetailsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_barangay) // Create a layout file for the dialog

        val editTextBarangay = dialog.findViewById<EditText>(R.id.editTextBarangay)
        val buttonUpload = dialog.findViewById<Button>(R.id.buttonUpload)

        buttonUpload.setOnClickListener {
            if (editTextBarangay.text.isNotEmpty()){
                uploadProfile(editTextBarangay.text.toString(), id)
            }else {
                Toast.makeText(applicationContext, "Please input barangay.", Toast.LENGTH_SHORT).show()
            }
        }

        // Make the dialog full-screen width
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun showLoadingDialog(): Dialog {
        val dialog = Dialog(this@ProfileDetailsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_upload) // Create a layout file for the dialog

        val imageView = dialog.findViewById<ImageView>(R.id.imageViewUpload)
        Glide.with(this@ProfileDetailsActivity).load(R.drawable.progress).apply(
            RequestOptions.diskCacheStrategyOf(
                DiskCacheStrategy.NONE)).into(imageView)

        // Make the dialog full-screen width
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun showBeneficiariesDialog(): Dialog {
        val dialog = Dialog(this@ProfileDetailsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_beneficiaries) // Create a layout file for the dialog

        recyclerViewBeneficiaries = dialog.findViewById(R.id.recyclerViewBeneficiaries)
        recyclerViewBeneficiaries.layoutManager = LinearLayoutManager(this@ProfileDetailsActivity)
        adapterBeneficiaries = ProfileBenefeciaryAdapter()
        recyclerViewBeneficiaries.adapter = adapterBeneficiaries

        val beneficiaries = localDatabase.getProfileBeneficiaries(id)
        adapterBeneficiaries?.addItems(beneficiaries)

        // Make the dialog full-screen width
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun showSkillsDialog(): Dialog {
        val dialog = Dialog(this@ProfileDetailsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_skills) // Create a layout file for the dialog

        recyclerViewSkills = dialog.findViewById(R.id.recyclerViewSkills)
        recyclerViewSkills.layoutManager = LinearLayoutManager(this@ProfileDetailsActivity)
        adapterSkills = ProfileSkillsAdapter()
        recyclerViewSkills.adapter = adapterSkills

        val skills = localDatabase.getProfileSkills(id)
        adapterSkills?.addItems(skills)

        // Make the dialog full-screen width
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun showLivelihoodDialog(): Dialog {
        val dialog = Dialog(this@ProfileDetailsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_livelihoods) // Create a layout file for the dialog

        recyclerViewLivelihood = dialog.findViewById(R.id.recyclerViewLivelihoods)
        recyclerViewLivelihood.layoutManager = LinearLayoutManager(this@ProfileDetailsActivity)
        adapterLivelihood = ProfileLivelihoodAdapter()
        recyclerViewLivelihood.adapter = adapterLivelihood

        val livelihood = localDatabase.getProfileLivelihood(id)
        adapterLivelihood?.addItems(livelihood)

        // Make the dialog full-screen width
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    private fun showPhotosDialog(): Dialog {
        val dialog = Dialog(this@ProfileDetailsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_photos) // Create a layout file for the dialog

        imageViewSolo = dialog.findViewById(R.id.imageViewDetailsSolo)
        imageViewFamily = dialog.findViewById(R.id.imageViewDetailsFamily)
        imageViewLivelihood = dialog.findViewById(R.id.imageViewDetailsLivelihood)



        val photos: ArrayList<Photo> = localDatabase.getPhotos(id)
        // Ensure the list is not empty and use the first entry
        if (photos.isNotEmpty()) {
            val photo = photos[0]

            // Personal photo
            photo.personal.let {
                imageViewSolo.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }

            // Family photo
            photo.family.let {
                imageViewFamily.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }

            // Livelihood photo
            photo.livelihood.let {
                imageViewLivelihood.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }
        }

        // Make the dialog full-screen width
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(applicationContext, PowerSearchActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }
}
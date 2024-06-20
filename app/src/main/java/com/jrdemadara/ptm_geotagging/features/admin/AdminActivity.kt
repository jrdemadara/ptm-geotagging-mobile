package com.jrdemadara.ptm_geotagging.features.admin

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
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.profiles.ProfilesActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefAccessToken = "pref_access_token"
    private lateinit var buttonUpload: Button
    private lateinit var buttonReupload: Button
    private lateinit var accessToken: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin)
        val toolbar = findViewById<View>(R.id.materialToolbarAdmin) as MaterialToolbar
        buttonUpload = findViewById(R.id.buttonUpload)
        buttonReupload = findViewById(R.id.buttonReupload)
        localDatabase = LocalDatabase(this@AdminActivity)
        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)
        accessToken = sharedPreferences.getString(prefAccessToken, null).toString()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        buttonUpload.setOnClickListener {
            uploadProfile()
        }
        buttonReupload.setOnClickListener {
            uploadAllProfile()
        }

    }

    private fun uploadProfile(){
        //* Check network connection
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                val loadingDialog = showLoadingDialog()
                loadingDialog.show()
                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)

                //* Get profile data
                val profiles = localDatabase.getProfiles()

                //* Iterate profile array
                if (profiles.isNotEmpty()){
                    val profileCount = profiles.size
                    var uploaded  = 0
                    profiles.forEach { profile ->
                        val profileData = JSONObject()
                        val profileIDArrayList = ArrayList<String>()
                        //* Personal Photo
                        var personalByteArray: ByteArray = byteArrayOf(0)
                        val base64StringPersonal = Base64.encodeToString(personalByteArray, Base64.DEFAULT)
                        val personalPhotoPart = base64StringPersonal.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val personalPhoto = MultipartBody.Part.createFormData("personalPhoto", "personalPhoto.jpg", personalPhotoPart)
                        //* Family Photo
                        var familyByteArray: ByteArray = byteArrayOf(0)
                        val base64StringFamily = Base64.encodeToString(familyByteArray, Base64.DEFAULT)
                        val familyPhotoPart = base64StringFamily.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val familyPhoto = MultipartBody.Part.createFormData("familyPhoto", "familyPhoto.jpg", familyPhotoPart)
                        //* Livelihood Photo
                        var livelihoodByteArray: ByteArray = byteArrayOf(0)
                        val base64StringLivelihood = Base64.encodeToString(livelihoodByteArray, Base64.DEFAULT)
                        val livelihoodPhotoPart = base64StringLivelihood.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val livelihoodPhoto = MultipartBody.Part.createFormData("livelihoodPhoto", "livelihoodPhoto.jpg", livelihoodPhotoPart)

                        profileData.put("precinct", profile.precinct)
                        profileData.put("lastname", profile.lastname)
                        profileData.put("firstname", profile.firstname)
                        profileData.put("middlename", profile.middlename)
                        profileData.put("extension", profile.extension)
                        profileData.put("birthdate", profile.birthdate)
                        profileData.put("occupation", profile.occupation)
                        profileData.put("phone", profile.phone)
                        profileData.put("lat", profile.lat)
                        profileData.put("lon", profile.lon)
                        profileData.put("qrcode", profile.qrcode)
                        profileData.put("hasptmid", profile.hasptmid)
                        profileIDArrayList.add(profile.id)
                        val beneficiariesArray = JSONArray()
                        val beneficiaries = localDatabase.getBeneficiaries(profile.id)
                        if (beneficiaries.isNotEmpty()){
                            beneficiaries.forEach {
                                val beneficiaryObject = JSONObject().apply {
                                    put("precinct", it.precinct)
                                    put("fullname", it.fullname)
                                    put("birthdate", it.birthdate)
                                }
                                beneficiariesArray.put(beneficiaryObject)
                            }
                            profileData.put("beneficiaries", beneficiariesArray)
                        }

                        val skillsArray = JSONArray()
                        val skills = localDatabase.getSkills(profile.id)
                        if (skills.isNotEmpty()){
                            skills.forEach {
                                skillsArray.put(it.skill)
                            }
                            profileData.put("skills", skillsArray)
                        }

                        val livelihoodArray = JSONArray()
                        val livelihoods = localDatabase.getLivelihood(profile.id)
                        if (livelihoods.isNotEmpty()){
                            livelihoods.forEach {
                                livelihoodArray.put(it.livelihood)
                            }
                            profileData.put("livelihoods", livelihoodArray)
                        }

                        val assistanceArray = JSONArray()
                        val assistance = localDatabase.getAssistance(profile.id)
                        if (assistance.isNotEmpty()){
                            assistance.forEach {
                                val assistanceObject = JSONObject().apply {
                                    put("assistance", it.assistance)
                                    put("amount", it.amount)
                                    put("released_at", it.releasedAt)
                                }
                                assistanceArray.put(assistanceObject)
                            }
                            profileData.put("assistance", assistanceArray)
                        }

                        val photos = localDatabase.getPhotos(profile.id)
                        if (photos.isNotEmpty()) {
                            photos.forEach {
                                personalByteArray = it.personal
                                familyByteArray = it.family
                                livelihoodByteArray = it.livelihood
                            }
                        }

                        // Execute the Retrofit request outside the loop
                        retrofit.uploadProfile(profileData.toString().toRequestBody(), personalPhoto, familyPhoto, livelihoodPhoto).enqueue(object : Callback<ResponseBody?> {
                            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                                if (response.isSuccessful) {
                                    if (response.code() == 201) {
                                        uploaded++
                                        localDatabase.markUploaded(profile.id)
                                        if (profileCount == uploaded) {
                                            Toast.makeText(applicationContext, "Successfully saved.", Toast.LENGTH_SHORT).show()
                                            loadingDialog.dismiss()
                                        }
                                    } else {
                                        // Handle unsuccessful response
                                        Toast.makeText(applicationContext, "Failed to save profile.", Toast.LENGTH_SHORT).show()

                                        loadingDialog.dismiss()
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    Toast.makeText(applicationContext, "Something went wrong.\nStatusCode: ${response.code()}", Toast.LENGTH_SHORT).show()
                                    loadingDialog.dismiss()
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                                // Handle failure
                                Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT).show()
                                loadingDialog.dismiss()
                            }
                        })
                    }

                } else {
                    Toast.makeText(applicationContext, "There is nothing to upload.", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }
            }
        }
    }
    private fun uploadAllProfile(){
        //* Check network connection
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                val loadingDialog = showLoadingDialog()
                loadingDialog.show()
                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)

                //* Get profile data
                val profiles = localDatabase.getAllProfiles()

                //* Iterate profile array
                if (profiles.isNotEmpty()){
                    val profileCount = profiles.size
                    var uploaded  = 0
                    profiles.forEach { profile ->
                        val profileData = JSONObject()
                        val profileIDArrayList = ArrayList<String>()
                        //* Personal Photo
                        var personalByteArray: ByteArray = byteArrayOf(0)
                        val base64StringPersonal = Base64.encodeToString(personalByteArray, Base64.DEFAULT)
                        val personalPhotoPart = base64StringPersonal.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val personalPhoto = MultipartBody.Part.createFormData("personalPhoto", "personalPhoto.jpg", personalPhotoPart)
                        //* Family Photo
                        var familyByteArray: ByteArray = byteArrayOf(0)
                        val base64StringFamily = Base64.encodeToString(familyByteArray, Base64.DEFAULT)
                        val familyPhotoPart = base64StringFamily.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val familyPhoto = MultipartBody.Part.createFormData("familyPhoto", "familyPhoto.jpg", familyPhotoPart)
                        //* Livelihood Photo
                        var livelihoodByteArray: ByteArray = byteArrayOf(0)
                        val base64StringLivelihood = Base64.encodeToString(livelihoodByteArray, Base64.DEFAULT)
                        val livelihoodPhotoPart = base64StringLivelihood.toRequestBody("image/jpeg".toMediaTypeOrNull())
                        val livelihoodPhoto = MultipartBody.Part.createFormData("livelihoodPhoto", "livelihoodPhoto.jpg", livelihoodPhotoPart)

                        profileData.put("precinct", profile.precinct)
                        profileData.put("lastname", profile.lastname)
                        profileData.put("firstname", profile.firstname)
                        profileData.put("middlename", profile.middlename)
                        profileData.put("extension", profile.extension)
                        profileData.put("birthdate", profile.birthdate)
                        profileData.put("occupation", profile.occupation)
                        profileData.put("phone", profile.phone)
                        profileData.put("lat", profile.lat)
                        profileData.put("lon", profile.lon)
                        profileData.put("qrcode", profile.qrcode)
                        profileData.put("hasptmid", profile.hasptmid)
                        profileIDArrayList.add(profile.id)
                        val beneficiariesArray = JSONArray()
                        val beneficiaries = localDatabase.getBeneficiaries(profile.id)
                        if (beneficiaries.isNotEmpty()){
                            beneficiaries.forEach {
                                val beneficiaryObject = JSONObject().apply {
                                    put("precinct", it.precinct)
                                    put("fullname", it.fullname)
                                    put("birthdate", it.birthdate)
                                }
                                beneficiariesArray.put(beneficiaryObject)
                            }
                            profileData.put("beneficiaries", beneficiariesArray)
                        }

                        val skillsArray = JSONArray()
                        val skills = localDatabase.getSkills(profile.id)
                        if (skills.isNotEmpty()){
                            skills.forEach {
                                skillsArray.put(it.skill)
                            }
                            profileData.put("skills", skillsArray)
                        }

                        val livelihoodArray = JSONArray()
                        val livelihoods = localDatabase.getLivelihood(profile.id)
                        if (livelihoods.isNotEmpty()){
                            livelihoods.forEach {
                                livelihoodArray.put(it.livelihood)
                            }
                            profileData.put("livelihoods", livelihoodArray)
                        }

                        val assistanceArray = JSONArray()
                        val assistance = localDatabase.getAssistance(profile.id)
                        if (assistance.isNotEmpty()){
                            assistance.forEach {
                                val assistanceObject = JSONObject().apply {
                                    put("assistance", it.assistance)
                                    put("amount", it.amount)
                                    put("released_at", it.releasedAt)
                                }
                                assistanceArray.put(assistanceObject)
                            }
                            profileData.put("assistance", assistanceArray)
                        }

                        val photos = localDatabase.getPhotos(profile.id)
                        if (photos.isNotEmpty()) {
                            photos.forEach {
                                personalByteArray = it.personal
                                familyByteArray = it.family
                                livelihoodByteArray = it.livelihood
                            }
                        }

                        // Execute the Retrofit request outside the loop
                        retrofit.uploadProfile(profileData.toString().toRequestBody(), personalPhoto, familyPhoto, livelihoodPhoto).enqueue(object :
                            Callback<ResponseBody?> {
                            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                                if (response.isSuccessful) {
                                    if (response.code() == 201) {
                                        uploaded++
                                        localDatabase.markUploaded(profile.id)
                                        if (profileCount == uploaded) {
                                            Toast.makeText(applicationContext, "Successfully saved.", Toast.LENGTH_SHORT).show()
                                            loadingDialog.dismiss()
                                        }
                                    } else {
                                        // Handle unsuccessful response
                                        Toast.makeText(applicationContext, "Failed to save profile.", Toast.LENGTH_SHORT).show()
                                        loadingDialog.dismiss()
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    Toast.makeText(applicationContext, "Something went wrong.\nStatusCode: ${response.code()}", Toast.LENGTH_SHORT).show()
                                    loadingDialog.dismiss()
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                                // Handle failure
                                Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT).show()
                                loadingDialog.dismiss()
                            }
                        })
                    }

                } else {
                    Toast.makeText(applicationContext, "There is nothing to upload.", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun showLoadingDialog(): Dialog {
        val dialog = Dialog(this@AdminActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_upload) // Create a layout file for the dialog

        val imageView = dialog.findViewById<ImageView>(R.id.imageViewUpload)
        Glide.with(this@AdminActivity).load(R.drawable.progress).apply(
            RequestOptions.diskCacheStrategyOf(
                DiskCacheStrategy.NONE)).into(imageView)

        return dialog
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(applicationContext, ProfilesActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

}
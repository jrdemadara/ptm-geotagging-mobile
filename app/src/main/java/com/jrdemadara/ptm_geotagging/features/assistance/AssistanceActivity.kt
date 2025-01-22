package com.jrdemadara.ptm_geotagging.features.assistance

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
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.AssistanceRequest
import com.jrdemadara.ptm_geotagging.features.profiles.ProfilesActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.capitalizeWords
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import kotlin.concurrent.schedule

class AssistanceActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefApp = "pref_app"
    private var prefAccessToken = "pref_access_token"
    private lateinit var accessToken: String
    private lateinit var spinnerAssistance: Spinner
    private lateinit var editTextAmount: EditText
    private lateinit var textViewLockin: TextView
    private lateinit var buttonScan: Button
    private lateinit var buttonLockin: Button
    private var isLockin: Boolean = false
    private var assistanceType: String = ""
    private var amount: String = ""

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assistance)
        val toolbar: Toolbar = findViewById(R.id.materialToolbarAssistance)
        setSupportActionBar(toolbar)

        // Enable the back arrow button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        localDatabase = LocalDatabase(this@AssistanceActivity)
        sharedPreferences = getSharedPreferences(prefApp, MODE_PRIVATE)
        accessToken = sharedPreferences.getString(prefAccessToken, null).toString()
        // Initialize Search
        spinnerAssistance = findViewById(R.id.spinnerAssistanceType)
        editTextAmount = findViewById(R.id.editTextAmount)
        textViewLockin = findViewById(R.id.textViewLockin)
        buttonScan = findViewById(R.id.buttonScan)
        buttonLockin = findViewById(R.id.buttonLockin)
        spinnerAssistanceType()
        buttonScan.setOnClickListener {
            if (isLockin) {
                realtimeValidation.launch(ScanOptions())
            } else {
                alertDialog("Oops!", "Please lock in before proceeding.")
                vibrate()
            }
        }

        buttonLockin.setOnClickListener {
            if (buttonLockin.text.equals("Lock in")) {
                //todo: lock in
                if (editTextAmount.text.toString().isNotEmpty()) {
                    textViewLockin.text = "You are good to go!"
                    textViewLockin.setTextColor(Color.parseColor("#4CAF50"))
                    isLockin = true
                    assistanceType = spinnerAssistance.selectedItem.toString()
                    amount = editTextAmount.text.toString()
                    buttonLockin.text = "Unlock"
                    editTextAmount.isEnabled = false
                    spinnerAssistance.isEnabled = false
                } else {
                    vibrate()
                    alertDialog("Oops!", "Release amount should not be empty.")
                }
            } else {
                //todo: unlock
                textViewLockin.text = "Please Lock in before proceeding."
                textViewLockin.setTextColor(Color.parseColor("#F44336"))
                isLockin = false
                assistanceType = ""
                amount = ""
                buttonLockin.text = "Lock in"
                editTextAmount.isEnabled = true
                spinnerAssistance.isEnabled = true
            }
        }

    }

    private fun spinnerAssistanceType() {
        val adapter = ArrayAdapter(
            this@AssistanceActivity,
            android.R.layout.simple_list_item_single_choice,
            localDatabase.getAssistanceType()
        )
        spinnerAssistance.adapter = adapter
        spinnerAssistance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(applicationContext, "QR Scanner has been cancelled", Toast.LENGTH_SHORT)
                .show()
        } else {

            val profileID = localDatabase.getProfileID(result.contents)
            val memberName = localDatabase.getProfileName(result.contents)
            if (profileID.isNotEmpty()) {
                val assistance = localDatabase.getAssistance(
                    profileID,
                    spinnerAssistance.selectedItem.toString()
                )
                if (assistance.isNotEmpty()) {
                    //todo: already claimed assistance
                    assistance.forEach {
                        alertDialog("Oops!", "Member ${memberName.capitalizeWords()} has already claimed ${it.assistance.replaceFirstChar( Char::uppercase )} assistance on ${it.releasedAt} with the sum of ₱${it.amount} pesos.")
                    }
                } else {
                    //todo: save assistance claim
                    localDatabase.saveAssistance(
                        profileID,
                        spinnerAssistance.selectedItem.toString(),
                        editTextAmount.text.toString(),
                    )
                    val successDialog = showSuccessDialog()
                    successDialog.show()
                    Timer().schedule(3000) {
                        successDialog.dismiss()
                    }
                }
            } else {
                alertDialog("Oops!", "INVALID QR CODE")
            }
        }
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
                retrofit.validateProfile(qrcode, spinnerAssistance.selectedItem.toString()).enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if (response.isSuccessful && response.body() != null) {
                            loadingDialog.dismiss()
                            try {
                                // Get the raw response body as a string
                                val responseString = response.body()?.string()
                                val jsonObject = responseString?.let { JSONObject(it) }

                                val id = jsonObject!!.optInt("id", 0)
                                val lastname = jsonObject.optString("lastname")
                                val firstname = jsonObject.optString("firstname")
                                val middlename = jsonObject.optString("middlename")
                                val extension = jsonObject.optString("extension")
                                val precinct = jsonObject.optString("precinct")
                                val barangay = jsonObject.optString("barangay")
                                val purok = jsonObject.optString("purok")
                                val phone = jsonObject.optString("phone")
                                val image = jsonObject.optString("image")
                                val lat = jsonObject.optString("lat")
                                val lon = jsonObject.optString("lon")
                                val assistanceExists =
                                    jsonObject.optBoolean("assistance_exists", false)
                                // alertDialog("Success", assistanceExists.toString())

                                // todo: view the data to the UI

                                if (id != 0){
                                    showAVDialog(
                                        id,
                                        image.toString(),
                                        precinct.toString(),
                                        "$lastname, $firstname $middlename $extension".trim(),
                                        phone.toString(),
                                        purok.toString(),
                                        barangay.toString(),
                                        claimStatus = assistanceExists,
                                        lat.toString(),
                                        lon.toString()
                                    ).show()
                                }


                                //todo: save the assistance claim


                            } catch (e: Exception) {
                                Log.e("JSON Parse Error", "Error parsing response", e)
                                alertDialog("Error", qrcode)
                                loadingDialog.dismiss()
                            }
                        } else {
                            // Handle case when the response is not successful or body is null
                            alertDialog("Error", "Failed to validate the profile.")
                            loadingDialog.dismiss()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        Log.e("Request Failure", t.message.toString())
                        alertDialog("Request Failure", "There was an error connecting to the server.")
                        loadingDialog.dismiss()
                    }
                })
            } else {
                alertDialog("Oops!", "INVALID QR CODE")
            }
        }
    }

    private fun showSuccessDialog(): Dialog {
        val dialog = Dialog(this@AssistanceActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_claim_success) // Create a layout file for the dialog

        val imageView = dialog.findViewById<ImageView>(R.id.imageViewClaimSuccess)
        Glide.with(this@AssistanceActivity).load(R.drawable.check).apply(
            RequestOptions.diskCacheStrategyOf(
                DiskCacheStrategy.NONE
            )
        ).into(imageView)

        return dialog
    }

    private fun showLoadingDialog(): Dialog {
        val dialog = Dialog(this@AssistanceActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_loading) // Create a layout file for the dialog

        val imageView = dialog.findViewById<ImageView>(R.id.imageViewLoading)
        Glide.with(this@AssistanceActivity).load(R.drawable.loading).apply(
            RequestOptions.diskCacheStrategyOf(
                DiskCacheStrategy.NONE
            )
        ).into(imageView)

        return dialog
    }

    private fun showAVDialog(id : Int, image: String, precinct: String, name: String, phone: String, purok: String, barangay: String, claimStatus: Boolean, lat: String, lon: String
    ): Dialog {
        val dialog = Dialog(this@AssistanceActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_assistance_verify) // Create a layout file for the dialog

        val imageView = dialog.findViewById<ImageView>(R.id.imageViewAVPhoto)
        val textViewPrecinct = dialog.findViewById<TextView>(R.id.textViewAVPrecinct)
        val textViewAVName = dialog.findViewById<TextView>(R.id.textViewAVName)
        val textViewAVPhone = dialog.findViewById<TextView>(R.id.textViewAVPhone)
        val textViewAVPurok = dialog.findViewById<TextView>(R.id.textViewAVPurok)
        val textViewAVBarangay = dialog.findViewById<TextView>(R.id.textViewAVBarangay)
        val textViewAVMunicipality = dialog.findViewById<TextView>(R.id.textViewAVMunicipality)
        val textViewAVClaimStatus = dialog.findViewById<TextView>(R.id.textViewAVClaimStatus)
        val buttonRelease = dialog.findViewById<Button>(R.id.buttonReleaseAssistance)
        val buttonLocation = dialog.findViewById<Button>(R.id.buttonLocation)

        // Set the values for text views
        textViewPrecinct.text = precinct
        textViewAVName.text = name
        textViewAVPhone.text = phone
        textViewAVPurok.text = purok
        textViewAVBarangay.text = barangay
        textViewAVMunicipality.text = "ISULAN"
        if (claimStatus){
            textViewAVClaimStatus.text = "CLAIMED"
            textViewAVClaimStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
            buttonRelease.visibility = View.GONE
        }else {
            textViewAVClaimStatus.text = "UNCLAIMED"
            textViewAVClaimStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
            buttonRelease.visibility = View.VISIBLE
        }

        buttonRelease.setOnClickListener {
            val builder = AlertDialog.Builder(this@AssistanceActivity) // Replace `YourActivity` with your activity's name
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to proceed?")
            builder.setPositiveButton("Yes") { dialogConfirm, _ ->
                // Handle the Yes button click
                dialogConfirm.dismiss() // Close the dialog
                val currentDate = Date()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val formattedDate = dateFormat.format(currentDate)  // Convert Date to String

                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)
                val loadingDialog = showLoadingDialog()
                loadingDialog.show()
                val assistanceRequest = AssistanceRequest(
                    profile_id = id,
                    assistance = assistanceType,
                    amount = amount.toDouble(),
                    released_at = formattedDate
                )

                    retrofit.releaseAssistance(assistanceRequest).enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {

                            if (response.isSuccessful && response.code() == 201) {
                                dialog.dismiss()
                                loadingDialog.dismiss()
                                // The request was successful with a 201 status code
                                val successDialog = showSuccessDialog()
                                successDialog.show()

                            } else {
                                // Handle other cases, such as status codes not being 201

                                alertDialog("Error", "Failed to create assistance. Please try again!")
                                loadingDialog.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            Log.e("Request Failure", t.message.toString())
                            alertDialog("Request Failure", "There was an error connecting to the server.")
                            loadingDialog.dismiss()
                        }
                    })
            }
            builder.setNegativeButton("No") { dialogConfirm, _ ->
                // Handle the No button click
                dialogConfirm.dismiss() // Close the dialog
            }

            val dialogConfirm = builder.create()
            dialogConfirm.show()
        }

        buttonLocation.setOnClickListener {
            val label = "Your Location"  // Optional label for the marker
            val uri = "geo:$lat,$lon?q=$lat,$lon($label)"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }

        val cleanBase64 = if (image.startsWith("data:image")) {
            image.substringAfter(",")
        } else {
            image
        }

        val bitmap = decodeBase64ToBitmap(cleanBase64)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            // Optionally set a placeholder image if decoding fails
            imageView.setImageResource(R.drawable.check)
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )



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
        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(applicationContext, ProfilesActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }
}
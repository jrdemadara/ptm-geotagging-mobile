package com.jrdemadara.ptm_geotagging.features.assistance

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.View
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.profiles.ProfilesActivity
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.util.capitalizeWords
import java.util.Timer
import kotlin.concurrent.schedule

class AssistanceActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
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

        // Initialize Search
        spinnerAssistance = findViewById(R.id.spinnerAssistanceType)
        editTextAmount = findViewById(R.id.editTextAmount)
        textViewLockin = findViewById(R.id.textViewLockin)
        buttonScan = findViewById(R.id.buttonScan)
        buttonLockin = findViewById(R.id.buttonLockin)
        spinnerAssistanceType()
        buttonScan.setOnClickListener {
            if (isLockin) {
                barcodeLauncher.launch(ScanOptions())
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


    private fun showSuccessDialog(): Dialog {
        val dialog = Dialog(this@AssistanceActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
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
            this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
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
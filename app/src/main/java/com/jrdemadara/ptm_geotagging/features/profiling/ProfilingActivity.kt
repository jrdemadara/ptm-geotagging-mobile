package com.jrdemadara.ptm_geotagging.features.profiling

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.UUID

class ProfilingActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var buttonNext: Button
    private lateinit var buttonPrevious: Button
    private lateinit var viewFlipper: ViewFlipper
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var flip: Int = 1
    private val beneficiariesList = mutableListOf<Beneficiaries>()
    private lateinit var adapter: BeneficiariesAdapter

    //* Profile Variables
    private lateinit var editTextLastname: EditText
    private lateinit var editTextFirstname: EditText
    private lateinit var editTextMiddlename: EditText
    private lateinit var editTextExtension: EditText
    private lateinit var editTextBirthdate: EditText
    private lateinit var editTextOccupation: EditText
    private lateinit var editTextPhone: EditText

    //* Beneficiary Variables
    private lateinit var recyclerViewBeneficiary: RecyclerView
    private lateinit var editTextPrecinct: EditText
    private lateinit var editTextBeneficiaryName: EditText
    private lateinit var editTextBeneficiaryBirthdate: EditText
    private lateinit var buttonBeneficiaryAdd: Button
    private lateinit var buttonBeneficiaryRemove: Button

    //* Skill Variables
    private lateinit var recyclerViewSkill: RecyclerView
    private lateinit var editTextSkill: EditText
    private lateinit var buttonSkillAdd: Button
    private lateinit var buttonSkillRemove: Button

    //* Livelihood Variables
    private lateinit var recyclerViewLivelihood: RecyclerView
    private lateinit var editTextLivelihood: EditText
    private lateinit var buttonLivelihoodAdd: Button
    private lateinit var buttonLivelihoodRemove: Button

    //* Image Variables
    private lateinit var capturedImagePersonal: ByteArray
    private lateinit var capturedImageFamily: ByteArray
    private lateinit var capturedImageLivelihood: ByteArray
    private lateinit var imageViewPersonal: ImageView
    private lateinit var imageViewFamily: ImageView
    private lateinit var imageViewLivelihood: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiling)
        localDatabase = LocalDatabase(this@ProfilingActivity)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewFlipper = findViewById(R.id.viewFlipper)
        buttonNext = findViewById(R.id.buttonNextView)
        buttonPrevious = findViewById(R.id.buttonPreviousView)
        //* Initialize Profile Variable
        editTextLastname = findViewById(R.id.editTextLastname)
        editTextFirstname = findViewById(R.id.editTextFirstname)
        editTextMiddlename = findViewById(R.id.editTextMiddlename)
        editTextExtension = findViewById(R.id.editTextExtension)
        editTextBirthdate = findViewById(R.id.editTextBirthdate)
        editTextOccupation = findViewById(R.id.editTextOccupation)
        editTextPhone = findViewById(R.id.editTextPhone)
        //* Initialize Beneficiary Variable
        recyclerViewBeneficiary = findViewById(R.id.recyclerViewBeneficiary)
        editTextPrecinct = findViewById(R.id.editTextPrecinct)
        editTextBeneficiaryName = findViewById(R.id.editTextBeneficiaryName)
        editTextBeneficiaryBirthdate = findViewById(R.id.editTextBeneficiaryBirthdate)
        buttonBeneficiaryAdd = findViewById(R.id.buttonBeneficiaryAdd)
        buttonBeneficiaryRemove = findViewById(R.id.buttonBeneficiaryRemove)
        //* Initialize Skill Variable
        recyclerViewSkill = findViewById(R.id.recyclerViewSkill)
        editTextSkill = findViewById(R.id.editTextSkill)
        buttonSkillAdd = findViewById(R.id.buttonSkillAdd)
        buttonSkillRemove = findViewById(R.id.buttonSkillRemove)
        //* Initialize Livelihood Variable
        recyclerViewLivelihood = findViewById(R.id.recyclerViewLivelihood)
        editTextLivelihood = findViewById(R.id.editTextLivelihood)
        buttonLivelihoodAdd = findViewById(R.id.buttonLivelihoodAdd)
        buttonLivelihoodRemove = findViewById(R.id.buttonLivelihoodRemove)
        //* Initialize Image Variable
        capturedImagePersonal = ByteArray(0)
        capturedImageFamily = ByteArray(0)
        capturedImageLivelihood = ByteArray(0)
        imageViewPersonal = findViewById(R.id.imageViewPersonal)
        imageViewFamily = findViewById(R.id.imageViewFamily)
        imageViewLivelihood = findViewById(R.id.imageViewLivelihood)
        // Initialize RecyclerView and adapter
        adapter = BeneficiariesAdapter(beneficiariesList)
        recyclerViewBeneficiary.layoutManager = LinearLayoutManager(this)
        recyclerViewBeneficiary.adapter = adapter
        checkPermission()
        getLastLocation()

        editTextBirthdate.setOnClickListener {
            showDatePickerDialog(editTextBirthdate)
        }

        editTextBeneficiaryBirthdate.setOnClickListener {
            showDatePickerDialog(editTextBeneficiaryBirthdate)
        }

        imageViewPersonal.setOnClickListener {
            openCamera("personal")
        }

        imageViewFamily.setOnClickListener {
            openCamera("family")
        }

        imageViewLivelihood.setOnClickListener {
            openCamera("livelihood")
        }

        buttonNext.setOnClickListener {
            if (flip < 7) {
                viewFlipper.showNext()
                flip++

                if (flip == 6) {
                    buttonNext.text = "Save"
                }
            }

            if (flip == 7) {
                val profileID: UUID = UUID.randomUUID()
                if (editTextLastname.text.isNotEmpty() &&
                    editTextFirstname.text.isNotEmpty() &&
                    editTextMiddlename.text.isNotEmpty() &&
                    editTextExtension.text.isNotEmpty() &&
                    editTextBirthdate.text.isNotEmpty() &&
                    editTextOccupation.text.isNotEmpty() &&
                    editTextPhone.text.isNotEmpty()
                    ) {
                    localDatabase.saveProfile(
                        profileID.toString(),
                        editTextLastname.text.toString().trim(),
                        editTextFirstname.text.toString().trim(),
                        editTextMiddlename.text.toString().trim(),
                        editTextExtension.text.toString().trim(),
                        editTextBirthdate.text.toString().trim(),
                        editTextOccupation.text.toString().trim(),
                        editTextPhone.text.toString().trim(),
                        latitude.toString(),
                        longitude.toString())
                } else {
                    // Show a message or handle the case where EditText fields are empty
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }

                if (editTextPrecinct.text.isNotEmpty() &&
                    editTextBeneficiaryName.text.isNotEmpty() &&
                    editTextBeneficiaryBirthdate.text.isNotEmpty()
                ) {
                    localDatabase.saveBeneficiaries(
                        profileID.toString(),
                        editTextPrecinct.text.toString().trim(),
                        editTextBeneficiaryName.text.toString().trim(),
                        editTextBeneficiaryBirthdate.text.toString().trim())
                } else {
                    // Show a message or handle the case where EditText fields are empty
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }

                if (editTextSkill.text.isNotEmpty()) {
                    localDatabase.saveSkills(
                        profileID.toString(),
                        editTextSkill.text.toString().trim())
                } else {
                    // Show a message or handle the case where EditText fields are empty
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }

                if (editTextLivelihood.text.isNotEmpty()) {
                    localDatabase.saveLivelihood(
                        profileID.toString(),
                        editTextLivelihood.text.toString().trim())
                } else {
                    // Show a message or handle the case where EditText fields are empty
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }

                if (capturedImagePersonal.decodeToString().isNotEmpty() &&
                    capturedImageFamily.decodeToString().isNotEmpty() &&
                    capturedImageLivelihood.decodeToString().isNotEmpty()
                    ) {
                    localDatabase.savePhotos(
                        profileID.toString(),
                        capturedImagePersonal.decodeToString(),
                        capturedImageFamily.decodeToString(),
                        capturedImageLivelihood.decodeToString())
                } else {
                    // Show a message or handle the case where EditText fields are empty
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this, "Profile has been saved successfully.", Toast.LENGTH_SHORT).show()
            }
        }
        buttonPrevious.setOnClickListener{
            if (flip > 1){
                viewFlipper.showPrevious()
                flip--
                buttonNext.text = "Next"
            }

        }

        buttonBeneficiaryAdd.setOnClickListener {
            val precinct = editTextPrecinct.text.toString()
            val fullname = editTextBeneficiaryName.text.toString()
            val birthdate = editTextBeneficiaryBirthdate.text.toString()

            val person = Beneficiaries(precinct, fullname, birthdate)
            beneficiariesList.add(person)
            adapter.notifyItemInserted(beneficiariesList.size - 1)
        }

        buttonBeneficiaryRemove.setOnClickListener {
            if (beneficiariesList.isNotEmpty()) {
                beneficiariesList.removeAt(beneficiariesList.size - 1)
                adapter.notifyItemRemoved(beneficiariesList.size)
            }
        }
    }

    private fun showDatePickerDialog(editTextDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _: DatePicker, selectedYear: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = "$selectedYear-${monthOfYear + 1}-$dayOfMonth"
                editTextDate.setText(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    //* Image Capture
    private fun openCamera(type: String) {
        if (type == "personal"){
            takePersonalPicture.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else if (type == "family"){
            takeFamilyPicture.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }else {
            takeLivelihoodPicture.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

    }

    private val takePersonalPicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageViewPersonal.setImageBitmap(imageBitmap)

                // Convert the Bitmap to a byte array
                capturedImagePersonal = bitmapToByteArray(imageBitmap)
                Log.e("Request Failure", capturedImagePersonal.decodeToString())
            }
        }

    private val takeFamilyPicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageViewFamily.setImageBitmap(imageBitmap)

                // Convert the Bitmap to a byte array
                capturedImageFamily = bitmapToByteArray(imageBitmap)
                Log.e("Request Failure", capturedImageFamily.decodeToString())
            }
        }

    private val takeLivelihoodPicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageViewLivelihood.setImageBitmap(imageBitmap)

                // Convert the Bitmap to a byte array
                capturedImageLivelihood = bitmapToByteArray(imageBitmap)
                Log.e("Request Failure", capturedImageLivelihood.decodeToString())
            }
        }

    // Function to convert Bitmap to byte array
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }



    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
    }

    //* Check Permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, get the location
                    getLastLocation()
                } else {
                    // Permission denied for location
                    // Handle this case or inform the user
                }
            }
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted for camera, open camera
                    //openCamera()
                } else {
                    // Permission denied for camera
                    // Handle this case or inform the user
                }
            }
            // Add more cases for other permissions if needed
        }
    }

    private fun checkPermission(){
        // Check for location permissions and request if not granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )
            return
        }

        // Check for camera permissions and request if not granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
            return
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 100
        private const val CAMERA_PERMISSION_CODE = 100
    }
}
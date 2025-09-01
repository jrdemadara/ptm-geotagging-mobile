package com.jrdemadara.ptm_geotagging.features.profiling

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.data.Barangay
import com.jrdemadara.ptm_geotagging.data.Municipality
import com.jrdemadara.ptm_geotagging.features.profiles.ProfilesActivity
import com.jrdemadara.ptm_geotagging.features.profiling.assistance.Assistance
import com.jrdemadara.ptm_geotagging.features.profiling.assistance.AssistanceAdapter
import com.jrdemadara.ptm_geotagging.features.profiling.beneficiary.Beneficiaries
import com.jrdemadara.ptm_geotagging.features.profiling.beneficiary.BeneficiariesAdapter
import com.jrdemadara.ptm_geotagging.features.profiling.livelihood.Livelihood
import com.jrdemadara.ptm_geotagging.features.profiling.livelihood.LivelihoodsAdapter
import com.jrdemadara.ptm_geotagging.features.profiling.search.SearchMemberActivity
import com.jrdemadara.ptm_geotagging.features.profiling.skill.Skills
import com.jrdemadara.ptm_geotagging.features.profiling.skill.SkillsAdapter
import com.jrdemadara.ptm_geotagging.features.profiling.tesda.Tesda
import com.jrdemadara.ptm_geotagging.features.profiling.tesda.TesdaAdapter
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import com.jrdemadara.ptm_geotagging.util.capitalizeWords
import com.khairo.escposprinter.EscPosPrinter
import com.khairo.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.UUID


class ProfilingActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefAccessToken = "pref_access_token"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var accessToken: String
    private var prefMunicipality = "pref_municipality"
    private var prefMunicipalityCode = "pref_municipality_code"
    private var prefBarangay = "pref_barangay"
    private var prefBarangayCode = "pref_barangay_code"
    private var municipality = ""
    private var municipalityCode = ""
    private var barangay = ""
    private var barangayCode = ""

    private lateinit var location: String
    private lateinit var buttonNext: Button
    private lateinit var buttonPrevious: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonSearchMember: Button
    private lateinit var textViewBarangay: TextView
    private lateinit var viewFlipper: ViewFlipper
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var flip: Int = 1
    private lateinit var uuid: UUID
    private var qrcode: String = ""
    private lateinit var qrcodeBeneficiaries: UUID

    //* Profile Variables
    private lateinit var editTextProfilePrecinct: EditText
    private lateinit var editTextLastname: EditText
    private lateinit var editTextFirstname: EditText
    private lateinit var editTextMiddlename: EditText
    private lateinit var editTextExtension: EditText
    private lateinit var editTextBirthdate: EditText
    private lateinit var editTextOccupation: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextPurok: EditText
    private lateinit var radioButtonIslam: RadioButton
    private lateinit var radioButtonNonIslam: RadioButton
    private var hasPTMID: String = "NO"
    private var group: String = "none"

    //* Beneficiary Variables
    private lateinit var recyclerViewBeneficiary: RecyclerView
    private lateinit var editTextPrecinct: EditText
    private lateinit var editTextBeneficiaryName: EditText
    private lateinit var editTextBeneficiaryBirthdate: EditText
    private lateinit var buttonBeneficiaryAdd: Button
    private lateinit var buttonBeneficiaryRemove: Button
    private lateinit var textViewBeneficiaryEmpty: TextView
    private lateinit var radioButtonIslamBeneficiary: RadioButton
    private lateinit var radioButtonNonIslamBeneficiary: RadioButton
    private val beneficiariesList = mutableListOf<Beneficiaries>()
    private lateinit var adapterBeneficiaries: BeneficiariesAdapter
    private var groupBeneficiary: String = "none"

    //* Skill Variables
    private lateinit var recyclerViewSkill: RecyclerView
    private lateinit var editTextSkill: EditText
    private lateinit var buttonSkillAdd: Button
    private lateinit var buttonSkillRemove: Button
    private lateinit var textViewSkillsEmpty: TextView
    private val skillsList = mutableListOf<Skills>()
    private lateinit var adapterSkills: SkillsAdapter

    //* Livelihood Variables
    private lateinit var recyclerViewLivelihood: RecyclerView
    private lateinit var editTextLivelihood: EditText
    private lateinit var editTextLivelihoodDetails: EditText
    private lateinit var buttonLivelihoodAdd: Button
    private lateinit var buttonLivelihoodRemove: Button
    private lateinit var textViewLivelihoodEmpty: TextView
    private val livelihoodList = mutableListOf<Livelihood>()
    private lateinit var adapterLivelihood: LivelihoodsAdapter

    //* Tesda Variables
    private lateinit var recyclerViewTesda: RecyclerView
    private lateinit var spinnerTesdaName: Spinner
    private lateinit var spinnerTesdaCourse: Spinner
    private lateinit var buttonTesdaAdd: Button
    private lateinit var buttonTesdaRemove: Button
    private lateinit var textViewTesdaEmpty: TextView
    private val tesdaList = mutableListOf<Tesda>()
    private lateinit var adapterTesda: TesdaAdapter

    //* Assistance Variables
    private lateinit var recyclerViewAssistance: RecyclerView
    private lateinit var spinnerAssistanceName: Spinner
    private lateinit var buttonAssistanceAdd: Button
    private lateinit var buttonAssistanceRemove: Button
    private lateinit var textViewAssistanceEmpty: TextView
    private val assistanceList = mutableListOf<Assistance>()
    private lateinit var adapterAssistance: AssistanceAdapter

    //* Image Variables
    private lateinit var capturedImagePersonal: ByteArray
    private lateinit var capturedImageFamily: ByteArray
    private lateinit var capturedImageLivelihood: ByteArray
    private lateinit var imageViewPersonal: ImageView
    private lateinit var imageViewFamily: ImageView
    private lateinit var imageViewLivelihood: ImageView


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiling)
        val toolbar: Toolbar = findViewById(R.id.materialToolbar3)
        setSupportActionBar(toolbar)

        // Enable the back arrow button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        localDatabase = LocalDatabase(this@ProfilingActivity)
        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)
        accessToken = sharedPreferences.getString(prefAccessToken, null).toString()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewFlipper = findViewById(R.id.viewFlipper)
        buttonSave = findViewById(R.id.buttonSave)
        buttonNext = findViewById(R.id.buttonNextView)
        buttonPrevious = findViewById(R.id.buttonPreviousView)
        buttonSearchMember = findViewById(R.id.buttonSearchMember)
        textViewBarangay = findViewById(R.id.textViewBarangay)

        uuid = UUID.randomUUID()

        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)

        municipality =
            getSharedPreferences("pref_app", MODE_PRIVATE).getString(prefMunicipality, null)
                .toString()
        barangay =
            getSharedPreferences("pref_app", MODE_PRIVATE).getString(prefBarangay, null).toString()

        municipalityCode =
            getSharedPreferences("pref_app", MODE_PRIVATE).getString(prefBarangayCode, null)
                .toString()

        barangayCode =
            getSharedPreferences("pref_app", MODE_PRIVATE).getString(prefMunicipalityCode, null)
                .toString()

        location = "${barangay.capitalizeWords()}, ${municipality.capitalizeWords()}"

        //* Initialize Profile Variable
        editTextProfilePrecinct = findViewById(R.id.editTextProfilePrecinct)
        editTextLastname = findViewById(R.id.editTextLastname)
        editTextFirstname = findViewById(R.id.editTextFirstname)
        editTextMiddlename = findViewById(R.id.editTextMiddlename)
        editTextExtension = findViewById(R.id.editTextExtension)
        editTextBirthdate = findViewById(R.id.editTextBirthdate)
        editTextOccupation = findViewById(R.id.editTextOccupation)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextPurok = findViewById(R.id.editTextPurok)
        radioButtonIslam = findViewById(R.id.radioButtonIslam)
        radioButtonNonIslam = findViewById(R.id.radioButtonNonIslam)
        //* Initialize Beneficiary Variable
        recyclerViewBeneficiary = findViewById(R.id.recyclerViewBeneficiary)
        editTextPrecinct = findViewById(R.id.editTextPrecinct)
        editTextBeneficiaryName = findViewById(R.id.editTextBeneficiaryName)
        editTextBeneficiaryBirthdate = findViewById(R.id.editTextBeneficiaryBirthdate)
        buttonBeneficiaryAdd = findViewById(R.id.buttonBeneficiaryAdd)
        buttonBeneficiaryRemove = findViewById(R.id.buttonBeneficiaryRemove)
        textViewBeneficiaryEmpty = findViewById(R.id.textViewBeneficiaryEmpty)
        radioButtonIslamBeneficiary = findViewById(R.id.radioButtonIslamBeneficiary)
        radioButtonNonIslamBeneficiary = findViewById(R.id.radioButtonNonIslamBeneficiary)
        //* Initialize Skill Variable
        recyclerViewSkill = findViewById(R.id.recyclerViewSkill)
        editTextSkill = findViewById(R.id.editTextSkill)
        buttonSkillAdd = findViewById(R.id.buttonSkillAdd)
        buttonSkillRemove = findViewById(R.id.buttonSkillRemove)
        textViewSkillsEmpty = findViewById(R.id.textViewSkillsEmpty)
        //* Initialize Livelihood Variable
        recyclerViewLivelihood = findViewById(R.id.recyclerViewLivelihood)
        editTextLivelihood = findViewById(R.id.editTextLivelihood)
        editTextLivelihoodDetails = findViewById(R.id.editTextLivelihoodDetails)
        buttonLivelihoodAdd = findViewById(R.id.buttonLivelihoodAdd)
        buttonLivelihoodRemove = findViewById(R.id.buttonLivelihoodRemove)
        textViewLivelihoodEmpty = findViewById(R.id.textViewLivelihoodEmpty)
        //* Initialize Tesda Variable
        recyclerViewTesda = findViewById(R.id.recyclerViewTesda)
        spinnerTesdaName = findViewById(R.id.spinnerTesdaName)
        spinnerTesdaCourse = findViewById(R.id.spinnerTesdaCourse)
        buttonTesdaAdd = findViewById(R.id.buttonTesdaAdd)
        buttonTesdaRemove = findViewById(R.id.buttonTesdaRemove)
        textViewTesdaEmpty = findViewById(R.id.textViewTesdaEmpty)

        //* Initialize Assistance Variable
        recyclerViewAssistance = findViewById(R.id.recyclerViewAssistance)
        spinnerAssistanceName = findViewById(R.id.spinnerAssistanceName)
        buttonAssistanceAdd = findViewById(R.id.buttonAssistanceAdd)
        buttonAssistanceRemove = findViewById(R.id.buttonAssistanceRemove)
        textViewAssistanceEmpty = findViewById(R.id.textViewAssistanceEmpty)
        //* Initialize Image Variable
        capturedImagePersonal = ByteArray(0)
        capturedImageFamily = ByteArray(0)
        capturedImageLivelihood = ByteArray(0)
        imageViewPersonal = findViewById(R.id.imageViewPersonal)
        imageViewFamily = findViewById(R.id.imageViewFamily)
        imageViewLivelihood = findViewById(R.id.imageViewLivelihood)
        // Initialize Beneficiaries RecyclerView and adapter
        adapterBeneficiaries = BeneficiariesAdapter(beneficiariesList)
        recyclerViewBeneficiary.layoutManager = LinearLayoutManager(this)
        recyclerViewBeneficiary.adapter = adapterBeneficiaries
        // Initialize Skills RecyclerView and adapter
        adapterSkills = SkillsAdapter(skillsList)
        recyclerViewSkill.layoutManager = LinearLayoutManager(this)
        recyclerViewSkill.adapter = adapterSkills
        // Initialize Livelihood RecyclerView and adapter
        adapterLivelihood = LivelihoodsAdapter(livelihoodList)
        recyclerViewLivelihood.layoutManager = LinearLayoutManager(this)
        recyclerViewLivelihood.adapter = adapterLivelihood
        // Initialize Tesda RecyclerView and adapter
        adapterTesda = TesdaAdapter(tesdaList)
        recyclerViewTesda.layoutManager = LinearLayoutManager(this)
        recyclerViewTesda.adapter = adapterTesda
        // Initialize Assistance RecyclerView and adapter
        adapterAssistance = AssistanceAdapter(assistanceList)
        recyclerViewAssistance.layoutManager = LinearLayoutManager(this)
        recyclerViewAssistance.adapter = adapterAssistance

        getIntentDataFromMemberSearch()

        checkPermission()
        getLastLocation()
        populatePersonSpinner()
        populateAssistanceSpinner()

        textViewBarangay.text = location

        radioButtonIslam.setOnClickListener {
            radioButtonNonIslam.isChecked = false
            group = "1"
        }

        radioButtonNonIslam.setOnClickListener {
            radioButtonIslam.isChecked = false
            group = "0"
        }

        textViewBarangay.setOnClickListener {
            val dialog = showLocationDialog()
            dialog.show()
        }

        radioButtonIslamBeneficiary.setOnClickListener {
            radioButtonIslamBeneficiary.isChecked = false
            groupBeneficiary = "1"
        }

        radioButtonNonIslamBeneficiary.setOnClickListener {
            radioButtonNonIslamBeneficiary.isChecked = false
            groupBeneficiary = "0"
        }

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
            if (flip < 9) {
                viewFlipper.showNext()
                flip++
                buttonPrevious.isEnabled = true
                if (flip == 8) {
                    buttonNext.isEnabled = false
                }
            }
        }
        buttonPrevious.setOnClickListener {
            if (flip > 1) {
                viewFlipper.showPrevious()
                flip--
                buttonPrevious.isEnabled = true
                buttonNext.isEnabled = true
            }
            if (flip == 1) {
                buttonPrevious.isEnabled = false
            }
        }

        buttonBeneficiaryAdd.setOnClickListener {
            qrcodeBeneficiaries = UUID.randomUUID()
            if (editTextPrecinct.text.isNotEmpty() && editTextBeneficiaryName.text.isNotEmpty() && editTextBeneficiaryBirthdate.text.isNotEmpty() && groupBeneficiary !== "none") {
                val precinct = editTextPrecinct.text.toString().trim()
                val fullname = editTextBeneficiaryName.text.toString().trim()
                val birthdate = editTextBeneficiaryBirthdate.text.toString().trim()
                val isMuslim: Int = if (groupBeneficiary == "1") 1 else 0

                val beneficiary = Beneficiaries(
                    precinct, fullname, birthdate, qrcodeBeneficiaries.toString(), isMuslim
                )
                beneficiariesList.add(beneficiary)
                adapterBeneficiaries.notifyItemInserted(beneficiariesList.size - 1)
                checkBeneficiariesList()
                editTextPrecinct.text.clear()
                editTextBeneficiaryName.text.clear()
                editTextBeneficiaryBirthdate.text.clear()
                editTextPrecinct.requestFocus()
            } else {
                Toast.makeText(
                    applicationContext, "Please fill the required field.", Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonBeneficiaryRemove.setOnClickListener {
            if (beneficiariesList.isNotEmpty()) {
                beneficiariesList.removeAt(beneficiariesList.size - 1)
                adapterBeneficiaries.notifyItemRemoved(beneficiariesList.size)
                checkBeneficiariesList()
            }
        }
        checkBeneficiariesList()

        buttonSkillAdd.setOnClickListener {
            val skill = editTextSkill.text.toString().trim()
            if (skill.isNotEmpty()) {
                val skills = Skills(skill)
                skillsList.add(skills)
                adapterSkills.notifyItemInserted(skillsList.size - 1)
                checkSkillsList()
                editTextSkill.text.clear()
            } else {
                Toast.makeText(
                    applicationContext, "Please fill the required field.", Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonSkillRemove.setOnClickListener {
            if (skillsList.isNotEmpty()) {
                skillsList.removeAt(skillsList.size - 1)
                adapterSkills.notifyItemRemoved(skillsList.size)
                checkSkillsList()
            }
        }
        checkSkillsList()

        buttonLivelihoodAdd.setOnClickListener {
            val livelihood = editTextLivelihood.text.toString().trim()
            val details = editTextLivelihoodDetails.text.toString().trim()
            if (livelihood.isNotEmpty()) {
                val livelihoods = Livelihood(livelihood, details)
                livelihoodList.add(livelihoods)
                adapterLivelihood.notifyItemInserted(livelihoodList.size - 1)
                checkLivelihoodList()
                editTextLivelihood.text.clear()
                editTextLivelihoodDetails.text.clear()
            } else {
                Toast.makeText(
                    applicationContext, "Please fill the required field.", Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonLivelihoodRemove.setOnClickListener {
            if (livelihoodList.isNotEmpty()) {
                livelihoodList.removeAt(livelihoodList.size - 1)
                adapterLivelihood.notifyItemRemoved(livelihoodList.size)
                checkLivelihoodList()
            }
        }
        checkLivelihoodList()

        buttonTesdaAdd.setOnClickListener {
            val name = spinnerTesdaName.selectedItem.toString().trim()
            val course = spinnerTesdaCourse.selectedItem.toString().trim()
            val tesda = Tesda(name, course)
            tesdaList.add(tesda)
            adapterTesda.notifyItemInserted(tesdaList.size - 1)
            checkTesdaList()
            spinnerTesdaName.setSelection(0)
            spinnerTesdaCourse.setSelection(0)
        }

        buttonTesdaRemove.setOnClickListener {
            if (tesdaList.isNotEmpty()) {
                tesdaList.removeAt(tesdaList.size - 1)
                adapterTesda.notifyItemRemoved(tesdaList.size)
                checkTesdaList()
            }
        }
        checkTesdaList()

        buttonAssistanceAdd.setOnClickListener {
            val name = spinnerAssistanceName.selectedItem.toString().trim()
            val assistance = Assistance(name)
            assistanceList.add(assistance)
            adapterAssistance.notifyItemInserted(assistanceList.size - 1)
            checkAssistanceList()
            spinnerAssistanceName.setSelection(0)
        }

        buttonAssistanceRemove.setOnClickListener {
            if (assistanceList.isNotEmpty()) {
                assistanceList.removeAt(assistanceList.size - 1)
                adapterAssistance.notifyItemRemoved(assistanceList.size)
                checkAssistanceList()
            }
        }
        checkAssistanceList()

        buttonSave.setOnClickListener {
            qrcode = qrcode.ifEmpty { UUID.randomUUID().toString() }
            buttonSave.text = "Saving..."
            buttonSave.isEnabled = false
            if (editTextProfilePrecinct.text.isNotEmpty() && editTextLastname.text.isNotEmpty() && editTextFirstname.text.isNotEmpty() && editTextMiddlename.text.isNotEmpty() && editTextBirthdate.text.isNotEmpty() && editTextOccupation.text.isNotEmpty() && editTextPhone.text.isNotEmpty() && editTextPurok.text.isNotEmpty() && barangay != "Click to add" && capturedImagePersonal.decodeToString()
                    .isNotEmpty() && group != "none"

            ) {

                //* Check network connection
                networkChecker = NetworkChecker(application)
                networkChecker.observe(this) { isConnected ->
                    if (isConnected) {
                        val loadingDialog = showLoadingDialog()
                        loadingDialog.show()
                        val retrofit = NodeServer.getRetrofitInstance(accessToken)
                            .create(ApiInterface::class.java)
                        Thread {
                            try {
                                val profileData = JSONObject()
                                profileData.put("precinct", editTextProfilePrecinct.text.toString())
                                profileData.put("lastname", editTextLastname.text.toString())
                                profileData.put("firstname", editTextFirstname.text.toString())
                                profileData.put("middlename", editTextMiddlename.text.toString())
                                profileData.put("extension", editTextExtension.text.toString())
                                profileData.put("birthdate", editTextBirthdate.text.toString())
                                profileData.put("occupation", editTextOccupation.text.toString())
                                profileData.put("phone", editTextPhone.text.toString())
                                profileData.put("lat", latitude)
                                profileData.put("lon", longitude)
                                profileData.put("qrcode", qrcode)
                                profileData.put("hasptmid", if (hasPTMID == "YES") 1 else 0)
                                profileData.put("municipality", municipalityCode)
                                profileData.put("barangay", barangayCode)
                                profileData.put("purok", editTextPurok.text.toString().trim())
                                profileData.put("ismuslim", group)

                                val beneficiariesArray = JSONArray()
                                beneficiariesList.forEach {
                                    val beneficiaryObject = JSONObject().apply {
                                        put("precinct", it.precinct)
                                        put("fullname", it.fullname)
                                        put("birthdate", it.birthdate)
                                        put("qrcode", it.qrcode)
                                        put("ismuslim", it.isIslam)
                                    }
                                    beneficiariesArray.put(beneficiaryObject)
                                }
                                profileData.put("beneficiaries", beneficiariesArray)


                                val skillsArray = JSONArray()
                                skillsList.forEach {
                                    skillsArray.put(it.skill)
                                }
                                profileData.put("skills", skillsArray)


                                val livelihoodArray = JSONArray()
                                livelihoodList.forEach {
                                    val livelihoodObject = JSONObject().apply {
                                        put("livelihood", it.livelihood)
                                        put("description", it.details)
                                    }
                                    livelihoodArray.put(livelihoodObject)
                                }
                                profileData.put("livelihoods", livelihoodArray)


                                val tesdaArray = JSONArray()
                                tesdaList.forEach {
                                    val tesdaObject = JSONObject().apply {
                                        put("name", it.name)
                                        put("course", it.course)
                                    }
                                    tesdaArray.put(tesdaObject)
                                }
                                profileData.put("tesda", tesdaArray)

//
//                                        val assistanceArray = JSONArray()
//
//                                        for (assistance in assistanceList) {
//                                            localDatabase.saveAssistance(
//                                                profileID,
//                                                assistance.assistance,
//                                            )
//                                        }
//
//
//                                        assistanceList.forEach {
//                                                val assistanceObject = JSONObject().apply {
//                                                    put("assistance", it.assistance)
//                                                    put("amount", it.assistance)
//                                                    put("released_at", it.releasedAt)
//                                                }
//                                                assistanceArray.put(assistanceObject)
//                                            }
//                                            profileData.put("assistance", assistanceArray)
//


                                fun createPhotoPart(
                                    photo: ByteArray, name: String
                                ): MultipartBody.Part {
                                    val base64String = Base64.encodeToString(photo, Base64.DEFAULT)
                                    val requestBody =
                                        base64String.toRequestBody("image/jpeg".toMediaTypeOrNull())
                                    return MultipartBody.Part.createFormData(
                                        name, "$name.jpg", requestBody
                                    )
                                }

                                val personalPhoto =
                                    createPhotoPart(capturedImagePersonal, "personalPhoto")
                                val familyPhoto =
                                    createPhotoPart(capturedImageFamily, "familyPhoto")
                                val livelihoodPhoto =
                                    createPhotoPart(capturedImageLivelihood, "livelihoodPhoto")

                                val response = retrofit.uploadProfile(
                                    profileData.toString().toRequestBody(),
                                    personalPhoto,
                                    familyPhoto,
                                    livelihoodPhoto
                                ).execute()
                                if (response.isSuccessful) {
                                    if (response.code() == 201) {
                                        Handler(Looper.getMainLooper()).post {
                                            Toast.makeText(
                                                applicationContext,
                                                "Successfully uploaded.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            loadingDialog.dismiss()
                                        }
                                    } else {
                                        // Handle unsuccessful response
                                        Handler(Looper.getMainLooper()).post {
                                            Toast.makeText(
                                                applicationContext,
                                                "Failed to save profile.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            loadingDialog.dismiss()
                                        }
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(
                                            applicationContext,
                                            "Something went wrong.\nStatusCode: ${response.message()}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        loadingDialog.dismiss()
                                    }
                                }
                            } catch (e: Exception) {
                                // Handle failure
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        applicationContext, e.message.toString(), Toast.LENGTH_SHORT
                                    ).show()
                                    loadingDialog.dismiss()
                                }
                            }
                        }.start() // Start the thread
                    }
                }

//                printReceipt(
//                    qrcode.toString(),
//                    editTextLastname.text.toString(),
//                    editTextFirstname.text.toString(),
//                    editTextMiddlename.text.toString()
//                )
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(applicationContext, ProfilesActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 5000)
            } else {
                buttonSave.text = "Proceed"
                buttonSave.isEnabled = true
                Toast.makeText(
                    applicationContext, "Please complete the required fields.", Toast.LENGTH_LONG
                ).show()
            }

        }

        buttonSearchMember.setOnClickListener {
            val intent = Intent(applicationContext, SearchMemberActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun populatePersonSpinner() {
        val options = mutableListOf<String>()
        val leader =
            "${editTextFirstname.text} ${editTextMiddlename.text} ${editTextLastname.text} ${editTextExtension.text}"
        options.add(leader)
        for (beneficiary in beneficiariesList) {
            options.add(beneficiary.fullname)
        }

        // Populate Spinner
        val adapter =
            ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTesdaName.adapter = adapter
        spinnerTesdaName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun populateAssistanceSpinner() {
        val adapter = ArrayAdapter(
            this@ProfilingActivity,
            android.R.layout.simple_list_item_single_choice,
            localDatabase.getAssistanceType()
        )
        spinnerAssistanceName.adapter = adapter
        spinnerAssistanceName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getIntentDataFromMemberSearch() {
        if (intent.hasExtra("qrcode")) {
            qrcode = intent.getStringExtra("qrcode").toString()
        }
        intent.getStringExtra("precinct")?.takeIf { it.isNotBlank() }?.let { precinct ->
            editTextProfilePrecinct.setText(precinct)
        }
        if (intent.hasExtra("lastname")) {
            editTextLastname.setText(intent.getStringExtra("lastname").toString().capitalizeWords())
        }
        if (intent.hasExtra("firstname")) {
            editTextFirstname.setText(
                intent.getStringExtra("firstname").toString().capitalizeWords()
            )
        }
        if (intent.hasExtra("middlename")) {
            editTextMiddlename.setText(
                intent.getStringExtra("middlename").toString().capitalizeWords()
            )
        }
        if (intent.hasExtra("extension")) {
            editTextExtension.setText(
                intent.getStringExtra("extension").toString().capitalizeWords()
            )
        }
        if (intent.hasExtra("birthdate")) {
            editTextBirthdate.setText(intent.getStringExtra("birthdate").toString())
        }
        if (intent.hasExtra("contact")) {
            editTextPhone.setText(intent.getStringExtra("contact").toString())
        }
        if (intent.hasExtra("occupation")) {
            editTextOccupation.setText(
                intent.getStringExtra("occupation").toString().capitalizeWords()
            )
        }

        if (intent.hasExtra("purok")) {
            editTextPurok.setText(
                intent.getStringExtra("purok").toString().capitalizeWords()
            )
        }

        if (intent.hasExtra("hasptmid")) {
            hasPTMID = intent.getStringExtra("hasptmid").toString()

        }

        if (intent.hasExtra("isMuslim")) {
            val isMuslim = intent.getBooleanExtra("isMuslim", false)
            radioButtonIslam.isChecked = isMuslim
            radioButtonNonIslam.isChecked = !isMuslim
            group = if (isMuslim) "1" else "0"
        }
    }

    private fun showLoadingDialog(): Dialog {
        val dialog = Dialog(this@ProfilingActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_upload) // Create a layout file for the dialog

        val imageView = dialog.findViewById<ImageView>(R.id.imageViewUpload)
        Glide.with(this@ProfilingActivity).load(R.drawable.progress).apply(
            RequestOptions.diskCacheStrategyOf(
                DiskCacheStrategy.NONE
            )
        ).into(imageView)

        // Make the dialog full-screen width
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }


    private fun checkBeneficiariesList() {
        if (beneficiariesList.isEmpty()) {
            recyclerViewBeneficiary.visibility = View.GONE
            textViewBeneficiaryEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewBeneficiary.visibility = View.VISIBLE
            textViewBeneficiaryEmpty.visibility = View.GONE
        }
    }

    private fun checkSkillsList() {
        if (skillsList.isEmpty()) {
            recyclerViewSkill.visibility = View.GONE
            textViewSkillsEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewSkill.visibility = View.VISIBLE
            textViewSkillsEmpty.visibility = View.GONE
        }
    }

    private fun checkLivelihoodList() {
        if (livelihoodList.isEmpty()) {
            recyclerViewLivelihood.visibility = View.GONE
            textViewLivelihoodEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewLivelihood.visibility = View.VISIBLE
            textViewLivelihoodEmpty.visibility = View.GONE
        }
    }

    private fun checkTesdaList() {
        if (tesdaList.isEmpty()) {
            recyclerViewTesda.visibility = View.GONE
            textViewTesdaEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewTesda.visibility = View.VISIBLE
            textViewTesdaEmpty.visibility = View.GONE
        }
    }

    private fun checkAssistanceList() {
        if (assistanceList.isEmpty()) {
            recyclerViewAssistance.visibility = View.GONE
            textViewAssistanceEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewAssistance.visibility = View.VISIBLE
            textViewAssistanceEmpty.visibility = View.GONE
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

    private fun showLocationDialog(): Dialog {
        networkChecker = NetworkChecker(application)
        val dialog = Dialog(this@ProfilingActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_barangay)

        val spinnerMunicipality = dialog.findViewById<Spinner>(R.id.spinnerMunicipality)
        val spinnerBarangay = dialog.findViewById<Spinner>(R.id.spinnerBarangay)
        val button = dialog.findViewById<Button>(R.id.buttonBarangay)

        var selectedMunicipalityCode: String? = null
        var selectedMunicipalityName: String? = null
        var selectedBarangayCode: String? = null
        var selectedBarangayName: String? = null

        // Fetch municipalities
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                val retrofit =
                    NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)

                retrofit.getMunicipalities().enqueue(object : Callback<List<Municipality>?> {
                    override fun onResponse(
                        call: Call<List<Municipality>?>, response: Response<List<Municipality>?>
                    ) {
                        val list: List<Municipality> = response.body() ?: emptyList()
                        val spinnerItems = list.map { SpinnerItem(it.code, it.name) }

                        Handler(Looper.getMainLooper()).post {
                            val adapter = ArrayAdapter(
                                dialog.context, android.R.layout.simple_spinner_item, spinnerItems
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerMunicipality.adapter = adapter
                        }
                    }

                    override fun onFailure(call: Call<List<Municipality>?>, t: Throwable) {
                        Log.e("Request Failure", t.message.toString())
                    }
                })
            }
        }

        // Municipality -> fetch barangays
        spinnerMunicipality.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position) as SpinnerItem
                selectedMunicipalityCode = selectedItem.code
                selectedMunicipalityName = selectedItem.name

                // API call for barangays
                val retrofit =
                    NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)
                val filter = HashMap<String, String>()
                filter["municipality"] = selectedMunicipalityCode!!  // FIXED
                retrofit.getBarangay(filter).enqueue(object : Callback<List<Barangay>?> {
                    override fun onResponse(
                        call: Call<List<Barangay>?>, response: Response<List<Barangay>?>
                    ) {
                        val list: List<Barangay> = response.body() ?: emptyList()
                        val spinnerItems = list.map { SpinnerItem(it.code, it.name) }

                        Handler(Looper.getMainLooper()).post {
                            val adapter = ArrayAdapter(
                                dialog.context, android.R.layout.simple_spinner_item, spinnerItems
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerBarangay.adapter = adapter
                        }
                    }

                    override fun onFailure(call: Call<List<Barangay>?>, t: Throwable) {
                        Log.e("Request Failure", t.message.toString())
                    }
                })

                // Track barangay selection
                spinnerBarangay.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>, view: View, position: Int, id: Long
                        ) {
                            val selectedBarangay = parent.getItemAtPosition(position) as SpinnerItem
                            selectedBarangayCode = selectedBarangay.code
                            selectedBarangayName = selectedBarangay.name
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Save prefs
        button.setOnClickListener {
            val prefs = getSharedPreferences("pref_app", MODE_PRIVATE).edit()
            prefs.putString(prefMunicipality, selectedMunicipalityName)
            prefs.putString(prefBarangay, selectedBarangayName)
            prefs.putString(prefMunicipalityCode, selectedMunicipalityCode)
            prefs.putString(prefBarangayCode, selectedBarangayCode)
            prefs.apply()

            textViewBarangay.text =
                "${selectedMunicipalityName?.capitalizeWords()}, ${selectedBarangayName?.capitalizeWords()}"
            dialog.dismiss()
        }

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
    }

    // Generic spinner item wrapper
    data class SpinnerItem(val code: String, val name: String) {
        override fun toString(): String = name
    }

    //* Image Capture
    private fun openCamera(type: String) {
        if (type == "personal") {
            takePersonalPicture.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else if (type == "family") {
            takeFamilyPicture.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
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
            }
        }

    private val takeFamilyPicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageViewFamily.setImageBitmap(imageBitmap)

                // Convert the Bitmap to a byte array
                capturedImageFamily = bitmapToByteArray(imageBitmap)
            }
        }

    private val takeLivelihoodPicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageViewLivelihood.setImageBitmap(imageBitmap)

                // Convert the Bitmap to a byte array
                capturedImageLivelihood = bitmapToByteArray(imageBitmap)
            }
        }

    // Function to convert Bitmap to byte array
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
            } else {
                // Request a fresh location
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 1000
                ).setMaxUpdates(1).build()

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            val freshLocation = result.lastLocation
                            if (freshLocation != null) {
                                latitude = freshLocation.latitude
                                longitude = freshLocation.longitude
                                fusedLocationClient.removeLocationUpdates(this)
                            }
                        }
                    },
                    Looper.getMainLooper()
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun printReceipt(
        profileCode: String, lastname: String, firstname: String, middlename: String
    ) {
        val bluetoothManager =
            applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
        if (!bluetoothManager.adapter.isEnabled) {
            Toast.makeText(
                applicationContext, "Please check your bluetooth connection.", Toast.LENGTH_LONG
            ).show()
        } else {
            checkPermission()

            val printer =
                EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
            printer.printFormattedText(
                "[C]<b>${lastname.uppercase()}, ${firstname.uppercase()} ${middlename.uppercase()}</b>\n" + "[C]<font size='normal'>QR Code Identifier</font> \n" + "[C]<qrcode size='32'>$profileCode</qrcode>\n".trimIndent()
            )

            printer.printFormattedText(
                "[C]<b>${lastname.uppercase()}, ${firstname.uppercase()} ${middlename.uppercase()}</b>\n" + "[C]<font size='normal'>QR Code Identifier</font> \n" + "[C]<qrcode size='32'>$profileCode</qrcode>\n".trimIndent()
            )
        }
    }

    //* Check Permission
    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkPermission() {
        // Check for location permissions and request if not granted
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), PERMISSIONS_REQUEST_LOCATION
            )
            return
        }

        // Check for camera permissions and request if not granted
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
            )
            return
        }

        // Check for bluetooth permissions and request if not granted
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH), BLUETOOTH_PERMISSION_CODE
            )
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                BLUETOOTH_CONNECT_PERMISSION_CODE
            )
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_SCAN), BLUETOOTH_SCAN_PERMISSION_CODE
            )
            return
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 100
        private const val CAMERA_PERMISSION_CODE = 100
        private const val BLUETOOTH_PERMISSION_CODE = 100
        private const val BLUETOOTH_CONNECT_PERMISSION_CODE = 100
        private const val BLUETOOTH_SCAN_PERMISSION_CODE = 100
    }

    override fun onSupportNavigateUp(): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
            .setMessage("Unsaved data will be lost. Are you sure you want to exit?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            val intent = Intent(applicationContext, ProfilesActivity::class.java)
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
        return true
    }
}
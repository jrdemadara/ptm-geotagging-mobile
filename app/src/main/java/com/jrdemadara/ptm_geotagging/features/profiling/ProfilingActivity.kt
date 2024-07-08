package com.jrdemadara.ptm_geotagging.features.profiling

import android.Manifest
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
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jrdemadara.ptm_geotagging.R
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
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.util.capitalizeWords
import com.khairo.escposprinter.EscPosPrinter
import com.khairo.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.UUID


class
ProfilingActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences
    private var prefBarangay= "pref_barangay"
    private lateinit var barangay: String
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
    private lateinit var qrcode: UUID

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
    private var hasPTMID: Int = 0

    //* Beneficiary Variables
    private lateinit var recyclerViewBeneficiary: RecyclerView
    private lateinit var editTextPrecinct: EditText
    private lateinit var editTextBeneficiaryName: EditText
    private lateinit var editTextBeneficiaryBirthdate: EditText
    private lateinit var buttonBeneficiaryAdd: Button
    private lateinit var buttonBeneficiaryRemove: Button
    private lateinit var textViewBeneficiaryEmpty: TextView
    private val beneficiariesList = mutableListOf<Beneficiaries>()
    private lateinit var adapterBeneficiaries: BeneficiariesAdapter

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewFlipper = findViewById(R.id.viewFlipper)
        buttonSave = findViewById(R.id.buttonSave)
        buttonNext = findViewById(R.id.buttonNextView)
        buttonPrevious = findViewById(R.id.buttonPreviousView)
        buttonSearchMember = findViewById(R.id.buttonSearchMember)
        textViewBarangay = findViewById(R.id.textViewBarangay)
        uuid = UUID.randomUUID()

        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)
        barangay = sharedPreferences.getString(prefBarangay, "Click to add").toString()

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
        //* Initialize Beneficiary Variable
        recyclerViewBeneficiary = findViewById(R.id.recyclerViewBeneficiary)
        editTextPrecinct = findViewById(R.id.editTextPrecinct)
        editTextBeneficiaryName = findViewById(R.id.editTextBeneficiaryName)
        editTextBeneficiaryBirthdate = findViewById(R.id.editTextBeneficiaryBirthdate)
        buttonBeneficiaryAdd = findViewById(R.id.buttonBeneficiaryAdd)
        buttonBeneficiaryRemove = findViewById(R.id.buttonBeneficiaryRemove)
        textViewBeneficiaryEmpty = findViewById(R.id.textViewBeneficiaryEmpty)
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

        textViewBarangay.text = barangay

        textViewBarangay.setOnClickListener {
            val dialog = showBarangayDialog()
            dialog.show()
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
        buttonPrevious.setOnClickListener{
            if (flip > 1){
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
            val precinct = editTextPrecinct.text.toString().trim()
            val fullname = editTextBeneficiaryName.text.toString().trim()
            val birthdate = editTextBeneficiaryBirthdate.text.toString().trim()

            if (editTextPrecinct.text.isNotEmpty() &&
                editTextBeneficiaryName.text.isNotEmpty() &&
                editTextBeneficiaryBirthdate.text.isNotEmpty()
            ) {
                val beneficiary = Beneficiaries(precinct, fullname, birthdate)
                beneficiariesList.add(beneficiary)
                adapterBeneficiaries.notifyItemInserted(beneficiariesList.size - 1)
                checkBeneficiariesList()
                editTextPrecinct.text.clear()
                editTextBeneficiaryName.text.clear()
                editTextBeneficiaryBirthdate.text.clear()
                editTextPrecinct.requestFocus()
            } else {
                Toast.makeText(applicationContext, "Please fill the required field.", Toast.LENGTH_SHORT).show()
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
            if (skill.isNotEmpty()
            ) {
                val skills = Skills(skill)
                skillsList.add(skills)
                adapterSkills.notifyItemInserted(skillsList.size - 1)
                checkSkillsList()
                editTextSkill.text.clear()
            } else {
                Toast.makeText(applicationContext, "Please fill the required field.", Toast.LENGTH_SHORT).show()
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
            if (livelihood.isNotEmpty()
            ) {
                val livelihoods = Livelihood(livelihood,details)
                livelihoodList.add(livelihoods)
                adapterLivelihood.notifyItemInserted(livelihoodList.size - 1)
                checkLivelihoodList()
                editTextLivelihood.text.clear()
                editTextLivelihoodDetails.text.clear()
            } else {
                Toast.makeText(applicationContext, "Please fill the required field.", Toast.LENGTH_SHORT).show()
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
            qrcode = UUID.randomUUID()
            buttonSave.text = "Saving..."
            buttonSave.isEnabled = false
            if (editTextProfilePrecinct.text.isNotEmpty() &&
                editTextLastname.text.isNotEmpty() &&
                editTextFirstname.text.isNotEmpty() &&
                editTextMiddlename.text.isNotEmpty() &&
                editTextBirthdate.text.isNotEmpty() &&
                editTextOccupation.text.isNotEmpty() &&
                editTextPhone.text.isNotEmpty() &&
                editTextPurok.text.isNotEmpty() &&
                barangay != "Click to add" &&
                capturedImagePersonal.decodeToString().isNotEmpty()
            ) {
                saveProfile(uuid.toString())
                saveBeneficiaries(uuid.toString())
                saveSkills(uuid.toString())
                saveLivelihood(uuid.toString())
                saveTesda(uuid.toString())
                saveAssistance(uuid.toString())
                savePhoto(uuid.toString())
                printReceipt(qrcode, editTextLastname.text.toString(),  editTextFirstname.text.toString(), editTextMiddlename.text.toString())
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(applicationContext, ProfilesActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 5000)
            }else{
                buttonSave.text = "Proceed"
                buttonSave.isEnabled = true
            }

        }

        buttonSearchMember.setOnClickListener {
            val intent = Intent(applicationContext, SearchMemberActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun populatePersonSpinner(){
        val options = mutableListOf<String>()
        val leader = "${editTextFirstname.text} ${editTextMiddlename.text} ${editTextLastname.text} ${editTextExtension.text}"
        options.add(leader)
        for (beneficiary in beneficiariesList) {
            options.add(beneficiary.fullname)
        }

        // Populate Spinner
        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTesdaName.adapter = adapter
        spinnerTesdaName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {}
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
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getIntentDataFromMemberSearch(){
        if (intent.hasExtra("precinct")){
            editTextProfilePrecinct.setText(intent.getStringExtra("precinct").toString().capitalizeWords())
        }
        if (intent.hasExtra("lastname")){
            editTextLastname.setText(intent.getStringExtra("lastname").toString().capitalizeWords())
        }
        if (intent.hasExtra("firstname")){
            editTextFirstname.setText(intent.getStringExtra("firstname").toString().capitalizeWords())
        }
        if (intent.hasExtra("middlename")){
            editTextMiddlename.setText(intent.getStringExtra("middlename").toString().capitalizeWords())
        }
        if (intent.hasExtra("extension")){
            editTextExtension.setText(intent.getStringExtra("extension").toString().capitalizeWords())
        }
        if (intent.hasExtra("birthdate")){
            editTextBirthdate.setText(intent.getStringExtra("birthdate").toString())
        }
        if (intent.hasExtra("contact")){
            editTextPhone.setText(intent.getStringExtra("contact").toString())
        }
        if (intent.hasExtra("occupation")){
            editTextOccupation.setText(intent.getStringExtra("occupation").toString().capitalizeWords())
        }
        if (intent.hasExtra("hasptmid")){
            hasPTMID = intent.getIntExtra("hasptmid", 0)

        }
    }

    private fun saveProfile(profileID: String){
        localDatabase.saveProfile(
            profileID,
            editTextProfilePrecinct.text.toString().trim(),
            editTextLastname.text.toString().trim(),
            editTextFirstname.text.toString().trim(),
            editTextMiddlename.text.toString().trim(),
            editTextExtension.text.toString().trim(),
            editTextBirthdate.text.toString().trim(),
            editTextOccupation.text.toString().trim(),
            editTextPhone.text.toString().trim(),
            latitude.toString(),
            longitude.toString(),
            barangay.trim(),
            editTextPurok.text.toString().trim(),
            qrcode.toString(),
            hasPTMID
        )
    }

    private fun saveBeneficiaries(profileID: String){
        for (beneficiary in beneficiariesList) {
            localDatabase.saveBeneficiaries(
                profileID,
                beneficiary.precinct,
                beneficiary.fullname,
                beneficiary.birthdate
            )
        }
    }

    private fun saveSkills(profileID: String){
        for (skill in skillsList) {
            localDatabase.saveSkills(
                profileID,
                skill.skill
            )
        }
    }

    private fun saveLivelihood(profileID: String){
        for (livelihood in livelihoodList) {
            localDatabase.saveLivelihood(
                profileID,
                livelihood.livelihood,
                livelihood.details
            )
        }
    }

    private fun saveTesda(profileID: String){
        for (tesda in tesdaList) {
            localDatabase.saveTesda(
                profileID,
                tesda.name,
                tesda.course
            )
        }
    }

    private fun saveAssistance(profileID: String){
        for (assistance in assistanceList) {
            localDatabase.saveAssistance(
                profileID,
                assistance.assistance,
            )
        }
    }

    private fun savePhoto(profileID: String){
        localDatabase.savePhotos(
            profileID,
            capturedImagePersonal,
            capturedImageFamily,
            capturedImageLivelihood)
    }

    private fun checkBeneficiariesList(){
        if (beneficiariesList.isEmpty()) {
            recyclerViewBeneficiary.visibility = View.GONE
            textViewBeneficiaryEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewBeneficiary.visibility = View.VISIBLE
            textViewBeneficiaryEmpty.visibility = View.GONE
        }
    }

    private fun checkSkillsList(){
        if (skillsList.isEmpty()) {
            recyclerViewSkill.visibility = View.GONE
            textViewSkillsEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewSkill.visibility = View.VISIBLE
            textViewSkillsEmpty.visibility = View.GONE
        }
    }

    private fun checkLivelihoodList(){
        if (livelihoodList.isEmpty()) {
            recyclerViewLivelihood.visibility = View.GONE
            textViewLivelihoodEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewLivelihood.visibility = View.VISIBLE
            textViewLivelihoodEmpty.visibility = View.GONE
        }
    }

    private fun checkTesdaList(){
        if (tesdaList.isEmpty()) {
            recyclerViewTesda.visibility = View.GONE
            textViewTesdaEmpty.visibility = View.VISIBLE
        } else {
            recyclerViewTesda.visibility = View.VISIBLE
            textViewTesdaEmpty.visibility = View.GONE
        }
    }

    private fun checkAssistanceList(){
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

    private fun showBarangayDialog(): Dialog {
        val dialog = Dialog(this@ProfilingActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_barangay) // Create a layout file for the dialog

        val spinner = dialog.findViewById<Spinner>(R.id.spinnerBarangay)
        val buttonBarangay = dialog.findViewById<Button>(R.id.buttonBarangay)

        val barangays = localDatabase.getBarangays()

        val adapter = ArrayAdapter(dialog.context, android.R.layout.simple_spinner_item, barangays)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {}
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        buttonBarangay.setOnClickListener {
                getSharedPreferences("pref_app", MODE_PRIVATE)
                    .edit()
                    .putString(prefBarangay, spinner.selectedItem.toString())
                    .apply()

            barangay = sharedPreferences.getString(prefBarangay, null).toString()
            textViewBarangay.text = barangay
            dialog.dismiss()
        }

        // Make the dialog full-screen width
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        return dialog
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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun printReceipt(profileCode: UUID, lastname: String, firstname: String, middlename: String) {
        val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
        if (!bluetoothManager.adapter.isEnabled) {
            Toast.makeText(applicationContext, "Please check your bluetooth connection.", Toast.LENGTH_LONG).show()
        } else {
            checkPermission()

            val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
            printer
                .printFormattedText(
                    "[C]<b>${lastname.uppercase()}, ${firstname.uppercase()} ${middlename.uppercase()}</b>\n" +
                            "[C]<font size='normal'>QR Code Identifier</font> \n" +
                            "[C]<qrcode size='32'>$profileCode</qrcode>\n".trimIndent()
                )

            printer
                .printFormattedText(
                    "[C]<b>${lastname.uppercase()}, ${firstname.uppercase()} ${middlename.uppercase()}</b>\n" +
                            "[C]<font size='normal'>QR Code Identifier</font> \n" +
                            "[C]<qrcode size='32'>$profileCode</qrcode>\n".trimIndent()
                )
        }
    }

    //* Check Permission
    @RequiresApi(Build.VERSION_CODES.S)
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

        // Check for bluetooth permissions and request if not granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH),
                BLUETOOTH_PERMISSION_CODE
            )
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
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
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                BLUETOOTH_SCAN_PERMISSION_CODE
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
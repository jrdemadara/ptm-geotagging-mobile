package com.jrdemadara.ptm_geotagging.features.assistance_list

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jrdemadara.ptm_geotagging.R
import com.jrdemadara.ptm_geotagging.features.profiles.ProfilesActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AssistanceListActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var sharedPreferences: SharedPreferences
    private var prefAccessToken = "pref_access_token"
    private lateinit var accessToken: String
    private lateinit var linearLayoutDate: LinearLayout
    private lateinit var linearLayoutData: LinearLayout
    private lateinit var editTextDateStart: EditText
    private lateinit var editTextDateEnd: EditText
    private lateinit var textViewTotalCount: TextView
    private lateinit var textViewTotalAmount: TextView
    private lateinit var textViewDateRange: TextView
    private lateinit var buttonLoad: Button
    private lateinit var buttonDone: Button
    private lateinit var recyclerView: RecyclerView
    private val list = mutableListOf<AssistanceList>()
    private lateinit var adapter: AssistanceListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assistance_list)
        val toolbar: Toolbar = findViewById(R.id.materialToolbarAssistanceList)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // Input format: 2025-01-02
        val outputDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())  // Output format: Jan, 2, 2025


        sharedPreferences = getSharedPreferences("pref_app", MODE_PRIVATE)
        accessToken = sharedPreferences.getString(prefAccessToken, null).toString()

        linearLayoutDate = findViewById(R.id.linearLayoutDate)
        linearLayoutData = findViewById(R.id.linearLayoutData)
        editTextDateStart = findViewById(R.id.editTextDateFrom)
        editTextDateEnd = findViewById(R.id.editTextDateTo)
        textViewDateRange = findViewById(R.id.textViewDateRange)
        textViewTotalCount = findViewById(R.id.textViewTotalCount)
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount)
        editTextDateEnd = findViewById(R.id.editTextDateTo)
        buttonLoad = findViewById(R.id.buttonLoad)
        buttonDone = findViewById(R.id.buttonALDone)
        recyclerView = findViewById(R.id.recyclerViewAssistanceSummry)

        adapter = AssistanceListAdapter(list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        linearLayoutDate.isVisible = true
        linearLayoutData.isVisible = false
        val loadingDialog = showLoadingDialog()

        editTextDateStart.setOnClickListener {
            showDatePickerDialog(editTextDateStart)
        }

        editTextDateEnd.setOnClickListener {
            showDatePickerDialog(editTextDateEnd)
        }

        buttonLoad.setOnClickListener {
            if (editTextDateStart.text.isNotEmpty() && editTextDateEnd.text.isNotEmpty()){
                networkChecker = NetworkChecker(application)
                networkChecker.observe(this) { isConnected ->
                    if (isConnected) {
                        loadingDialog.show()
                        val retrofit = NodeServer.getRetrofitInstance(accessToken).create(
                            ApiInterface::class.java)
                        val filter = HashMap<String, String>()
                        filter["start_date"] = editTextDateStart.text.toString()
                        filter["end_date"] = editTextDateEnd.text.toString()
                        retrofit.getAssistanceByDate(filter).enqueue(object :
                            Callback<List<AssistanceList>?> {
                            override fun onResponse(
                                call: Call<List<AssistanceList>?>,
                                response: Response<List<AssistanceList>?>
                            ){
                                linearLayoutData.isVisible = true
                                linearLayoutDate.isVisible = false
                                val startDate = inputDateFormat.parse(editTextDateStart.text.toString())
                                val endDate = inputDateFormat.parse(editTextDateEnd.text.toString())

                                // Format the parsed dates
                                val formattedStartDate = startDate?.let { outputDateFormat.format(it) }
                                val formattedEndDate = endDate?.let { outputDateFormat.format(it) }

                                // Set the formatted date range
                                val dateRange = "$formattedStartDate - $formattedEndDate"
                                textViewDateRange.text = dateRange

                                loadingDialog.dismiss()

                                val list: List<AssistanceList>? = response.body()
                                // Check if the list is not null and update the RecyclerView
                                if (list != null) {
                                    adapter.clear()
                                    adapter.addItems(list)
                                    adapter.notifyDataSetChanged()

                                    val totalCount = adapter.itemCount.toString()
                                    textViewTotalCount.text = totalCount

                                    val totalAmount = adapter.getTotalAmount()
                                    textViewTotalAmount.text = String.format(Locale.getDefault(), "₱%,.2f", totalAmount)
                                } else {
                                    Toast.makeText(applicationContext, "No data available.", Toast.LENGTH_SHORT).show()
                                }

                                //assert(list != null)
//                                if (list != null) {
//                                    localDatabase.truncateAssistanceType()
//                                    for (x in list) {
//                                        localDatabase.updateAssistanceType(
//                                            x.assistance,
//                                        )
//                                    }
//                                }
                            }
                            override fun onFailure(call: Call<List<AssistanceList>?>, t: Throwable) {
                                loadingDialog.dismiss()
                                Toast.makeText(applicationContext, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }else{
                Toast.makeText(applicationContext, "Please provide a date range.", Toast.LENGTH_SHORT).show()

            }


        }

        buttonDone.setOnClickListener {
            linearLayoutData.isVisible = false
            linearLayoutDate.isVisible = true
        }

    }

    private fun showDatePickerDialog(editTextDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, monthOfYear, dayOfMonthn ->
                val formattedDate = String.format(
                    Locale.US, "%04d-%02d-%02d", selectedYear, monthOfYear + 1, dayOfMonthn
                )
                editTextDate.setText(formattedDate)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun showLoadingDialog(): Dialog {
        val dialog = Dialog(this@AssistanceListActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_loading) // Create a layout file for the dialog

        val imageView = dialog.findViewById<ImageView>(R.id.imageViewLoading)
        Glide.with(this@AssistanceListActivity).load(R.drawable.loading).apply(
            RequestOptions.diskCacheStrategyOf(
                DiskCacheStrategy.NONE
            )
        ).into(imageView)

        return dialog
    }


    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(applicationContext, ProfilesActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }
}
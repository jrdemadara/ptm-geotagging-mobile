package com.jrdemadara.ptm_geotagging.features.profiling

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jrdemadara.ptm_geotagging.R
import java.io.ByteArrayOutputStream

class ProfilingActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var buttonNext: Button
    private lateinit var buttonPrevious: Button
    private lateinit var viewFlipper: ViewFlipper
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var flip: Int = 1

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewFlipper = findViewById(R.id.viewFlipper)
        buttonNext = findViewById(R.id.buttonNextView)
        buttonPrevious = findViewById(R.id.buttonPreviousView)
        //* Initialize Image Variable
        imageViewPersonal = findViewById(R.id.imageViewPersonal)
        imageViewFamily = findViewById(R.id.imageViewFamily)
        imageViewLivelihood = findViewById(R.id.imageViewLivelihood)
        checkPermission()
        getLastLocation()

        imageViewPersonal.setOnClickListener {
            openCamera("personal")
        }

        imageViewFamily.setOnClickListener {
            openCamera("family")
        }

        imageViewLivelihood.setOnClickListener {
            openCamera("livelihood")
        }

        buttonNext.setOnClickListener{
            if (flip <= 4 ){
                viewFlipper.showNext()
                flip++
            }else {
                buttonNext.text = "Save"
            }

        }
        buttonPrevious.setOnClickListener{
            if (flip >= 1){
                viewFlipper.showPrevious()
                flip--
            }

        }
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
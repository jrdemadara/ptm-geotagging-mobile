package com.jrdemadara.ptm_geotagging

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.jrdemadara.ptm_geotagging.data.Municipality
import com.jrdemadara.ptm_geotagging.features.login.LoginActivity
import com.jrdemadara.ptm_geotagging.features.profiles.ProfilesActivity
import com.jrdemadara.ptm_geotagging.features.register.RegisterActivity
import com.jrdemadara.ptm_geotagging.server.ApiInterface
import com.jrdemadara.ptm_geotagging.server.LocalDatabase
import com.jrdemadara.ptm_geotagging.server.NodeServer
import com.jrdemadara.ptm_geotagging.util.NetworkChecker
import com.khairo.escposprinter.EscPosPrinter
import com.khairo.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.khairo.escposprinter.textparser.PrinterTextParserImg
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var networkChecker: NetworkChecker
    private lateinit var localDatabase: LocalDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var prefApp = "pref_app"
    private var prefAccessToken = "pref_access_token"
    private lateinit var accessToken: String
    private lateinit var buttonGetStarted: Button

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        localDatabase = LocalDatabase(this)
        sharedPreferences = getSharedPreferences(prefApp, MODE_PRIVATE)
        buttonGetStarted = findViewById(R.id.buttonGetStarted)
        accessToken = sharedPreferences.getString(prefAccessToken, "").toString()
        updateMunicipalities()
        testPrint()
        if (accessToken.isNotEmpty()) {
            val intent = Intent(applicationContext, ProfilesActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonGetStarted.setOnClickListener{
            checkFirstStart()
        }
    }

    private fun checkFirstStart(){
            //* App is already initialized
            //* Proceed to login
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
    }

    private fun updateMunicipalities() {
        networkChecker = NetworkChecker(application)
        networkChecker.observe(this) { isConnected ->
            if (isConnected) {
                val retrofit = NodeServer.getRetrofitInstance(accessToken).create(ApiInterface::class.java)
                retrofit.getMunicipalities().enqueue(object : Callback<List<Municipality>?> {
                    override fun onResponse(
                        call: Call<List<Municipality>?>,
                        response: Response<List<Municipality>?>
                    ){
                        val list: List<Municipality>? = response.body()
                        assert(list != null)
                        if (list != null) {
                            localDatabase.truncateTables()
                            for (x in list) {
                                localDatabase.updateMunicipalities(
                                    x.name
                                )
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<Municipality>?>, t: Throwable) {
                    }
                })
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun testPrint() {
        val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
        if (!bluetoothManager.adapter.isEnabled) {
            Toast.makeText(applicationContext, "Please check your bluetooth connection.", Toast.LENGTH_LONG).show()
        } else {
            checkPermission()

            val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
            printer
                .printFormattedText(
                    "[C]I am ready to print."
                )
        }
    }

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
}
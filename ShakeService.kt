package com.example.womanssafetyapp.Service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.JobIntentService
import androidx.core.content.ContextCompat
import com.example.womanssafetyapp.Activity.MainActivity
import com.example.womanssafetyapp.Activity.SignInActivity
import com.example.womanssafetyapp.Activity.SignUpActivity
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.sqrt

class ShakeService : JobIntentService(), LocationListener {
    lateinit var tintent : Intent
    var count = 0


    companion object {
        val JOB_ID = 2
        val TAG = "TAG"

//       open fun enqueueWork(context: Context, intent: Intent) {
//        }

        @JvmStatic
        fun enqueueWork(context: Context, mIntent: Intent) {
            enqueueWork(context, ShakeService::class.java, JOB_ID, mIntent)

        }
    }

    private val permissionRequest = 101
    val jobId = 1000


    var myMsg: String = ""
    private lateinit var locationManager: LocationManager
    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2


    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f


    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101
    override fun onHandleWork(intent: Intent) {
        tintent = intent

        Log.i(TAG, "AAYA IDHAR")
//        Toast.makeText(applicationContext, "Service startinggggggggggggg", Toast.LENGTH_SHORT).show()


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //     Log.i(TAG,"sensormanager pakda")
        Objects.requireNonNull(sensorManager)!!.registerListener(
            sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        //   Log.i(TAG, "YAHAAAAA")
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH


    }
    override fun onDestroy() {

        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()

        Log.i(TAG, "stop service wala toast daala")
        //enqueueWork(this, tintent)
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onSensorChanged(event: SensorEvent) {
            Log.i(TAG, "shayad idhar atka")
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            if (acceleration > 12) {

                count++




            }
            if(count == 7){
                count = 0
                Toast.makeText(applicationContext, "Shake event detected in service", Toast.LENGTH_SHORT)
                    .show()
                Log.i(TAG, "shake detected")
                getLocation()
                Log.i(TAG, "got location")
                sendMessage()
                Log.i(TAG, "sent msgs")
//                Timer("SettingUp", false).schedule(500000) {
//                    sendMessage()
//                }
//                Timer("SettingUp", false).schedule(1000000) {
//                    sendMessage()
//                }
//                Timer("SettingUp", false).schedule(1500000) {
//                    sendMessage()
//                }
//                Timer("SettingUp", false).schedule(2000000) {
//                    sendMessage()
//                }
//                Timer("SettingUp", false).schedule(2500000) {
//                    sendMessage()
//                }
//                Timer("SettingUp", false).schedule(3500000) {
//                    sendMessage()
//                }

            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }


    @RequiresApi(Build.VERSION_CODES.M)
    public fun checkLocationPermission(){
        //checkSelfPermission(LOCATION_SERVICE)
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            Log.i(TAG, " location permission nhi diya")
        }
//        if ((ContextCompat.checkSelfPermission(MainActivity@this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
//            ActivityCompat.requestPermissions(MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
//        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // MainActivity().checkLocationPermission()
        checkLocationPermission()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

    }
    override fun onLocationChanged(location: Location) {
        //  tvGpsLocation = findViewById(R.id.text)
        // tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
        myMsg = "I might need help.\nCurrent location:\n" + "http://maps.google.com/maps?saddr=" + location.latitude + "," + location.longitude + "\nAt height: " + location.altitude
        sendMessage()
    }
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == locationPermissionCode) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_SHORT)
//                        .show()                }
//            else {
//                Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT)
//                        .show()                }
//        }
//    }

    fun sendMessage() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            myMessage()
        } else {
            Toast.makeText(this,"Gve permissions", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "SMS PERMISSION NOT GIVEN")
//            ActivityCompat.requestPermissions(MainActivity@this, arrayOf(Manifest.permission.SEND_SMS),
//                    permissionRequest)

        }
    }
    private fun myMessage() {
        //  val myNumber: String = editTextNumber.text.toString().trim()
        // val myMsg: String = editTextMessage.text.toString().trim()

        val contact1 = SignInActivity.contact1
        val contact2 = SignInActivity.contact2
        val contact3 = SignInActivity.contact3


                val smsManager: SmsManager = SmsManager.getDefault()
        if(contact1 != "")
                smsManager.sendTextMessage(contact1, null, myMsg, null, null)
        if(contact2 != "")
                smsManager.sendTextMessage(contact2, null, myMsg, null, null)
        if(contact3 != "")
                smsManager.sendTextMessage(contact3, null, myMsg, null, null)


                Toast.makeText(this, "Messages Sent to "+contact1+" "+contact2+" "+contact3+" ", Toast.LENGTH_SHORT).show()

        }
    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}


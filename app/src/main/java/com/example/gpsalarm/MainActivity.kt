package com.example.gpsalarm

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    var mLocationManager : LocationManager? = null
    var mLocationListener : LocationListener? = null

    lateinit var tv1 : TextView
    lateinit var tv2 : TextView
    lateinit var btn1 : Button
    lateinit var et1 : EditText

    var latitude : Double = 0.0
    var longitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var curAddress = "위치"

        tv1 = findViewById(R.id.tv1)
        tv2 = findViewById(R.id.tv2)
        btn1 = findViewById(R.id.btn1)
        et1 = findViewById(R.id.et1)

        LocationHelper().startListeningUserLocation(this , object : LocationHelper.MyLocationListener {
            override fun onLocationChanged(location: Location) {
                // Here you got user location :)
                Log.d("Location","" + location.latitude + "," + location.longitude)
                tv1.text = "" + location.latitude + "," + location.longitude
                latitude = location.latitude
                longitude = location.longitude
//                tv2.text = curAddress

//                thread(start = true) {
//                    var mResultList: List<Address>? = null
//                    var mGeoCoder =  Geocoder(applicationContext, Locale.KOREAN)
//
//                    try{
//                        mResultList = mGeoCoder.getFromLocation(
//                            latitude, longitude, 1
//                        )
//                    }catch(e: IOException){
//                        runOnUiThread {
//                            Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
//                        }
//
//                        e.printStackTrace()
//                    }
//                    if(mResultList != null){
//                        Log.d("CheckCurrentLocation", mResultList[0].getAddressLine(0))
//                        curAddress = mResultList[0].getAddressLine(0)
//                        runOnUiThread {
//                            tv2.text = curAddress
//                        }
//
//                    }
//                }

            }
        })

        btn1.setOnClickListener {

//            tv2.text = getLocationFromAddress(et1.text.toString())


            thread(start = true) {
                var mResultList: List<Address>? = null
                var mGeoCoder =  Geocoder(applicationContext, Locale.KOREAN)

                try{
                    mResultList = mGeoCoder.getFromLocation(
                        latitude, longitude, 1
                    )
                }catch(e: IOException){
                    runOnUiThread {
                        Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
                    }

                    e.printStackTrace()
                }
                if(mResultList != null){
                    Log.d("CheckCurrentLocation", mResultList[0].getAddressLine(0))
                    curAddress = mResultList[0].getAddressLine(0)
                    runOnUiThread {
                        tv2.text = curAddress
                    }
                }
            }
        }
    }

//    private fun getLocationFromAddress(address: String): CharSequence? {
//        var mGeoCorder = Geocoder(applicationContext, Locale.KOREAN)
//        try {
//            var mResultList: List<Address>? = mGeoCorder.getFromLocationName(address, 1)
//            latitude = mResultList!!.get(0).latitude
//            longitude = mResultList!!.get(0).longitude
//        } catch(e: IOException) {
//            e.printStackTrace()
//        }
//
//        return ("" + latitude + "," + longitude)
//    }

    class LocationHelper {

        val LOCATION_REFRESH_TIME = 3000 // 3 seconds. The Minimum Time to get location update
        val LOCATION_REFRESH_DISTANCE = 30 // 30 meters. The Minimum Distance to be changed to get location update
        val MY_PERMISSIONS_REQUEST_LOCATION = 100

        var myLocationListener: MyLocationListener? = null

        interface MyLocationListener {
            fun onLocationChanged(location: Location)
        }

        fun startListeningUserLocation(context: Context, myListener: MyLocationListener) {
            myLocationListener = myListener

            val mLocationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

            val mLocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    //your code here
                    myLocationListener!!.onLocationChanged(location) // calling listener to inform that updated location is available
                }
                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
// check for permissions
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME.toLong(),LOCATION_REFRESH_DISTANCE.toFloat(), mLocationListener)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(context as Activity,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // permission is denined by user, you can show your alert dialog here to send user to App settings to enable permission
                } else {
                    ActivityCompat.requestPermissions(context,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),MY_PERMISSIONS_REQUEST_LOCATION)
                }
            }
        }

    }

}
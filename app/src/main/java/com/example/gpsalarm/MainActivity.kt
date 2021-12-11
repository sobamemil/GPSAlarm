package com.example.gpsalarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.*
import android.media.RingtoneManager
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
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    var mLocationManager : LocationManager? = null
    var mLocationListener : LocationListener? = null
    var mLocationReceiver : BroadcastReceiverClass? = null

    lateinit var tv1 : TextView
    lateinit var tv2 : TextView
    lateinit var btn1 : Button
    lateinit var et1 : EditText
    lateinit var btn2 : Button
    lateinit var btn3 : Button

    var latitude : Double = 0.0
    var longitude : Double = 0.0

    var destination : String = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var curAddress = "위치"

        tv1 = findViewById(R.id.tv1)
        tv2 = findViewById(R.id.tv2)
        btn1 = findViewById(R.id.btn1)
        et1 = findViewById(R.id.et1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)

        // 브로드캐스트 리시버가 메시지를 받을 수 있도록 설정
        // 액션이 com.example.gpsalarm.BroadcastReceiver 브로드캐스트 메시지를 받도록 설정




        LocationHelper().startListeningUserLocation(this , object : LocationHelper.MyLocationListener {
            override fun onLocationChanged(location: Location) {
                // Here you got user location :)
                Log.d("LogTest","" + location.latitude + "," + location.longitude)
                tv1.text = "" + location.latitude + "," + location.longitude
                latitude = location.latitude
                longitude = location.longitude

                curAddress = Geocoder(applicationContext, Locale.KOREAN).getFromLocation(latitude, longitude, 1).toString()

//                Log.d("LogTest", curAddress)
//                toast("curAddress : " + curAddress)
//                if(curAddress == destination) {
//                    toast("목적지에 도착했습니다.")
//                }
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


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

//        var receiver = BroadcastReceiverClass()
//        var filter = IntentFilter("com.example.gpsalarm.BroadcastReceiverClass").apply {
//            addAction(LocationManager.KEY_PROXIMITY_ENTERING)
//        }
//        this.registerReceiver(receiver, filter)
//
//        var intent = Intent("com.example.gpsalarm.BroadcastReceiverClass")
//        var proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
//
//        val mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//        mLocationManager.addProximityAlert(37.363, 127.960, 10000f, -1, proximityIntent)
/**/

//        val br : BroadcastReceiver = BroadcastReceiver()
//        val filter1 = IntentFilter().apply{
//            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
//            addAction(LocationManager.KEY_PROXIMITY_ENTERING)
//            //addAction(Intent.ACTION_SCREEN_OFF)
//        }
//        registerReceiver(br, filter1)



        btn1.setOnClickListener {

            tv2.text = getLocationFromAddress(et1.text.toString())


//            thread(start = true) {
//                var mResultList: List<Address>? = null
//                var mGeoCoder =  Geocoder(applicationContext, Locale.KOREAN)
//
//                try{
//                    mResultList = mGeoCoder.getFromLocation(
//                        latitude, longitude, 1
//                    )
//                }catch(e: IOException){
//                    runOnUiThread {
//                        Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
//                    }
//
//                    e.printStackTrace()
//                }
//                if(mResultList != null){
//                    Log.d("CheckCurrentLocation", mResultList[0].getAddressLine(0))
//                    curAddress = mResultList[0].getAddressLine(0)
//                    runOnUiThread {
//                        tv2.text = curAddress
//                    }
//                }
//            }
        }

        btn2.setOnClickListener {
            startActivity<MapsActivity>()
        }

        btn3.setOnClickListener {

            if(tv2.text != "현재위치" || tv2.text != "") {
                destination = tv2.text.toString()

                val latlong = getLatLongFromLocation(destination)
                val latitude = (floor(latlong[0]*1000) / 1000)
                val longitude = (floor(latlong[1]*1000) / 1000)

//                val latitude = latlong[0]
//                val longitude = latlong[1]

                Log.d("LogTest", "latitude : " + latitude + ", longitude : " + longitude )


                var receiver = BroadcastReceiverClass()
                var filter = IntentFilter("com.example.gpsalarm.BroadcastReceiverClass").apply {
                    addAction(LocationManager.KEY_PROXIMITY_ENTERING)
                }
                registerReceiver(receiver, filter)

                var intent = Intent("com.example.gpsalarm.BroadcastReceiverClass")
                var proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

                val mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                mLocationManager.addProximityAlert(latitude, longitude, 1000f, -1, proximityIntent)

            }
        }
    }

    private fun getLocationFromAddress(address: String): CharSequence? {

        var mGeoCoder = Geocoder(applicationContext, Locale.KOREAN)

        var tmpAddr : String = ""

        try {
            var mResultList: List<Address>? = mGeoCoder.getFromLocationName(address, 1)
            if(mResultList != null) {
                latitude = mResultList!!.get(0).latitude
                longitude = mResultList!!.get(0).longitude
                Log.d("LogTest", mResultList[0].getAddressLine(0))
                tmpAddr = mResultList[0].getAddressLine(0)
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }



//        return ("" + latitude + "," + longitude)
        return tmpAddr
    }

    private fun getLatLongFromLocation(location: String) : Array<Double> {
        var mGeoCoder = Geocoder(applicationContext, Locale.KOREAN)
        var mResultList: List<Address>? = mGeoCoder.getFromLocationName(location, 1)

        if(mResultList != null) {
            latitude = mResultList!!.get(0).latitude
            longitude = mResultList!!.get(0).longitude
        }

        var array = arrayOf(latitude, longitude)

        return array
    }

    class LocationHelper {

        val LOCATION_REFRESH_TIME = 3000 // 3 seconds. The Minimum Time to get location update
        val LOCATION_REFRESH_DISTANCE = 1 // 1 meters. The Minimum Distance to be changed to get location update
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
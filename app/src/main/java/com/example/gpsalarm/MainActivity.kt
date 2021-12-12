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
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

object DistanceManager {

    private const val R = 6372.8 * 1000

//    /**
//     * 두 좌표의 거리를 계산한다.
//     *
//     * @param lat1 위도1
//     * @param lon1 경도1
//     * @param lat2 위도2
//     * @param lon2 경도2
//     * @return 두 좌표의 거리(m)
//     */
    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
        val c = 2 * asin(sqrt(a))
        return (R * c).toInt()
    }
}

class MainActivity : AppCompatActivity() {

    var mLocationManager : LocationManager? = null
    var mLocationListener : LocationListener? = null
    var mLocationReceiver : BroadcastReceiverClass? = null
    lateinit var lManager : LocationManager
    lateinit var pIntent : PendingIntent

    var radius : Float = 1000f
        set(value) {
            Toast.makeText(this, "반경이 ${value/1000}km로 변경되었습니다.", Toast.LENGTH_SHORT).show()
        }

    lateinit var tv2 : TextView
    lateinit var tv3 : TextView
    lateinit var btn1 : Button
    lateinit var et1 : EditText
    lateinit var btn2 : Button
    lateinit var btn3 : Button
    lateinit var btn4 : Button

//    var latitude : Double = 0.0
//    var longitude : Double = 0.0

    var destination : String = "-1"
    var desLat = 0.0
    var desLong = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var curAddress = "위치"

        tv2 = findViewById(R.id.tv2)
        btn1 = findViewById(R.id.btn1)
        et1 = findViewById(R.id.et1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        tv3 = findViewById(R.id.tv3)

        // 브로드캐스트 리시버가 메시지를 받을 수 있도록 설정
        // 액션이 com.example.gpsalarm.BroadcastReceiver 브로드캐스트 메시지를 받도록 설정
        var receiver = BroadcastReceiverClass()
        var filter = IntentFilter("com.example.gpsalarm.BroadcastReceiverClass").apply {
            addAction(LocationManager.KEY_PROXIMITY_ENTERING)
        }
        registerReceiver(receiver, filter)



        LocationHelper().startListeningUserLocation(this , object : LocationHelper.MyLocationListener {
            override fun onLocationChanged(location: Location) {
                // Here you got user location :)
//                Log.d("LogTest","" + location.latitude + "," + location.longitude)
//                tv1.text = "" + location.latitude + "," + location.longitude
                var curLatitude = location.latitude
                var curLongitude = location.longitude

                if(desLat != 0.0 && desLong != 0.0) {
                    val distance = DistanceManager.getDistance(curLatitude, curLongitude, desLat, desLong)
                    Log.d("LogTest", distance.toString())
                    if(distance < radius) {
                        tv3.text = "목적지에 주변에 도착하였습니다."
                    } else {
                        tv3.text = "목적지까지 " + distance + "m 남았습니다."
                    }

                }
//
//                curAddress = Geocoder(applicationContext, Locale.KOREAN).getFromLocation(latitude, longitude, 1).toString()
            }
        })


        btn1.setOnClickListener {
            if(et1.text.isEmpty()) {
                Toast.makeText(this, "목적지를 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val tmp = getLocationFromAddress(et1.text.toString())
                if( tmp != null) {
                    tv2.text = tmp.toString()
                } else {
                    Toast.makeText(this, "검색된 장소가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }



        }

        btn2.setOnClickListener {
            var intent = Intent(this, MapsActivity::class.java)
            startActivityForResult(intent, 100)

        }

        btn3.setOnClickListener {
            if (desLat == 0.0 && desLong == 0.0) {
                Toast.makeText(this, "목적지가 등록되지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else {
                registerDestination()
            }
        }

        btn4.setOnClickListener {
//            val uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//            val ringtone = RingtoneManager.getRingtone( this, uriRingtone)
            var ringtone = BroadcastReceiverClass.ringtone
            if(ringtone.isPlaying) {
                ringtone.stop()
                Toast.makeText(this, "알람이 꺼졌습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "알람이 울리고 있지 않습니다.", Toast.LENGTH_SHORT).show()
            }




//            var intent = Intent("com.example.gpsalarm.BroadcastReceiverClass")
//            var proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
//            val mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
//            mLocationManager.removeProximityAlert(proximityIntent)
            lManager.removeProximityAlert(pIntent)
            pIntent.cancel()

            desLong = 0.0
            desLat = 0.0
            tv2.text = "목적지를 등록해주세요"
            tv3.text = ""

            btn3.visibility = View.VISIBLE
            btn4.visibility = View.INVISIBLE


        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.setChecked(true)
        when(item.itemId) {
            R.id.item1 ->
                this.radius = 500f // 반경 500m
            R.id.item2 ->
                this.radius = 1000f // 반경 1km
            R.id.item3 ->
                this.radius = 10000f // 반경 10km
        }

        return super.onOptionsItemSelected(item)
    }

    fun registerDestination(lat: Double = 0.0, long: Double = 0.0) {
        var latitude = 0.0
        var longitude = 0.0

        if(lat == 0.0 && long == 0.0) {
            if(tv2.text != "목적지를 등록해주세요" || tv2.text != "") {
                destination = tv2.text.toString()

                val latlong = getLatLongFromLocation(destination)
                latitude = (floor(latlong[0]*1000) / 1000)
                longitude = (floor(latlong[1]*1000) / 1000)

//                val latitude = latlong[0]
//                val longitude = latlong[1]
            }
        } else {
            latitude = lat
            longitude = long
        }

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

        Log.d("LogTest", "latitude : " + latitude + ", longitude : " + longitude )
        desLat = latitude
        desLong = longitude

        var intent = Intent("com.example.gpsalarm.BroadcastReceiverClass")
        var proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        pIntent = proximityIntent

        val mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        mLocationManager.addProximityAlert(latitude, longitude, radius, -1, proximityIntent)
        lManager = mLocationManager
        Toast.makeText(this@MainActivity, "목적지 주변에 도착하면 알려드릴게요!!", Toast.LENGTH_SHORT).show()

        btn3.visibility = View.INVISIBLE
        btn4.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if( (data?.hasExtra("latitude") == true && data?.hasExtra("longitude") == true) ) {
                var lat = data?.getDoubleExtra("latitude", 0.0)
                var long = data?.getDoubleExtra("longitude", 0.0)
                if(lat != 0.0 && long != 0.0) {
                    var mGeoCoder = Geocoder(applicationContext, Locale.KOREAN)

                    var tmpAddr : String = ""

                    try {
                        var mResultList: List<Address>? = mGeoCoder.getFromLocation(lat, long, 1)
                        if(mResultList != null) {
                            tmpAddr = mResultList[0].getAddressLine(0)
                            tv2.text = tmpAddr
                            registerDestination(lat, long)
                        }
                    } catch(e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun getLocationFromAddress(address: String): CharSequence? {

        var mGeoCoder = Geocoder(applicationContext, Locale.KOREAN)

        var tmpAddr: CharSequence? = null

        var mResultList: List<Address>? = mGeoCoder.getFromLocationName(address, 1)
        if(!mResultList.isNullOrEmpty()) {
            var latitude = mResultList!!.get(0).latitude
            var longitude = mResultList!!.get(0).longitude
            Log.d("LogTest", mResultList[0].getAddressLine(0))
            tmpAddr = mResultList[0].getAddressLine(0)
        }

//        return ("" + latitude + "," + longitude)
        return tmpAddr
    }

    private fun getLatLongFromLocation(location: String) : Array<Double> {
        var mGeoCoder = Geocoder(applicationContext, Locale.KOREAN)
        var mResultList: List<Address>? = mGeoCoder.getFromLocationName(location, 1)

        var latitude = 0.0
        var longitude = 0.0

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
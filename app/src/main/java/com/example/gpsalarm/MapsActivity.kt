package com.example.gpsalarm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.gpsalarm.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mMarker : Marker
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: MyLocationCallBack

    private lateinit var btn_set : Button

    private lateinit var videoMark : GroundOverlayOptions

    var lat = 0.0
    var long = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initLocation()

        btn_set = findViewById(R.id.btn_set)

        btn_set.setOnClickListener {
            if(lat != 0.0 && long != 0.0) {
                var intent = Intent()
                intent.putExtra("latitude", lat)
                intent.putExtra("longitude", long)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this@MapsActivity, "위치가 선택되지 않았습니다!!!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true // zoom 가능하도록 설정
//        mMap.setOnMapClickListener { point ->
//            mMarker.remove()
//
//            var mOptions : MarkerOptions = MarkerOptions()
//            mOptions.title("선택한 위치")
//            mOptions.position(point)
//
//            mMarker = mMap.addMarker(mOptions)
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,17f))
//
//        }


        mMap.setOnMapLongClickListener { point ->

            mMap.clear()

            videoMark = GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.presence_video_busy)).position(point, 50f, 50f)
            mMap.addGroundOverlay(videoMark)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,17f))

            lat = point.latitude
            long = point.longitude

        }

//        val seoul = LatLng(37.715133, 126.734086)
//        mMarker = mMap.addMarker(MarkerOptions().position(seoul).title("Marker in Seoul"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15F))
    }

    private fun initLocation(){
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        locationCallback = MyLocationCallBack()

        locationRequest = LocationRequest()

        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
    }

    override fun onResume() {
        super.onResume()
        addLocationListener()
    }

    private fun addLocationListener(){
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
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)

    }

    inner class MyLocationCallBack : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation

            location?.run {
//                mMarker.remove()
                mMap.clear()

                val latLng = LatLng(latitude,longitude)
                var mOptions : MarkerOptions = MarkerOptions()
                mOptions.title("현재위치")
                mOptions.position(latLng)

                mMarker = mMap.addMarker(mOptions)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))

                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }
}
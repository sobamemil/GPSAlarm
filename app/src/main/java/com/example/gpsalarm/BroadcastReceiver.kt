package com.example.gpsalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import android.widget.Toast

class BroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent) {
        var isEntering = p1.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)
        if (isEntering == true) {
            Toast.makeText(p0, "목표 지점에 glglgl !!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(p0, "목표 지점에서 멀어지고 있습니다!!", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.gpsalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.util.Log
import android.widget.Toast

class BroadcastReceiverClass : BroadcastReceiver() {
    companion object {
        lateinit var ringtone : Ringtone
    }
    override fun onReceive(p0: Context, p1: Intent) {
        var isEntering = p1.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)
        Log.d("LogTest", "onReceive 들어옴")
        if (isEntering) {
            Toast.makeText(p0, "목표 지점 주변에 도착했습니다 !!", Toast.LENGTH_SHORT).show()
            val uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(p0, uriRingtone)
            ringtone.play()
        } else {
            Toast.makeText(p0, "목표 지점에서 멀어지고 있습니다!!", Toast.LENGTH_SHORT).show()
        }
    }
}
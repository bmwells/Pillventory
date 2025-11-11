package com.example.apptest.ui.inventory.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Alarm!! Don't Forget to Take Inventory", Toast.LENGTH_LONG).show()

        // Start playing the alarm sound
        val mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Stop the alarm sound after 20 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer.stop()
            mediaPlayer.release()
        }, 20000)
    }
}





package com.doubleclick.alarmclock

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.doubleclick.alarmclock.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var timePicker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        binding.selectTime.setOnClickListener {
            timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build()
            timePicker!!.show(supportFragmentManager, "androidknowledge")
            timePicker!!.addOnPositiveButtonClickListener {
                if (timePicker!!.hour > 12) {
                    binding.selectTime.setText(
                        String.format("%02d", timePicker!!.hour - 12) + ":" + String.format(
                            "%02d",
                            timePicker!!.minute
                        ) + "PM"
                    )
                } else {
                    binding.selectTime.setText(timePicker!!.hour.toString() + ":" + timePicker!!.minute + "AM")
                }
                calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, timePicker!!.hour)
                calendar.set(Calendar.MINUTE, timePicker!!.minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }
        binding.setAlarm.setOnClickListener {
            alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this@MainActivity, AlarmReceiver::class.java)
            pendingIntent = PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
            alarmManager!!.setInexactRepeating(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
            Toast.makeText(this@MainActivity, "Alarm Set", Toast.LENGTH_SHORT).show()
        }
        binding.cancelAlarm.setOnClickListener {
            val intent = Intent(this@MainActivity, AlarmReceiver::class.java)
            pendingIntent = PendingIntent.getBroadcast(this@MainActivity, 0, intent, 0)
            if (alarmManager == null) {
                alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            }
            alarmManager!!.cancel(pendingIntent)
            Toast.makeText(this@MainActivity, "Alarm Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "akchannel"
            val desc = "Channel for Alarm Manager"
            val imp = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("androidknowledge", name, imp)
            channel.description = desc
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}
package com.kenant42.kotlinnotificationswithworkmanagerstudy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kenant42.kotlinnotificationswithworkmanagerstudy.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonNormalNotification.setOnClickListener {
            createNotification()
        }

        binding.buttonOTWR.setOnClickListener {
            val request = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(this).enqueue(request)

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
                .observe(this){
                    val status = it.state.name
                    Log.e("STATUS OF BACKGROUND PROCESS",status)
                }

        }

        binding.buttonPWR.setOnClickListener {
            val networkModeConstraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<NotificationWorker>(5,TimeUnit.SECONDS)
                .setInitialDelay(10,TimeUnit.SECONDS)
                .setConstraints(networkModeConstraint)
                .build()

            WorkManager.getInstance(this).enqueue(request)

        }
    }

    fun createNotification() {
        var builder: NotificationCompat.Builder
        val notManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(applicationContext, MainActivity::class.java)
        val toGoIntent = PendingIntent.getActivity(
            applicationContext, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = "CHANNELID"
            val channelName = "CHANNEL NAME"
            val channelDescription = "CHANNEL DESCRIPTION"
            val channelPriority = NotificationManager.IMPORTANCE_HIGH

            var channel = notManager.getNotificationChannel(channelID)
            if (channel == null) {
                channel = NotificationChannel(channelID, channelName, channelPriority)
                channel.description = channelDescription
                notManager.createNotificationChannel(channel)
            }

            builder = NotificationCompat.Builder(applicationContext, channelID)
            builder.setContentTitle("HEADING")
                .setContentText("BODY")
                .setSmallIcon(R.drawable.airplanemode)
                .setContentIntent(toGoIntent)
                .setAutoCancel(true)

        } else {
            builder = NotificationCompat.Builder(applicationContext)
            builder.setContentTitle("HEADING")
                .setContentText("BODY")
                .setSmallIcon(R.drawable.airplanemode)
                .setContentIntent(toGoIntent)
                .setAutoCancel(true)
                .priority = Notification.PRIORITY_HIGH
        }

        notManager.notify(1, builder.build())
    }
}


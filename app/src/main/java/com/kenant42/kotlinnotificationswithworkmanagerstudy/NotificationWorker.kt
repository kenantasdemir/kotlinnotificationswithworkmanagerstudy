package com.kenant42.kotlinnotificationswithworkmanagerstudy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(appContext:Context,workerParams:WorkerParameters):Worker(appContext,workerParams) {
    override fun doWork(): Result {
        createNotification()
        return Result.success()
    }

    fun createNotification(){
        var builder :NotificationCompat.Builder
        val notManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(applicationContext,MainActivity::class.java)
        val toGoIntent = PendingIntent.getActivity(applicationContext,1,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelID = "CHANNELID"
            val channelName = "CHANNEL NAME"
            val channelDescription = "CHANNEL DESCRIPTION"
            val channelPriority = NotificationManager.IMPORTANCE_HIGH

            var channel = notManager.getNotificationChannel(channelID)
            if(channel == null){
                channel = NotificationChannel(channelID,channelName,channelPriority)
                channel.description = channelDescription
                notManager.createNotificationChannel(channel)
            }

            builder = NotificationCompat.Builder(applicationContext,channelID)
            builder.setContentTitle("HEADING")
                .setContentText("BODY")
                .setSmallIcon(R.drawable.airplanemode)
                .setContentIntent(toGoIntent)
                .setAutoCancel(true)

        }else{
            builder = NotificationCompat.Builder(applicationContext)
            builder.setContentTitle("HEADING")
                .setContentText("BODY")
                .setSmallIcon(R.drawable.airplanemode)
                .setContentIntent(toGoIntent)
                .setAutoCancel(true)
                .priority = Notification.PRIORITY_HIGH
        }

        notManager.notify(1,builder.build())
    }
}
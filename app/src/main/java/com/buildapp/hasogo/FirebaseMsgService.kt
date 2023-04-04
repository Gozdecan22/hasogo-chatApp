package com.buildapp.hasogo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.buildapp.hasogo.messages.LatestMessagesActivity
import com.buildapp.hasogo.util.FirebaseUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber


class FirebaseMsgService: FirebaseMessagingService() {
    val TAG = " FIREBASE_SERVICE"


    override fun onNewToken(token: String) {
        Timber.d(TAG, "Refreshed token: $token")
        super.onNewToken(token)
    }


        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            super.onMessageReceived(remoteMessage)

            remoteMessage.notification?.let {
                if ((it.title != null) && (it.body != null))
                    sendNotification(it.title!!, it.body!!)
            }
        }





        private fun sendNotification(messageTitle: String, messageBody: String) {
            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
            val defaultSoundUri: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, FirebaseUtils.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    FirebaseUtils.CHANNEL_ID,
                    FirebaseUtils.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }

    }






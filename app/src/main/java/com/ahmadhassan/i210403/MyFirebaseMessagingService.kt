package com.ahmadhassan.i210403
//package com.ahmadhassan.i210403
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Build
//import android.util.Log
//import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
//import androidx.core.app.NotificationCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.getSystemService
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.Response
//import org.chromium.base.Callback
//import org.json.JSONObject
//import java.io.IOException
//
//class MyFirebaseMessagingService: FirebaseMessagingService() {
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d("MyToken",token)
//        // Use this token for message passing, save it with user data
//    }
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        val intent = Intent(this, HomeActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        createNotificationChannel()
//        var builder = NotificationCompat.Builder(this, "CHANNEL_ID")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle(message.notification?.title.toString())
//            .setContentText(message.notification?.body.toString())
//            .setStyle(NotificationCompat.BigTextStyle()
//                .bigText("Much longer text that cannot fit one line..."))
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .build()
//        val notificationManager = ContextCompat.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(0, builder)
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel("CHANNEL_ID", "name", importance).apply {
//                description = "descriptionText"
//            }
//            // Register the channel with the system.
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    private fun askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    android.Manifest.permission.POST_NOTIFICATIONS
//                ) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                // by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                // "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                // If the user selects "No thanks," allow the user to continue without notifications.
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
//        fun sendPushNotification(token: String, title: String, subtitle: String, body: String, data: Map<String, String> = emptyMap()) {
//            val url = "https://fcm.googleapis.com/fcm/send"
//            val bodyJson = JSONObject()
//            bodyJson.put("to", token)
//            bodyJson.put("notification",
//                JSONObject().also {
//                    it.put("title", title)
//                    it.put("subtitle", subtitle)
//                    it.put("body", body)
//                    it.put("sound", "social_notification_sound.wav")
//                }
//            )
//            if (data.isNotEmpty()) {
//                bodyJson.put("data", JSONObject(data))
//            }
//            var key="AAAAnvM5s2Y:APA91bHCDv8LZH3KJSZ7j2OigP1mLSXFnTU-T3JEqq2PctVikyOLuOn16oQkHtLNQuha-e"
//            val request = Request.Builder()
//                .url(url)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "key=$key")
//                .post(
//                    bodyJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
//                )
//                .build()
//            val client = OkHttpClient()
//            client.newCall(request).enqueue(
//                object : Callback {
//                    override fun onResponse(call: Call, response: Response) {
//                        Log.d("TAG", "onResponse: ${response} ")
//                    }
//                    override fun onFailure(call: Call, e: IOException) {Log.d("TAG", "onFailure: ${e.message.toString()}")
//                    } } )
//        }
//
//
//
//    }
//

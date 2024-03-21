package com.ahmadhassan.i210403

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.*
import java.util.Timer
import java.util.TimerTask

class CallActivity  : AppCompatActivity() {
    private var agoraEngine: RtcEngine? = null // The RTCEngine instance
    private val appId: String = "b622bd5b7cfa4de1bcc3f41d5467c6e6" // Your App ID from Agora console
    private val token = "007eJxTYDD6/Wn+v/xVy44KvBRs1DMLWmz+a9tlxnsncyRvr56yRcxegSHJzMgoKcU0yTw5LdEkJdUwKTnZOM3EMMXUxMw82SzV7Oav36kNgYwMGxRZWBgZIBDE52DwTc0ryS/yTWVgAACsjiPO"
    private var channelName: String = "MentorMe" // The name of the channel to join
    private var uid: Int = 0 // UID of the remote user
    private var isJoined = true // Status of the video call
    private var isBroadcaster = true // Local user role
    private val PERMISSION_ID =12
    private val requestedPermission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)

    private fun checkPermissions() : Boolean{
        return !((ContextCompat.checkSelfPermission(this,requestedPermission[0]
        )) != PackageManager.PERMISSION_GRANTED  || (ContextCompat.checkSelfPermission(this,requestedPermission[1]
        )) != PackageManager.PERMISSION_GRANTED)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_activty)
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, requestedPermission, PERMISSION_ID);
        }

        val callerName = findViewById<TextView>(R.id.callerTextView)
        callerName.text = MentorChatInstance.getInstance().name

        setupAgoraEngine()
        if(isJoined){
            joinChannel()
        }

        //back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            leaveChannel()
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun setupAgoraEngine(): Boolean {
        try {
            // Set the engine configuration
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
        } catch (e: Exception) {
            Log.e("Agora", Log.getStackTraceString(e))
            return false
        }
        return true
    }

    private fun joinChannel() {
        if(checkPermissions()) {
            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            agoraEngine?.startPreview()
            Log.d("VoiceCallActivity", "Preview started") // Add logging statement

            agoraEngine?.joinChannel(token, channelName, uid, option)
        }
        else{
            requestPermissions(requestedPermission, PERMISSION_ID)
        }
    }

    private fun leaveChannel() {
        agoraEngine!!.leaveChannel()
        isJoined = false
    }

    private val mRtcEventHandler : IRtcEngineEventHandler = object : IRtcEngineEventHandler(){
        override fun onUserJoined(uid: Int, elapsed: Int) {
//            showMessage("User Joined")

        }
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            isJoined = true
//            showMessage("Join Channel Success")
        }
        override fun onUserOffline(uid: Int, reason: Int) {
//            showMessage("User Offline")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread{
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
        Log.d("VoiceCallActivity", "Engine destroyed") // Add logging statement

    }
}
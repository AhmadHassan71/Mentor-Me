package com.ahmadhassan.i210403

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class VideoCallActivity  : AppCompatActivity() {
    private val PERMISSION_ID =12
    private val requestedPermission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)

    private val appId ="b622bd5b7cfa4de1bcc3f41d5467c6e6"
    private val channelName = "MentorMe"
    private val token = "007eJxTYDDotXV4nbU0lekHb911z6nrFi2aanzue+RH2ZRG/oWfa1crMCSZGRklpZgmmSenJZqkpBomJScbp5kYppiamJknm6Wa7fr4L7UhkJFBPEiHiZEBAkF8Dgbf1LyS/CLfVAYGAD4TIqI="
    private val uid = 0
    private var isJoined = true // Status of the video call
    private var agoraEngine: RtcEngine? = null // Agora engine
    private var localSurfaceView: SurfaceView? = null // Local video view container
    private var remoteSurfaceView: SurfaceView? = null  // Remote video view container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.videocall_activty)
//        localSurfaceView = findViewById(R.id.localView)
//        remoteSurfaceView = findViewById(R.id.remoteView)
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, requestedPermission, PERMISSION_ID);
        }

        setUpVideoSdkEngine()
        setupLocalVideo()
        if(isJoined){
            joinChannel()
        }

        //back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            leaveCall()
            onBackPressedDispatcher.onBackPressed()
        }

        val camButton = findViewById<ImageView>(R.id.switchCamera)
        camButton.setOnClickListener{
//            val intent = Intent(this, VideoActivity::class.java)
//            startActivity(intent)
            agoraEngine!!.switchCamera()
        }

    }

    private fun setUpVideoSdkEngine(){
        try{
        val config = RtcEngineConfig()
        config.mContext = baseContext
        config.mAppId = appId
        config.mEventHandler = mRtcEventHandler
        agoraEngine = RtcEngine.create(config)
        agoraEngine!!.enableVideo()
        }
        catch (e: Exception){
            Log.d("VideoCallError", e.message.toString())
        }

    }

    private val mRtcEventHandler : IRtcEngineEventHandler = object : IRtcEngineEventHandler(){
        override fun onUserJoined(uid: Int, elapsed: Int) {
//            showMessage("User Joined")
             setupRemoteVideo(uid)

        }
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            isJoined = true
//            showMessage("Join Channel Success")
        }
        override fun onUserOffline(uid: Int, reason: Int) {
//            showMessage("User Offline")
            remoteSurfaceView!!.visibility = SurfaceView.INVISIBLE
        }

        override fun onFirstLocalVideoFramePublished(
            source: Constants.VideoSourceType?,
            elapsed: Int
        ) {
            super.onFirstLocalVideoFramePublished(source, elapsed)
            Log.d("VideoCallActivity", "First local video frame published")
        }
    }

//    private fun showMessage(message: String){
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
    private fun checkPermissions() : Boolean{
        return !((ContextCompat.checkSelfPermission(this,requestedPermission[0]
        )) != PackageManager.PERMISSION_GRANTED  || (ContextCompat.checkSelfPermission(this,requestedPermission[1]
        )) != PackageManager.PERMISSION_GRANTED)
    }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = RtcEngine.CreateRendererView(this)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)

        // Adding remoteSurfaceView to the remote video FrameLayout
        findViewById<FrameLayout>(R.id.remoteView).addView(remoteSurfaceView)

        // bind it to remote user
        agoraEngine!!.setupRemoteVideo(VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid))

    }
    private fun setupLocalVideo() {
        localSurfaceView=RtcEngine.CreateRendererView(this)
        localSurfaceView!!.setZOrderMediaOverlay(true)
        Log.d("VideoCallActivity", "Local video setup complete")
        findViewById<FrameLayout>(R.id.localView).addView(localSurfaceView)

        agoraEngine!!.setupLocalVideo(VideoCanvas(localSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
            0))

        localSurfaceView!!.visibility = SurfaceView.VISIBLE



        Log.d("VideoCallActivity", "Local video setup complete")

    }

    private fun joinChannel() {
        if(checkPermissions()) {



            val option = ChannelMediaOptions()
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER

//            setupLocalVideo()



            // Log to check preview start
            Log.d("VideoCallActivity", "Preview started")


            // Before calling startPreview again
            agoraEngine!!.stopPreview();

            // Then call
            agoraEngine!!.startPreview();
            agoraEngine!!.joinChannel(token, channelName, uid, option)
        }
        else{
//            requestPermissions(requestedPermission, PERMISSION_ID)
        }
    }
    private fun leaveCall() {
        if(agoraEngine != null){
            agoraEngine!!.leaveChannel()
        }
        if(localSurfaceView != null){
            localSurfaceView!!.visibility = SurfaceView.INVISIBLE
        }
        if(remoteSurfaceView != null){
            remoteSurfaceView!!.visibility = SurfaceView.INVISIBLE
        }
        isJoined = false

    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread{
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
        Log.d("VideoCallActivity", "Engine destroyed") // Add logging statement

    }
}
package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class VideoCallActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.videocall_activty)

        //back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val camButton = findViewById<ImageView>(R.id.videoImage)
        camButton.setOnClickListener{
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }

    }
}
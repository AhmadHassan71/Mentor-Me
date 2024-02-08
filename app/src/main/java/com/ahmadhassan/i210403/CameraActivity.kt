package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class CameraActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activty)

        //back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        val camButton = findViewById<ImageView>(R.id.videoImage)
        camButton.setOnClickListener{
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }

    }
}
package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VerifyActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verifyphone)

        val loginButton: Button = findViewById(R.id.verifyButton)
        loginButton.setOnClickListener {
            // Start the HomeActivity when the verify button is clicked
            val intent = Intent(this@VerifyActivity, HomeActivity::class.java)
            startActivity(intent)
        }

    }
}
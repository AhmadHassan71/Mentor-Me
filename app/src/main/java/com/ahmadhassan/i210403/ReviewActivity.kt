package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review)


        // on submit review button go to  MentorProfileActivity
        val submitReviewButton = findViewById<Button>(R.id.submitFeedbackButton)
        submitReviewButton.setOnClickListener {
            val intent = Intent(this, MentorProfileActivity::class.java)
            startActivity(intent)
        }

        // on back button go to  MentorProfileActivity
        findViewById<Button>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, MentorProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class ReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review)
        val mentor = intent.getSerializableExtra("mentor") as? Mentors
        val mentorname = findViewById<TextView>(R.id.MentorNameTextView)
        val mentorPFP = findViewById<ImageView>(R.id.mentorPFP)
        if (mentor != null) {
            mentorname.text = mentor.name
            if (mentor.profilePicture.isNotEmpty()){
                // it should fill the image view with the mentor's profile picture
                mentorPFP.scaleType = ImageView.ScaleType.FIT_CENTER

                Picasso.get().load(mentor.profilePicture).into(mentorPFP)
            }
            // it should fill the image view with the mentor's profile picture
        }

        // on submit review button go to  MentorProfileActivity
        val submitReviewButton = findViewById<Button>(R.id.submitFeedbackButton)
        submitReviewButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // on back button go to  MentorProfileActivity
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
package com.ahmadhassan.i210403

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class MentorProfileActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mentorprofile)
        val mentor = intent.getSerializableExtra("mentor") as? Mentors
        val name = findViewById<TextView>(R.id.MentorNameTextView)
        val jobTitle = findViewById<TextView>(R.id.jobTitleTextView)
        val profileImage = findViewById<ImageView>(R.id.mentorProfilePicture)
        val company = findViewById<TextView>(R.id.companyTextView)
        val description = findViewById<TextView>(R.id.descriptionTextView)
        if (mentor != null) {
            name.text = mentor.name
            jobTitle.text = "${mentor.jobTitle} at"
            company.text = mentor.company
            val mentorPfp = mentor.profilePicture
            description.text = mentor.description
            if (mentorPfp.isNotEmpty()){
                // it should fill the image view with the mentor's profile picture
                profileImage.scaleType = ImageView.ScaleType.FIT_CENTER

                Picasso.get().load(mentorPfp).into(profileImage)

        }}


        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val reviewButton = findViewById<Button>(R.id.reviewButton)
        reviewButton.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            startActivity(intent)
        }

        val bookSession = findViewById<Button>(R.id.bookButton)
        bookSession.setOnClickListener {
            val intent = Intent(this,BookASessionActivity::class.java)
            startActivity(intent)
        }
        val joinCommunityButton = findViewById<Button>(R.id.joinCommunityButton)
        joinCommunityButton.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

    }
}
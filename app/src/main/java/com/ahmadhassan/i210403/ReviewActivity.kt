package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import me.zhanghai.android.materialratingbar.MaterialRatingBar

class ReviewActivity : AppCompatActivity() {
    val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("Reviews")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review)
        val mentor = intent.getSerializableExtra("mentor") as? Mentors
        val mentorname = findViewById<TextView>(R.id.MentorNameTextView)
        val mentorPFP = findViewById<ImageView>(R.id.mentorPFP)
        val user = UserInstance.getInstance()


        if (mentor != null) {
            mentorname.text = mentor.name
            if (mentor.profilePicture.isNotEmpty()){
                // it should fill the image view with the mentor's profile picture
                val url = "http://" + DatabaseIP.IP + "/MentorProfilePics/" + mentor.profilePicture

                Picasso.get().load(url).into(mentorPFP)
            }
            // it should fill the image view with the mentor's profile picture
        }

        // on submit review button go to  MentorProfileActivity
        val submitReviewButton = findViewById<Button>(R.id.submitFeedbackButton)
        submitReviewButton.setOnClickListener {

            val reviewDescription = findViewById<TextView>(R.id.DescriptionText)
            if(reviewDescription.text.isEmpty()){
                Toast.makeText(this, "Please enter a review", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val ratingBar = findViewById<MaterialRatingBar>(R.id.ratingBar)
            if (ratingBar.rating == 0.0f){
                Toast.makeText(this, "Please enter a rating", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val review = Review(mentorname.text.toString(), ratingBar.rating, reviewDescription.text.toString(), mentor!!.mentorId, user!!.userId)
            database.push().setValue(review)

            onBackPressedDispatcher.onBackPressed()
        }

        // on back button go to  MentorProfileActivity
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
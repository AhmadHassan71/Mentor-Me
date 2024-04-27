package com.ahmadhassan.i210403

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
        val rating = findViewById<TextView>(R.id.RatingTextView)
        if (mentor != null) {
            name.text = mentor.name
            jobTitle.text = "${mentor.jobTitle} at"
            company.text = mentor.company
            val mentorPfp = mentor.profilePicture
            description.text = mentor.description
            if (mentorPfp.isNotEmpty()){
//                // it should fill the image view with the mentor's profile picture
//                profileImage.scaleType = ImageView.ScaleType.CENTER_CROP
                val url = "http://" + DatabaseIP.IP + "/MentorProfilePics/" + mentorPfp

                Picasso.get().load(url).into(profileImage)

        }

            val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Reviews")
            // get the reviews for this specific mentor
            databaseReference.get().addOnSuccessListener {
                val reviews = it.children
                var totalRating = 0.0
                var count = 0
                for (review in reviews){
                    val reviewObj = review.getValue(Review::class.java)
                    if (reviewObj != null){
                        if (reviewObj.mentorId == mentor.mentorId){
                            totalRating += reviewObj.rating
                            count++
                        }
                    }
                }
                if (count > 0){
                    val averageRating = totalRating / count
                    rating.text = "⭐ %.1f".format(averageRating)
                }
            }

        }


        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val reviewButton = findViewById<Button>(R.id.reviewButton)
        reviewButton.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("mentor", mentor)
            startActivity(intent)
        }

        val bookSession = findViewById<Button>(R.id.bookButton)
        bookSession.setOnClickListener {
            val intent = Intent(this,BookASessionActivity::class.java)
            intent.putExtra("mentor", mentor)
            startActivity(intent)
        }
        val joinCommunityButton = findViewById<Button>(R.id.joinCommunityButton)
        joinCommunityButton.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            intent.putExtra("mentor", mentor)
            startActivity(intent)
        }

    }
}
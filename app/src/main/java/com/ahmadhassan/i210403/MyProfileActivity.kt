package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyProfileActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_profile)

        // back button
        val backButton = findViewById<android.widget.ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val addMentorButton = findViewById<ImageView>(R.id.addMentorButton)
        addMentorButton.setOnClickListener {
            val intent = Intent(this, AddNewMentorActivity::class.java)
            startActivity(intent)
        }

        // bottom navigation view
        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                // handle search item
                R.id.navigation_search -> {
                    startActivity(Intent(this, SearchPageActivity::class.java))
                    true
                }
                R.id.navigation_chat -> {
                    startActivity(Intent(this, ChatActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val recyclerViewTopMentor: RecyclerView = findViewById(R.id.favMentorsRecyclerView)
        recyclerViewTopMentor.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val mentorsList = listOf(
            Mentors("John Doe", "Software Engineer", "$50/hr", "Available","Favorite"),
            Mentors("Jane Smith", "Data Scientist", "$60/hr", "Unavailable","Favorite"),
            Mentors("Jack Son", "Software Engineer", "$50/hr", "Available","Favorite"),
        )
        val adapter = CardAdapter(mentorsList, this)
        recyclerViewTopMentor.adapter = adapter


        val recyclerView: RecyclerView = findViewById(R.id.reviewRecycleView)
        recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val reviews = listOf(
            Review( "Mentor 1", 4.5f, "Great mentor! taught me everything I know."),
            Review( "Mentor 2", 3.0f, "Average mentor. Could be better."),
            Review( "Mentor 3", 5.0f, "Best mentor ever!"),
            Review( "Mentor 4", 2.5f, "Not a good mentor. Waste of time."),

        )
        val adapter2 = ReviewAdapter(reviews)
        recyclerView.adapter = adapter2


        // optionsImageView opens EditProfileActivity
        val optionsImageView = findViewById<ImageView>(R.id.optionsImageView)
        optionsImageView.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        // editProfileButton opens EditProfileActivity
        val editProfileButton = findViewById<ImageView>(R.id.editProfilePicture)
        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        val editCoverButton = findViewById<ImageView>(R.id.editCoverPicture)
        editCoverButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // bookedSessionsButton opens BookedSessionActivity
        val bookedSessionsButton = findViewById<Button>(R.id.bookedSessionsButton)
        bookedSessionsButton.setOnClickListener {
            val intent = Intent(this, BookedSessionActivity::class.java)
            startActivity(intent)
        }

        adapter.setOnItemClickListener {
            val intent = Intent(this@MyProfileActivity, MentorProfileActivity::class.java)
            startActivity(intent)
        }

    }
}
package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MentorProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mentorprofile)


        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
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
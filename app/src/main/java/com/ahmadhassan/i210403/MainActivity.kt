package com.ahmadhassan.i210403

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val recyclerView: RecyclerView = findViewById(R.id.TopMentors)

        // Set layout manager
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        // Create dummy data
        val mentorsList = listOf(
            Mentors("John Doe", "Software Engineer", "$50/hr", "Available"),
            Mentors("Jane Smith", "Data Scientist", "$60/hr", "Unavailable"),
            Mentors("Michael ", "UX Designer", "$55/hr", "Available"),
            // Add more mentors as needed
        )

        // Create adapter
        val adapter = CardAdapter(mentorsList, this)

        // Set adapter
        recyclerView.adapter = adapter
    }
}

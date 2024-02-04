package com.ahmadhassan.i210403
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val recyclerViewTopMentor: RecyclerView = findViewById(R.id.TopMentors)
        val recyclerViewEdMentor: RecyclerView = findViewById(R.id.EdMentors)
        val recyclerViewRecentMentor: RecyclerView = findViewById(R.id.RecentMentors)

        // Set layout manager
        recyclerViewTopMentor.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerViewEdMentor.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerViewRecentMentor.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // Create dummy data
        val mentorsList = listOf(
            Mentors("John Doe", "Software Engineer", "$50/hr", "Available"),
            Mentors("Jane Smith", "Data Scientist", "$60/hr", "Unavailable"),
            Mentors("Michael ", "UX Designer", "$55/hr", "Available"),
            Mentors("Jack Son", "Software Engineer", "$50/hr", "Available")
        )

        // Create adapter
        val adapter = CardAdapter(mentorsList, this)

        // Set adapter
        recyclerViewTopMentor.adapter = adapter
        recyclerViewEdMentor.adapter = adapter
        recyclerViewRecentMentor.adapter = adapter
    }
}

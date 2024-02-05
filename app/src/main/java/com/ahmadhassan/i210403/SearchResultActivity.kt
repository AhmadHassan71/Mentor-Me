package com.ahmadhassan.i210403

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchresult)
        val recyclerViewTopMentor: RecyclerView = findViewById(R.id.TopMentors)

        // Set layout manager
        recyclerViewTopMentor.layoutManager =
            LinearLayoutManager(this)


        // Create dummy data
        val mentorsList = listOf(
            Mentors("John Doe", "Software Engineer", "$50/hr", "Available","Favorite"),
            Mentors("Jane Smith", "Data Scientist", "$60/hr", "Unavailable","Not Favorite"),
            Mentors("Michael ", "UX Designer", "$55/hr", "Available","️Not Favorite"),
            Mentors("Jack Son", "Software Engineer", "$50/hr", "Available","Not Favorite"),
            Mentors("Jung Li", "Data Engineer", "$70/hr", "Available","Favorite"),
            Mentors("Emily Brown", "Frontend Developer", "$65/hr", "Available", "Favorite"),
            Mentors("Alex Johnson", "Machine Learning Engineer", "$80/hr", "Unavailable", "Favorite"),
            Mentors("Sarah Lee", "Product Manager", "$75/hr", "Available", "Not Favorite"),
            Mentors("David Miller", "Full Stack Developer", "$70/hr", "Available", "Favorite")

        )

        // Create adapter
        val adapter = VerticalCardAdapter(mentorsList, this)

        // Set adapter
        recyclerViewTopMentor.adapter = adapter
    }
}
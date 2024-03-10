package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


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


        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val addMentorButton = findViewById<ImageView>(R.id.addMentorButton)
        addMentorButton.setOnClickListener {
            val intent = Intent(this, AddNewMentorActivity::class.java)
            startActivity(intent)
        }



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
                R.id.navigation_profile -> {
                    // highlight the my profile icon
                    startActivity(Intent(this, MyProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        adapter.setOnItemClickListener(object : VerticalCardAdapter.OnItemClickListener {
            override fun onItemClick(mentor: Mentors) {
                val intent = Intent(this@SearchResultActivity, MentorProfileActivity::class.java)
                startActivity(intent)
            }
        })

    }

}
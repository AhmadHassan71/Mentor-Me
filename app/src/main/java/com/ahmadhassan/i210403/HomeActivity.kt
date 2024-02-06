package com.ahmadhassan.i210403
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // on click home item in the menu go to  SearchPageActivity


        val recyclerViewTopMentor: RecyclerView = findViewById(R.id.TopMentors)
        val recyclerViewEdMentor: RecyclerView = findViewById(R.id.EdMentors)
        val recyclerViewRecentMentor: RecyclerView = findViewById(R.id.RecentMentors)

        // Set layout manager
        recyclerViewTopMentor.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerViewEdMentor.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerViewRecentMentor.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // Create dummy data
        val mentorsList = listOf(
            Mentors("John Doe", "Software Engineer", "$50/hr", "Available","Favorite"),
            Mentors("Jane Smith", "Data Scientist", "$60/hr", "Unavailable","Not Favorite"),
            Mentors("Michael ", "UX Designer", "$55/hr", "Available","️Not Favorite"),
            Mentors("Jack Son", "Software Engineer", "$50/hr", "Available","Not Favorite"),
        )

        // Create adapter
        val adapter = CardAdapter(mentorsList, this)

        // Set adapter
        recyclerViewTopMentor.adapter = adapter
        recyclerViewEdMentor.adapter = adapter
        recyclerViewRecentMentor.adapter = adapter


        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavView.selectedItemId = R.id.navigation_home

        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    // highlight the search icon
                    R.id.navigation_search
                    startActivity(Intent(this, SearchPageActivity::class.java))
                    true
                }
                R.id.navigation_chat -> {
                    // highlight the chat icon

                    startActivity(Intent(this, ChatActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    // highlight the my profile icon
                    startActivity(Intent(this, MyProfileActivity::class.java))
                    true
                }
                // Handle other menu items if needed
                else -> false
            }.also {
                // Set the selected item as checked to highlight it
                item.isChecked = true
            }
        }

        adapter.setOnItemClickListener {
            val intent = Intent(this@HomeActivity, MentorProfileActivity::class.java)
            startActivity(intent)
        }

        val addMentorButton = findViewById<ImageView>(R.id.addMentorButton)
        addMentorButton.setOnClickListener {
            val intent = Intent(this, AddNewMentorActivity::class.java)
            startActivity(intent)
        }
    }

}

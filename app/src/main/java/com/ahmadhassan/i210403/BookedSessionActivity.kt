package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


class BookedSessionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booked_session)

        val recyclerView: RecyclerView = findViewById(R.id.bookedMentors)
        val sessions = listOf<Session>(
            Session("John Doe", "Software Engineer", "23 Dec 2023", "12:20 pm", R.drawable.donny_savage),
            Session("Jane Smith", "Product Manager", "24 Dec 2023", "10:00 am", R.drawable.donny_savage),
            Session("Michael ", "UX Designer", "25 Dec 2023", "11:00 am", R.drawable.donny_savage),
            Session("Jack Son", "Data Scientist", "26 Dec 2023", "12:00 pm", R.drawable.donny_savage)
        )
        val adapter = SessionAdapter(sessions)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)





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




        adapter.setOnItemClickListener(object : SessionAdapter.OnItemClickListener {
            override fun onItemClick(session: Session) {
                // Handle item click here
                val intent = Intent(this@BookedSessionActivity, MentorProfileActivity::class.java)
                startActivity(intent)
            }
        })

    }

}
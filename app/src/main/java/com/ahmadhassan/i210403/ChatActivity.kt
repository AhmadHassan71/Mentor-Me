package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_menu)


        // back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // bottom navigation view

        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
       bottomNavView.selectedItemId = R.id.navigation_chat
        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
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
                // Handle other menu items if needed
                else -> false
            }.also {
                // Set the selected item as checked to highlight it
                item.isChecked = true
            }
        }

        // RecyclerView for profile pics
        val addMentorButton = findViewById<ImageView>(R.id.addMentorButton)
        addMentorButton.setOnClickListener {
            val intent = Intent(this, AddNewMentorActivity::class.java)
            startActivity(intent)
        }
        val recyclerView: RecyclerView = findViewById(R.id.myCommunityRecyclerView)

        // Sample data for profile pics (replace with your actual data)
        val profilePics = listOf(
            R.drawable.paul_personpic,
            R.drawable.personpic,
            R.drawable.personpic,
            R.drawable.personpic,
            R.drawable.personpic,
            R.drawable.personpic,
            R.drawable.personpic,
        )

        val adapter = CommunityProfilePicsAdapter(profilePics)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity,RecyclerView.HORIZONTAL, false)



        val recyclerView2: RecyclerView = findViewById(R.id.allMessagesRecyclerView)

        // Sample data for person profiles (replace with your actual data)
        val profiles = listOf(
            PersonProfile(R.drawable.personpic, "Person 1", 3),
            PersonProfile(R.drawable.paul_personpic, "Person 2", 0),
            PersonProfile(R.drawable.personpic, "Person 3", 5),
            PersonProfile(R.drawable.paul_personpic, "Person 4", 0),
            PersonProfile(R.drawable.personpic, "Person 5", 1)
        )

        val adapter2 = PersonProfileAdapter(profiles)
        recyclerView2.adapter = adapter2
        recyclerView2.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)


        adapter2.setOnItemClickListener(object : PersonProfileAdapter.OnItemClickListener {
            override fun onItemClick(profile: PersonProfile) {
                val intent = Intent(this@ChatActivity, ChatRoomActivity::class.java)
                // Pass any necessary data to the ChatRoomActivity using intent extras if needed
                startActivity(intent)
            }
        })
    }
}
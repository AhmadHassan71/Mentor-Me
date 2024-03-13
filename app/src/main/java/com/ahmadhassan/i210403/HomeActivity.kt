package com.ahmadhassan.i210403

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val userIdSharedPreferences : SharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
        val userId = userIdSharedPreferences.getString("userId", null)

//        Toast.makeText(this, "User ID: $userId", Toast.LENGTH_SHORT).show()

        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val dbref = database.getReference("Users").child(userId)
            dbref.get().addOnSuccessListener { dataSnapshot ->
                val user = dataSnapshot.getValue(User::class.java)
                val username = user?.fullName
                val welcomeText = findViewById<TextView>(R.id.nameText)
                welcomeText.text = "$username!"
            }
        }


        val recyclerViewTopMentor: RecyclerView = findViewById(R.id.TopMentors)
        val recyclerViewEdMentor: RecyclerView = findViewById(R.id.EdMentors)
        val recyclerViewRecentMentor: RecyclerView = findViewById(R.id.RecentMentors)

        recyclerViewTopMentor.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerViewEdMentor.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerViewRecentMentor.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val mentorsList = mutableListOf<Mentors>()
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Mentors")
        
        

        ref.get().addOnSuccessListener { dataSnapshot ->
            for (mentorSnapshot in dataSnapshot.children) {
                val mentorData = mentorSnapshot.getValue(Mentors::class.java)

                mentorData?.let { mentorsList.add(it) }
            }
            // Create adapter after fetching data
            val adapter = CardAdapter(mentorsList, this)

            // Set adapter for each RecyclerView
            recyclerViewTopMentor.adapter = adapter
            recyclerViewEdMentor.adapter = adapter
            recyclerViewRecentMentor.adapter = adapter

            adapter.setOnItemClickListener {
                val intent = Intent(this@HomeActivity, MentorProfileActivity::class.java)
                startActivity(intent)
            }

        }.addOnFailureListener { exception ->
            // Handle failure
            Toast.makeText(this, "Error getting data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }

        val addMentorButton = findViewById<ImageView>(R.id.addMentorButton)
        addMentorButton.setOnClickListener {
            val intent = Intent(this, AddNewMentorActivity::class.java)
            startActivity(intent)
        }

        val notificationButton = findViewById<ImageView>(R.id.imageView2)
        notificationButton.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavView.selectedItemId = R.id.navigation_home

        bottomNavView.setOnItemSelectedListener { item ->
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


    }
}

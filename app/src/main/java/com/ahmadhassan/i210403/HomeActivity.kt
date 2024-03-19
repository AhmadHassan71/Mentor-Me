package com.ahmadhassan.i210403

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)



//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            // Get new FCM registration token
//            val token = task.result
//            Log.d("MyToken", token)
//        })


        val userIdSharedPreferences : SharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
        val userId = userIdSharedPreferences.getString("userId", null)

//        Toast.makeText(this, "User ID: $userId", Toast.LENGTH_SHORT).show()

        if (userId != null) {
            UserInstance.fetchUser(userId) { user ->
                if (user != null) {
                    // User data fetched successfully
                    val welcomeText = findViewById<TextView>(R.id.nameText)
                    welcomeText.text = "${user.fullName}!"
                } else {
                    // Handle case where user data couldn't be fetched
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
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

            adapter.setOnItemClickListener {mentor ->
                val intent = Intent(this@HomeActivity, MentorProfileActivity::class.java)
                intent.putExtra("mentor", mentor) // Pass mentor object
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

package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase


class BookedSessionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booked_session)

        val recyclerView: RecyclerView = findViewById(R.id.bookedMentors)

        val bookedSessionList :  MutableList<BookedSession> = mutableListOf()
        val sessions: MutableList<Session> = mutableListOf()
        // get booked sessions from the server
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("BookedSessions")
        ref.get().addOnSuccessListener { dataSnapshot ->
            for (data in dataSnapshot.children) {
                val session = data.getValue(BookedSession::class.java)
                if (session!!.userId == UserInstance.getInstance()!!.userId) {
                    bookedSessionList.add(session)
                }
            }

            // Fetch mentor data for each booked session
            for (eachSession in bookedSessionList) {
                database.getReference("Mentors").child(eachSession.mentorId).get().addOnSuccessListener { mentorDataSnapshot ->
                    val mentor = mentorDataSnapshot.getValue(Mentors::class.java)
                    // if mentor name has space remove the space and text after it
                    val mentorName = mentor!!.name
                    val mentorNameWithoutSpace = mentorName.substringBefore(" ")
                    val session = Session(mentorNameWithoutSpace, mentor.jobTitle, eachSession.date, eachSession.time, mentor.profilePicture!!)
                    sessions.add(session)

                    // Update RecyclerView adapter once all data is fetched
                    val adapter = SessionAdapter(sessions)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@BookedSessionActivity)
                    adapter.setOnItemClickListener(object : SessionAdapter.OnItemClickListener {
                        override fun onItemClick(session: Session) {
                            // Handle item click here
                            val intent = Intent(this@BookedSessionActivity, MentorProfileActivity::class.java)
                            startActivity(intent)
                        }
                    })
                }.addOnFailureListener {
                    Log.d("BookedSessionActivity", "Failed to get mentor data from the server")
                }
            }
        }.addOnFailureListener {
            Log.d("BookedSessionActivity", "Failed to get data from the server")
        }



//        val sessions = listOf<Session>(
//            Session("John Doe", "Software Engineer", "23 Dec 2023", "12:20 pm", R.drawable.donny_savage),
//            Session("Jane Smith", "Product Manager", "24 Dec 2023", "10:00 am", R.drawable.donny_savage),
//            Session("Michael ", "UX Designer", "25 Dec 2023", "11:00 am", R.drawable.donny_savage),
//            Session("Jack Son", "Data Scientist", "26 Dec 2023", "12:00 pm", R.drawable.donny_savage)
//        )






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






    }

}
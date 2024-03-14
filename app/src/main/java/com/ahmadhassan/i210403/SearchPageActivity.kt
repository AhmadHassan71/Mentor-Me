package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase

class SearchPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchpage)



        // on click listener for the search button go to search result page
        val searchButton = findViewById<ImageView>(R.id.searchButton)
        searchButton.setOnClickListener {
            val searchText = findViewById<EditText>(R.id.searchEditText).text.toString()
            if (searchText.isEmpty()) {
                Toast.makeText(this, "Please enter mentor name to search", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            queryMentors(searchText) { filteredList ->
                val intent = Intent(this, SearchResultActivity::class.java)
                intent.putExtra("matchingMentors", filteredList.toTypedArray())
                startActivity(intent)
                finish()
            }
        }
        //on click listener for the back button go to home page
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
        bottomNavView.selectedItemId = R.id.navigation_search
        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_chat -> {
                    startActivity(Intent(this, ChatActivity::class.java))
                    true
                } R.id.navigation_profile -> {
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

    private fun queryMentors(searchText: String, callback: (List<Mentors>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Mentors")
        val mentorsList = mutableListOf<Mentors>()

        ref.get().addOnSuccessListener { dataSnapshot ->
            for (mentorSnapshot in dataSnapshot.children) {
                val mentorData = mentorSnapshot.getValue(Mentors::class.java)
                mentorData?.let { mentorsList.add(it) }
            }

            val filteredList = mentorsList.filter { mentor ->
                mentor.name.startsWith(searchText, ignoreCase = true) ||
                        mentor.name.contains(searchText, ignoreCase = true) ||
                        mentor.jobTitle.startsWith(searchText, ignoreCase = true) ||
                        mentor.jobTitle.contains(searchText, ignoreCase = true)
            }

            callback(filteredList)
        }.addOnFailureListener { exception ->
            // Handle failure
            Toast.makeText(this, "Error getting data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

}

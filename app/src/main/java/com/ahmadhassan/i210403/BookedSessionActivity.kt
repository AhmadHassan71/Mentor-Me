package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONArray


class BookedSessionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booked_session)



//        val sessions = listOf<Session>(
//            Session("John Doe", "Software Engineer", "23 Dec 2023", "12:20 pm", R.drawable.donny_savage),
//            Session("Jane Smith", "Product Manager", "24 Dec 2023", "10:00 am", R.drawable.donny_savage),
//            Session("Michael ", "UX Designer", "25 Dec 2023", "11:00 am", R.drawable.donny_savage),
//            Session("Jack Son", "Data Scientist", "26 Dec 2023", "12:00 pm", R.drawable.donny_savage)
//        )



        fetchSessions(UserInstance.getInstance()!!.userId)


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



    private fun fetchSessions(userId: String) {
        val url = "http://${DatabaseIP.IP}/getsessions.php" // Replace with your actual URL
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, // Assuming getsessions.php expects a POST request
            url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response) // Parse the response as JSON array
                    val sessions = mutableListOf<Session>() // List to store Session objects

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val session = Session(
                            jsonObject.getString("mentorName").split(" ")[0],
                            jsonObject.getString("jobTitle"),
                            jsonObject.getString("date"),
                            jsonObject.getString("time"),
                            jsonObject.getString("mentorProfilePic")
                        )
                        sessions.add(session)

                    }
                    val adapter = SessionAdapter(sessions)
                    val recyclerView: RecyclerView = findViewById(R.id.bookedMentors)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@BookedSessionActivity)
                    adapter.setOnItemClickListener(object : SessionAdapter.OnItemClickListener {
                        override fun onItemClick(session: Session) {
                            // Handle item click here
                            val intent = Intent(this@BookedSessionActivity, MentorProfileActivity::class.java)
                            startActivity(intent)
                        }
                    })

                    // Handle the retrieved list of sessions (e.g., update UI)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle parsing error (e.g., show a toast message)
                    Toast.makeText(this, "Error fetching sessions: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Handle network error
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userId"] = userId.toString()
                return params
            }
        }

        requestQueue.add(stringRequest)
    }


}
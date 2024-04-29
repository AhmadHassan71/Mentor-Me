package com.ahmadhassan.i210403


import PersonProfile
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val chatRoomsRef = database.getReference("ChatRooms")
    private val ref = database.getReference("Mentors")
    private val apiUrl = "http://${DatabaseIP.IP}/getallmentors.php" // Replace with your API endpoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_menu)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }



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
                    // Do nothing as we are already in ChatActivity
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, MyProfileActivity::class.java))
                    true
                }
                else -> false
            }.also {
                item.isChecked = true
            }
        }

        val addMentorButton = findViewById<ImageView>(R.id.addMentorButton)
        addMentorButton.setOnClickListener {
            val intent = Intent(this, AddNewMentorActivity::class.java)
            startActivity(intent)
        }

//        val mentorsList = mutableListOf<Mentors>()


        fetchAllMentors( { mentors ->
            // Success callback: mentors have been fetched successfully
            // Create adapter after fetching data


            val recyclerView: RecyclerView = findViewById(R.id.myCommunityRecyclerView)
                val adapter = CommunityProfilePicsAdapter(mentors.map { "http://" + DatabaseIP.IP + "/MentorProfilePics/" + it.profilePicture})
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity, RecyclerView.HORIZONTAL, false)

                val recyclerView2: RecyclerView = findViewById(R.id.allMessagesRecyclerView)
                val adapter2 = PersonProfileAdapter(mentors.map { mentor ->
                    PersonProfile("http://" + DatabaseIP.IP + "/MentorProfilePics/" + mentor.profilePicture, mentor.name, 0)
                })
                recyclerView2.adapter = adapter2
                recyclerView2.layoutManager = LinearLayoutManager(this@ChatActivity, RecyclerView.VERTICAL, false)

            adapter2.setOnItemClickListener(object : PersonProfileAdapter.OnItemClickListener {
                  override fun onItemClick(profile: PersonProfile) {
                    val currUser = UserInstance.getInstance()
                    val currMentor = mentors[mentors.indexOfFirst { it.name == profile.personName }]
                    checkOrCreateChatRoom(currUser!!, currMentor)
                }
            })

        }, { error ->
            // Error callback: handle error when fetching mentors
            Toast.makeText(this, "Error getting mentors: $error", Toast.LENGTH_SHORT).show()
        })
    }
    private fun fetchAllMentors( onSuccess: (List<Mentors>) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET,
            apiUrl,
            null,
            { response ->
                val mentorsList = mutableListOf<Mentors>()

                try {
                    val gson = Gson()
                    val jsonArray = JSONArray(response.toString())

                    for (i in 0 until jsonArray.length()) {
                        val mentorJson = jsonArray.getJSONObject(i)
                        val mentor = gson.fromJson(mentorJson.toString(), Mentors::class.java)
                        mentorsList.add(mentor)
                    }

                    onSuccess(mentorsList)
                } catch (e: Exception) {
                    onError("Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                onError("Error fetching mentors: ${error.message}")
            }
        )

        requestQueue.add(jsonArrayRequest)
    }
    private fun checkOrCreateChatRoom(currUser: User, currMentor: Mentors) {
        val url = "http://${DatabaseIP.IP}/checkorcreatechatroom.php"

        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, url,
            Response.Listener<String> { response ->
                try {
                    val jsonObject = JSONObject(response)
//                    val roomId = jsonObject.getString("roomId")
                    val userId = jsonObject.getInt("userId")
                    val mentorId = jsonObject.getInt("mentorId")
                    val messagesJsonArray = jsonObject.getJSONArray("messages")

                    val messagesList = mutableListOf<Message>()
                    if (messagesJsonArray.length() > 0) { // Check if messages array is not empty
                        for (i in 0 until messagesJsonArray.length()) {
                            val messageJson = messagesJsonArray.getJSONObject(i)
                            val messageId = messageJson.getString("messageId")
                            val audioMessage = messageJson.getBoolean("audioMessage")
                            val userId = messageJson.getInt("userId")
                            val imageUrl = messageJson.getString("imageUrl")
                            val sentByCurrentUser = messageJson.getBoolean("sentByCurrentUser")
                            val text = messageJson.getString("text")
                            val timestamp = messageJson.getString("timestamp")

                            val message = Message(messageId,  text, timestamp, imageUrl, sentByCurrentUser,audioMessage)
                            messagesList.add(message)
                        }
                    }
                        messagesList.add(Message("1", "Hello",
                           "${ System.currentTimeMillis() - (1000 * 60 * 60 * 20) }", currMentor.profilePicture, true))
                        messagesList.add(Message("2", "Hi", "${ System.currentTimeMillis() - (1000 * 30 * 60 * 20) }", currMentor.profilePicture, false))
                        messagesList.add(Message("3", "How are you?", "${ System.currentTimeMillis() - (1000 * 40 * 60 * 20) }", currMentor.profilePicture, false))
                        messagesList.add(Message("4", "I'm good, thanks!", "${ System.currentTimeMillis() - (1000 * 60 * 50 * 20) }", currMentor.profilePicture, true))
                        messagesList.add(Message("5", "How can I help you today?", "${ System.currentTimeMillis() - (1000 * 60 * 60 * 20) }", currMentor.profilePicture, false))


                    val intent = Intent(this@ChatActivity, ChatRoomActivity::class.java)
                    val chatRoom = ChatRoom(mentorId.toString(), currMentor.mentorId, currUser.userId, messagesList)
                    ChatRoomInstance.setInstance(chatRoom)
                    MentorChatInstance.setInstance(currMentor)
                    startActivity(intent)
                    finish()

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle JSON parsing error
                }
            },

            Response.ErrorListener { error ->
                error.printStackTrace()
                // Handle Volley error
            }) {
            override fun getParams(): MutableMap<String, String> {
            val params = HashMap<String, String>()
            params["userId"] = currUser.userId
            params["mentorId"] = currMentor.mentorId
            return params
        }}

        requestQueue.add(stringRequest)
    }


}

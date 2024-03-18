package com.ahmadhassan.i210403


import PersonProfile
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {
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

        val mentorsList = mutableListOf<Mentors>()
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Mentors")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (mentorSnapshot in dataSnapshot.children) {
                    val mentorData = mentorSnapshot.getValue(Mentors::class.java)
                    mentorData?.let { mentorsList.add(it) }
                }

                val recyclerView: RecyclerView = findViewById(R.id.myCommunityRecyclerView)
                val adapter = CommunityProfilePicsAdapter(mentorsList.map { it.profilePicture })
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity, RecyclerView.HORIZONTAL, false)

                val recyclerView2: RecyclerView = findViewById(R.id.allMessagesRecyclerView)
                val adapter2 = PersonProfileAdapter(mentorsList.map { mentor ->
                    PersonProfile(mentor.profilePicture, mentor.name, 0)
                })
                recyclerView2.adapter = adapter2
                recyclerView2.layoutManager = LinearLayoutManager(this@ChatActivity, RecyclerView.VERTICAL, false)

                adapter2.setOnItemClickListener(object : PersonProfileAdapter.OnItemClickListener {
                    override fun onItemClick(profile: PersonProfile) {
                        val currUser = UserInstance.getInstance() ?: return
                        val currMentor = mentorsList.find { it.name == profile.personName } ?: return
                        checkOrCreateChatRoom(currUser, currMentor)
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(this@ChatActivity, "Failed to load mentors", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkOrCreateChatRoom(currUser: User, currMentor: Mentors) {
        val database = FirebaseDatabase.getInstance()
        val chatRoomsRef = database.getReference("ChatRooms")
        Log.d("ChatActivityH", "chatRoomRef: $chatRoomsRef")

        chatRoomsRef.orderByChild("userId").equalTo(UserInstance.getInstance()!!.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (roomSnapshot in dataSnapshot.children) {
                        Log.d("ChatActivityH", "roomSnapshot: $roomSnapshot")
                        val chatRoom = roomSnapshot.getValue(ChatRoom::class.java)
                        Log.d("ChatActivityH", "chatRoom: $chatRoom")
                        Log.d("ChatActivityH", "chatRoom: $chatRoom")
                        // log the current mentor id and user id
                        Log.d("ChatActivityH", "currMentor.mentorId: ${currMentor.mentorId}")
                        Log.d("ChatActivityH", "currUser.userId: ${currUser.userId}")

                        if (chatRoom != null && chatRoom.mentorId == currMentor.mentorId) {
                            // If a chat room exists, navigate to the chat room activity
                            val intent = Intent(this@ChatActivity, ChatRoomActivity::class.java)
                            intent.putExtra("chatRoom", chatRoom)
                            startActivity(intent)
                            finish()
                            return
                        }
                    }
                    // If no chat room exists, create a new one
                    createNewChatRoom(currUser, currMentor)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@ChatActivity, "Failed to check chat rooms", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun createNewChatRoom(currUser: User, currMentor: Mentors) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("ChatRooms")

        val chatRoomId = ref.push().key ?: return

        // Create a new chat room with an empty message list
        val newChatRoom = ChatRoom(chatRoomId, currMentor.mentorId, currUser.userId, emptyList())

        // Save the chat room to the database
        ref.child(chatRoomId).setValue(newChatRoom).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // If the chat room is successfully saved, add initial messages to it
                addInitialMessagesToChatRoom(chatRoomId, currMentor)
            } else {
                Toast.makeText(this@ChatActivity, "Failed to open chat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addInitialMessagesToChatRoom(chatRoomId: String, currMentor: Mentors) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("ChatRooms").child(chatRoomId).child("messages")

        // Add initial messages to the chat room
        val initialMessages = listOf(
            Message("1", "Hello! ${UserInstance.getInstance()!!.fullName}", "10:20", imageUrl = currMentor.profilePicture,false),
            Message("2", "Hi there!", "11:15", imageUrl ="", sentByCurrentUser = true),
            Message("3", "How are you?", "11:20", imageUrl =currMentor.profilePicture, sentByCurrentUser = false),
            Message("4", "I'm fine, thanks! ${currMentor.name}", "12:00", imageUrl = "", sentByCurrentUser = true)
            // Add more initial messages as needed
        )

        // Save the initial messages to the database
        ref.setValue(initialMessages).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // If the messages are successfully saved, navigate to the chat room activity
                val intent = Intent(this@ChatActivity, ChatRoomActivity::class.java)
                val newChatRoom = ChatRoom(chatRoomId, "", "", initialMessages) // Provide only the ID and messages
                intent.putExtra("chatRoom", newChatRoom)
                startActivity(intent)
            } else {
                Toast.makeText(this@ChatActivity, "Failed to open chat", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

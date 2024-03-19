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
    private val database = FirebaseDatabase.getInstance()
    private val chatRoomsRef = database.getReference("ChatRooms")
    private val ref = database.getReference("Mentors")
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

        Log.d("ChatActivityH", "chatRoomRef: $chatRoomsRef")

        chatRoomsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (roomSnapshot in dataSnapshot.children) {
                    val roomId = roomSnapshot.key // Get the room ID
                    val messagesSnapshot = roomSnapshot.child("messages") // Get the messages node
                    val messagesList = mutableListOf<Message>() // Create a list to hold messages

                    // Iterate through the child nodes under "messages"
                    for (messageSnapshot in messagesSnapshot.children) {
                        val message = messageSnapshot.getValue(Message::class.java) // Parse each message
                        if (message != null) {
                            messagesList.add(message) // Add the message to the list
                        }
                    }

                    // Assuming `mentorId` and `userId` are properties of the ChatRoom class
                    val mentorId = roomSnapshot.child("mentorId").getValue(String::class.java)
                    val userId = roomSnapshot.child("userId").getValue(String::class.java)

                    if (roomId != null && mentorId != null && userId != null &&
                        mentorId == currMentor.mentorId && userId == currUser.userId) {
                        // If a chat room exists for the selected mentor and user, navigate to it
                        val intent = Intent(this@ChatActivity, ChatRoomActivity::class.java)
                        val chatRoom = ChatRoom(roomId,mentorId  , userId,messagesList) // Create ChatRoom instance
                        ChatRoomInstance.setInstance(chatRoom)
                        MentorChatInstance.setInstance(currMentor)
                        startActivity(intent)
                        finish()
                        return
                    }
                }
                // If no chat room exists for the selected mentor and user, create a new one
                createNewChatRoom(currUser, currMentor)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Log.e("ChatActivityH", "Failed to check chat rooms: ${databaseError.message}")
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
                val newChatRoom = ChatRoom(chatRoomId,UserInstance.getInstance()!!.userId ,currMentor.mentorId , initialMessages) // Provide only the ID and messages
                ChatRoomInstance.setInstance(newChatRoom)
                MentorChatInstance.setInstance(currMentor)
                startActivity(intent)
            } else {
                Toast.makeText(this@ChatActivity, "Failed to open chat", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

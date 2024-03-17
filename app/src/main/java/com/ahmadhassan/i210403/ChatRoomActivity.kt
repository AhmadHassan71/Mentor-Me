package com.ahmadhassan.i210403



import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date


class ChatRoomActivity : AppCompatActivity() {
    private lateinit var messageList: MutableList<Message>
    private lateinit var adapter: ChatAdapter
    private lateinit var messageEditText: EditText
    private lateinit var sendMessageButton: ImageView
    private lateinit var database: DatabaseReference

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_room)

        // get intent from previous activity
        val intent = intent
//        val personProfile: PersonProfile? = intent.getParcelableExtra("personProfile")
//        val personProfile: PersonProfile? = intent.getParcelableExtra("personProfile", PersonProfile::class.java)
        val chatRoom: ChatRoom = intent.getParcelableExtra("chatRoom")!!
        Log.d("ChatRoomActivityR", "ChatRoom: $chatRoom")

        database = FirebaseDatabase.getInstance().getReference("ChatRooms").child(chatRoom.roomId).child("messages")

        val personName = findViewById<TextView>(R.id.MentorNameTextView)
        var personProfilePic = ""

        val mentorDb = FirebaseDatabase.getInstance().getReference("Mentors").child(chatRoom.mentorId)
        mentorDb.get().addOnSuccessListener {
            val mentor = it.getValue(Mentors::class.java)
            if (mentor != null) {
                personName.text = mentor.name
                personProfilePic = mentor.profilePicture
            }
        }



        // Message Service
        val messageList :MutableList<Message> = mutableListOf()
//        messageList.add(Message("1", "Hello!", "10:20", imageUrl = personProfile!!.profilePicture))
//        messageList.add(Message("2", "Hi there!", "11:15", imageUrl = personProfile.profilePicture, sentByCurrentUser = false))
//        messageList.add(Message("3", "How are you?", "11:20", imageUrl = personProfile.profilePicture, sentByCurrentUser = false))
//        messageList.add(Message("4", "I'm fine, thanks!", "12:00", imageUrl = personProfile!!.profilePicture, sentByCurrentUser = true))
//        messageList.add(Message("5", "Check out this image!", "13:25", imageUrl = personProfile.profilePicture, sentByCurrentUser = true))
//        messageList.add(Message("8", "I think you are good", "13:25", imageUrl = personProfile.profilePicture, sentByCurrentUser = false))
        val messageBox: View = findViewById(R.id.messageBox)
        messageEditText = messageBox.findViewById(R.id.messageEditText)
        sendMessageButton = messageBox.findViewById(R.id.sendButton)

        Log.d("ChatRoomActivity", "MessageList: $messageList")


        val recyclerView: RecyclerView = findViewById(R.id.communityRecyclerView)
        val adapter = ChatAdapter(messageList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        sendMessageButton.setOnClickListener {
            val messageContent = messageEditText.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val currTime = System.currentTimeMillis()
                val currentTime = SimpleDateFormat("HH:mm").format(Date(currTime))
                val lastMessage = messageList.lastOrNull()
                val newId = (lastMessage?.id?.toIntOrNull() ?: 0) + 1
                val newMessage = Message(newId.toString(), messageContent, currentTime
                    ,imageUrl = UserInstance.getInstance()?.profilePic ?: ""
                    , sentByCurrentUser = true)


                database.child(newId.toString()).setValue(newMessage)
                messageEditText.text.clear()
//                messageList.add(newMessage)
//                adapter.notifyItemInserted(messageList.size - 1)
//                recyclerView.scrollToPosition(messageList.size - 1)
                messageEditText.text.clear()
                val mentorResponse = when {
                    messageContent.contains("help", ignoreCase = true) -> "I'm here to help."
                    messageContent.contains("thank", ignoreCase = true) -> "Let me know if there's anything else."
                    messageContent.contains("happy", ignoreCase = true) -> "That's great to hear! "
                    messageContent.contains("sad", ignoreCase = true) -> "You can talk to me?"
                    messageContent.contains("stress", ignoreCase = true) -> "Stress can be tough."
                    else -> "Interesting. Tell me more."
                }

                // Mentor's response
                val mentorMessage = Message((newId + 1).toString(), mentorResponse, currentTime, imageUrl = personProfilePic, sentByCurrentUser = false)
                database.child((newId+1).toString()).setValue(mentorMessage)

            }
            else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messageList.add(it)
                    adapter.notifyItemInserted(messageList.size - 1)
                    recyclerView.scrollToPosition(messageList.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })


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

        val addMentorButton = findViewById<ImageView>(R.id.addMentorButton)
        addMentorButton.setOnClickListener {
            val intent = Intent(this, AddNewMentorActivity::class.java)
            startActivity(intent)
        }
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // call
        findViewById<ImageView>(R.id.CallImageView).setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.VideoCallImageView).setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            startActivity(intent)
        }
        // For gallery access
        findViewById<ImageView>(R.id.imageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivity(intent)
        }

        // For camera access
        findViewById<ImageView>(R.id.cameraButton).setOnClickListener {
            val intent = Intent(this,CameraActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.micButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            startActivity(intent)
        }


// For accessing files
        findViewById<ImageView>(R.id.attachmentButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // This will allow any type of file
            startActivity(intent)
        }


    }


}


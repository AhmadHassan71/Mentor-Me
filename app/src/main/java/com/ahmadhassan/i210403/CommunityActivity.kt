package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date

class CommunityActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance().getReference("messages")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community)

        // get mentor from previous activity
        val mentor = intent.getSerializableExtra("mentor") as? Mentors

        val mentorname = findViewById<TextView>(R.id.MentorNameTextView)
        val mentorPFP = findViewById<ImageView>(R.id.mentorPFP)
        if (mentor != null) {
            "${mentor.name}'s".also { mentorname.text = it }
            if (mentor.profilePicture.isNotEmpty()){
                // it should fill the image view with the mentor's profile picture
                Picasso.get().load(mentor.profilePicture).into(mentorPFP)
            }
            // it should fill the image view with the mentor's profile picture
        }

        val messageList = mutableListOf<Message>()
        messageList.add(Message("1", "Hello!", "10:20", imageUrl = "https://t3.ftcdn.net/jpg/02/43/12/34/360_F_243123463_zTooub557xEWABDLk0jJklDyLSGl2jrr.jpg",false ))
        messageList.add(Message("2", "Hi there!", "11:15", imageUrl ="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQSQKaS7LP80SEcKgz9-d_ORjkh1B9hPSUqkeI_mLSnDg&s", sentByCurrentUser = false))
        messageList.add(Message("3", "How are you?", "11:20", imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRxk-u72gDf_LNwBEtKljf5_gCcMxXG5DROGHOrZ-xIYg&s", sentByCurrentUser = false))
        messageList.add(Message("4", "I'm fine, thanks!", "12:00", imageUrl ="https://img.freepik.com/free-photo/young-bearded-man-with-striped-shirt_273609-5677.jpg?size=626&ext=jpg&ga=GA1.1.735520172.1710547200&semt=sph" , sentByCurrentUser = false))
        messageList.add(Message("5", "Check out this image!", "13:25", imageUrl ="https://t4.ftcdn.net/jpg/03/83/25/83/360_F_383258331_D8imaEMl8Q3lf7EKU2Pi78Cn0R7KkW9o.jpg",false))

        val messageBox: View = findViewById(R.id.messageBox)
        val messageEditText: EditText = messageBox.findViewById(R.id.messageEditText)
        val sendMessageButton: ImageView = messageBox.findViewById(R.id.sendButton)

        val recyclerView: RecyclerView = findViewById(R.id.communityRecyclerView)
        val adapter = ChatAdapter(messageList,database )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        sendMessageButton.setOnClickListener {
            val messageContent = messageEditText.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val currTime = System.currentTimeMillis()
                val currentTime = SimpleDateFormat("HH:mm").format(Date(currTime))
                val lastMessage = messageList.lastOrNull()
                val newId = (lastMessage?.id?.toIntOrNull() ?: 0) + 1
                val newMessage = Message(newId.toString(), messageContent, currentTime,"", sentByCurrentUser = true)
                messageList.add(newMessage)
                adapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)
                messageEditText.text.clear()
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }


        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavView.selectedItemId = R.id.navigation_search

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
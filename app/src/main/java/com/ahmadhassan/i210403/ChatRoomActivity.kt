package com.ahmadhassan.i210403

import ChatAdapter
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatRoomActivity : AppCompatActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_room)

        val messageList = mutableListOf<Message>()
        messageList.add(Message("1", "Hello!", "10:20", imageUrl = R.drawable.paul_personpic))
        messageList.add(Message("2", "Hi there!", "11:15", imageUrl =R.drawable.paul_personpic, sentByCurrentUser = false))
        messageList.add(Message("3", "How are you?", "11:20", imageUrl = R.drawable.paul_personpic, sentByCurrentUser = false))
        messageList.add(Message("4", "I'm fine, thanks!", "12:00", imageUrl = R.drawable.paul_personpic, sentByCurrentUser = true))
        messageList.add(Message("5", "Check out this image!", "13:25", imageUrl =R.drawable.paul_personpic, sentByCurrentUser = true))

        val recyclerView: RecyclerView = findViewById(R.id.communityRecyclerView)

        val adapter = ChatAdapter(messageList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavView.selectedItemId = R.id.navigation_chat

        bottomNavView.setOnNavigationItemSelectedListener { item ->
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
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.CallImageView).setOnClickListener {
            val phoneNumber = "tel:+1234567890"
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
            startActivity(dialIntent)
        }

        findViewById<ImageView>(R.id.VideoCallImageView).setOnClickListener {
            val phoneNumber = "tel:+1234567890"
            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
            startActivity(dialIntent)
        }
        // For gallery access
        findViewById<ImageView>(R.id.imageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivity(intent)
        }

// For camera access
        findViewById<ImageView>(R.id.cameraButton).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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

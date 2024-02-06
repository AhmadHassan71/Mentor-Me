package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class AddNewMentorActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_mentor)


        val spinner: Spinner = findViewById(R.id.availabilityEditText)

        val items = arrayOf("Available", "Not Available")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter


        // back
        val backButton = findViewById<android.widget.ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        // bottom navigation view
        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        bottomNavView.setOnNavigationItemSelectedListener { item ->
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

        findViewById<ConstraintLayout>(R.id.uploadVideoLaout).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.videoImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.uploadViewTextView).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivity(intent)
        }

        findViewById<ConstraintLayout>(R.id.uploadImageLayout).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.cameraImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.cameraTextView).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivity(intent)
        }

        findViewById<Button>(R.id.uploadButton).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }



    }
}
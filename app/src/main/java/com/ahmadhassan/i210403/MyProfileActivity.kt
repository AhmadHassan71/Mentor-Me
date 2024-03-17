package com.ahmadhassan.i210403

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class MyProfileActivity: AppCompatActivity() {

    private lateinit var pfpURL: String
    private lateinit var coverURL: String
    private var isPfp: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_profile)

        // set the profile picture and cover picture
        val profilePicture = findViewById<ImageView>(R.id.profilePicture)
        val coverPicture = findViewById<ImageView>(R.id.coverPicture)

        val userIdSharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
        val userId = userIdSharedPreferences.getString("userId", null)
        UserInstance.fetchUser(userId ?: "") { user ->
            if (user != null) {
                // User data fetched successfully
                val profileUrl = user.profilePic
                val coverUrl = user.bannerPic

                // Load profile picture using Picasso
                if (profileUrl.isNotEmpty())
                    Picasso.get().load(profileUrl).into(profilePicture)
                // Load cover picture using Picasso
                if (coverUrl.isNotEmpty())
                    Picasso.get().load(coverUrl).into(coverPicture)

                val nameTextView = findViewById<TextView>(R.id.myNameTextView)
                nameTextView.text = user.fullName
                val locationTextView = findViewById<TextView>(R.id.locationTextView)
                locationTextView.text = user.city
            } else {
                // Handle case where user data couldn't be fetched
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
        }


        // back button
        val backButton = findViewById<android.widget.ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val addMentorButton = findViewById<ImageView>(R.id.addMentorButton)
        addMentorButton.setOnClickListener {
            val intent = Intent(this, AddNewMentorActivity::class.java)
            startActivity(intent)
        }

        // bottom navigation view
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

                else -> false
            }
        }

        val recyclerViewTopMentor: RecyclerView = findViewById(R.id.favMentorsRecyclerView)
        recyclerViewTopMentor.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val mentorsList = listOf(
            Mentors("","John Doe", "Software Engineer", "$50/hr", "Available", "Favorite", "","Hard Worker",""),
            Mentors("","Jane Smith", "Data Scientist", "$60/hr", "Unavailable", "Favorite", "","",""),
            Mentors("","Jack Son", "Software Engineer", "$50/hr", "Available", "Favorite", "","",""),
        )
        val adapter = CardAdapter(mentorsList, this)
        recyclerViewTopMentor.adapter = adapter


        val recyclerView: RecyclerView = findViewById(R.id.reviewRecycleView)
        recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val reviews = listOf(
            Review("Mentor 1", 4.5f, "Great mentor! taught me everything I know."),
            Review("Mentor 2", 3.0f, "Average mentor. Could be better."),
            Review("Mentor 3", 5.0f, "Best mentor ever!"),
            Review("Mentor 4", 2.5f, "Not a good mentor. Waste of time."),

            )
        val adapter2 = ReviewAdapter(reviews)
        recyclerView.adapter = adapter2


        // optionsImageView opens EditProfileActivity
        val optionsImageView = findViewById<ImageView>(R.id.optionsImageView)
        optionsImageView.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        // editProfileButton opens EditProfileActivity
        val editProfileButton = findViewById<ImageView>(R.id.editProfilePicture)
        editProfileButton.setOnClickListener {
            isPfp = true
            uploadImage()
        }
        val editCoverButton = findViewById<ImageView>(R.id.editCoverPicture)
        editCoverButton.setOnClickListener {
            isPfp = false
            uploadImage()
        }

        // bookedSessionsButton opens BookedSessionActivity
        val bookedSessionsButton = findViewById<Button>(R.id.bookedSessionsButton)
        bookedSessionsButton.setOnClickListener {
            val intent = Intent(this, BookedSessionActivity::class.java)
            startActivity(intent)
        }

        adapter.setOnItemClickListener {
            val intent = Intent(this@MyProfileActivity, MentorProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun uploadPfPToFirebase(imageUri: Uri) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef = storageRef.child("profilepics").child("Users").child("profilepics")
            .child(imageUri.toString())

        imageUri.let {
            val uploadTask = imagesRef.putFile(it)

            uploadTask.addOnSuccessListener {
                // Image uploaded successfully
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    pfpURL = uri.toString()
                    Toast.makeText(this, "Image has been saved", Toast.LENGTH_SHORT).show()

                    // update the profile picture in the database
                    updateProfilePicture(pfpURL)
                }.addOnFailureListener {
                    // Handle failure to get download URL
                }
            }.addOnFailureListener { exception ->
                // Handle unsuccessful upload
                Toast.makeText(
                    this,
                    "Failed to upload image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } ?: run {
            Toast.makeText(this, "Invalid image URI", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateProfilePicture(pfpURL: String) {
        val userIdSharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
        val userId = userIdSharedPreferences.getString("userId", null)
        if (userId != null) {
            var updatedUser: User? = User()
            UserInstance.fetchUser(userId){ user ->
                updatedUser = user?.let {
                    User(
                        it.userId,
                        user.email,
                        user.fullName,
                        user.city,
                        user.country,
                        pfpURL,
                        user.bannerPic
                    )
                }
            }
            updatedUser?.let {
                UserInstance.updateUser(it) { success ->
                    if (success) {
                        // Update successful
                        Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()
                    } else {
                        // Update failed
                        Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun uploadCoverToFirebase(imageUri: Uri) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef = storageRef.child("profilepics").child("Users").child("coverpics")
            .child(imageUri.toString())

        imageUri.let {
            val uploadTask = imagesRef.putFile(it)

            uploadTask.addOnSuccessListener {
                // Image uploaded successfully
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    coverURL = uri.toString()
                    Toast.makeText(this, "Image has been saved", Toast.LENGTH_SHORT).show()
                    updateBannerPicture(coverURL)
                }.addOnFailureListener {
                    // Handle failure to get download URL
                }
            }.addOnFailureListener { exception ->
                // Handle unsuccessful upload
                Toast.makeText(
                    this,
                    "Failed to upload image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } ?: run {
            Toast.makeText(this, "Invalid image URI", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBannerPicture(coverURL: String) {
        // update the profile picture in the database
        val userIdSharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
        val userId = userIdSharedPreferences.getString("userId", null)
        if (userId != null) {
            var updatedUser: User? = User()
            UserInstance.fetchUser(userId) { user ->
                updatedUser = user?.let {
                    User(
                        it.userId,
                        user.email,
                        user.fullName,
                        user.city,
                        user.country,
                        user.profilePic,
                        coverURL
                    )
                }
            }
            updatedUser?.let {
                UserInstance.updateUser(it) { success ->
                    if (success) {
                        // Update successful
                        Toast.makeText(this, "Cover photo updated", Toast.LENGTH_SHORT).show()
                    } else {
                        // Update failed
                        Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    }


    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (isPfp) {
                uploadPfPToFirebase(uri)
                findViewById<ImageView>(R.id.profilePicture).setImageURI(uri)
            } else {
                uploadCoverToFirebase(uri)
                findViewById<ImageView>(R.id.coverPicture).setImageURI(uri)
            }
        } ?: run {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage(){
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch("image/*")
    }
}
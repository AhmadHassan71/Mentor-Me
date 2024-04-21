package com.ahmadhassan.i210403

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ahmadhassan.i210403.UserInstance.getInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Objects

class MyProfileActivity: AppCompatActivity() {

    private lateinit var pfpURL: String
    private lateinit var coverURL: String
    private var isPfp: Boolean = false
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Reviews")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_profile)

        // set the profile picture and cover picture
        val profilePicture = findViewById<ImageView>(R.id.profilePicture)
        val coverPicture = findViewById<ImageView>(R.id.coverPicture)

        val userIdSharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
        val userId = userIdSharedPreferences.getString("userId", null)
        val user = getInstance()

                // User data fetched successfully
                val profileUrl = user?.profilePic
                val coverUrl = user?.bannerPic

                // Load profile picture using Picasso
                if (profileUrl!!.isNotEmpty())
                    Picasso.get().load(profileUrl).into(profilePicture)
                // Load cover picture using Picasso
                if (coverUrl!!.isNotEmpty())
                    Picasso.get().load(coverUrl).into(coverPicture)

                val nameTextView = findViewById<TextView>(R.id.myNameTextView)
                nameTextView.text = user.fullName
                val locationTextView = findViewById<TextView>(R.id.locationTextView)
                locationTextView.text = user.city




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
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Favorite")
        val mentorsList = mutableListOf<Mentors>()
        val favList = mutableListOf<Favorite>()
        ref.get().addOnSuccessListener { dataSnapshot ->
            for (favSnapshot in dataSnapshot.children) {
                val favData = favSnapshot.getValue(Favorite::class.java)
                favData?.let { favList.add(it) }
            }
            for (fav in favList){
                if(fav.userId == getInstance()!!.userId) {
                    val mentorRef = database.getReference("Mentors").child(fav.mentorId)
                    mentorRef.get().addOnSuccessListener { mentorSnapshot ->
                        val mentorData = mentorSnapshot.getValue(Mentors::class.java)
                        mentorData?.let { mentorsList.add(it) }
                    }
                }
            }
            val adapter = CardAdapter(mentorsList, this)
            recyclerViewTopMentor.adapter = adapter
            adapter.setOnItemClickListener { mentor ->
                val intent = Intent(this@MyProfileActivity, MentorProfileActivity::class.java)
                intent.putExtra("mentor", mentor) // Pass mentor object
                startActivity(intent)
            }
        }.addOnFailureListener { exception ->
            // Handle failure
            Toast.makeText(this, "Error getting data: ${exception.message}", Toast.LENGTH_SHORT).show()
        }






        val recyclerView: RecyclerView = findViewById(R.id.reviewRecycleView)
        recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val reviews:MutableList<Review> = mutableListOf()
        databaseReference.get().addOnSuccessListener {
            val snapshots = it.children
            for (review in snapshots){
                val reviewObj = review.getValue(Review::class.java)
                if (reviewObj != null){
                    if (reviewObj.userId == userId){
                        reviews.add(reviewObj)
                    }
                }
            }
            val adapter2 = ReviewAdapter(reviews)
            recyclerView.adapter = adapter2

        }
//            listOf(
//            Review("Mentor 1", 4.5f, "Great mentor! taught me everything I know."),
//            Review("Mentor 2", 3.0f, "Average mentor. Could be better."),
//            Review("Mentor 3", 5.0f, "Best mentor ever!"),
//            Review("Mentor 4", 2.5f, "Not a good mentor. Waste of time."),
//
//            )



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
            UserInstance.updateUser(this, User(userId,
                getInstance()!!.email, getInstance()?.fullName,
                getInstance()!!.city, getInstance()!!.country, pfpURL, getInstance()!!.bannerPic,
                getInstance()!!.fcmToken )) { success ->
                if (success) {
                    // Update successful
                    Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show()
                } else {
                    // Update failed
                    Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT)
                        .show()
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
            UserInstance.fetchUser(this,userId) { user ->
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
                UserInstance.updateUser(this,it) { success ->
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
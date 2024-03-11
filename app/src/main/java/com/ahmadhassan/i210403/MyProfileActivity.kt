package com.ahmadhassan.i210403

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

class MyProfileActivity: AppCompatActivity() {

    private lateinit var pfpURL:String
    private lateinit var coverURL:String
    private var isPfp : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_profile)

        // set the profile picture and cover picture
        val profilePicture = findViewById<ImageView>(R.id.profilePicture)
        val coverPicture = findViewById<ImageView>(R.id.coverPicture)
        val user = FirebaseAuth.getInstance().currentUser
        val profile = user?.photoUrl
        val cover = user?.photoUrl
        if (profile != null) {
            profilePicture.setImageURI(profile)
        }
        if (cover != null) {
            coverPicture.setImageURI(cover)
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
            Mentors("John Doe", "Software Engineer", "$50/hr", "Available","Favorite",""),
            Mentors("Jane Smith", "Data Scientist", "$60/hr", "Unavailable","Favorite",""),
            Mentors("Jack Son", "Software Engineer", "$50/hr", "Available","Favorite",""),
        )
        val adapter = CardAdapter(mentorsList, this)
        recyclerViewTopMentor.adapter = adapter


        val recyclerView: RecyclerView = findViewById(R.id.reviewRecycleView)
        recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val reviews = listOf(
            Review( "Mentor 1", 4.5f, "Great mentor! taught me everything I know."),
            Review( "Mentor 2", 3.0f, "Average mentor. Could be better."),
            Review( "Mentor 3", 5.0f, "Best mentor ever!"),
            Review( "Mentor 4", 2.5f, "Not a good mentor. Waste of time."),

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
        val imagesRef = storageRef.child("profilepics").child("Users").child("profilepics").child(imageUri.toString())

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
                Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Invalid image URI", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateProfilePicture(pfpURL: String): Uri? {
        // update the profile picture in the database


        return null
    }

    private fun uploadCoverToFirebase(imageUri: Uri) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef = storageRef.child("profilepics").child("Users").child("coverpics").child(imageUri.toString())

        imageUri.let {
            val uploadTask = imagesRef.putFile(it)

            uploadTask.addOnSuccessListener {
                // Image uploaded successfully
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    coverURL = uri.toString()
                    Toast.makeText(this, "Image has been saved", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    // Handle failure to get download URL
                }
            }.addOnFailureListener { exception ->
                // Handle unsuccessful upload
                Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Invalid image URI", Toast.LENGTH_SHORT).show()
        }
    }
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                // Pick and show the image on the screen in the constraint layout
                if(isPfp){
                    uploadPfPToFirebase(uri)
                    findViewById<ImageView>(R.id.profilePicture).setImageURI(uri)
//                    Toast.makeText(this, "Profile Picture has been saved", Toast.LENGTH_SHORT).show()


                }
                else{
                    uploadCoverToFirebase(uri)
                    findViewById<ImageView>(R.id.coverPicture).setImageURI(uri)
//                    Toast.makeText(this, "Cover Picture has been saved", Toast.LENGTH_SHORT).show()
                }

            } ?: run {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Failed to pick image", Toast.LENGTH_SHORT).show()
        }
    }
    private fun uploadImage(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }
}
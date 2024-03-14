package com.ahmadhassan.i210403

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddNewMentorActivity: AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var pfpURL:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_mentor)

        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference


        val spinner: Spinner = findViewById(R.id.availabilityEditText)

        val items = arrayOf("Available", "Not Available")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter


        // back
        val backButton = findViewById<android.widget.ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
                R.id.navigation_profile -> {
                    // highlight the my profile icon
                    startActivity(Intent(this, MyProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        findViewById<ConstraintLayout>(R.id.uploadVideoLayout).setOnClickListener {
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
            uploadImage()
        }
        findViewById<ImageView>(R.id.cameraImage).setOnClickListener {
            uploadImage()
        }
        findViewById<TextView>(R.id.cameraTextView).setOnClickListener {
            uploadImage()
        }

//        findViewById<Button>(R.id.uploadButton).setOnClickListener {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//        }

        findViewById<Button>(R.id.uploadButton).setOnClickListener {
            if (validateInputs()) { // Add a validation function
                val mentor = createMentorObject()
                storeMentorData(mentor)  // Implement based on your storage choice

                // After successful storage
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
            }
            else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun uploadToFirebase(imageUri: Uri) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef = storageRef.child("profilepics").child("Mentors").child(imageUri.toString())

        imageUri.let {
            val uploadTask = imagesRef.putFile(it)

            uploadTask.addOnSuccessListener {
                // Image uploaded successfully
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    pfpURL = uri.toString()
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
    private val pickImage =  registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
                    // Pick and show the image on the screen in the constraint layout
                    uploadToFirebase(uri)
                }
            ?: run {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
    }
    private fun uploadImage(){
        pickImage.launch("image/*")
    }
    private fun validateInputs(): Boolean {
        // name validation
        val name = findViewById<EditText>(R.id.NameEditText)
        if (name.text.toString().isEmpty()) {
            name.error = "Name is required"
            return false
        }
        // job title validation
        val jobTitle = findViewById<EditText>(R.id.descriptionEditText)
        if (jobTitle.text.toString().isEmpty()) {
            jobTitle.error = "Job title is required"
            return false
        }
        //availability validation
        val availability = findViewById<Spinner>(R.id.availabilityEditText)
        if (availability.selectedItem.toString().isEmpty()) {
            Toast.makeText(this, "Availability is required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true // Replace with actual validation
    }

    private fun createMentorObject(): Mentors {
        val name = findViewById<EditText>(R.id.NameEditText).text.toString()
        val jobTitle = findViewById<EditText>(R.id.jobTitleEditText).text.toString()
        val _rate = findViewById<EditText>(R.id.priceEditText).text.toString()
        // add $/hr at the end of _rate
        val rate = "$$_rate/hr"
        val availability = findViewById<Spinner>(R.id.availabilityEditText).selectedItem.toString()
        val profilePicture = pfpURL
        val isFavorite = false // Set to true if mentor is a favorite
        val description = findViewById<EditText>(R.id.descriptionEditText).text.toString()
        val company = findViewById<EditText>(R.id.employerEditText).text.toString()

        return Mentors(name, jobTitle, rate, availability, if (isFavorite) "Favorite" else "Not Favorite",profilePicture,description,company)
    }

    private fun storeMentorData(mentor: Mentors) {
        val newMentorKey = databaseRef.push().key // Create a unique key for the mentor

        if (newMentorKey != null) {

            databaseRef.child("Mentors").child(newMentorKey).setValue(mentor)
                .addOnSuccessListener {
                    Toast.makeText(this, "Mentor added successfully", Toast.LENGTH_SHORT).show()
                    // empty the fields
                    findViewById<EditText>(R.id.NameEditText).setText("")
                    findViewById<EditText>(R.id.descriptionEditText).setText("")
                    findViewById<EditText>(R.id.jobTitleEditText).setText("")
                    findViewById<EditText>(R.id.priceEditText).setText("")
                    findViewById<EditText>(R.id.employerEditText).setText("")

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add mentor", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error creating mentor key", Toast.LENGTH_SHORT).show()
        }
    }
}
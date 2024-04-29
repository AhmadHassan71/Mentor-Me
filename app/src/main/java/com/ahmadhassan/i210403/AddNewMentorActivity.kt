package com.ahmadhassan.i210403

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddNewMentorActivity: AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var pfpURL:String
    private var selectedImage: String? = null
    @RequiresApi(Build.VERSION_CODES.P)
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
//                val mentorId = databaseRef.child("Mentors").push().key // Generate a unique mentor ID
//                mentorId?.let {
//                    storeMentorData(mentor, mentorId)  // Pass mentor ID along with mentor object
//                } ?: run {
//                    Toast.makeText(this, "Error creating mentor ID", Toast.LENGTH_SHORT).show()
//                }

                createMentorAccount()
//                val mentor = createMentorObject(mentorId)


                // After successful storage
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun createMentorAccount(){
        val mentor = createMentorObject("1");
        val url = "http://${DatabaseIP.IP}/registermentor.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                // Handle response from the server
                Toast.makeText(this, "Mentor Added Successfully", Toast.LENGTH_SHORT).show()
                Log.d("RegisterMentorActivity", "Response: $response")
                if (response.contains("Mentor registered successfully")) {
                    // Registration successful
                    // Optionally handle further actions after successful registration
                    val mentorId = response.split(":")[1].trim()
                    // Use mentorId as needed
                    findViewById<EditText>(R.id.NameEditText).setText("")
                    findViewById<EditText>(R.id.descriptionEditText).setText("")
                    findViewById<EditText>(R.id.jobTitleEditText).setText("")
                    findViewById<EditText>(R.id.priceEditText).setText("")
                    findViewById<EditText>(R.id.employerEditText).setText("")
                    selectedImage = null
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                // Optionally display an error message
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["name"] = mentor.name
                params["jobTitle"] = mentor.jobTitle
                params["rate"] = mentor.rate
                params["availability"] = mentor.availability
                params["favorite"] = mentor.favorite
                params["description"] = mentor.description
                params["company"] = mentor.company
                params["profilePicture"] = selectedImage ?: ""
                return params
            }
        }

        requestQueue.add(stringRequest)


    }


    private fun encodeImage(bitmap: Bitmap?): String? {
        bitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

            // Log image size and other properties for debugging
            Log.d("ImageInfo", "Image Size: ${byteArrayOutputStream.size()}")
            Log.d("ImageInfo", "Encoded Image Len gth: ${encodedImage.length}")

            return encodedImage
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            data?.let {
                val imageUri = it.data
                try {
                    val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                    val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.setTargetSampleSize(1) // shrinking by
                        decoder.isMutableRequired = true // this resolve the hardware type of bitmap problem
                    }

                    selectedImage = encodeImage(bitmap)



                    Log.d("ImageURI", imageUri.toString())

//                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                    imageView.setImageBitmap(bitmap)
                    Log.d("ImageInfo", "Selected Image: $selectedImage")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private fun uploadImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImage.launch(intent)
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
        if(selectedImage == null){
            Toast.makeText(this, "Profile Picture is required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true // Replace with actual validation
    }

    private fun createMentorObject(mentorId:String): Mentors {

        val name = findViewById<EditText>(R.id.NameEditText).text.toString()
        val jobTitle = findViewById<EditText>(R.id.jobTitleEditText).text.toString()
        val _rate = findViewById<EditText>(R.id.priceEditText).text.toString()
        // add $/hr at the end of _rate
        val rate = "$$_rate/hr"
        val availability = findViewById<Spinner>(R.id.availabilityEditText).selectedItem.toString()
        val profilePicture = selectedImage ?: "" // Set to the image URL
        val isFavorite = false // Set to true if mentor is a favorite
        val description = findViewById<EditText>(R.id.descriptionEditText).text.toString()
        val company = findViewById<EditText>(R.id.employerEditText).text.toString()

        return Mentors(mentorId,name, jobTitle, rate, availability, if (isFavorite) "Favorite" else "Not Favorite",profilePicture,description,company)
    }

    private fun storeMentorData(mentor: Mentors, newMentorKey: String?) {

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
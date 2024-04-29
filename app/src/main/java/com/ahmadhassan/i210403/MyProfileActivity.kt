package com.ahmadhassan.i210403

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmadhassan.i210403.UserInstance.getInstance
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Objects

class MyProfileActivity: AppCompatActivity() {

    private lateinit var pfpURL: String
    private lateinit var coverURL: String
    private var isPfp: Boolean = false
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Reviews")
    private var selectedImage: String? = null // Variable to store base64 encoded image

    @RequiresApi(Build.VERSION_CODES.P)
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
                val profileUrl = "http://${DatabaseIP.IP}/UserPfPs/${user?.profilePic}"
                val coverUrl ="http://${DatabaseIP.IP}/UserCovers/${user?.bannerPic}"

                // Load profile picture using Picasso
                if (profileUrl!!.isNotEmpty())
                    Picasso.get().load(profileUrl).into(profilePicture)
                // Load cover picture using Picasso
                if (coverUrl!!.isNotEmpty())
                    Picasso.get().load(coverUrl).into(coverPicture)

                val nameTextView = findViewById<TextView>(R.id.myNameTextView)
                nameTextView.text = user!!.fullName
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
        fetchReviews(getInstance()!!.userId.toInt())

//        val recyclerViewTopMentor: RecyclerView = findViewById(R.id.favMentorsRecyclerView)
//        recyclerViewTopMentor.layoutManager =
//            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
//        val database = FirebaseDatabase.getInstance()
//        val ref = database.getReference("Favorite")
//        val mentorsList = mutableListOf<Mentors>()
//        val favList = mutableListOf<Favorite>()
//        ref.get().addOnSuccessListener { dataSnapshot ->
//            for (favSnapshot in dataSnapshot.children) {
//                val favData = favSnapshot.getValue(Favorite::class.java)
//                favData?.let { favList.add(it) }
//            }
//            for (fav in favList){
//                if(fav.userId == getInstance()!!.userId) {
//                    val mentorRef = database.getReference("Mentors").child(fav.mentorId)
//                    mentorRef.get().addOnSuccessListener { mentorSnapshot ->
//                        val mentorData = mentorSnapshot.getValue(Mentors::class.java)
//                        mentorData?.let { mentorsList.add(it) }
//                    }
//                }
//            }
//            val adapter = CardAdapter(mentorsList, this)
//            recyclerViewTopMentor.adapter = adapter
//            adapter.setOnItemClickListener { mentor ->
//                val intent = Intent(this@MyProfileActivity, MentorProfileActivity::class.java)
//                intent.putExtra("mentor", mentor) // Pass mentor object
//                startActivity(intent)
//            }
//        }.addOnFailureListener { exception ->
//            // Handle failure
//            Toast.makeText(this, "Error getting data: ${exception.message}", Toast.LENGTH_SHORT).show()
//        }




        fetchFavoriteMentors()


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

    private fun fetchFavoriteMentors() {
        val url = "http://${DatabaseIP.IP}/getfavorites.php" // Replace with your actual URL
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.POST, // Assuming getfavorites.php expects a POST request
            url,
            { response ->
                val mentors = mutableListOf<Mentors>()
                try {
                    val jsonArray = JSONArray(response) // Parse the response as JSON array

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val mentor = Mentors(
                            // Replace with actual field names from your database
                            jsonObject.getString("mentorId"),
                            jsonObject.getString("name"),
                            jsonObject.getString("jobTitle"),
                            jsonObject.getString("rate"),
                            jsonObject.getString("availability"),
                            "Favorite",
                            jsonObject.getString("profilePicture"),
                            jsonObject.getString("description"),
                            jsonObject.getString("company"),

                        )
//                        Log.d("Mentorhere", mentor.mentorId)
//                        Log.d("Mentorhere", mentor.name)
//                        Log.d("Mentorhere", mentor.jobTitle)
//                        Log.d("Mentorhere", mentor.rate)
//                        Log.d("Mentorhere", mentor.availability)
//                        Log.d("Mentorhere", mentor.favorite)
//                        Log.d("Mentorhere", mentor.profilePicture)
//                        Log.d("Mentorhere", mentor.description)
//                        Log.d("Mentorhere", mentor.company)
                        mentors.add(mentor)
                    }
                    val recyclerViewTopMentor: RecyclerView = findViewById(R.id.favMentorsRecyclerView)
                    recyclerViewTopMentor.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                    val adapter = CardAdapter(mentors, this)
                    recyclerViewTopMentor.adapter = adapter
                    adapter.setOnItemClickListener { mentor ->
                        val intent = Intent(this, MentorProfileActivity::class.java)
                        intent.putExtra("mentor", mentor) // Pass mentor object
                        startActivity(intent)
                    }
                // Handle the retrieved list of favorite mentors (e.g., update UI)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle parsing error (e.g., show a toast message)
//                    Toast.makeText(this, "Error fetching mentors: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Handle network error
//                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(stringRequest)
    }
    private fun fetchReviews(userId: Int) {
        val url = "http://${DatabaseIP.IP}/getreviews.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                val reviewsList = mutableListOf<Review>()
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        // Replace with actual key names from your PHP response
                        val mentorName = jsonObject.getString("mentorName")
                        val rating = jsonObject.getDouble("rating").toFloat()
                        val reviewText = jsonObject.getString("reviewDescription")
                        val mentorId = jsonObject.getString("mentorId")

                        val review = Review(
                            mentorName,
                            rating,
                            reviewText,
                            // Adjust user ID retrieval based on your implementation
                            getInstance()!!.userId.toString(), // Assuming userId is accessible within the class
                            mentorId
                        )
                        reviewsList.add(review)
                    }

                    val recyclerView: RecyclerView = findViewById(R.id.reviewRecycleView)
                    recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                    val adapter = ReviewAdapter(reviewsList)
                    recyclerView.adapter = adapter
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userId"] = userId.toString()
                return params
            }
        }

        requestQueue.add(stringRequest)
    }


//    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        uri?.let {
//            if (isPfp) {
//                uploadPfPToFirebase(uri)
//                findViewById<ImageView>(R.id.profilePicture).setImageURI(uri)
//            } else {
//                uploadCoverToFirebase(uri)
//                findViewById<ImageView>(R.id.coverPicture).setImageURI(uri)
//            }
//        } ?: run {
//            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
//        }
//    }

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

                    if (isPfp) {
//                        uploadPfPToFirebase(uri)
                        uploadPfP(selectedImage)
                        findViewById<ImageView>(R.id.profilePicture).setImageBitmap(bitmap)
                    } else {
//                        uploadCoverToFirebase(uri)
                        uploadCoverPhoto(selectedImage)

//                        findViewById<ImageView>(R.id.coverPicture).setImageURI(uri)
                        findViewById<ImageView>(R.id.coverPicture).setImageBitmap(bitmap)

                    }

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

    private fun uploadCoverPhoto(selectedImage: String?) {
        val url ="http://${DatabaseIP.IP}/uploadUserCover.php"

        selectedImage?.let { uri ->
            try {
//                val requestBody = "rollno=$roll&name=$name&image=$uri"

                val stringRequest = object : StringRequest(
                    Method.POST, url,
                    Response.Listener { response ->
                        // Handle successful response
                        Log.d("API Response", response)
                        Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        // Optionally, you can refresh the data after successful upload
//                        get()
                    },
                    Response.ErrorListener { error ->
                        // Handle error
                        Toast.makeText(this, "Upload Failure", Toast.LENGTH_SHORT).show();
                        Log.e("API Error", "Error occurred: ${error.message}")
                    }) {
                    //                    override fun getBody(): ByteArray {
//                        return requestBody.toByteArray(Charset.defaultCharset())
//                    }
//
//                    override fun getBodyContentType(): String {
//                        return "application/x-www-form-urlencoded; charset=UTF-8"
//                    }
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["userid"] = getInstance()!!.userId
                        params["image"] = uri
                        return params
                    }
                }

                Volley.newRequestQueue(this).add(stringRequest)

            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error
                Toast.makeText(this, "Error reading image", Toast.LENGTH_SHORT).show();
                Log.e("Image Upload Error", "Error reading image: ${e.message}")
            }
        } ?: run {
            // If selected image URI is null
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        if (selectedImage != null) {
            getInstance()!!.bannerPic=selectedImage
        }

    }

    private fun uploadPfP(selectedImage: String?) {
        val url ="http://${DatabaseIP.IP}/uploadUserPfP.php"

        selectedImage?.let { uri ->
            try {
//                val requestBody = "rollno=$roll&name=$name&image=$uri"

                val stringRequest = object : StringRequest(
                    Method.POST, url,
                    Response.Listener { response ->
                        // Handle successful response
                        Log.d("API Response", response)
                        UserInstance.fetchUser(this, getInstance()!!.userId){
                            user ->
                            if (user != null){
                                getInstance()!!.profilePic = user.profilePic
                            }
                        }
                        Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        // Optionally, you can refresh the data after successful upload
//                        get()
                    },
                    Response.ErrorListener { error ->
                        // Handle error
                        Toast.makeText(this, "Upload Failure", Toast.LENGTH_SHORT).show();
                        Log.e("API Error", "Error occurred: ${error.message}")
                    }) {
                    //                    override fun getBody(): ByteArray {
//                        return requestBody.toByteArray(Charset.defaultCharset())
//                    }
//
//                    override fun getBodyContentType(): String {
//                        return "application/x-www-form-urlencoded; charset=UTF-8"
//                    }
                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["userid"] = getInstance()!!.userId
                        params["image"] = uri
                        return params
                    }
                }

                Volley.newRequestQueue(this).add(stringRequest)

            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error
                Toast.makeText(this, "Error reading image", Toast.LENGTH_SHORT).show();
                Log.e("Image Upload Error", "Error reading image: ${e.message}")
            }
        } ?: run {
            // If selected image URI is null
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        if (selectedImage != null) {
            getInstance()!!.profilePic=selectedImage
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun uploadImage(){
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImage.launch(intent)
    }
}



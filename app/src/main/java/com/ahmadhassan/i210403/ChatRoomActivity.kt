package com.ahmadhassan.i210403


import android.media.MediaRecorder

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import android.Manifest
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.akexorcist.screenshotdetection.ScreenshotDetectionDelegate
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.io.ByteArrayOutputStream


class ChatRoomActivity : AppCompatActivity(),ScreenshotDetectionDelegate.ScreenshotDetectionListener {
    private val screenshotDetectionDelegate = ScreenshotDetectionDelegate(this, this)
    private lateinit var adapter: ChatAdapter
    private lateinit var messageEditText: EditText
    private lateinit var sendMessageButton: ImageView
    private var mediaRecorder: MediaRecorder ?= null
    private lateinit var audioPath: String
    private lateinit var audioFile: File
    private var isRecording = false
    private var messageList: MutableList<Message> = mutableListOf()
    private lateinit var recyclerView: RecyclerView




    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_room)

        checkReadExternalStoragePermission()
//
//        Log.d("ChatRoomActivity", "MessageImageUri: $messageImageUri")

        // get intent from previous activity
        val intent = intent
//        val personProfile: PersonProfile? = intent.getParcelableExtra("personProfile")
//        val personProfile: PersonProfile? = intent.getParcelableExtra("personProfile", PersonProfile::class.java)
        val chatRoom: ChatRoom = ChatRoomInstance.getInstance()
        Log.d("ChatRoomActivity", "ChatRoom: $chatRoom")



        val personName = findViewById<TextView>(R.id.MentorNameTextView)
        personName.text = MentorChatInstance.getInstance().name
        val personProfilePic = MentorChatInstance.getInstance().profilePicture



        // Message Service
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


         recyclerView= findViewById(R.id.communityRecyclerView)
//        val adapter = ChatAdapter(messageList)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this)

        messageList = chatRoom.messages.toMutableList()

        adapter  = ChatAdapter(messageList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.scrollToPosition(messageList.size - 1)

        sendMessageButton.setOnClickListener {

            val messageImageUri = CameraImageObject.getImageUri()
            Log.d("ChatRoomActivity", "MessageImageUri: $messageImageUri")
            val messageContent = messageEditText.text.toString().trim()


            if (messageImageUri.isNotEmpty()) {
                //upload image to firebase
                // upload message image uri to storage and get the uri
                Log.d("ChatRoomActivity", "ImageUri: $messageImageUri")
                var imageUri = ""
               uploadImageToServer(Uri.parse(messageImageUri))
                CameraImageObject.resetInstance()
                SendNotification(Message("1", "Image", "10:20", imageUrl = imageUri, sentByCurrentUser = true))


                return@setOnClickListener
            } else {


                if (messageContent.isNotEmpty()) {
                    val currTime = System.currentTimeMillis()
                    val currentTime = SimpleDateFormat("HH:mm").format(Date(currTime))
                    val lastMessage = messageList.lastOrNull()
                    val newId = (lastMessage?.id?.toIntOrNull() ?: 0) + 1
                    val newMessage = Message(
                        newId.toString(),
                        messageContent,
                        currentTime,
                        imageUrl = "",
                        sentByCurrentUser = true,
                        audioMessage = false
                    )


//                    database.child(newId.toString()).setValue(newMessage)
                    sendMessageToServer(newMessage,"")
                    SendNotification(newMessage)
                    messageList.add(newMessage)
                    messageEditText.text.clear()
//                messageList.add(newMessage)
                adapter.notifyItemInserted(messageList.size - 1)
                    recyclerView.scrollToPosition(messageList.size - 1)
//                recyclerView.scrollToPosition(messageList.size - 1)
                    messageEditText.text.clear()
                    val mentorResponse = when {
                        messageContent.contains("help", ignoreCase = true) -> "I'm here to help."
                        messageContent.contains(
                            "thank",
                            ignoreCase = true
                        ) -> "Let me know if there's anything else."

                        messageContent.contains(
                            "happy",
                            ignoreCase = true
                        ) -> "That's great to hear! "

                        messageContent.contains("sad", ignoreCase = true) -> "You can talk to me?"
                        messageContent.contains(
                            "stress",
                            ignoreCase = true
                        ) -> "Stress can be tough."

                        else -> "Interesting. Tell me more."
                    }
                    // Add delay in mentor's response

                    // Mentor's response
                    Handler(Looper.getMainLooper()).postDelayed({
                        val mentorMessage = Message(
                            (newId + 1).toString(),
                            mentorResponse,
                            currentTime,
                            imageUrl = personProfilePic,
                            sentByCurrentUser = false
                        )
                        messageList.add(mentorMessage)
                        SendNotification(mentorMessage)
                        adapter.notifyItemInserted(messageList.size - 1)
                    }, 5000)



                } else {
                    Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }





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
            val addMentorIntent = Intent(this, AddNewMentorActivity::class.java)
            startActivity(addMentorIntent)
        }
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            ChatRoomInstance.clearInstance()
            MentorChatInstance.clearInstance()
            onBackPressedDispatcher.onBackPressed()
        }

        // call
        findViewById<ImageView>(R.id.CallImageView).setOnClickListener {
            val callIntent = Intent(this, CallActivity::class.java)
            startActivity(callIntent)
        }

        findViewById<ImageView>(R.id.VideoCallImageView).setOnClickListener {
            val videoCallIntent = Intent(this, VideoCallActivity::class.java)
            startActivity(videoCallIntent)
        }
        // For gallery access
//        findViewById<ImageView>(R.id.imageButton).setOnClickListener {
//            pickImage.launch("image/*")
//
//        }
        findViewById<ImageView>(R.id.imageButton).setOnClickListener {
            pickImage.launch("image/*")
        }

        // For camera access
        findViewById<ImageView>(R.id.cameraButton).setOnClickListener {
            val cameraIntent = Intent(this,CameraActivity::class.java)
            cameraIntent.putExtra("chatRoom", chatRoom)
            startActivity(cameraIntent)
        }

        findViewById<ImageView>(R.id.micButton).setOnClickListener {
            Log.d("ChatRoomActivityD", "Mic button clicked")
            audioFile = File(
                externalMediaDirs.firstOrNull(),
                "${UserInstance.getInstance()!!.userId}.3gp"
            )
            audioPath = audioFile.absolutePath
            if (!isRecording) {
                // Check if permissions are granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("ChatRoomActivityD", "Requesting permissions")
                    // Request both RECORD_AUDIO and WRITE_EXTERNAL_STORAGE permissions
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_AUDIO),
                        10
                    )
                    startRecording()

                } else {
                    // Permission already granted, start recording
                    Log.d("ChatRoomActivityD", "Start recording")
                    startRecording()
                }
            } else {
                // Stop recording if it's already recording

                stopRecording()
            }
        }


// For accessing files
        findViewById<ImageView>(R.id.attachmentButton).setOnClickListener {
            val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // This will allow any type of file
            startActivity(fileIntent)
        }
        findViewById<ImageView>(R.id.VideoCallImageView).setOnClickListener{
            val videoCallIntent = Intent(this, VideoCallActivity::class.java)
            startActivity(videoCallIntent)
        }
        findViewById<ImageView>(R.id.CallImageView).setOnClickListener{
            val callIntent = Intent(this, CallActivity::class.java)
            startActivity(callIntent)
        }



    }




    private fun sendMessageToServer(messageContent: Message, imageUrl: String) {
        // Implement sending message to server using HTTP POST request
        // You can use Volley, Retrofit, or OkHttp to make the HTTP request
        // Example using Volley:
        val url = "http://${DatabaseIP.IP}/sendmessage.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                // Handle the response from the server
                // Assuming the server responds with success or failure message
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                if (response == "Message sent successfully") {
                    // Clear input fields after successful message sending
                    messageEditText.text.clear()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                error.printStackTrace()
//                Toast.makeText(this, "Failed to send message: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.d("ChatRoomActivity", "Failed to send message: ${error.message}")
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["text"] = messageContent.text
                params["mentorId"] = MentorChatInstance.getInstance().mentorId ?: ""
                params["sent_by_current_user"] = true.toString()
                params["timestamp"] = messageContent.timestamp
                params["image_url"] = imageUrl
                params["audio_message"] = messageContent.audioMessage.toString()
                params["userId"] = UserInstance.getInstance()?.userId ?: ""
                return params
            }
        }
        requestQueue.add(stringRequest)

    }


    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // You can handle the selected image URI here
            // Send the image URI to the server for uploading
            uploadImageToServer(uri)

        }
    }

    private fun uploadImageToServer(uri: Uri) {
        val url = "http://${DatabaseIP.IP}/uploadimage.php"

        // Get the user and mentor IDs
        val userId = UserInstance.getInstance()?.userId ?: ""
        val mentorId = MentorChatInstance.getInstance()?.mentorId ?: ""

        // Convert the image URI to a bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        // Convert the bitmap to a Base64 encoded string
        val encodedImage = encodeImage(bitmap)

        // Create a new Volley request queue
        val requestQueue = Volley.newRequestQueue(this)

        // Create a string request
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                // Handle the response from the server
                Log.d("UploadImage", "Response from server: $response")
                // Process the response as needed
                // get the part containing the path and filename.jpg from response

                messageList.add(Message((messageList.size-1).toString(), "", System.currentTimeMillis().toString(), imageUrl = response, sentByCurrentUser = true))
                adapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)
            },
            Response.ErrorListener { error ->
                // Handle errors that occur during the request
                Log.e("UploadImage", "Error uploading image: $error")
                // Display an error message or take appropriate action
            }) {

            // Override the getParams method to send POST parameters with the request
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userId"] = userId
                params["mentorId"] = mentorId
                params["imageData"] = encodedImage ?: ""
                params["audioMessage"] = false.toString()
                params["sentByCurrentUser"] = true.toString()
                params["timestamp"] = SimpleDateFormat("HH:mm").format(Date())
                params["text"] = "" // Set the text to empty for image messages
                return params
            }
        }

        // Add the string request to the request queue
        requestQueue.add(stringRequest)

    }

    // Helper function to encode the bitmap image to Base64 string
    private fun encodeImage(bitmap: Bitmap?): String? {
        bitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)

            // Log image size and other properties for debugging
            Log.d("ImageInfo", "Image Size: ${byteArrayOutputStream.size()}")
            Log.d("ImageInfo", "Encoded Image Length: ${encodedImage.length}")

            return encodedImage
        }
        return null
    }


    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this@ChatRoomActivity)
        } else MediaRecorder()
    }
    private fun startRecording() {
        mediaRecorder = createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            Log.d("ChatRoomActivityD", "AudioPath: $audioPath")
            setOutputFile(audioFile.absolutePath) // Use the absolute path directly
            try {
                prepare()
                start()
                isRecording = true
                mediaRecorder = this
                Log.d("ChatRoomActivityD", "Recording started")
                Toast.makeText(this@ChatRoomActivity, "Recording started", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this@ChatRoomActivity, "Failed to start recording: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ChatRoomActivityD", "Failed to start recording: ${e.message}")
            } catch (e: IllegalStateException) {
                Toast.makeText(this@ChatRoomActivity, "Failed to start recording: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun stopRecording() {
        Log.d("ChatRoomActivityD", "Stop recording")
        mediaRecorder?.stop()
        mediaRecorder?.reset()
        mediaRecorder = null
        isRecording = false
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()

        val audioFileUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", audioFile)

        // Get content resolver
        val contentResolver = applicationContext.contentResolver

        // Open an input stream for the content URI
        contentResolver.openInputStream(audioFileUri)?.use { inputStream ->
            // Read the file data from the input stream
            val fileData = inputStream.readBytes()

            // Upload the audio file data
            uploadAudioToServer(fileData)
        } ?: run {
            Log.e("ChatRoomActivityD", "Failed to open input stream for URI: $audioFileUri")
            // Handle the case where the input stream could not be opened
        }
    }

    private fun uploadAudioToServer(fileData: ByteArray) {
        val url = "http://${DatabaseIP.IP}/uploadaudio.php"

        // Get the user and mentor IDs
        val userId = UserInstance.getInstance()?.userId ?: ""
        val mentorId = MentorChatInstance.getInstance().mentorId ?: ""

        // Convert the file data to a Base64 string
        val encodedFileData = Base64.encodeToString(fileData, Base64.DEFAULT)

        // Create a new Volley request queue
        val requestQueue = Volley.newRequestQueue(this)

        // Create a string request
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String> { response ->
                // Handle the response from the server
                Log.d("UploadAudio", "Response from server: $response")
                // Process the response as needed
                messageList.add(Message((messageList.size-1).toString(), "", System.currentTimeMillis().toString(), imageUrl = response, sentByCurrentUser = true, audioMessage = true))
                adapter.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)
            },
            Response.ErrorListener { error ->
                // Handle errors that occur during the request
                Log.e("UploadAudio", "Error uploading audio: $error")
                // Display an error message or take appropriate action
            }) {

            // Override the getParams method to send POST parameters with the request
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userId"] = userId
                params["mentorId"] = mentorId
                params["audioFile"] = encodedFileData // Include the Base64 encoded audio data
                params["timestamp"] = SimpleDateFormat("HH:mm").format(Date())
                return params
            }
        }

        // Add the string request to the request queue
        requestQueue.add(stringRequest)
    }

    private fun getFileDataFromUri(uri: Uri): ByteArray? {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.let {
                val buffer = ByteArrayOutputStream()
                val bufferSize = 1024
                val data = ByteArray(bufferSize)
                var nRead: Int
                while (inputStream.read(data, 0, bufferSize).also { nRead = it } != -1) {
                    buffer.write(data, 0, nRead)
                }
                buffer.flush()
                return buffer.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    private fun SendNotification(message: Message) {

        if(!message.sentByCurrentUser){
            Log.d("SendNotification", "Sending Notification")
            val jsonObject = JSONObject()
            val notification = JSONObject()
            val data = JSONObject()
            notification.put("title", MentorChatInstance.getInstance().name)
            notification.put("body", message.text)
            notification.put("click_action", "ChatRoomActivity")
            data.put("message", message.text)
            data.put("title", MentorChatInstance.getInstance().name)
            data.put("click_action", "ChatRoomActivity")
            jsonObject.put("notification", notification)
            jsonObject.put("data", data)
            jsonObject.put("to", UserInstance.getInstance()!!.fcmToken)

            callAPI(jsonObject)
        }

    }
    private fun SendScreenShotNotification(message: String) {

        Log.d("SendNotification", "Sending Notification")

        val jsonObject = JSONObject()
        val notification = JSONObject()
        val data = JSONObject()
        notification.put("title", "Screenshot Detected")
        notification.put("body", message)
        notification.put("click_action", "ChatRoomActivity")
        data.put("message", message)
        data.put("title", "Screenshot Detected")
        data.put("click_action", "ChatRoomActivity")
        jsonObject.put("notification", notification)
        jsonObject.put("data", data)
        jsonObject.put("to", UserInstance.getInstance()!!.fcmToken)
        callAPI(jsonObject)
    }

    private fun callAPI(jsonObject: JSONObject){
        val JSON : MediaType = "application/json; charset=utf-8".toMediaType()
        val client : OkHttpClient = OkHttpClient()
        val url : String = "https://fcm.googleapis.com/fcm/send"
        val body : RequestBody = RequestBody.create(JSON, jsonObject.toString())
        val request : Request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Bearer AAAAAI0jfrw:APA91bGSfgKDPAesmtlFLNRqVJkoAKUd3PrfUFDS9WvyOit0TFmFTjCDi6tsjEVOyA_bJmVMMHgYcYExwvQCHknLbBPrjH-BXD1vmqJvYsgQ4Y9eKzcVsQLxrBkhMACcsQfhQqleSgHu")
            .build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("NotificationStatus", "Failed to send notification: ${e.message}")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                Log.d("NotificationStatus", "Notification sent successfully")
            }
        })
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 3009

    }
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun onStart() {
        super.onStart()
        screenshotDetectionDelegate.startScreenshotDetection()
    }
    override fun onStop() {
        super.onStop()
        screenshotDetectionDelegate.stopScreenshotDetection()
    }

    override fun onScreenCaptured(path: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            SendScreenShotNotification("${UserInstance.getInstance()!!.fullName} took a screenshot")
        }, 5000)

    }

    override fun onScreenCapturedWithDeniedPermission() {
        Handler(Looper.getMainLooper()).postDelayed({
            SendScreenShotNotification("${UserInstance.getInstance()!!.fullName} took a screenshot")
        }, 5000)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.getOrNull(0) == PackageManager.PERMISSION_DENIED) {
                    showReadExternalStoragePermissionDeniedMessage()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    private fun checkReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestReadExternalStoragePermission()
        }
    }

    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION)
    }

    private fun showReadExternalStoragePermissionDeniedMessage() {
//        Toast.makeText(this, "Read external storage permission has denied", Toast.LENGTH_SHORT).show()
        Log.d("ChatRoomActivity", "Read external storage permission has denied")
    }
}




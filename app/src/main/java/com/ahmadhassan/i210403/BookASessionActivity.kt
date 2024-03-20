package com.ahmadhassan.i210403

import android.content.Intent
import android.graphics.drawable.Drawable
import java.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class BookASessionActivity : AppCompatActivity() {
    private var selectedTimeSlot: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.book_a_session)


        val mentor = intent.getSerializableExtra("mentor") as? Mentors
        val mentorname = findViewById<TextView>(R.id.MentorNameTextView)
        val mentorPFP = findViewById<ImageView>(R.id.mentorPFP)
        if (mentor != null) {
            mentor.name.also { mentorname.text = it }
            if (mentor.profilePicture.isNotEmpty()){
                // it should fill the image view with the mentor's profile picture
                Picasso.get().load(mentor.profilePicture).into(mentorPFP)
            }
            // it should fill the image view with the mentor's profile picture
        }

        val calenderView: CalendarView = findViewById(R.id.calendarView)
        var dateString: String = ""
        var timeString: String = ""

        calenderView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val selectedDate = calendar.time
            Log.d("CalendarPage", "Selected Date: $selectedDate")

            // Convert selectedDate to a string if needed
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateString = dateFormat.format(selectedDate)
            Log.d("CalendarPage", "Selected Date String: $dateString")

        }


        // on back button go to  MentorProfileActivity
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val timeSlot1 = findViewById<TextView>(R.id.timeSlot1)
        val timeSlot2 = findViewById<TextView>(R.id.timeSlot2)
        val timeSlot3 = findViewById<TextView>(R.id.timeSlot3)
        timeSlot1.setOnClickListener {
            selectTimeSlot(timeSlot1)
            timeString = timeSlot1.text.toString()
        }
        timeSlot2.setOnClickListener {
            selectTimeSlot(timeSlot2)
            timeString = timeSlot2.text.toString()
        }
        timeSlot3.setOnClickListener {
            selectTimeSlot(timeSlot3)
            timeString = timeSlot3.text.toString()
        }


        // on submit review button go to  MentorProfileActivity
        val submitReviewButton = findViewById<Button>(R.id.bookAppointmentButton)
        submitReviewButton.setOnClickListener {
            val bookedSession = BookedSession(mentor!!.mentorId, dateString, timeString, UserInstance.getInstance()!!.userId)
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("BookedSessions")
            val newRef = ref.push()
            newRef.setValue(bookedSession).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Session Booked", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                    // delay
                    SendNotification("You have booked a session with ${mentor.name} on $dateString at $timeString.")
                    }, 5000)


                    onBackPressedDispatcher.onBackPressed()
                } else {
                    Toast.makeText(this, "Failed to book session", Toast.LENGTH_SHORT).show()
                }
            }
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
        findViewById<ImageView>(R.id.ChatImageView).setOnClickListener {
            val currUser = UserInstance.getInstance()!!
            val currMentor = mentor!!
            checkOrCreateChatRoom(currUser, currMentor)
        }
    }

    private fun SendNotification(message: String) {

            Log.d("SendNotification", "Sending Notification")
            val jsonObject = JSONObject()
            val notification = JSONObject()
            val data = JSONObject()
            notification.put("title","New Session Booked")
            notification.put("body", message)
            notification.put("click_action", "ChatRoomActivity")
            data.put("message", message)
            data.put("title", MentorChatInstance.getInstance().name)
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
    fun selectTimeSlot(timeSlot: TextView) {
        // Reset background tint of previously selected time slot, if any
        selectedTimeSlot?.background?.let {
            val unwrappedDrawable = DrawableCompat.unwrap<Drawable>(it)
            DrawableCompat.setTint(
                unwrappedDrawable,
                ContextCompat.getColor(this, R.color.unselected_bkg_color)
            )
        }
        selectedTimeSlot?.setTextColor(ContextCompat.getColor(this, R.color.black))
        // Change background tint of clicked time slot
        timeSlot.background?.let {
            val unwrappedDrawable = DrawableCompat.unwrap<Drawable>(it)
            DrawableCompat.setTint(
                unwrappedDrawable,
                ContextCompat.getColor(this, R.color.selected_bkg_color)
            )
        }
        timeSlot.setTextColor(ContextCompat.getColor(this, R.color.white))

        // Update selectedTimeSlot reference
        selectedTimeSlot = timeSlot


    }
    private fun checkOrCreateChatRoom(currUser: User, currMentor: Mentors) {
        val database = FirebaseDatabase.getInstance()
        val chatRoomsRef = database.getReference("ChatRooms")
        Log.d("ChatActivityH", "chatRoomRef: $chatRoomsRef")

        chatRoomsRef.orderByChild("userId").equalTo(UserInstance.getInstance()!!.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (roomSnapshot in dataSnapshot.children) {
                        Log.d("ChatActivityH", "roomSnapshot: $roomSnapshot")
                        val chatRoom = roomSnapshot.getValue(ChatRoom::class.java)
                        Log.d("ChatActivityH", "chatRoom: $chatRoom")
                        Log.d("ChatActivityH", "chatRoom: $chatRoom")
                        // log the current mentor id and user id
                        Log.d("ChatActivityH", "currMentor.mentorId: ${currMentor.mentorId}")
                        Log.d("ChatActivityH", "currUser.userId: ${currUser.userId}")

                        if (chatRoom != null && chatRoom.mentorId == currMentor.mentorId) {
                            // If a chat room exists, navigate to the chat room activity
                            val intent = Intent(this@BookASessionActivity, ChatRoomActivity::class.java)
                            intent.putExtra("chatRoom", chatRoom)
                            startActivity(intent)
                            finish()
                            return
                        }
                    }
                    // If no chat room exists, create a new one
                    createNewChatRoom(currUser, currMentor)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@BookASessionActivity, "Failed to check chat rooms", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun createNewChatRoom(currUser: User, currMentor: Mentors) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("ChatRooms")

        val chatRoomId = ref.push().key ?: return

        // Create a new chat room with an empty message list
        val newChatRoom = ChatRoom(chatRoomId, currMentor.mentorId, currUser.userId, emptyList())

        // Save the chat room to the database
        ref.child(chatRoomId).setValue(newChatRoom).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // If the chat room is successfully saved, add initial messages to it
                addInitialMessagesToChatRoom(chatRoomId, currMentor)
            } else {
                Toast.makeText(this, "Failed to open chat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addInitialMessagesToChatRoom(chatRoomId: String, currMentor: Mentors) {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("ChatRooms").child(chatRoomId).child("messages")

        // Add initial messages to the chat room
        val initialMessages = listOf(
            Message("1", "Hello! ${UserInstance.getInstance()!!.fullName}", "10:20", imageUrl = currMentor.profilePicture,false),
            Message("2", "Hi there!", "11:15", imageUrl = currMentor.profilePicture, sentByCurrentUser = true),
            Message("3", "How are you?", "11:20", imageUrl = currMentor.profilePicture, sentByCurrentUser = false),
            Message("4", "I'm fine, thanks! ${currMentor.name}", "12:00", imageUrl = currMentor.profilePicture, sentByCurrentUser = true)
            // Add more initial messages as needed
        )

        // Save the initial messages to the database
        ref.setValue(initialMessages).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // If the messages are successfully saved, navigate to the chat room activity
                val intent = Intent(this, ChatRoomActivity::class.java)
                val newChatRoom = ChatRoom(chatRoomId, "", "", initialMessages) // Provide only the ID and messages
                intent.putExtra("chatRoom", newChatRoom)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Failed to open chat", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
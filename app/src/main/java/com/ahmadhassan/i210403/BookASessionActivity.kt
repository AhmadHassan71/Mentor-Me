package com.ahmadhassan.i210403

import android.content.Intent
import android.graphics.drawable.Drawable
import java.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.squareup.picasso.Picasso

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
                mentorPFP.scaleType = ImageView.ScaleType.FIT_CENTER
                Picasso.get().load(mentor.profilePicture).into(mentorPFP)
            }
            // it should fill the image view with the mentor's profile picture
        }

        val calendarView: CalendarView = findViewById(R.id.calendarView)


        val today = Calendar.getInstance()
        calendarView.date = today.timeInMillis

        // on submit review button go to  MentorProfileActivity
        val submitReviewButton = findViewById<Button>(R.id.submitFeedbackButton)
        submitReviewButton.setOnClickListener {
            val intent = Intent(this, MentorProfileActivity::class.java)
            intent.putExtra("mentor", mentor)
            startActivity(intent)
        }

        // on back button go to  MentorProfileActivity
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val timeSlot1 = findViewById<TextView>(R.id.timeSlot1)
        val timeSlot2 = findViewById<TextView>(R.id.timeSlot2)
        val timeSlot3 = findViewById<TextView>(R.id.timeSlot3)
        timeSlot1.setOnClickListener { selectTimeSlot(timeSlot1) }
        timeSlot2.setOnClickListener { selectTimeSlot(timeSlot2) }
        timeSlot3.setOnClickListener { selectTimeSlot(timeSlot3) }


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
            val intent = Intent(this,ChatRoomActivity::class.java)
            startActivity(intent)
        }
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
}
package com.ahmadhassan.i210403

import android.widget.ImageView

data class Message(
    val id: String, // Unique identifier for the message
    val text: String, // Content of the message
    val timestamp: String, // Timestamp of the message
    val imageUrl: Int? = R.drawable.personpic, // URL of the image, if any
    val sentByCurrentUser: Boolean = false, // True if the message was sent by the current user

)

package com.ahmadhassan.i210403

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView

class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mentorName: TextView = itemView.findViewById(R.id.MentorName)
    val ratingBar: AppCompatRatingBar = itemView.findViewById(R.id.ratingBar)
    val reviewDescription: TextView = itemView.findViewById(R.id.reviewDescription)
}

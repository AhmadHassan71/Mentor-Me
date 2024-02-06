package com.ahmadhassan.i210403

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val profilePicture: ImageView = itemView.findViewById(R.id.profilePicture)
    val mentorName: TextView = itemView.findViewById(R.id.MentorName)
    val jobTitle: TextView = itemView.findViewById(R.id.JobTitle)
    val date: TextView = itemView.findViewById(R.id.Date)
    val time: TextView = itemView.findViewById(R.id.Time)
}

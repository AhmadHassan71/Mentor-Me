package com.ahmadhassan.i210403

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_card, parent, false)
        return ReviewViewHolder(view)
    }
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentReview = reviews[position]
        holder.mentorName.text = currentReview.mentorName
        holder.ratingBar.rating = currentReview.rating
        holder.reviewDescription.text = currentReview.reviewDescription
    }

    override fun getItemCount(): Int {
        return reviews.size
    }
}

package com.ahmadhassan.i210403

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PersonProfileAdapter(private val profiles: List<PersonProfile>) :
    RecyclerView.Adapter<PersonProfileAdapter.ProfileViewHolder>() {

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profilePictureImageView: ImageView = itemView.findViewById(R.id.profilePictureImageView)
        private val personNameTextView: TextView = itemView.findViewById(R.id.personNameTextView)
        private val unreadMessagesTextView: TextView = itemView.findViewById(R.id.unreadMessagesTextView)

        fun bind(personProfile: PersonProfile) {
            profilePictureImageView.setImageResource(personProfile.profilePicture)
            personNameTextView.text = personProfile.personName
            unreadMessagesTextView.text = if (personProfile.unreadMessages > 0) {
                "Unread Messages: ${personProfile.unreadMessages}"
            } else {
                "No Unread Messages"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_person_profile_card, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        holder.bind(profile)
    }

    override fun getItemCount(): Int {
        return profiles.size
    }
}

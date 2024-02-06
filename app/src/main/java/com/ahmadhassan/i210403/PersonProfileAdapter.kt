package com.ahmadhassan.i210403

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
            if (personProfile.unreadMessages == 1) {
                "${personProfile.unreadMessages} New Message".also { unreadMessagesTextView.text = it }
                // Change text color to red for one unread message
                unreadMessagesTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            } else if (personProfile.unreadMessages > 1) {
                "${personProfile.unreadMessages} New Messages".also { unreadMessagesTextView.text = it }
                // Change text color to red for multiple unread messages
                unreadMessagesTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            } else {
                "No Unread Messages".also { unreadMessagesTextView.text = it }
                // Reset text color to default for no unread messages
                unreadMessagesTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
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

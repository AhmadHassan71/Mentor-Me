package com.ahmadhassan.i210403

import PersonProfile
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PersonProfileAdapter(private val profiles: List<PersonProfile>) :
    RecyclerView.Adapter<PersonProfileAdapter.ProfileViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val profilePictureImageView: ImageView = itemView.findViewById(R.id.profilePictureImageView)
        private val personNameTextView: TextView = itemView.findViewById(R.id.personNameTextView)
        private val unreadMessagesTextView: TextView = itemView.findViewById(R.id.unreadMessagesTextView)
        private val context: Context = itemView.context

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val profile = profiles[position]
                itemClickListener?.onItemClick(profile)
            }
        }

        fun bind(personProfile: PersonProfile) {
//            profilePictureImageView.setImageResource(personProfile.profilePicture)
            Picasso.get().load(personProfile.profilePicture).into(profilePictureImageView)
            personNameTextView.text = personProfile.personName
            if (personProfile.unreadMessages == 1) {
                "${personProfile.unreadMessages} New Message".also { unreadMessagesTextView.text = it }
                unreadMessagesTextView.setTextColor(ContextCompat.getColor(context, R.color.red))
            } else if (personProfile.unreadMessages > 1) {
                "${personProfile.unreadMessages} New Messages".also { unreadMessagesTextView.text = it }
                unreadMessagesTextView.setTextColor(ContextCompat.getColor(context, R.color.red))
            } else {
                "No Unread Messages".also { unreadMessagesTextView.text = it }
                unreadMessagesTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
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

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun getItemCount(): Int {
        return profiles.size
    }

    interface OnItemClickListener {
        fun onItemClick(profile: PersonProfile)
    }
}

package com.ahmadhassan.i210403

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CommunityProfilePicsAdapter(private val profilePics: List<Int>) :
    RecyclerView.Adapter<CommunityProfilePicsAdapter.ProfilePicViewHolder>() {

    inner class ProfilePicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val communityProfilePic: ImageView = itemView.findViewById(R.id.communityProfilePic)

        fun bind(profilePic: Int) {
            communityProfilePic.setImageResource(profilePic)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_community_profile_pics, parent, false)
        return ProfilePicViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfilePicViewHolder, position: Int) {
        val profilePic = profilePics[position]
        holder.bind(profilePic)
    }

    override fun getItemCount(): Int {
        return profilePics.size
    }
}

package com.ahmadhassan.i210403

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SessionAdapter(private val sessions: List<Session>) :
    RecyclerView.Adapter<SessionViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(session: Session)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bookedsession_verticalcard, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val currentSession = sessions[position]
        if(currentSession.mentorProfilePic != "")
            Picasso.get().load("http://" + DatabaseIP.IP + "/MentorProfilePics/" + currentSession.mentorProfilePic).into(holder.profilePicture)
        holder.mentorName.text = currentSession.mentorName
        holder.jobTitle.text = currentSession.jobTitle
        holder.date.text = currentSession.date
        holder.time.text = currentSession.time

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(currentSession)
        }
    }

    override fun getItemCount(): Int {
        return sessions.size
    }
}


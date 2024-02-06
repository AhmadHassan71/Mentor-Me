package com.ahmadhassan.i210403
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationViewHolder(
    itemView: View,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val notificationTextView: TextView = itemView.findViewById(R.id.notificationTextView)

    init {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClick(position)
            }
        }
    }

    fun bind(notification: Notification) {
        notificationTextView.text = notification.message
    }
}

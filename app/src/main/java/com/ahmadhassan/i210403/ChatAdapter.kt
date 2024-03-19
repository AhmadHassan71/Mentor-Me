package com.ahmadhassan.i210403

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView

class ChatAdapter(private val messageList: MutableList<Message>, private val database: DatabaseReference) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE = 0
    private val VIEW_TYPE_MESSAGE_USER = 1
    private val VIEW_TYPE_MESSAGE_AUDIO = 2

    override fun getItemViewType(position: Int): Int {
   return if (messageList[position].audioMessage) {
                VIEW_TYPE_MESSAGE_AUDIO
            } else if (messageList[position].sentByCurrentUser) {
            VIEW_TYPE_MESSAGE_USER
            } else {
            VIEW_TYPE_MESSAGE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MESSAGE_USER -> {
                // Inflate layout for messages sent by the current user
                val view = inflater.inflate(R.layout.chat_message_user, parent, false)
                UserMessageViewHolder(view)
            }
            VIEW_TYPE_MESSAGE_AUDIO -> {
                val view = inflater.inflate(R.layout.audio_message_user, parent, false)
                AudioMessageViewHolder(view)
            }
            else -> {
                // Inflate layout for messages received from others
                val view = inflater.inflate(R.layout.chat_message, parent, false)
                MessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_USER -> {
                val userHolder = holder as UserMessageViewHolder
                userHolder.bind(message)
            }
            VIEW_TYPE_MESSAGE_AUDIO -> {
                val audioHolder = holder as AudioMessageViewHolder
                audioHolder.bind(message)
            }
            else -> {
                val messageHolder = holder as MessageViewHolder
                messageHolder.bind(message)
            }
        }
    }

    // ViewHolder for messages sent by the current user
    private inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.userchatMessageTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.usertimeTextView)
        private val imageMessage: ImageView = itemView.findViewById(R.id.userMessageImage)

        fun bind(message: Message) {
            Log.d("ChatAdapter", "User message URL: ${message.imageUrl} ${message.audioMessage}")
            messageTextView.text = message.text
            timestampTextView.text = message.timestamp
            // Load image if available
            if(message.imageUrl != "") {
                Picasso.get().load(message.imageUrl).into(imageMessage)
                timestampTextView.text = ""
            }
            // Implement edit and delete functionality here
            itemView.setOnLongClickListener {
                showEditDeleteDialog(message)
                true // Consume the long click event
            }
        }

        private fun showEditDeleteDialog(message: Message) {
            val dialogBuilder = AlertDialog.Builder(itemView.context)
            dialogBuilder.setTitle("Edit or Delete Message")
            dialogBuilder.setMessage("Choose an action for the message.")

            dialogBuilder.setPositiveButton("Edit") { _, _ ->
                // Handle edit action
                editMessage(message)
            }

            dialogBuilder.setNegativeButton("Delete") { _, _ ->
                // Handle delete action
                deleteMessage(message)
            }

            dialogBuilder.setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = dialogBuilder.create()
            dialog.show()
        }

        private fun editMessage(message: Message) {
            val editText = EditText(itemView.context)
            editText.setText(message.text)
            val dialogBuilder = AlertDialog.Builder(itemView.context)
            dialogBuilder.setTitle("Edit Message")
            dialogBuilder.setView(editText)

            dialogBuilder.setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString()
                if (newText.isNotEmpty()) {
                    // Update the message in the database
                    messageList[adapterPosition].text = newText
                    notifyItemChanged(adapterPosition)
                    val messageId = message.id
                    database.child(messageId).child("text").setValue(newText)
                }
            }

            dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = dialogBuilder.create()
            dialog.show()
        }

        private fun deleteMessage(message: Message) {
            // Implement logic to delete the message
            // Remove the message from the messageList and notify the adapter
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val messageId = messageList[position].id
                messageList.removeAt(position)
                notifyItemRemoved(position)
                database.child(messageId).removeValue()
            }
        }
    }

    // ViewHolder for audio messages
    private inner class AudioMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val audioPlayer = itemView.findViewById<VoicePlayerView>(R.id.voicePlayerView)
        fun bind(message: Message) {
            Log.d("ChatAdapter", "Audio message URL: ${message.imageUrl}")
            audioPlayer.setAudio(message.imageUrl)

        }
    }

    // ViewHolder for messages received from others
    private inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.chatMessageTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val messageImageView: ImageView = itemView.findViewById(R.id.chatImageView)

        fun bind(message: Message) {
            messageTextView.text = message.text
            timestampTextView.text = message.timestamp.toString()
            // Load image if available
            // Replace "R.drawable.placeholder_image" with your actual placeholder image resource
            if (message.imageUrl != "")
                Picasso.get().load(message.imageUrl).into(messageImageView)
            // Implement edit and delete functionality here
            itemView.setOnLongClickListener {
                // Implement edit and delete logic here
                true // Consume the long click event
            }
        }
    }
}
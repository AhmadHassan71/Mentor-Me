import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmadhassan.i210403.Message
import com.ahmadhassan.i210403.R

class ChatAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE = 0
    private val VIEW_TYPE_MESSAGE_USER = 1

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sentByCurrentUser) {
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

        fun bind(message: Message) {
            messageTextView.text = message.text
            timestampTextView.text = message.timestamp.toString()
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
            messageImageView.setImageResource(message.imageUrl !!)
        }
    }
}
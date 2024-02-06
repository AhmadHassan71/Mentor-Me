package com.ahmadhassan.i210403
//
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationActivity : AppCompatActivity() {

    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification)

        val notifications = mutableListOf(
            Notification("Ali has accepted your request"),
            Notification("Mentor is available"),
            Notification("Your Profile is updated"),
            Notification("Are you looking for a mentor?"),
        )

        adapter = NotificationAdapter(notifications) { position ->
            // Remove the clicked item
            adapter.removeItem(position)
        }

        val recyclerView: RecyclerView = findViewById(R.id.notificationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        // clear all
        val clearAllButton = findViewById<TextView>(R.id.clearAllTextView)
        clearAllButton.setOnClickListener {
            notifications.clear()
            adapter.notifyDataSetChanged()
        }

        //back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}


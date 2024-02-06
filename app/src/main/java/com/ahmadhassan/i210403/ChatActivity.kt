package com.ahmadhassan.i210403

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_menu)

        val recyclerView: RecyclerView = findViewById(R.id.myCommunityRecyclerView)

        // Sample data for profile pics (replace with your actual data)
        val profilePics = listOf(
            R.drawable.paul_personpic,
            R.drawable.personpic,
            R.drawable.personpic,
            R.drawable.personpic,
            R.drawable.personpic,
            R.drawable.personpic,
            R.drawable.personpic,
        )

        val adapter = CommunityProfilePicsAdapter(profilePics)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity,RecyclerView.HORIZONTAL, false)



        val recyclerView2: RecyclerView = findViewById(R.id.allMessagesRecyclerView)

        // Sample data for person profiles (replace with your actual data)
        val profiles = listOf(
            PersonProfile(R.drawable.personpic, "Person 1", 3),
            PersonProfile(R.drawable.paul_personpic, "Person 2", 0),
            PersonProfile(R.drawable.personpic, "Person 3", 5),
            PersonProfile(R.drawable.paul_personpic, "Person 4", 0),
            PersonProfile(R.drawable.personpic, "Person 5", 1)
        )

        val adapter2 = PersonProfileAdapter(profiles)
        recyclerView2.adapter = adapter2
        recyclerView2.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}
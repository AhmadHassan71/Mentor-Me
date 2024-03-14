package com.ahmadhassan.i210403

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object UserInstance {
    private var currentUser: User? = null
    private val database = FirebaseDatabase.getInstance()

    fun getInstance(): User? {
        return currentUser
    }

    fun fetchUser(userId: String, callback: (User?) -> Unit) {
        if (currentUser != null) {
            Log.d("UserInstance", "Using already stored User")
            callback(getInstance())
            return
        }
        val dbref = database.getReference("Users").child(userId)
        dbref.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            currentUser = user
            callback(user)
            Log.d("UserInstance", "User: $user")
        }.addOnFailureListener { error ->
            Log.d("UserInstance", "Error: ${error.message}")
            // Handle failure
            callback(null)
        }
    }

    fun updateUser(updatedUser: User, callback: (Boolean) -> Unit) {
        val dbref = database.getReference("Users").child(updatedUser.userId)
        dbref.setValue(updatedUser).addOnSuccessListener {
            currentUser = updatedUser
            callback(true)
        }.addOnFailureListener { error ->
            Log.d("UserInstance", "Error: ${error.message}")
            // Handle failure
            callback(false)
        }
    }

    fun clearInstance() {
        currentUser = null
    }
}

package com.ahmadhassan.i210403

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var dbref : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs: SharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false) // Check saved state

        if (isLoggedIn) {
            // do not show login screen if user is already logged in
            setContentView(R.layout.loading)
            val userIdSharedPreferences: SharedPreferences =
                getSharedPreferences("userIdPreferences", MODE_PRIVATE)
            val userId = userIdSharedPreferences.getString("userId", null)


            // If the user is already logged in, start the HomeActivity
            UserInstance.fetchUser(userId!!) { user ->
                if (user != null) {
//                    if(intent.extras!=null){
//                        Log.d("INTENT_OKAY", "User fetched successfully")
//                        intent.extras!!.getString("userId")
//                        val intent = Intent(this@LoginActivity, ChatActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                    else{
                        Log.d("INTENT_NULL", "User fetched successfully")
                        getFCMToken()
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
//                    }
                } else {
                    // Handle case where user data couldn't be fetched
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            setContentView(R.layout.login)


            auth = FirebaseAuth.getInstance() // Initialize Firebase Auth

            val loginButton: Button = findViewById(R.id.LoginButton)
            loginButton.setOnClickListener {
                loginUser()
            }

//        val loginButton: Button = findViewById(R.id.LoginButton)
//        loginButton.setOnClickListener {
//            val emailEditText: EditText = findViewById(R.id.EmailEditText)
//            val passwordEditText: EditText = findViewById(R.id.PasswordEditText)
//
//            val email: String = emailEditText.text.toString().trim()
//            val password: String = passwordEditText.text.toString().trim()
//
//            if (email.isEmpty() || password.isEmpty()) {
//                // Check if email or password is empty
//                Toast.makeText(this@LoginActivity, "Please enter your email and password", Toast.LENGTH_SHORT).show()
//            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                // Check if email is in correct format using regex
//                Toast.makeText(this@LoginActivity, "Invalid email format", Toast.LENGTH_SHORT).show()
//            } else {
//                // Start the HomeActivity when the login button is clicked
//                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
//                startActivity(intent)
//            }
//        }


            val signUpTextView: TextView = findViewById(R.id.SignUpTextView)
            signUpTextView.setOnClickListener {
                // Start the RegisterActivity when the sign up text view is clicked
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            val forgotPasswordTextView: TextView = findViewById(R.id.ForgotPasswordTextView)
            forgotPasswordTextView.setOnClickListener {
                // Start the ForgotPasswordActivity when the forgot password text view is clicked
                val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }

        }
    }
//    private fun loginUser() {
//        val emailEditText: EditText = findViewById(R.id.EmailEditText)
//        val passwordEditText: EditText = findViewById(R.id.PasswordEditText)
//
//        val email = emailEditText.text.toString().trim()
//        val password = passwordEditText.text.toString().trim()
//
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
//            return  // Exit the function
//        }
//
//
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//
//                    val loggedInUser : String = emailEditText.text.toString()
//                    database = FirebaseDatabase.getInstance()
//                    dbref = auth.currentUser?.uid?.let { database.getReference("Users").child(it) }!!
//                    dbref.get().addOnSuccessListener {
//                        if (it.key != null) {
//                            // Login successful
//                            val sharedPrefs: SharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
//                            sharedPrefs.edit().putBoolean("isLoggedIn", true).apply()
//                            val currentUser = UserInstance.getInstance(it.key!!)
//                            Log.d("Login Activity",currentUser.fullName.toString())
//                            val userIdSharedPreferences : SharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
//                            userIdSharedPreferences.edit().putString("userId", it.key).apply()
//                            val intent = Intent(this, HomeActivity::class.java)
//                            intent.putExtra("currentUser", it.key)
//                            startActivity(intent)  // Start activity here
//                        }
//                    }
//                    // put currentUser in the intent
//
//                    finish()
//                } else {
//                    // Handle error
////                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
//
//                }
//            }
//    }

    private fun loginUser() {
        val emailEditText: EditText = findViewById(R.id.EmailEditText)
        val passwordEditText: EditText = findViewById(R.id.PasswordEditText)

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            return  // Exit the function
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch user data after successful authentication
                    UserInstance.fetchUser(auth.currentUser?.uid ?: "") { user ->
                        if (user != null) {
                            // User fetched successfully
                            val sharedPrefs: SharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
                            sharedPrefs.edit().putBoolean("isLoggedIn", true).apply()

                            val userIdSharedPreferences : SharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
                            userIdSharedPreferences.edit().putString("userId", user.userId).apply()

                            val intent = Intent(this, HomeActivity::class.java)
                            UserInstance.fetchUser(user.userId) { user ->
                                if (user != null) {
                                   getFCMToken()
                                } else {
                                    // Handle case where user data couldn't be fetched
                                    Toast.makeText(
                                        this,
                                        "Failed to fetch user data",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }


                            startActivity(intent)
                            finish()
                        } else {
                            // Handle case where user data couldn't be fetched
                            Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Handle authentication failure
                    Toast.makeText(this, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful){
                val token = it.result
                UserInstance.setFCMToken(token) { success ->
                    if (success) {
                        Log.d("LoginActivity", "FCM Token updated successfully")
                    } else {
                        Log.d("LoginActivity", "Failed to update FCM Token")
                    }
                }
            }
        }
    }

}

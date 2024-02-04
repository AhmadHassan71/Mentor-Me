package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val loginButton: Button = findViewById(R.id.LoginButton)
        loginButton.setOnClickListener {
            // Start the HomeActivity when the login button is clicked
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
        }


        val signUpTextView: TextView = findViewById(R.id.SignUpTextView)
        signUpTextView.setOnClickListener {
            // Start the RegisterActivity when the sign up text view is clicked
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}

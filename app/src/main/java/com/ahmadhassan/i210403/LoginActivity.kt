package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val loginButton: Button = findViewById(R.id.LoginButton)
        loginButton.setOnClickListener {
            val emailEditText: EditText = findViewById(R.id.EmailEditText)
            val passwordEditText: EditText = findViewById(R.id.PasswordEditText)

            val email: String = emailEditText.text.toString().trim()
            val password: String = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                // Check if email or password is empty
                Toast.makeText(this@LoginActivity, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Check if email is in correct format using regex
                Toast.makeText(this@LoginActivity, "Invalid email format", Toast.LENGTH_SHORT).show()
            } else {
                // Start the HomeActivity when the login button is clicked
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }



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

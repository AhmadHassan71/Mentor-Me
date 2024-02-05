package com.ahmadhassan.i210403



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resetpassword)

        val loginButton: ImageView = findViewById(R.id.backButton)
        loginButton.setOnClickListener {
            // Start the HomeActivity when the login button is clicked
            val intent = Intent(this@ResetPasswordActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val sendButton: Button = findViewById(R.id.sendButton)
        sendButton.setOnClickListener {
            // Start the HomeActivity when the login button is clicked
            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val loginTextView: TextView = findViewById(R.id.LoginText)
        loginTextView.setOnClickListener {
            // Start the HomeActivity when the login button is clicked
            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

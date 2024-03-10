package com.ahmadhassan.i210403

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 2000 // Delay in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)
        val sharedPrefs: SharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false) // Check saved state
        Handler(Looper.getMainLooper()).postDelayed({
        if (isLoggedIn) {
            // If the user is already logged in, start the HomeActivity
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // If the user is not logged in, start the LoginActivity
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        }, SPLASH_DELAY)
    }
}

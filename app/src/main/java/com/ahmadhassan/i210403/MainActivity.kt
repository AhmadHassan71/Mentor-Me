package com.ahmadhassan.i210403

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 2000 // Delay in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)

        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(40 * 1024 * 1024); // 40 MB cache size


          Handler(Looper.getMainLooper()).postDelayed({

            // If the user is not logged in, start the LoginActivity
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }, SPLASH_DELAY)
    }
}

package com.ahmadhassan.i210403

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 2000 // Delay in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}

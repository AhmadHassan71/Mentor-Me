package com.ahmadhassan.i210403

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {
    private var isCitySelected = false


    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }






        val spinner: Spinner = findViewById(R.id.CityEditText)
        val spinner2: Spinner = findViewById(R.id.CountryEditText)

        val countries = arrayOf(
            "Enter Country", "Pakistan", "India", "Bangladesh", "Sri Lanka", "Nepal",
            "Bhutan", "Maldives", "Afghanistan", "Iran", "China"
        )
        val cities = arrayOf(
            "Enter City", "Karachi", "Lahore", "Faisalabad", "Rawalpindi", "Multan",
            "Gujranwala", "Hyderabad", "Peshawar", "Quetta", "Islamabad"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cities
        )

        val logoutButton: Button = findViewById(R.id.LogOutButton)
        logoutButton.setOnClickListener {
            val sharedPrefs: SharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("isLoggedIn", false).apply()
            val userIdSharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
            userIdSharedPreferences.edit().putString("userId", null).apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (!isCitySelected) {
                    if (position == 0) return

                    val selectedCity = parent.getItemAtPosition(position).toString()
//                    Toast.makeText(
//                        applicationContext,
//                        "Selected city: $selectedCity",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    isCitySelected = true

                    val updatedCities = cities.filter { it != "Enter City" }.toTypedArray()
                    val updatedAdapter = ArrayAdapter(
                        applicationContext,
                        android.R.layout.simple_spinner_item,
                        updatedCities
                    )
                    updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = updatedAdapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        // do same for country
        val adapter2 = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            countries
        )
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (!isCitySelected) {
                    if (position == 0) return

                    val selectedCountry = parent.getItemAtPosition(position).toString()
//                    Toast.makeText(
//                        applicationContext,
//                        "Selected country: $selectedCountry",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    isCitySelected = true

                    val updatedCountries = countries.filter { it != "Enter Country" }.toTypedArray()
                    val updatedAdapter = ArrayAdapter(
                        applicationContext,
                        android.R.layout.simple_spinner_item,
                        updatedCountries
                    )
                    updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner2.adapter = updatedAdapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        val profilePicture = findViewById<ImageView>(R.id.pfpimage)
        var user = User()
        val userIdSharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
        val userId = userIdSharedPreferences.getString("userId", null)
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val dbref = database.getReference("Users").child(userId)
            dbref.get().addOnSuccessListener { dataSnapshot ->
                user = dataSnapshot.getValue(User::class.java)!!
                user?.let {
                    val profile = user.profilePic


                    // Load profile picture using Picasso
                    Picasso.get().load(profile).into(profilePicture)

                    val nameTextView = findViewById<EditText>(R.id.NameEditText)
                    nameTextView.setText(user.fullName)

                    val emailTextView = findViewById<TextView>(R.id.EmailEditText)
                    emailTextView.text = (user.email)

                    spinner.setSelection(cities.indexOf(user.city))
                    spinner2.setSelection(countries.indexOf(user.country))


                }
            }
        }

        // save button
        val saveButton = findViewById<Button>(R.id.updateButton)
        saveButton.setOnClickListener {
            val nameEditText = findViewById<EditText>(R.id.NameEditText)
            val name = nameEditText.text.toString()
            val city = spinner.selectedItem.toString()
            val country = spinner2.selectedItem.toString()

            val updatedUser = User(
                user.userId,
                user.email,
                name,
                city,
                country,
                user.profilePic,
                user.bannerPic,

            )
            val database = FirebaseDatabase.getInstance()
            val dbref = database.getReference("Users").child(userId!!)
            dbref.setValue(updatedUser).addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            }
        }


    }
}
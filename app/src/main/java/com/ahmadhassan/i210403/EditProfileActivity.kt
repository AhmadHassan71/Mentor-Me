package com.ahmadhassan.i210403
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {
    private var isCitySelected = false
    val countries = arrayOf(
        "Pakistan", "India", "Bangladesh", "Sri Lanka", "Nepal",
        "Bhutan", "Maldives", "Afghanistan", "Iran", "China"
    )
    val cities = arrayOf(
        "Karachi", "Lahore", "Faisalabad", "Rawalpindi", "Multan",
        "Gujranwala", "Hyderabad", "Peshawar", "Quetta", "Islamabad"
    )

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)
        var user: User? = null


        val userIdSharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
        val userId = userIdSharedPreferences.getString("userId", null)


        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val spinner: Spinner = findViewById(R.id.CityEditText)
        val spinner2: Spinner = findViewById(R.id.CountryEditText)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cities
        )

        val logoutButton: Button = findViewById(R.id.LogOutButton)
        logoutButton.setOnClickListener {

            UserInstance.clearInstance()
            val sharedPrefs: SharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("isLoggedIn", false).apply()
            val userIdSharedPreferences = getSharedPreferences("userIdPreferences", MODE_PRIVATE)
            userIdSharedPreferences.edit().putString("userId","").apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
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

//                    val updatedCities = cities.filter { it != "Enter City" }.toTypedArray()
//                    val updatedAdapter = ArrayAdapter(
//                        applicationContext,
//                        android.R.layout.simple_spinner_item,
//                        updatedCities
//                    )
//                    updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                    spinner.adapter = updatedAdapter
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

//                    val updatedCountries = countries.filter { it != "Enter Country" }.toTypedArray()
//                    val updatedAdapter = ArrayAdapter(
//                        applicationContext,
//                        android.R.layout.simple_spinner_item,
//                        updatedCountries
//                    )
//                    updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                    spinner2.adapter = updatedAdapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        UserInstance.fetchUser(userId!!) { _user ->
            if (_user != null) {
                user = _user
                updateUI(user)
            } else {
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
        }

        // Save button click listener
        val saveButton = findViewById<Button>(R.id.updateButton)
        saveButton.setOnClickListener {
            val nameEditText = findViewById<EditText>(R.id.NameEditText)
            val name = nameEditText.text.toString()
            val city = spinner.selectedItem.toString()
            val country = spinner2.selectedItem.toString()

            val updatedUser = User(
                user!!.userId,
                user!!.email, // Maintain the original email
                name,
                city,
                country,
                user!!.profilePic,
                user!!.bannerPic
            )

            UserInstance.updateUser(updatedUser) { success ->
                if (success) {
                    // Update successful
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MyProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Update failed
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to update UI with user data
    private fun updateUI(user: User? = null) {
        user?.let { user ->
            val profilePicture = findViewById<ImageView>(R.id.pfpimage)
            val profile = user.profilePic
            if(profile != "")
                Picasso.get().load(profile).into(profilePicture)

            val nameTextView = findViewById<EditText>(R.id.NameEditText)
            nameTextView.setText(user.fullName)

            val emailTextView = findViewById<TextView>(R.id.EmailEditText)
            emailTextView.text = user.email


            Log.d("EditProfileActivity", "User city: ${user.city}")
            val cityEditText = findViewById<Spinner>(R.id.CityEditText)
            val cityIndex = cities.indexOf(user.city)
            if (cityIndex != -1) {
                cityEditText.setSelection(cityIndex)
            }

            val countryEditText = findViewById<Spinner>(R.id.CountryEditText)
            countryEditText.setSelection(countries.indexOf(user.country))
        }
    }
}


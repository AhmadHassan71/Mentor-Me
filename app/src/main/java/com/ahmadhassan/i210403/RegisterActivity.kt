package com.ahmadhassan.i210403
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private var isCitySelected = false
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var spinner: Spinner
    private lateinit var spinner2: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("Users")
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        spinner = findViewById(R.id.CityEditText)
        spinner2 = findViewById(R.id.CountryEditText)
//        val loginButton: Button = findViewById(R.id.LoginButton)
//        loginButton.setOnClickListener {
//            // Start the HomeActivity when the login button is clicked
//            val intent = Intent(this@RegisterActivity, VerifyActivity::class.java)
//            startActivity(intent)
//        }



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
                    Toast.makeText(
                        applicationContext,
                        "Selected city: $selectedCity",
                        Toast.LENGTH_SHORT
                    ).show()

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

        val loginTextView: TextView = findViewById(R.id.LoginText)
        loginTextView.setOnClickListener {
            // Start the HomeActivity when the login button is clicked
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

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
                    Toast.makeText(
                        applicationContext,
                        "Selected country: $selectedCountry",
                        Toast.LENGTH_SHORT
                    ).show()
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

        val signupButton: Button = findViewById(R.id.SignUpButton)
        signupButton.setOnClickListener { signupUser() }
    }


        private fun signupUser() {
            val email = findViewById<EditText>(R.id.EmailEditText).text.toString()
            val password = findViewById<EditText>(R.id.PasswordEditText).text.toString()

            if (validateInput(email, password)) {
                createAccount(email, password)
            }
        }

        private fun validateInput(email: String, password: String): Boolean {
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                return false
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                return false
            }

            return true
        }

        private fun createAccount(email: String, password: String) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Signup successful
                        val userId = auth.currentUser!!.uid
                        saveUserDataToDatabase(userId)

                        // Start VerifyActivity
                        val intent = Intent(this,MyProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Handle Error
//                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this, "This Account already exists", Toast.LENGTH_SHORT).show()

                    }
                }
        }

        private fun saveUserDataToDatabase(userId: String) {
            val fullName = findViewById<EditText>(R.id.NameEditText).text.toString()
            val city = spinner.selectedItem.toString()
            val country = spinner2.selectedItem.toString()
            val email = findViewById<EditText>(R.id.EmailEditText).text.toString()
            val user = User(email, fullName, city, country)
            database = FirebaseDatabase.getInstance()
            databaseRef = database.getReference("Users")
            val userId = databaseRef.push().key

            databaseRef.child(userId.toString()).setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "User data saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                   Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                }
        }

        data class User(val email: String, val fullName: String? = null, val city: String, val country: String)
}

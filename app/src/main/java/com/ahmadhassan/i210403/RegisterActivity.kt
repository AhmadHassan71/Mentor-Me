package com.ahmadhassan.i210403
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class RegisterActivity : AppCompatActivity() {
    private var isCitySelected = false
    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference


        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

//        val loginButton: Button = findViewById(R.id.LoginButton)
//        loginButton.setOnClickListener {
//            // Start the HomeActivity when the login button is clicked
//            val intent = Intent(this@RegisterActivity, VerifyActivity::class.java)
//            startActivity(intent)
//        }

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

        val name = findViewById<EditText>(R.id.NameEditText).toString()
        val email = findViewById<EditText>(R.id.EmailEditText).toString()
        val password = findViewById<EditText>(R.id.PasswordEditText).toString()
        val contact = findViewById<EditText>(R.id.ContactEditText).toString()
        var city = ""
        var country = ""


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
                    city = selectedCity

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
                    country = selectedCountry
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


        val signUpButton: Button = findViewById(R.id.SignUpButton)
        signUpButton.setOnClickListener {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty() || city.isEmpty() || country.isEmpty()) {
                // Check if any field is empty
                Toast.makeText(this@RegisterActivity, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Check if email is in correct format using regex
                Toast.makeText(this@RegisterActivity, "Invalid email format", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(name, email, password, contact, city, country)
                databaseRef.child("Users").child(contact).setValue(user)
                val intent = Intent(this@RegisterActivity, VerifyActivity::class.java)
                startActivity(intent)
            }
        }



    }
}

data class User(val name: String, val email: String, val password: String, val contact: String, val city: String, val country: String)

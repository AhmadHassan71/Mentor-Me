package com.ahmadhassan.i210403
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RegisterActivity : AppCompatActivity() {
    private var isCitySelected = false
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        val loginButton: Button = findViewById(R.id.LoginButton)
        loginButton.setOnClickListener {
            // Start the HomeActivity when the login button is clicked
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
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

    }
}

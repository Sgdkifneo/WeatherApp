package com.example.weatherapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // Paste your OpenWeatherMap key between the quotes
    private val apiKey = "dd5a05277971767795dbd9243fc0aae7"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getWeather()
    }

    // STEP 8: Ask the weather website
    private fun getWeather() {
        val tvCity = findViewById<TextView>(R.id.tvCity)
        val tvMain = findViewById<TextView>(R.id.tvMain)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val ivIcon = findViewById<ImageView>(R.id.ivIcon)

        ApiClient.weatherService.getWeather("New York", apiKey)
            .enqueue(object : Callback<WeatherResponse> {

                // STEP 9: The website answered
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val weather = body?.weather?.firstOrNull()

                        if (body != null && weather != null) {
                            tvCity.text = body.name
                            tvMain.text = weather.main
                            tvDescription.text = weather.description

                            val iconUrl =
                                "https://openweathermap.org/img/wn/${weather.icon}@2x.png"
                            Glide.with(this@MainActivity).load(iconUrl).into(ivIcon)
                        } else {
                            showMessage("The answer was empty")
                        }
                    } else {
                        // STEP 10: The website said "no" (wrong key, etc.)
                        showMessage("Server error: ${response.code()}")
                    }
                }

                // STEP 10: The call failed completely (no internet, etc.)
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    showMessage("Failed: ${t.localizedMessage}")
                }
            })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
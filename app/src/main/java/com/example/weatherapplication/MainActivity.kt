package com.example.weatherapplication

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.widget.SearchView
import com.example.weatherapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//f28196c1d8f70622103dc7e0d9745dd6

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("Ahmedabad")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object: android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityname:String) {
        val retrofit=Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/").build().create(ApiInterface::class.java)

        val response=retrofit.getWeatherData(cityname,"f28196c1d8f70622103dc7e0d9745dd6","metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if (response.isSuccessful && responseBody!=null){
                    val temperature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windspeed=responseBody.wind.speed
                    val sunrise=responseBody.sys.sunrise.toLong()
                    val sunset=responseBody.sys.sunset.toLong()
                    val sealevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxtemp=responseBody.main.temp_max
                    val mintemp=responseBody.main.temp_min

                    binding.temperature.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxtemp.text="Max Temp: $maxtemp °C"
                    binding.mintemp.text="Min Temp: $mintemp °C"
                    binding.humidity.text="$humidity %"
                    binding.windspeed.text="$windspeed m/s"
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.sea.text="$sealevel hPa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityname.text="$cityname"

                    //Log.d("TAG", "onResponse: $temperature")

                    changeImagesAccordingToWeather(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    fun changeImagesAccordingToWeather(condition:String){
        when(condition){
            "Haze" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Partly Clouds", "Clouds","Overcast","Mist" ,"Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle","Moderate Rain","Showers" ,"Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow","Heavy Snow" ,"Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }
    fun dayName(timestamp:Long):String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    fun date():String{
        val sdf= SimpleDateFormat("dd MMM YYYY", Locale.getDefault())
        return sdf.format(Date())
    }

    fun time(timestamp: Long):String{
        val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
}
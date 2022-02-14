package com.example.project2

import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.project2.databinding.ActivityMainBinding
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val API_ID = "eacf014d6097a1e0c13fc0da7bfdd211"
    val WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather"

    val MIN_TIME: Long = 5000
    val MIN_DISTANCE = 1000f
    val REQUEST_CODE = 101


    var Location_Provider = LocationManager.GPS_PROVIDER

    /*var NameofCity: TextView? = null, var weatherState:TextView? = null, var Temperature:TextView? = null
    var mweatherIcon: ImageView? = null

    var mCityFinder: RelativeLayout? = null */


    var mLocationManager: LocationManager? = null
    var mLocationListner: LocationListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*weatherState = findViewById<View>(R.id.weatherCondition)
        Temperature = findViewById<View>(R.id.temperature)
        mweatherIcon = findViewById<View>(R.id.weatherIcon)
        mCityFinder = findViewById(R.id.cityFinder)
        NameofCity = findViewById(R.id.cityName)  */


        binding.cityFinder.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, cityFinder::class.java)
            startActivity(intent)
        })
    }

    override fun onResume() {
        super.onResume()
        val mIntent = intent
        val city = mIntent.getStringExtra("City")
        city?.let { getWeatherForNewCity(it) } ?: getWeatherForCurrentLocation()
    }

    private fun getWeatherForNewCity(city: String) {
        val params = RequestParams()
        params.put("q", city)
        params.put("appid", API_ID)
        letsdoSomeNetworking(params)
    }

    private fun getWeatherForCurrentLocation() {
        mLocationManager = getSystemService<Any>(Context.LOCATION_SERVICE) as LocationManager
        mLocationListner = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val Latitude: String = java.lang.String.valueOf(location.getLatitude())
                val Longitude: String = java.lang.String.valueOf(location.getLongitude())
                val params = RequestParams()
                params.put("lat", Latitude)
                params.put("lon", Longitude)
                params.put("appid", API_ID)
                letsdoSomeNetworking(params)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                //not able to get location
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }
        mLocationManager!!.requestLocationUpdates(
            Location_Provider,
            MIN_TIME,
            MIN_DISTANCE,
            mLocationListner
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Locationget Succesffully", Toast.LENGTH_SHORT)
                    .show()
                getWeatherForCurrentLocation()
            } else {
                //user denied the permission
            }
        }
    }


    private fun letsdoSomeNetworking(params: RequestParams) {
        val client = AsyncHttpClient()
        client.get(WEATHER_URL, params, object : JsonHttpResponseHandler() {
            fun onSuccess(statusCode: Int, headers: Array<Header?>?, response: JSONObject?) {
                Toast.makeText(this@MainActivity, "Data Get Success", Toast.LENGTH_SHORT).show()
                val weatherD: weatherData = weatherData.fromJson(response)
                updateUI(weatherD)


                // super.onSuccess(statusCode, headers, response);
            }

            fun onFailure(
                statusCode: Int,
                headers: Array<Header?>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        })
    }

    private fun updateUI(weather: weatherData) {
        binding.temperature.setText(weather.getmTemperature())
        binding.cityName.setText(weather.getMcity())
        binding.weatherCondition.setText(weather.getmWeatherType())
        val resourceID = resources.getIdentifier(
            weather.getMicon(), "drawable",
            packageName
        )
        binding.weatherIcon.setImageResource(resourceID)
    }

    override fun onPause() {
        super.onPause()
        if (mLocationManager != null) {
            mLocationManager!!.removeUpdates(mLocationListner!!)
        }
    }
}
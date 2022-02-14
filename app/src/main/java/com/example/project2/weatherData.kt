package com.example.project2

import org.json.JSONException
import org.json.JSONObject


class weatherData {
    private var mTemperature: String? = null
    var micon: String? = null
        private set
    var mcity: String? = null
        private set
    private var mWeatherType: String? = null
    private var mCondition = 0
    fun getmTemperature(): String {
        return "$mTemperature°C"
    }

    fun getmWeatherType(): String? {
        return mWeatherType
    }

    companion object {
        fun fromJson(jsonObject: JSONObject): weatherData? {
            return try {
                val weatherD = weatherData()
                weatherD.mcity = jsonObject.getString("name")
                weatherD.mCondition =
                    jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id")
                weatherD.mWeatherType =
                    jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
                weatherD.micon = updateWeatherIcon(weatherD.mCondition)
                val tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15
                val roundedValue = Math.rint(tempResult).toInt()
                weatherD.mTemperature = Integer.toString(roundedValue)
                weatherD
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            }
        }

        private fun updateWeatherIcon(condition: Int): String {
            if (condition >= 0 && condition <= 300) {
                return "thunderstrom1"
            } else if (condition >= 300 && condition <= 500) {
                return "lightrain"
            } else if (condition >= 500 && condition <= 600) {
                return "shower"
            } else if (condition >= 600 && condition <= 700) {
                return "snow2"
            } else if (condition >= 701 && condition <= 771) {
                return "fog"
            } else if (condition >= 772 && condition <= 800) {
                return "overcast"
            } else if (condition == 800) {
                return "sunny"
            } else if (condition >= 801 && condition <= 804) {
                return "cloudy"
            } else if (condition >= 900 && condition <= 902) {
                return "thunderstrom1"
            }
            if (condition == 903) {
                return "snow1"
            }
            if (condition == 904) {
                return "sunny"
            }
            return if (condition >= 905 && condition <= 1000) {
                "thunderstrom2"
            } else "dunno"
        }
    }
}
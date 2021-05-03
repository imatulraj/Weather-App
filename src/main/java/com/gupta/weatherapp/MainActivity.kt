@file:Suppress("DEPRECATION")

package com.gupta.weatherapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.solver.state.State
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
       var CITY: String = "delhi,IN"
        val API: String = "06c921750b9a82d8f5d1294e1586276f" // Use API key
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        SHOW_PROGRESS.visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.userfeed).visibility=View.VISIBLE
        errorText.visibility=View.GONE
        show.setOnClickListener {
            CITY = city.text.toString()
            weatherTask().execute()
        }
    }

    fun newoperation(view: View) {
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        SHOW_PROGRESS.visibility = View.GONE
        findViewById<ConstraintLayout>(R.id.userfeed).visibility=View.VISIBLE


    }
        inner class weatherTask() : AsyncTask<String, Void, String>() {
            override fun onPreExecute() {
                super.onPreExecute()
                findViewById<ConstraintLayout>(R.id.userfeed).visibility=View.GONE
                SHOW_PROGRESS.visibility = View.VISIBLE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.GONE
            }

            override fun doInBackground(vararg params: String?): String? {
                var response:String?
                try{
                    response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(Charsets.UTF_8)
                }catch (e: Exception){
                    response = null
                }
                return response
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                try {
                    /* Extracting JSON returns from the API */
                    val jsonObj = JSONObject(result)
                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                    val updatedAt:Long = jsonObj.getLong("dt")
                    val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                    val temp = main.getString("temp")+"°C"
                    val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                    val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                    val pressure = main.getString("pressure")
                    val humidity = main.getString("humidity")

                    val sunrise:Long = sys.getLong("sunrise")
                    val sunset:Long = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed")
                    val weatherDescription = weather.getString("description")

                    val address = jsonObj.getString("name")+", "+sys.getString("country")

                    /* Populating extracted data into our views */
                    findViewById<TextView>(R.id.address).text = address
                    findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                    findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                    findViewById<TextView>(R.id.temp).text = temp
                    findViewById<TextView>(R.id.min_temp).text = tempMin
                    findViewById<TextView>(R.id.max_temp).text = tempMax
                    findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                    findViewById<TextView>(R.id.sunrset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                    findViewById<TextView>(R.id.wind).text = windSpeed
                    findViewById<TextView>(R.id.pressure).text = pressure
                    findViewById<TextView>(R.id.humidity).text = humidity

                    /* Views populated, Hiding the loader, Showing the main design */
                            SHOW_PROGRESS.visibility = View.GONE
                    findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE


                } catch (e: Exception) {
                    SHOW_PROGRESS.visibility = View.GONE
                    findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
                }

            }
        }


}

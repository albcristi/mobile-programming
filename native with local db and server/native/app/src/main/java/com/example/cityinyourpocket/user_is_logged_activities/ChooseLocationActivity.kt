package com.example.cityinyourpocket.user_is_logged_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.cityinyourpocket.MainActivity
import com.example.cityinyourpocket.R
import com.example.cityinyourpocket.service.EventService
import com.example.cityinyourpocket.service.NetworkConnectionService
import kotlinx.android.synthetic.main.activity_choose_location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ChooseLocationActivity : AppCompatActivity() {
    private val eventService: EventService = EventService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_location)
        eventService.dbInit(this@ChooseLocationActivity)
        showEventsInTheCity.visibility = View.INVISIBLE;

        val userName: String? = intent.getStringExtra("USER_NAME")
        welcomeMessageUser.text = "Hello, ${userName}!"
        cityUserInput.doOnTextChanged { text, start, before, count ->
           handleCityUserInput()
       }
        MainActivity.wasOffline = !eventService.isOnline();
        showEventsInTheCity.setOnClickListener {
            val intent = Intent(this@ChooseLocationActivity, ListWithEventsMainPage::class.java);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("CITY", cityUserInput.text.toString());
            startActivity(intent);
        }
        val networkConnection = NetworkConnectionService(applicationContext);
        networkConnection.observe(this, Observer { isConnected ->
            if(isConnected){
                if(MainActivity.wasOffline){
                    eventService.performServerUpdates();
                    MainActivity.wasOffline = false
                    Toast
                        .makeText(
                            this,
                            "Device is back online",
                            Toast.LENGTH_LONG
                        )
                        .show()
                }
            }
            else{
                MainActivity.wasOffline = true;
                Toast
                    .makeText(this,
                        "Device is offline. Some features will not be available",
                        Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun handleCityUserInput(){
        val city :String = cityUserInput.text.toString()
        GlobalScope.launch(Dispatchers.Main) {
            if (eventService.cityHasEvents(city))
                this@ChooseLocationActivity.showEventsInTheCity.visibility = View.VISIBLE
            else
                this@ChooseLocationActivity.showEventsInTheCity.visibility = View.INVISIBLE
        }
    }
}
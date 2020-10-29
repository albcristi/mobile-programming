package com.example.cityinyourpocket.user_is_logged_activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.cityinyourpocket.R
import com.example.cityinyourpocket.service.EventService
import kotlinx.android.synthetic.main.activity_choose_location.*

class ChooseLocationActivity : AppCompatActivity() {
    private val eventService: EventService = EventService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_location)
        showEventsInTheCity.visibility = View.INVISIBLE;
        val userName: String? = intent.getStringExtra("USER_NAME")
        welcomeMessageUser.text = "Hello, ${userName}!"
        cityUserInput.doOnTextChanged { text, start, before, count ->
           handleCityUserInput()
       }

        showEventsInTheCity.setOnClickListener {
            val intent = Intent(this, ListWithEventsMainPage::class.java);
            intent.putExtra("USER_NAME", userName);
            intent.putExtra("CITY", cityUserInput.text.toString());
            startActivity(intent);
        }
    }

    private fun handleCityUserInput(){
        val city :String = cityUserInput.text.toString()
        if(eventService.cityHasEvents(city))
            showEventsInTheCity.visibility = View.VISIBLE
        else
            showEventsInTheCity.visibility = View.INVISIBLE

    }
}
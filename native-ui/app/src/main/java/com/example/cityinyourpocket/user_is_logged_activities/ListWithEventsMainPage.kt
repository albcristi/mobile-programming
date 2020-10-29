package com.example.cityinyourpocket.user_is_logged_activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaDrm
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityinyourpocket.R
import com.example.cityinyourpocket.adaptor.CityListAdaptor
import com.example.cityinyourpocket.model.Event
import com.example.cityinyourpocket.service.EventService
import com.example.cityinyourpocket.user_is_logged_activities.create_event.CreateEventActivity
import com.example.cityinyourpocket.user_is_logged_activities.event_details.EventDetails
import com.example.cityinyourpocket.user_is_logged_activities.user_owned_events.UserOwnedEvents
import kotlinx.android.synthetic.main.activity_list_with_events_main_page.*

private const val REQUEST_CODE = 1234;
private const val REQUEST_CODE_EVENT_DETAILS = 1235;

class ListWithEventsMainPage : AppCompatActivity(), CityListAdaptor.OnEventListener {
    var userName: String = "";
    var city: String = "";
    var eventService: EventService = EventService()
    private lateinit var events: MutableList<Event>
    private lateinit var adapter: CityListAdaptor;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_with_events_main_page)
        userName = intent.getStringExtra("USER_NAME").toString();
        city = intent.getStringExtra("CITY").toString();
        events = eventService.getEventsFromCity(city)
        adapter = CityListAdaptor(events, this);
        listOfCitiesId.adapter = adapter;
        listOfCitiesId.layoutManager = LinearLayoutManager(this)

        addNewEventButton.setOnClickListener {
            val intent : Intent = Intent(this@ListWithEventsMainPage, CreateEventActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        userOwnedEvents.setOnClickListener {
            if(eventService.getUserOwnedEvents(userName).size>0) {
                val intent: Intent =
                    Intent(this@ListWithEventsMainPage, UserOwnedEvents::class.java)
                intent.putExtra("USER_NAME", userName)
                startActivity(intent)
            }
            else{
                AlertDialog.Builder(this)
                    .setTitle("You have no created events!")
                    .setPositiveButton("OK", null)
                    .show()

            }
        }
    }

    private fun addNewEventInTheCurrentlyShowingEvents(event: Event){
        events.add(0,event);
        adapter.notifyItemInserted(0)
    }

    override fun onResume() {
        events = eventService.getEventsFromCity(city)
        adapter = CityListAdaptor(events, this);
        listOfCitiesId.adapter = adapter;
        listOfCitiesId.layoutManager = LinearLayoutManager(this)
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val event = data?.getSerializableExtra("NEW_EVENT") as Event
            event.hostUserName = userName
            eventService.addEvent(event)
            if(event.location.city == city)
                addNewEventInTheCurrentlyShowingEvents(event)
        }
        if(requestCode == REQUEST_CODE_EVENT_DETAILS && resultCode == Activity.RESULT_OK){
            val code = data?.getStringExtra("code").toString()
            val event = data?.getSerializableExtra("event") as Event
            if(code == "1")
                deleteRequired(event)

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onEventClickListener(position: Int) {
        val int = Intent(this@ListWithEventsMainPage, EventDetails::class.java)
        int.putExtra("event", events[position])
        var isOwner = "false"
        if(events[position].hostUserName == userName)
            isOwner="true"
        int.putExtra("is_owner", isOwner)
        startActivityForResult(int, REQUEST_CODE_EVENT_DETAILS)
    }

    private fun getEventPosition(id: Long): Int{
        var pos = -1;
        for(i in 0..events.size-1){
            if(events[i].evId == id)
                pos = i
        }
        return  pos
    }

    private fun deleteRequired(event: Event){
        eventService.removeEvent(event.evId)
        if(event.location.city == city || city == "all"){
            // remove event from our recycle view
            val pos = getEventPosition(event.evId)
            if(pos>-1){
                events.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                AlertDialog.Builder(this)
                    .setTitle("Event deleted")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
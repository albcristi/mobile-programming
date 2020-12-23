package com.example.cityinyourpocket.user_is_logged_activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaDrm
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityinyourpocket.MainActivity
import com.example.cityinyourpocket.R
import com.example.cityinyourpocket.adaptor.CityListAdaptor
import com.example.cityinyourpocket.model.Event
import com.example.cityinyourpocket.service.EventService
import com.example.cityinyourpocket.service.NetworkConnectionService
import com.example.cityinyourpocket.user_is_logged_activities.create_event.CreateEventActivity
import com.example.cityinyourpocket.user_is_logged_activities.event_details.EventDetails
import com.example.cityinyourpocket.user_is_logged_activities.user_owned_events.UserOwnedEvents
import kotlinx.android.synthetic.main.activity_list_with_events_main_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val REQUEST_CODE = 1234;
private const val REQUEST_CODE_EVENT_DETAILS = 1235;
private const val REQUEST_CODE_USER_OWNED_EVENTS = 1236;

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
        eventService.dbInit(this);
        MainActivity.wasOffline = !eventService.isOnline();
        GlobalScope.launch(Dispatchers.Main) {
            events = eventService.getEventsFromCity(city)
            adapter = CityListAdaptor(events, this@ListWithEventsMainPage);
            listOfCitiesId.adapter = adapter;
            listOfCitiesId.layoutManager = LinearLayoutManager(this@ListWithEventsMainPage)
            addNewEventButton.setOnClickListener {
                val intent: Intent =
                    Intent(this@ListWithEventsMainPage, CreateEventActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)
            }

            userOwnedEvents.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main){
                    if (eventService.getUserOwnedEvents(userName).size > 0) {
                        val intent: Intent =
                            Intent(this@ListWithEventsMainPage, UserOwnedEvents::class.java)
                        intent.putExtra("USER_NAME", userName)
                        startActivityForResult(intent, REQUEST_CODE_USER_OWNED_EVENTS)
                    } else {
                        AlertDialog.Builder(this@ListWithEventsMainPage)
                            .setTitle("You have no created events!")
                            .setPositiveButton("OK", null)
                            .show()

                    }
                }
            }
            val networkConnection = NetworkConnectionService(applicationContext);
            networkConnection.observe(this@ListWithEventsMainPage, Observer { isConnected ->
                if(isConnected){
                    if(MainActivity.wasOffline){
                        MainActivity.wasOffline = false
                        eventService.performServerUpdates()
                        Toast
                            .makeText(this@ListWithEventsMainPage,
                                "Device back online. Performing updates!",
                                Toast.LENGTH_LONG)
                            .show()

                    }
                }
                else{
                    MainActivity.wasOffline = true;
                    Toast
                        .makeText(this@ListWithEventsMainPage,
                            "Device is offline. Some features will not be available",
                            Toast.LENGTH_LONG)
                        .show()
                }
            })
        }
    }

    private fun addNewEventInTheCurrentlyShowingEvents(event: Event){
        events.add(0, event);
        adapter.notifyItemInserted(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val event = data?.getSerializableExtra("NEW_EVENT") as Event
            event.hostUserName = userName
            GlobalScope.launch (Dispatchers.Main) {
                val ev = eventService.addEvent(event);
                if(ev!=null)
                    event.evId = ev
                if (ev!=null && event.location.city == city || city == "all")
                    addNewEventInTheCurrentlyShowingEvents(event)
            }

        }
        if(requestCode == REQUEST_CODE_EVENT_DETAILS && resultCode == Activity.RESULT_OK){
            val code = data?.getStringExtra("code").toString()
            val event = data?.getSerializableExtra("event") as Event
            if(code == "1") {
                GlobalScope.launch(Dispatchers.IO) {
                    eventService.removeEvent(event);
                }.invokeOnCompletion {
                   GlobalScope.launch(Dispatchers.Main) {
                       if(!eventService.containsEvent(event))
                           deleteRequired(event);
                       else{
                           AlertDialog.Builder(this@ListWithEventsMainPage)
                               .setTitle("Delete failed")
                               .setPositiveButton("OK", null)
                               .show()
                       }
                   }
                }
            }
            if(code == "2") {
                updateListElement(event)
            }

        }
        if(requestCode == REQUEST_CODE_USER_OWNED_EVENTS && resultCode == Activity.RESULT_OK){
            var remove = data?.getIntegerArrayListExtra("to_remove")
            var update = data?.getIntegerArrayListExtra("to_update");
            GlobalScope.launch(Dispatchers.Main) {
                if (remove != null) {
                    for (ev: Int in remove)
                        removeById(ev.toLong())
                }
                if(update != null){
                    for(ev: Int in update){
                        val  evn = eventService.getEvent(ev.toLong())
                        if(evn!=null){
                            updateListElement(evn)
                        }
                    }
                }
            }
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

    private fun updateListElement(event: Event){
       var pos = this.getEventPosition(event.evId);
        if(pos == -1){
            pos = getEventByServerId(event.serverIdentifier)
        }
        if(pos>-1){
            events[pos] = event;
            adapter.notifyItemChanged(pos)
        }
    }


    private fun removeById(id: Long){
        val pos = getEventPosition(id)
        if(pos>-1){
            events.removeAt(pos)
            adapter.notifyItemRemoved(pos)
        }
    }

    private fun getEventByServerId(serverID: Int): Int{
        var pos = -1;
        for(i in 0..events.size-1){
            if(events[i].serverIdentifier == serverID)
                pos = i
        }
        return  pos
    }

    private fun deleteRequired(event: Event){
        if(event.location.city == city || city == "all"){
            // remove event from our recycle view
            var pos = getEventPosition(event.evId)
            if(pos==-1){
                pos = getEventByServerId(event.serverIdentifier)
            }
            if(pos>-1){
                events.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                AlertDialog.Builder(this@ListWithEventsMainPage)
                    .setTitle("Event deleted")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
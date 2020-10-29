package com.example.cityinyourpocket.user_is_logged_activities.user_owned_events

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityinyourpocket.R
import com.example.cityinyourpocket.adaptor.CityListAdaptor
import com.example.cityinyourpocket.model.Event
import com.example.cityinyourpocket.service.EventService
import com.example.cityinyourpocket.user_is_logged_activities.event_details.EventDetails
import kotlinx.android.synthetic.main.activity_user_owned_events.*


private const val REQUEST_CODE_EVENT_DETAILS = 1236;
class UserOwnedEvents : AppCompatActivity(), CityListAdaptor.OnEventListener {

    private val eventService: EventService = EventService()
    private lateinit var events: MutableList<Event>;
    private lateinit var adapter: CityListAdaptor;
    companion object {
       lateinit var user: String;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_owned_events)
        user = intent.getStringExtra("USER_NAME").toString();
        welcomeMsgUserOwnedEventsLayout.text = "Welcome, @${user}!"
        events = eventService.getUserOwnedEvents(user)
        adapter = CityListAdaptor(events, this)
        userOwnedEventsRecycleView.adapter = adapter
        userOwnedEventsRecycleView.layoutManager = LinearLayoutManager(this)
    }

    override fun onEventClickListener(position: Int) {
        val int = Intent(this, EventDetails::class.java)
        int.putExtra("event", events[position])
        int.putExtra("is_owner", "true")
        startActivityForResult(int, REQUEST_CODE_EVENT_DETAILS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE_EVENT_DETAILS && resultCode == Activity.RESULT_OK){
            val code = data?.getStringExtra("code").toString()
            val event = data?.getSerializableExtra("event") as Event
            if(code == "1")
                deleteRequired(event)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun deleteRequired(event: Event){
        eventService.removeEvent(event.evId)
        // remove event from our recycle view
        val pos = getEventPosition(event.evId)
        if(pos>-1) {
            events.removeAt(pos)
            adapter.notifyItemRemoved(pos)
            AlertDialog.Builder(this)
                .setTitle("Event deleted")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun getEventPosition(id: Long): Int{
        var pos = -1;
        for(i in 0..events.size-1){
            if(events[i].evId == id)
                pos = i
        }
        return  pos
    }

    override fun onResume() {
        events = eventService.getUserOwnedEvents(user)
        adapter = CityListAdaptor(events, this)
        userOwnedEventsRecycleView.adapter = adapter
        userOwnedEventsRecycleView.layoutManager = LinearLayoutManager(this)
        super.onResume()
    }

}
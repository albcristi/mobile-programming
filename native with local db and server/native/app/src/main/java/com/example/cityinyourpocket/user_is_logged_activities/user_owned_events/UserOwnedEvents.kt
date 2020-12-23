package com.example.cityinyourpocket.user_is_logged_activities.user_owned_events

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityinyourpocket.MainActivity
import com.example.cityinyourpocket.R
import com.example.cityinyourpocket.adaptor.CityListAdaptor
import com.example.cityinyourpocket.model.Event
import com.example.cityinyourpocket.service.EventService
import com.example.cityinyourpocket.service.NetworkConnectionService
import com.example.cityinyourpocket.user_is_logged_activities.event_details.EventDetails
import kotlinx.android.synthetic.main.activity_user_owned_events.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val REQUEST_CODE_EVENT_DETAILS = 1236;

class UserOwnedEvents : AppCompatActivity(), CityListAdaptor.OnEventListener {

    private val eventService: EventService = EventService()
    private lateinit var events: MutableList<Event>;
    private lateinit var adapter: CityListAdaptor;
    private var deletedEvents: MutableList<Long> = mutableListOf();
    private var updatedEvents: MutableList<Long> = mutableListOf();

    companion object {
       lateinit var user: String;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_owned_events)
        eventService.dbInit(this@UserOwnedEvents)
        MainActivity.wasOffline = !eventService.isOnline()
        user = intent.getStringExtra("USER_NAME").toString();
        welcomeMsgUserOwnedEventsLayout.text = "Welcome, @${user}!"
        GlobalScope.launch(Dispatchers.Main) {
            events = eventService.getUserOwnedEvents(user)
            adapter = CityListAdaptor(events, this@UserOwnedEvents)
            userOwnedEventsRecycleView.adapter = adapter
            userOwnedEventsRecycleView.layoutManager = LinearLayoutManager(this@UserOwnedEvents)
        }
        goBackTxtUser.setOnClickListener {
            val intent = Intent()
            intent.putIntegerArrayListExtra("to_remove", this.getArrayListOfElements(this.deletedEvents))
            intent.putIntegerArrayListExtra("to_update", this.getArrayListOfElements(this.updatedEvents))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        val networkConnection = NetworkConnectionService(applicationContext);
        networkConnection.observe(this, Observer { isConnected ->
            if(isConnected){
                if(MainActivity.wasOffline){
                    MainActivity.wasOffline = false
                    eventService.performServerUpdates()
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
                MainActivity.wasOffline = true
                Toast
                    .makeText(this,
                        "Device is offline. Some features will not be available",
                        Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun getArrayListOfElements(elems: MutableList<Long>): ArrayList<Int>{
        var arr = arrayListOf<Int>();
        for(elem: Long in elems)
            arr.add(elem.toInt())
        return arr
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
            if(code == "2"){
                updateRequired(event)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun deleteRequired(event: Event){
        GlobalScope.launch(Dispatchers.Main){
            eventService.removeEvent(event);
        }
        // remove event from our recycle view
        val pos = getEventPosition(event.evId)
        if(pos>-1) {
            this.deletedEvents.add(event.evId);
            removeFromUpdated(event.evId)
            events.removeAt(pos)
            adapter.notifyItemRemoved(pos)
            AlertDialog.Builder(this@UserOwnedEvents)
                .setTitle("Event deleted")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun updateRequired(event: Event){
        val pos = getEventPosition(event.evId)
        this.updatedEvents.add(event.evId);
        if(pos == -1)
            return;
        events[pos] = event;
        adapter.notifyItemChanged(pos);
    }

    private fun removeFromUpdated(evid: Long){
        var pos = -1;
        for(i in 0..this.updatedEvents.size-1)
            if(this.updatedEvents[i] == evid)
                pos = i;
        if(pos > -1)
            this.updatedEvents.removeAt(pos);
    }

    private fun getEventPosition(id: Long): Int{
        var pos = -1;
        for(i in 0..events.size-1){
            if(events[i].evId == id)
                pos = i
        }
        return  pos
    }

}
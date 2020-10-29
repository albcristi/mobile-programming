package com.example.cityinyourpocket.user_is_logged_activities.event_details

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.cityinyourpocket.R
import com.example.cityinyourpocket.model.Event
import com.example.cityinyourpocket.service.EventService
import com.example.cityinyourpocket.user_is_logged_activities.ListWithEventsMainPage
import com.example.cityinyourpocket.user_is_logged_activities.create_event.CreateEventActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_event_details.*
import java.lang.Exception


private const val REQUEST_CODE_UPDATE : Int = 1347;

class EventDetails : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var event: Event
    private lateinit var mapView: MapView
    private lateinit var isOwner: String
    private lateinit var mapObj: GoogleMap
    private val eventService : EventService = EventService()
    /*
    ACTIVITY RETURNS 0 - if no changes are needed
                     1 - if a delete operation needs
                     to be performed
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)
        event = intent.getSerializableExtra("event") as Event
        titleEventDetails.text = event.name
        descriptionEventDetails.text = event.description
        startDateEventDetails.text = "Opening date: ${event.openHour}, ${event.startDate} \n Ends: ${event.endDate}"
        hostEventDetails.text = "Hosted by @${event.hostUserName}"
        isOwner = intent.getStringExtra("is_owner").toString()

        val mapViewBundle = savedInstanceState?.getBundle("AIzaSyB2en9pUK09-dYhzdOqW0mPdMjE5Z7Zx1c")
        mapView = findViewById(R.id.eventLocationEventDetails)
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)

        if(isOwner!="true"){
            removeEventDetails.isVisible = false
            editEventDetail.isVisible = false
        }
        else{
            // set event handlers
            try {
                // HANDLER FOR REMOVE
                removeEventDetails.setOnClickListener {
                    val dialog = AlertDialog.Builder(this)
                        .setTitle("Remove Event")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Remove Event", null)
                        .show()
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setOnClickListener {
                            dialog.dismiss()
                            val  intent = Intent()
                            intent.putExtra("event", event)
                            intent.putExtra("code", "1")
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                }
            }
            catch (e: Exception){
                postMessage(e.message.toString())
            }
            // HANDLER FOR UPDATE
            editEventDetail.setOnClickListener {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Edit Event")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Edit Event", null)
                    .show()

                dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setOnClickListener {
                        dialog.dismiss()
                        val  intent = Intent(this@EventDetails, CreateEventActivity::class.java)
                        startActivityForResult(intent, REQUEST_CODE_UPDATE);
                    }

            }

        }

        backButtonEventDetails.setOnClickListener{
            val intent = Intent()
            intent.putExtra("event", event)
            intent.putExtra("code", "0")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    private fun postMessage(errMsg: String){
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }

    override fun onMapReady(map: GoogleMap) {
        mapObj = map
        map.addMarker(MarkerOptions()
            .position(LatLng(event.location.latitude, event.location.longitude))
            .title(event.name))
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isTiltGesturesEnabled = true
        map.moveCamera(CameraUpdateFactory
            .newLatLngZoom(LatLng(event.location.latitude, event.location.longitude), 13f))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(REQUEST_CODE_UPDATE == requestCode && resultCode == Activity.RESULT_OK){
            val updated = data?.getSerializableExtra("NEW_EVENT") as Event
            updated.hostUserName = event.hostUserName
            updated.evId = event.evId
            eventService.updateEvent(updated)
            event = eventService.getEvent(event.evId)!!
            titleEventDetails.text = event.name
            descriptionEventDetails.text = event.description
            startDateEventDetails.text = "Opening date: ${event.openHour}, ${event.startDate} \n Ends: ${event.endDate}"
            hostEventDetails.text = "Hosted by @${event.hostUserName}"
            mapObj.clear()
            mapObj.addMarker(MarkerOptions()
                .position(LatLng(event.location.latitude, event.location.longitude))
                .title(event.name))
            mapObj.moveCamera(CameraUpdateFactory
                .newLatLngZoom(LatLng(event.location.latitude, event.location.longitude), 13f))

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }


    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
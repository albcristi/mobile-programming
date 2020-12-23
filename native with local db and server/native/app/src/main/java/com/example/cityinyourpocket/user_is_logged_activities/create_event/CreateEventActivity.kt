package com.example.cityinyourpocket.user_is_logged_activities.create_event

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.cityinyourpocket.R
import com.example.cityinyourpocket.model.Event
import com.example.cityinyourpocket.model.EventLocation
import com.example.cityinyourpocket.service.EventService
import com.example.cityinyourpocket.validator.EventValidator

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.modal_create_event_layout.*
import java.lang.Exception
import java.util.*

class CreateEventActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var eventService = EventService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        eventService.dbInit(this@CreateEventActivity);
        if(eventService.isOnline()) {
            // give user a hint on how he should create a new event
            mapFragment.view?.let {
                Snackbar.make(
                    it, "Long press to select location for your event!",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("OK", {})
                    .show()
            };
        }
        else{
            handleLocationSelection(LatLng(0.0,0.0))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener { latLng ->
            Toast.makeText(this, "Selected city: "+getCity(latLng), Toast.LENGTH_SHORT).show()
            // handle the long press on the map: LOCATION SELECTION
            handleLocationSelection(latLng)
        }
    }

    private fun handleLocationSelection(latLong: LatLng){
        val modal = LayoutInflater.from(this)
            .inflate(R.layout.modal_create_event_layout, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Create Event")
            .setView(modal)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Create Event", null)
            .show()
        // handle the click on the Create Event button
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setOnClickListener {
                // check to see if user introduced data is correct
                try {
                    val eventName: String =
                        modal.findViewById<TextView>(R.id.modalEventTitleCreateEventLayout)
                            .text.toString();
                    val eventDescription: String =
                        modal.findViewById<TextView>(R.id.descriptionCreateEventModalLayout)
                            .text.toString();
                    val startDate: String =
                        modal.findViewById<TextView>(R.id.startDateCreateEventModalLayout)
                        .text.toString();
                    val endDate: String =
                        modal.findViewById<TextView>(R.id.endDateCreateEventModalLayout)
                            .text.toString();
                    val startHour: String =
                        modal.findViewById<TextView>(R.id.startHourCreateEventModalLayout)
                            .text.toString()

                    var location = EventLocation(getCity(latLong), latLong.longitude, latLong.latitude)
                    val event = Event(
                        name = eventName,
                        description = eventDescription,
                        startDate = startDate,
                        endDate = endDate,
                        hostUserName = "",
                        evId = 0,
                        location = location,
                        openHour = startHour,
                        serverIdentifier = 0
                    )

                    var preliminaryChecksOnEvent: Boolean
                    val validator = EventValidator(toBeValidated = event)
                    preliminaryChecksOnEvent = validator.eventNameIsOk() &&
                            validator.startDateIsValid()  &&
                            validator.endDateIsValid() &&
                            validator.hourIsValid()
                    if (!preliminaryChecksOnEvent) {
                        postErrorMessage("Make sure data is correct!")
                        return@setOnClickListener
                    }
                    // EVENT SHOULD BE CREATED AND DATA RETURNED TO PARENT ACTIVITY
                    dialog.dismiss()
                    val intent = Intent()
                    intent.putExtra("NEW_EVENT", event)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                catch (e: Exception){
                    postErrorMessage(e.message.toString())
                    return@setOnClickListener
                }

            }
    }


    private fun postErrorMessage(errMsg: String){
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show()
    }

    private  fun getCity(latLng: LatLng): String{
        if(latLng.latitude == 0.0 && latLng.longitude ==0.0){
            return "unknown";
        }
        val geo: Geocoder = Geocoder(this, Locale.getDefault())
        val lst = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
        var res ="unknown"

        try {
            if(lst == null)
                return res
            if (lst[0] != null)
                res = lst[0]!!.locality
            return res
        }
        catch (e: Exception){
            return "unknown"
        }
    }
}



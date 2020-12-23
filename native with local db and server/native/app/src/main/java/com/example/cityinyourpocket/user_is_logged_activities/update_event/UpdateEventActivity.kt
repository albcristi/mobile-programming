package com.example.cityinyourpocket.user_is_logged_activities.update_event

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.cityinyourpocket.R
import com.example.cityinyourpocket.model.Event
import com.example.cityinyourpocket.model.EventLocation
import com.example.cityinyourpocket.service.EventService
import com.example.cityinyourpocket.validator.EventValidator
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.util.*

class UpdateEventActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var eventService = EventService()
    private var event: Event? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_event)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map2) as SupportMapFragment
        mapFragment.getMapAsync(this)
        eventService.dbInit(this@UpdateEventActivity);
        event = intent.getSerializableExtra("event") as Event
        if(eventService.isOnline()) {
            // give user a hint on how he should create a new event
            mapFragment.view?.let {
                Snackbar.make(
                    it, "Long press to select location for your event! If you don't want to change"+
                    " the location, just select a random one!",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("OK", {})
                    .show()
            };
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

    private fun handleLocationSelection(latLong: LatLng){
        val modal = LayoutInflater.from(this)
            .inflate(R.layout.modal_update_event, null)
        modal.findViewById<TextView>(R.id.modalEventTitleCreateEventLayout).text = event!!.name
        modal.findViewById<TextView>(R.id.descriptionCreateEventModalLayout).text = event!!.description
        modal.findViewById<TextView>(R.id.startDateCreateEventModalLayout).text = event!!.startDate
        modal.findViewById<TextView>(R.id.endDateCreateEventModalLayout).text = event!!.endDate
        modal.findViewById<TextView>(R.id.startHourCreateEventModalLayout).text = event!!.openHour

        val dialog = AlertDialog.Builder(this)
            .setTitle("Update Event")
            .setView(modal)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Update", null)
            .show()
        // handle the click on the Create Event button
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setOnClickListener {
                // check to see if user introduced data is correct
                try {
                    val eventName: String =
                        modal.findViewById<TextView>(R.id.modalEventTitleCreateEventLayout)
                            .text.toString();
                    event!!.name = eventName
                    val eventDescription: String =
                        modal.findViewById<TextView>(R.id.descriptionCreateEventModalLayout)
                            .text.toString();
                    event!!.description = eventDescription
                    val startDate: String =
                        modal.findViewById<TextView>(R.id.startDateCreateEventModalLayout)
                            .text.toString();
                    event!!.startDate = startDate
                    val endDate: String =
                        modal.findViewById<TextView>(R.id.endDateCreateEventModalLayout)
                            .text.toString();
                    event!!.endDate = endDate
                    val startHour: String =
                        modal.findViewById<TextView>(R.id.startHourCreateEventModalLayout)
                            .text.toString()
                    event!!.openHour = startHour
                    var location: EventLocation = event!!.location
                    if(modal.findViewById<CheckBox>(R.id.modifyLocation).isChecked)
                        location = EventLocation(getCity(latLong), latLong.longitude, latLong.latitude)
                    event!!.location = location
                    var preliminaryChecksOnEvent: Boolean
                    val validator = EventValidator(toBeValidated = event!!)
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
                    intent.putExtra("UPDATED_EVENT", event!!)
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

}
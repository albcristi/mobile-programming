package com.example.cityinyourpocket.model

import org.json.JSONObject
import java.io.Serializable


class Event(var name: String,
            var description: String,
            var hostUserName: String,
            var location: EventLocation,
            var startDate: String,
            var endDate: String,
            var openHour: String,
            var evId: Long,
            var serverIdentifier: Int): Serializable{

    companion object{
        fun fromDTOtoEvent(jsonObject: JSONObject): Event{
            return Event(jsonObject.getString("title"), jsonObject.getString("description"), jsonObject.getString("host"),
            EventLocation(jsonObject.getString("city"), jsonObject.getDouble("long"), jsonObject.getDouble("lat")),
                jsonObject.getString("start_date"), jsonObject.getString("end_date"), jsonObject.getString("start_hour"),
                0, jsonObject.getInt("event_id")
            )
        }
    }
}
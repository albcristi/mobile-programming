package com.example.cityinyourpocket.model

import java.io.Serializable

class Event(var name: String,
            var description: String,
            var hostUserName: String,
            var location: EventLocation,
            var startDate: String,
            var endDate: String,
            var openHour: String,
            var evId: Long): Serializable{

}
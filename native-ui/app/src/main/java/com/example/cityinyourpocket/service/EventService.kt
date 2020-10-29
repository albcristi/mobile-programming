package com.example.cityinyourpocket.service
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.cityinyourpocket.model.Event
import com.example.cityinyourpocket.model.EventLocation
import kotlin.math.log

class EventService {
    private
    companion object{
        var  cities: MutableList<String> = mutableListOf()
        var events: MutableList<Event> = mutableListOf()
    }

    constructor() {
        if(cities.size == 0) {
            cities.add("Cluj-Napoca")
            cities.add("Bologa")
            cities.add("Bucharest")
            var loc1 = EventLocation(
                city = "Cluj-Napoca",
                latitude = 46.769896,
                longitude = 23.578607
            );
            var loc2 = EventLocation(
                city = "Bologa",
                latitude = 46.880920,
                longitude = 22.877560
            );
            var loc3 = EventLocation(
                city = "Bucharest",
                latitude = 44.470731,
                longitude = 26.082503
            )
            events.add(
                Event(
                    "Beer Festival", "Beer and more beer", "mirela",
                    loc1, "2020-10-20", "2020-10-22", "18:00", 1
                )
            )
            events.add(
                Event(
                    "Some History", "History and beer", "cristi",
                    loc2, "2020-10-20", "2020-10-22", "18:00", 2
                )
            )
            events.add(
                Event(
                    "Beer Festival", "Beer and more beer", "mirela",
                    loc3, "2020-10-20", "2020-10-22", "18:00", 3
                )
            )
            events.add(
                Event(
                    "Programming and pizza", "Lots of Java and some pizza", "cristi",
                    loc1, "2020-10-20", "2020-10-22", "18:00", 4
                )
            )
        }
    }

    fun cityHasEvents(city: String): Boolean{
        if(city=="all")
            return true
        for(currentCity in EventService.cities)
            if(currentCity == city)
                return true
        return false
    }


    fun getEventsFromCity(city: String): MutableList<Event>{
        if(city == "all"){
            var res = mutableListOf<Event>()
            for(event in EventService.events)
                res.add(event)
            return res
        }

        var result = mutableListOf<Event>()
        for(event in EventService.events)
            if(event.location.city == city)
                result.add(event);

        return  result;
    }

    fun getNextId(): Long{
        var nextID : Long = 0;
        for(event in events)
            if(nextID < event.evId)
                nextID = event.evId
        nextID+=1
        return nextID
    }

    fun addEvent(event: Event){
        val id : Long = getNextId()
        event.evId = id;
        events.add(event)
    }

    fun getUserOwnedEvents(user: String): MutableList<Event>{
        var result = mutableListOf<Event>()
        for(event in EventService.events)
            if(event.hostUserName == user)
                result.add(event);

        return  result;
    }


    fun removeEvent(id: Long): Boolean{
        var pos = -1;
        for(i in 0..events.size-1)
            if(events[i].evId == id)
                pos = i;
        if(pos>-1) {
            events.removeAt(pos)
            return true
        }
        return false
    }


    fun updateEvent(event: Event){
        for(ev in events){
            if(ev.evId == event.evId){
                ev.name = event.name
                ev.description = event.description
                ev.location.city = event.location.city
                ev.location.latitude = event.location.latitude
                ev.location.longitude = event.location.longitude
                ev.startDate = event.startDate
                ev.endDate = event.endDate
                ev.openHour = event.openHour
            }
        }
    }

    fun getEvent(id: Long): Event?{
        for(event in events)
            if(event.evId==id)
                return event
        return null
    }
}
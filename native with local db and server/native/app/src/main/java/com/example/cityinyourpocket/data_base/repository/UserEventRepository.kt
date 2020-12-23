package com.example.cityinyourpocket.data_base.repository

import com.example.cityinyourpocket.data_base.dao.UserEventDao
import com.example.cityinyourpocket.data_base.entities.UserEventEntity

class UserEventRepository(private val eventDao: UserEventDao) {

    suspend fun createUserEvent(userEventEntity: UserEventEntity): Long?{
        eventDao.createUserEvent(userEventEntity);
        val act = eventDao.getLatestId();
        return act;
    }

    suspend fun getAllEventsFromCity(city: String): MutableList<UserEventEntity>{
        if(city=="all"){
            return eventDao.getAllEvents();
        }
        return eventDao.getAllEventsFromCity(city);
    }

    suspend fun cityHasEvents(city: String): Boolean{
        return this.getAllEventsFromCity(city).size > 0;
    }

    suspend fun getAllEventsFromUser(hostName: String): MutableList<UserEventEntity>{
        return eventDao.getAllUserOwnedEvents(hostName)
    }

    suspend fun userHasEvents(hostName: String): Boolean{
        return eventDao.getAllUserOwnedEvents(hostName).size > 0;
    }

    suspend fun removeEvent(event: UserEventEntity){
        eventDao.deleteUserEvent(event);
        eventDao.removeEventSpecial(event.event_id);
    }

    suspend fun containsEvent(id: Long): Boolean{
        return eventDao.containsEvent(id) > 0;
    }

    suspend fun updateEvent(entity: UserEventEntity): Boolean{
        if(!containsEvent(entity.event_id))
            return false;
        eventDao.updateUserEvent(entity)
        println(getEvent(entity.event_id))
        println(checkUpdate(entity))
        return checkUpdate(event = entity)

    }

    private suspend fun checkUpdate(event: UserEventEntity): Boolean{
        val storedEvent = getEvent(event.event_id) ?: return false;
        return (storedEvent.city == event.city) &&
                (storedEvent.description == event.description) &&
                (storedEvent.startHour == event.startHour) &&
                (storedEvent.startDate == event.startDate) &&
                (storedEvent.endDate == event.endDate) &&
                (storedEvent.host_name == event.host_name) &&
                (storedEvent.city == event.city) &&
                (storedEvent.longitude == event.longitude) &&
                (storedEvent.lat == event.lat) &&
                (storedEvent.server == event.server)
    }

    suspend fun getEvent(ev: Long): UserEventEntity?{
        return eventDao.getEvent(ev);
    }

    suspend fun getLocallyStoredEvents(): MutableList<UserEventEntity>{
        return eventDao.getLocalStoredEvents();
    }

    suspend fun getEventLocalIDByServerId(serverID: Int): Long{
        val res = eventDao.getEventByServerId(serverID)
        if(res.size == 0)
            return -1
        return res[0].event_id
    }
}
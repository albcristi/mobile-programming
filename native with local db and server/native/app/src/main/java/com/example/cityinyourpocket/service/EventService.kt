package com.example.cityinyourpocket.service
import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.JsonWriter
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cityinyourpocket.data_base.db_config.LocalDatabase
import com.example.cityinyourpocket.data_base.entities.UserEventEntity
import com.example.cityinyourpocket.data_base.repository.UserEventRepository
import com.example.cityinyourpocket.model.Event
import com.example.cityinyourpocket.model.EventLocation
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.locks.ReentrantLock


class EventService {
    private var eventRepository: UserEventRepository? = null;
    private var hasConnection: Boolean = false;

    private var context: Context? = null;

    companion object{
        var  cities: MutableList<String> = mutableListOf()
        var events: MutableList<Event> = mutableListOf()
        var BASE_URL = "http://192.168.1.2:8000/api/event/";
        val lock = ReentrantLock();
    }

    fun toModel(entity: UserEventEntity): Event {
        return Event(entity.event_name, entity.description, entity.host_name,
        EventLocation(entity.city,entity.longitude, entity.lat), entity.startDate,
        entity.endDate, entity.startHour, entity.event_id, entity.server)
    }

    fun toEntity(model: Event): UserEventEntity{
        return UserEventEntity(
            model.evId, model.hostUserName, model.name, model.location.city,
            model.location.longitude, model.location.latitude, model.openHour,
            model.startDate, model.endDate, model.description, model.serverIdentifier
        )
    }

    fun dbInit(context: Context){
        this.context = context;
        var eventDao = LocalDatabase.getDatabase(context).userEventDao();
        this.eventRepository = UserEventRepository(eventDao);
        this.hasConnection = true;
    }

    fun isOnline(): Boolean{
        if(this.context == null)
            return false;
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        cm.apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } ?: false
        }
    }


    fun performServerUpdates(){
        GlobalScope.launch (Dispatchers.IO){
            lock.lock()
            val toBeSent =
                eventRepository!!.getLocallyStoredEvents()
            for(event in toBeSent){
                val id = addEventServer(toModel(event))
                event.server = id
                eventRepository!!.updateEvent(event)
            }
            if(lock.isHeldByCurrentThread)
                lock.unlock();
        }
    }

    suspend fun getCityEventsAfterDeviceBackOnline(city: String): MutableList<Event>{
        lock.lock()
        val events = getEventsFromCityAsync(city).await()
        lock.unlock()
        return events
    }

    suspend fun cityHasEvents(city: String): Boolean{
        return cityHasEventsAsync(city).await();
    }

    private suspend fun cityHasEventsAsync(city: String): Deferred<Boolean>{
       return GlobalScope.async(Dispatchers.IO) {
           if (city == "all")
               return@async true
           if(isOnline()){
                return@async cityHasEventsAsyncServer(city)
           }
           if (!hasConnection)
                return@async false
           return@async eventRepository!!.cityHasEvents(city)
        }
    }

    private fun cityHasEventsAsyncServer(city: String): Boolean{
        val url = "${BASE_URL}has-events/city=${city}";
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val httpClient = OkHttpClient()
        httpClient.newCall(request)
            .execute()
            .use { response ->
                if(!response.isSuccessful)
                    return false;
                val json = JSONObject(response.body!!.string())
                return json.getBoolean("message");
            }
    }


    suspend fun getEventsFromCity(city: String): MutableList<Event>{
      return getEventsFromCityAsync(city).await();
    }

    private suspend fun getEventsFromCityAsync(city: String): Deferred<MutableList<Event>>{
        return GlobalScope.async(Dispatchers.IO){
            if(isOnline()){
                return@async getEventsFromCityServer(city)
            }
            if(!hasConnection){
                return@async mutableListOf<Event>();
            }
            var dbData = eventRepository!!.getAllEventsFromCity(city);
            var events = mutableListOf<Event>()
            for(event: UserEventEntity in dbData)
                events.add(toModel(event))
            return@async events;
        }
    }

    private suspend fun getEventsFromCityServer(city: String): MutableList<Event>{
        val url = "${BASE_URL}city-events/city=${city}"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val httpClient = OkHttpClient()
        httpClient.newCall(request)
            .execute()
            .use {response ->
                if(!response.isSuccessful)
                    return mutableListOf()
                val jsonArrayList = JSONObject(response.body!!.string()).getJSONArray("events")
                var elements = mutableListOf<Event>()
                for(index in 0 until jsonArrayList.length()){
                    var jsonObject = jsonArrayList[index] as JSONObject
                    var event = Event.fromDTOtoEvent(jsonObject)
                    var id: Long = eventRepository!!.getEventLocalIDByServerId(event.serverIdentifier)
                    event.evId = id
                    elements.add(event)
                }
                return elements
            }
    }

    suspend fun addEvent(event: Event): Long?{
        return addEventAsync(event).await()
    }

    private fun addEventAsync(event: Event): Deferred<Long?>{
        return GlobalScope.async(Dispatchers.IO){
            event.evId = 0;
            event.serverIdentifier = -1;
            if(isOnline()){
                val serverId = addEventServer(event);
                event.serverIdentifier = serverId
            }
            val ev = eventRepository!!.createUserEvent(toEntity(event));
            return@async ev;
        }
    }

    private fun addEventServer(event: Event): Int{
        /*
        Performs an add operation and returns the id assigned
        to the newly created event in the server side
         */
        val url = "${BASE_URL}new-event";
        val serializedEvent = Gson().toJson(event);
        val requestBody: RequestBody = FormBody.Builder()
            .add("event", serializedEvent)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build();
        val client = OkHttpClient()
        client.newCall(request)
            .execute()
            .use { response ->
                if (response.isSuccessful) {
                    val res = JSONObject(response.body!!.string());
                    return res.getInt("event")
                }
                return -1
            }
    }

    suspend fun getUserOwnedEvents(user: String): MutableList<Event>{
       return this.getUserOwnedEventsAsync(user).await();
    }

    private suspend fun getUserOwnedEventsAsync(user: String): Deferred<MutableList<Event>>{
        return GlobalScope.async (Dispatchers.IO){
            if(!isOnline()) {
                val events = eventRepository!!.getAllEventsFromUser(user);
                val transformed = mutableListOf<Event>();
                for (event: UserEventEntity in events)
                    transformed.add(toModel(event))
                return@async transformed
            }
            return@async getUserOwnedEventsFromServer(user)
        }
    }

    private suspend fun getUserOwnedEventsFromServer(hostName: String): MutableList<Event>{
        val url = "${BASE_URL}owned-events/user=$hostName"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val httpClient = OkHttpClient()
        httpClient.newCall(request)
            .execute()
            .use { response ->
                if(!response.isSuccessful)
                    return mutableListOf()
                val jsonArrayList = JSONObject(response.body!!.string()).getJSONArray("events")
                val elements = mutableListOf<Event>()
                for(index in 0 until jsonArrayList.length()){
                    val jsonObject = jsonArrayList[index] as JSONObject
                    val event = Event.fromDTOtoEvent(jsonObject)
                    val id: Long = eventRepository!!.getEventLocalIDByServerId(event.serverIdentifier)
                    event.evId = id
                    elements.add(event)
                }
                return elements
            }
    }

    suspend fun removeEvent(event: Event): Boolean{
        return removeEventAsync(toEntity(event)).await();
    }

    private suspend fun removeEventAsync(event: UserEventEntity): Deferred<Boolean>{
        return GlobalScope.async(Dispatchers.IO){
            if(!isOnline())
                return@async false;
            val result = removeEventServer(event.server);
            println("Contained in db ${eventRepository!!.containsEvent(event.event_id)},id=${event.event_id}")
//            if(eventRepository!!.containsEvent(event.event_id) && !result){
//                eventRepository!!.removeEvent(event);
//            }
            println("Server result: ${result}");
            if(result) {
                eventRepository!!.removeEvent(event);
                println("Local DB result: "+!eventRepository!!.containsEvent(event.event_id))
                return@async !eventRepository!!.containsEvent(event.event_id)
            }
            return@async result;
        }
    }

    private fun removeEventServer(serverId: Int): Boolean{
        val url = "${BASE_URL}instance/${serverId}";
        val request = Request.Builder()
            .url(url)
            .delete()
            .build();
        val client = OkHttpClient()
        client.newCall(request)
            .execute()
            .use { response ->
                if(!response.isSuccessful)
                    return false
                return JSONObject(response.body!!.string()).getBoolean("result");
            }
    }

    suspend fun updateEvent(event: Event): Boolean{
        return updateEventAsync(event).await()
    }

   private suspend fun updateEventAsync(event: Event): Deferred<Boolean>{
        return GlobalScope.async(Dispatchers.IO){
            if(!isOnline())
                return@async false
            val url = "${BASE_URL}instance/${event.serverIdentifier}"
            val body: RequestBody = FormBody.Builder()
                .add("event", Gson().toJson(event))
                .build()
            val request = Request.Builder()
                .url(url)
                .put(body)
                .build()
            val client = OkHttpClient()
            client.newCall(request)
                .execute()
                .use { response ->
                    if(!response.isSuccessful)
                        return@async false
                    val obj = JSONObject(response.body!!.string())
                    if(obj.getBoolean("result")) {
                        if (eventRepository!!.containsEvent(event.evId))
                            return@async eventRepository!!.updateEvent(toEntity(event))
                        return@async true
                    }
                    return@async false
                }
        }
    }

    suspend fun getEvent(ev: Long): Event? {
        return getEventAsync(ev).await()?.let { toModel(it) }
    }

    private suspend fun getEventAsync(ev: Long): Deferred<UserEventEntity?>{
        return GlobalScope.async(Dispatchers.IO){
            var ev: UserEventEntity? = eventRepository!!.getEvent(ev)
            if(ev ==null) {
                return@async UserEventEntity(
                    0, "", "", "",
                    0.0, 0.0, "", "", "", "",-1
                );
            }
            else {
                return@async ev
            }
        }
    }

    suspend fun containsEvent(event: Event): Boolean{
        return containsEventAsync(event).await();
    }

    private fun containsEventAsync(event: Event): Deferred<Boolean>{
        return GlobalScope.async(Dispatchers.IO) {
            val result = eventRepository!!.containsEvent(event.evId);
            return@async result;
        }
    }

}
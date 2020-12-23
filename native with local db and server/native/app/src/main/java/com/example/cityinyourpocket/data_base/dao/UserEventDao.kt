package com.example.cityinyourpocket.data_base.dao

import androidx.room.*
import com.example.cityinyourpocket.data_base.entities.UserEventEntity
import com.example.cityinyourpocket.model.User

@Dao
interface UserEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUserEvent(userEvent: UserEventEntity)

    @Update
    suspend fun updateUserEvent(userEvent: UserEventEntity)

    @Query("SELECT * FROM EventTable")
    suspend fun getAllEvents(): MutableList<UserEventEntity>

    @Query("SELECT * FROM EventTable WHERE city=:cityLocation")
    suspend fun getAllEventsFromCity(cityLocation: String): MutableList<UserEventEntity>

    @Query("SELECT * FROM EventTable WHERE host_name=:hostUser")
    suspend fun getAllUserOwnedEvents(hostUser: String): MutableList<UserEventEntity>

    @Delete
    suspend fun deleteUserEvent(userEvent: UserEventEntity)

    @Query("SELECT COUNT(*) FROM EventTable WHERE event_id=:someId")
    suspend fun containsEvent(someId: Long): Int

    @Query( "SELECT * FROM EventTable WHERE event_id=:someId")
    suspend fun getEvent(someId: Long): UserEventEntity?

    @Query("DELETE FROM EventTable where event_id=:someId")
    suspend fun removeEventSpecial(someId: Long)

    @Query("SELECT max(event_id) from EventTable ")
    suspend fun getLatestId(): Long?

    @Query("SELECT * FROM EventTable WHERE server=-1")
    suspend fun getLocalStoredEvents(): MutableList<UserEventEntity>

    @Query("SELECT * FROM EventTable WHERE server=:someId")
    suspend fun getEventByServerId(someId: Int): MutableList<UserEventEntity>
}
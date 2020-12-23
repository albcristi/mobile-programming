package com.example.cityinyourpocket.data_base.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "EventTable",
    foreignKeys = [
    ForeignKey( entity = UserEntity::class,
                parentColumns = ["username"],
                childColumns = ["host_name"])
    ])
data class UserEventEntity (
    @PrimaryKey(autoGenerate = true)
    var event_id: Long,
    var host_name: String,
    var event_name: String,
    var city: String,
    var longitude: Double,
    var lat: Double,
    @ColumnInfo(name = "start_hour")
    var startHour: String,
    @ColumnInfo(name = "start_date")
    var startDate: String,
    @ColumnInfo(name = "end_date")
    var endDate: String,
    var description: String,
    var server: Int
){
    constructor(): this(0,"","",
        "",0.0,0.0,"","","","", -1)
}

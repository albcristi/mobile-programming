package com.example.cityinyourpocket.data_base.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserTable")
data class UserEntity(
    @PrimaryKey
    val username: String,
    @ColumnInfo(name="full_name")
    val fullName: String,
    val password: String
)
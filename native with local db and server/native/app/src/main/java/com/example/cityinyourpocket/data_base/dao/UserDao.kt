package com.example.cityinyourpocket.data_base.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cityinyourpocket.data_base.entities.UserEntity


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: UserEntity)

    @Query("SELECT * FROM UserTable")
    suspend fun getAllData(): List<UserEntity>

    @Query("SELECT * FROM UserTable where username=:usrName")
    suspend fun getUser(usrName: String): UserEntity?

    @Query("SELECT * FROM UserTable where username=:usrName and password=:pass")
    suspend fun getUserByCredentials(usrName: String, pass: String): UserEntity?


}
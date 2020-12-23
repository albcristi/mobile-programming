package com.example.cityinyourpocket.data_base.db_config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cityinyourpocket.data_base.dao.UserDao
import com.example.cityinyourpocket.data_base.dao.UserEventDao
import com.example.cityinyourpocket.data_base.entities.UserEntity
import com.example.cityinyourpocket.data_base.entities.UserEventEntity

@Database(entities = [UserEntity::class, UserEventEntity::class], version = 6, exportSchema = false)
abstract class LocalDatabase : RoomDatabase(){
     abstract fun userDao(): UserDao
     abstract fun userEventDao(): UserEventDao

    companion object{
        @Volatile
        private var INSTANCE: LocalDatabase? = null;

        fun getDatabase(context: Context): LocalDatabase{
            val tempInstance = INSTANCE;
            if(tempInstance != null)
                return tempInstance;
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "local_database"
                ).fallbackToDestructiveMigration().build();
                INSTANCE = instance;
                return instance;
            }
        }
    }
}
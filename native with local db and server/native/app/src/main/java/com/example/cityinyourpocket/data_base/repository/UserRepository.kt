package com.example.cityinyourpocket.data_base.repository

import com.example.cityinyourpocket.data_base.dao.UserDao
import com.example.cityinyourpocket.data_base.entities.UserEntity

class UserRepository (private val userDao: UserDao){

    suspend fun createUser(userName: String, fullName: String, password: String){
        val entity = UserEntity(userName, fullName, password)
        userDao.addUser(entity);
        println(entity);
    }

    suspend fun verifyLogIng(userName: String, password: String): Boolean{
        val user = userDao.getUserByCredentials(userName, password) ?: return false
        return true;
    }

    suspend fun getUserByUserName(usrName: String): UserEntity?{
        return userDao.getUser(usrName)
    }
}
package com.example.cityinyourpocket.service

import android.util.Log
import com.example.cityinyourpocket.model.User

class UserService {
    companion object{
         var users: MutableList<User> = mutableListOf();
    }

    constructor(){
        if(users.size == 0){
            users.add(User("cristi", "parola"))
            users.add(User("mirela", "parola"))
            users.add(User("dana", "parola"))
        }
    }

    fun verifyLogInData(userName: String, password: String): Boolean{
        for(user in users)
            if(user.username == userName && user.password == password)
                return true;
        return false;
    }

    fun userNameIsFree(userName: String): Boolean{
        for(user in users)
            if(user.username == userName)
                return false
        return true
    }

    fun addUser(user: User){
        users.add(user)
        Log.d("UService", "added "+user.username)
    }
}
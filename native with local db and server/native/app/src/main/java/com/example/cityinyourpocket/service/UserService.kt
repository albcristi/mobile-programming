package com.example.cityinyourpocket.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.cityinyourpocket.data_base.db_config.LocalDatabase
import com.example.cityinyourpocket.data_base.repository.UserRepository
import com.example.cityinyourpocket.model.User
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import org.json.JSONStringer
import kotlin.math.log

class UserService {
    companion object{
        var users: MutableList<User> = mutableListOf();
        var BASE_URL = "http://192.168.1.2:8000/api/user/";
    }

    var userRepository: UserRepository? = null;
    var context: Context? = null;

    constructor(){
    }

    fun doDBInit(context: Context){
        this.context = context;
        var userDao = LocalDatabase.getDatabase(context).userDao();
        this.userRepository = UserRepository(userDao);

    }


    suspend fun userNameIsFree(userName: String): Boolean{
        val result =  this.userNameIsFreeAsync(userName).await();
        return result;
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


    private fun userNameIsFreeAsync(userName: String): Deferred<Boolean>{
        return GlobalScope.async(Dispatchers.IO){
            if(!isOnline())
                return@async false;
            val url = "${UserService.BASE_URL}availability/user=${userName}";
            val request: Request = Request.Builder()
                .url(url)
                .get()
                .build();
            val client = OkHttpClient()
            client.newCall(request)
                .execute()
                .use { response ->
                    if(!response.isSuccessful)
                        return@async false;
                    val jsonObject = JSONObject(response.body!!.string())
                    return@async jsonObject.getBoolean("unique")
                }
        }
    }


    fun addUser(user: User) {
        if(isOnline()) {
            GlobalScope.launch(Dispatchers.IO) {
                val url = "${UserService.BASE_URL}new-account";
                val requestBody: RequestBody = FormBody.Builder()
                    .add("username", user.username)
                    .add("password", user.password)
                    .add("full_name", user.name!!)
                    .build();
                val request: Request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
                val client = OkHttpClient()
                client.newCall(request)
                    .execute()
                    .use {response ->
                        if(response.isSuccessful) {
                            val jsonObject = JSONObject(response.body!!.string())
                            if (jsonObject.getBoolean("created")) {
                                userRepository?.createUser(
                                    user.username,
                                    user.username,
                                    user.password
                                )
                            }
                        }
                    }
            }
        }
    }

    suspend fun doLogIn(userName: String, password: String): Boolean{
        return doLogInAsync(userName, password).await();
    }

    private fun doLogInAsync(userName: String, password: String): Deferred<Boolean> {
        return GlobalScope.async(Dispatchers.IO) {
            if(isOnline()){
                val status = doServerLogIn(userName, password);
                if(status){
                    if(userRepository!!.getUserByUserName(userName) == null){
                        userRepository!!.createUser(userName, userName, password);
                    }
                }
                return@async status;
            }
            else {
                if (userRepository == null)
                    return@async false;
                return@async userRepository!!.verifyLogIng(userName, password);
            }

        }
     }

    private fun doServerLogIn(userName: String, password: String): Boolean{
        val url = "${UserService.BASE_URL}log-in/user=${userName}";
        val requestBody: RequestBody = FormBody.Builder()
            .add("password", password)
            .build();
        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build();
        val client = OkHttpClient()
        client.newCall(request)
            .execute()
            .use {response ->
                return if(response.isSuccessful){
                    val jsonObject = JSONObject(response.body!!.string());
                    println(jsonObject.get("logged"))
                    jsonObject.getBoolean("logged");
                } else{
                    false;
                }
            }
    }

}
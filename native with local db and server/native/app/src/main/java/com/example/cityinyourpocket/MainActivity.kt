package com.example.cityinyourpocket

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import kotlinx.android.synthetic.main.activity_main.*;
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.cityinyourpocket.model.User
import com.example.cityinyourpocket.service.EventService
import com.example.cityinyourpocket.service.NetworkConnectionService
import com.example.cityinyourpocket.service.UserService
import com.example.cityinyourpocket.user_is_logged_activities.ChooseLocationActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val userService: UserService = UserService()
    companion object {
        var wasOffline: Boolean = false;
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val becomeClient: TextView = findViewById(R.id.singInBecomeAMember)

        userService.doDBInit(this);
        wasOffline = !userService.isOnline()
        becomeClient.setOnClickListener {
            // HANDLE WHEN THE USER WANTS TO CREATE A NEW ACCOUNT
            if(userService.isOnline()) {
                val intent = Intent(this, RegisterUserActivity::class.java)
                startActivity(intent);
            }
            else{
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Can not create account while offline")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        logInButton.setOnClickListener {
            // HANDLE WHEN USER WANTS TO LOG IN
            try {
                val userName: String? = signInUserName.text.toString();
                val userPassword: String? = signInPasswordField.text.toString();
                if(userName == null || userPassword == null){
                    throw Exception("some exeception")
                }
                GlobalScope.launch(Dispatchers.Main) {
                    val verifiedCredentials = userService.doLogIn(userName, userPassword);
                    if (verifiedCredentials) {
                        val intent =
                            Intent(this@MainActivity, ChooseLocationActivity::class.java)
                        intent.putExtra("USER_NAME", userName);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Log-in failed")
                            .setPositiveButton("OK", null)
                            .show()
                    }

                }
            }
            catch (e: Exception) {
                signInFailed.text = e.message;
            }
        }

        val networkConnection = NetworkConnectionService(applicationContext);
        networkConnection.observe(this, Observer {isConnected ->
            if(isConnected){
                if(wasOffline) {
                    val eventService = EventService()
                    eventService.dbInit(this)
                    eventService.performServerUpdates()
                    Toast
                        .makeText(
                            this,
                            "Device is back online",
                            Toast.LENGTH_LONG
                        )
                        .show()
                }
            }
            else{
                wasOffline = true;
                Toast
                    .makeText(this,
                        "Device is offline. Some features will not be available",
                        Toast.LENGTH_LONG)
                    .show()
            }
        })

    }

}
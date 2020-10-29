package com.example.cityinyourpocket

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import kotlinx.android.synthetic.main.activity_main.*;
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.cityinyourpocket.model.User
import com.example.cityinyourpocket.service.UserService
import com.example.cityinyourpocket.user_is_logged_activities.ChooseLocationActivity
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val userService: UserService = UserService()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val becomeClient: TextView = findViewById(R.id.singInBecomeAMember)
        becomeClient.setOnClickListener {
            // HANDLE WHEN THE USER WANTS TO CREATE A NEW ACCOUNT
            val intent = Intent(this, RegisterUserActivity::class.java)
            startActivity(intent);
        }

        logInButton.setOnClickListener {
            // HANDLE WHEN USER WANTS TO LOG IN
            try {
                val userName: String? = signInUserName.text.toString();
                val userPassword: String? = signInPasswordField.text.toString();
                if(userName == null || userPassword == null){
                    throw Exception("some exeception")
                }
                val verifiedCredentials: Boolean = userService.verifyLogInData(userName, userPassword)
                if(verifiedCredentials) {
                    val intent = Intent(this, ChooseLocationActivity::class.java)
                    intent.putExtra("USER_NAME", userName);
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder(this)
                        .setTitle("Log-in failed")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            catch (e: Exception) {
                signInFailed.text = e.message;
            }
        }
    }

}
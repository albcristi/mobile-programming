package com.example.cityinyourpocket

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.cityinyourpocket.model.User
import com.example.cityinyourpocket.service.UserService
import kotlinx.android.synthetic.main.activity_register_user.*
import kotlinx.android.synthetic.main.modal_message_register_failed.*
import kotlinx.android.synthetic.main.modal_message_register_failed.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterUserActivity : AppCompatActivity() {
    val userService: UserService = UserService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userService.doDBInit(this)
        setContentView(R.layout.activity_register_user)

        registerButton.setOnClickListener {
            var cont = LayoutInflater.from(this)
                .inflate(R.layout.modal_message_register_failed, null)
            cont.passwordsMatch.setTextColor(Color.RED)

            var usrName: String = registerUserName.text.toString()
            var name: String = registerName.text.toString()
            var password: String  =  registerPassword.text.toString()
            var passwordRe: String = registerReenteredPassword.text.toString();
            var ageConf: Boolean = registerOver18.isChecked

            var show = false
            GlobalScope.launch(Dispatchers.Main) {
                if (!userService.userNameIsFree(usrName) || usrName == "") {
                    show = true
                    cont.usrNameCheckBox.setTextColor(Color.RED)
                } else
                    cont.usrNameCheckBox.setTextColor(Color.GREEN)

                if (password == "" || passwordRe == "" || password != passwordRe || password.length < 4) {
                    show = true
                    cont.passwordsMatch.setTextColor(Color.RED)
                } else
                    cont.passwordsMatch.setTextColor(Color.GREEN)


                if (!ageConf) {
                    show = true
                    cont.ageConfirmedCheck.setTextColor(Color.RED)
                } else
                    cont.ageConfirmedCheck.setTextColor(Color.GREEN)

                if (name == "") {
                    show = true
                    cont.nameCheck.setTextColor(Color.RED)
                } else
                    cont.nameCheck.setTextColor(Color.GREEN)
                if (show)
                    AlertDialog.Builder(this@RegisterUserActivity)
                        .setTitle("Register Status")
                        .setView(cont)
                        .setPositiveButton("OK", null)
                        .show()
                else {
                    var user: User = User(username = usrName, password = password)
                    user.name = name
                    userService.addUser(user)
                    val d = AlertDialog.Builder(this@RegisterUserActivity)
                        .setTitle("Account Created")
                        .setPositiveButton("OK", null)
                        .show()
                    d.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setOnClickListener {
                            finish()
                        }
                }
            }
        }
    }

}
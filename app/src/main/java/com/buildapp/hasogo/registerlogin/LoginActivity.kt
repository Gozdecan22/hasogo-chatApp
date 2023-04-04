package com.buildapp.hasogo.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buildapp.hasogo.R
import com.buildapp.hasogo.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        login_button_login.setOnClickListener {
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val intent = Intent(this@LoginActivity, LatestMessagesActivity::class.java)
                        startActivity(intent)

                    }else{
                     Toast.makeText(this@LoginActivity, "User does not exist", Toast.LENGTH_SHORT).show()
                    }

                }
        }

        back_to_register_textview.setOnClickListener {
            finish()
        }

    }
}

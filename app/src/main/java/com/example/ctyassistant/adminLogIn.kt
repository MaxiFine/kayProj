package com.example.ctyassistant

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import com.example.ctyassistant.databinding.ActivityAdminLogInBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber


class adminLogIn : AppCompatActivity() {


    private lateinit var binding: ActivityAdminLogInBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        setSupportActionBar(binding.loginToolbar)

        binding.loginToolbar.setTitle("Log In As An Admin")

        binding.loginToolbar.setNavigationOnClickListener {
            onBackPressedMethod()
        }

        binding.btnlogin.setOnClickListener {
            checkDetails()
        }

        binding.btnforgotpw.setOnClickListener {
            intent = Intent(this@adminLogIn, forgetPassword::class.java)
            startActivity(intent)
        }
    }


    fun checkDetails() {
        val email = binding.emailLogin.text.toString()
        val passw = binding.pwordLogin.text.toString()

        if (email.isEmpty() && passw.isEmpty()) {
            Toast.makeText(
                this@adminLogIn, "provide email and password",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (email.isEmpty()) {
            binding.emailLogin.setError("provide an email");
            binding.emailLogin.requestFocus();
        }
        if (passw.isEmpty()) {
            binding.pwordLogin.setError("provide an email");
            binding.pwordLogin.requestFocus();
        }

        if (email.isNotEmpty() && passw.isNotEmpty()) {
            binding.progressLogin.visibility = View.VISIBLE

            checkLogin(email, passw)
        } else {
            Toast.makeText(this, "please check the fields", Toast.LENGTH_SHORT).show()
        }

    }

    fun checkLogin(email: String, password: String) {

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressLogin.visibility = View.GONE

                toAdmin()
            } else {
                binding.progressLogin.visibility = View.GONE

                Toast.makeText(
                    this, it.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()

                Timber.tag(tag).d(it.exception)
            }
        }.addOnFailureListener {

            binding.progressLogin.visibility = View.GONE
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            Timber.tag(tag).d(it)
        }

    }

    fun toAdmin() {
        intent = Intent(this@adminLogIn, mapActivity::class.java)
        startActivity(intent)
    }

    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                //method here
                toAdmin()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    toAdmin()
                }
            })
        }
    }

}
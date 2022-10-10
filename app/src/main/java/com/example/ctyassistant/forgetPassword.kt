package com.example.ctyassistant

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import com.example.ctyassistant.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class forgetPassword : AppCompatActivity() {


    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        binding.forgotpasswordToolbar.setTitle("Forgot Password")
        binding.forgotpasswordToolbar.setNavigationOnClickListener {
            onBackPressedMethod()
        }

        binding.btnsendemail.setOnClickListener {

            val email = binding.emailFpassword.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(
                    this@forgetPassword,
                    "please provide an email", Toast.LENGTH_SHORT
                ).show()
            }

            if (email.isNotEmpty()) {

                binding.progressFpassword.visibility = View.VISIBLE

                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {

                        binding.progressFpassword.visibility = View.GONE

                        Toast.makeText(
                            this@forgetPassword,
                            "a reset link has been sent to your email", Toast.LENGTH_SHORT
                        ).show()

                        toLogin()

                    } else {
                        binding.progressFpassword.visibility = View.GONE
                        Toast.makeText(
                            this@forgetPassword, it.toString(), Toast.LENGTH_SHORT
                        ).show()

                        Timber.tag(tag).d(it.exception)
                    }
                }.addOnFailureListener {
                    binding.progressFpassword.visibility = View.GONE

                    Toast.makeText(
                        this@forgetPassword,
                        "Failed + ${it}", Toast.LENGTH_SHORT
                    ).show()

                    Timber.tag(tag).d(it)
                }
            }
        }

    }

    fun toLogin() {
        intent = Intent(this@forgetPassword, adminLogIn::class.java)
        startActivity(intent)
    }

    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                //method here
                toLogin()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    toLogin()
                }
            })
        }
    }


}

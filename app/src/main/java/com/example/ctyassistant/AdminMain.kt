package com.example.ctyassistant

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import com.example.ctyassistant.databinding.ActivityAdminMainBinding

class AdminMain : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        setSupportActionBar(binding.accDetToolbar)

        binding.accDetToolbar.setTitle("Admin")

        binding.accDetToolbar.setNavigationOnClickListener{
            toMain()
        }

        binding.addAnAdminAccDet.setOnClickListener {
            toAddAdmin()
        }

        binding.viewfeedbacksAdmin.setOnClickListener {
           toViewFeedn()
        }

        binding.viewLocationsAccDet.setOnClickListener {
            toViewLocation()
        }

        binding.addPlaceAccDet.setOnClickListener {
           toAddAPlace()
        }

        binding.AccDetLogout.setOnClickListener {
           toMain()
        }

    }

    fun toAddAdmin(){
        intent = Intent(this@AdminMain, addAnAdmin::class.java)
        startActivity(intent)
    }

    fun toViewFeedn(){
        intent = Intent(this@AdminMain, viewFeedback::class.java)
        startActivity(intent)
    }

    fun toViewLocation(){
        intent = Intent(this@AdminMain, viewLocations::class.java)
        startActivity(intent)
    }

    fun toAddAPlace(){
        intent = Intent(this@AdminMain, addALocation::class.java)
        startActivity(intent)
    }


    fun toMain() {
        intent = Intent(this@AdminMain, mapActivity::class.java)
        startActivity(intent)
    }

    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                toMain()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    toMain()
                }
            })
        }
    }




}
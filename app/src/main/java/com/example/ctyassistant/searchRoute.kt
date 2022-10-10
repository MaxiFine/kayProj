package com.example.ctyassistant

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import com.example.ctyassistant.databinding.ActivitySearchRouteBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.maps.MapboxMap


class searchRoute : AppCompatActivity() {

    private var mapboxMap: MapboxMap? = null

    private lateinit var binding: ActivitySearchRouteBinding

    private var databaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Places")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        setSupportActionBar(binding.routeToolbar)

        binding.routeToolbar.setNavigationOnClickListener {
            onBackPressedMethod()
        }

        binding.searchDir.setOnClickListener {
            getDirection()
        }

    }

    private fun getDirection() {

        var from = binding.fromEditText.text.toString()
        var to = binding.toEditText.text.toString()

        if (from.isEmpty() && to.isEmpty()) {
            Toast.makeText(
                this, "please fill all the fields",
                Toast.LENGTH_SHORT
            ).show()
        } else if (from.isEmpty()) {
            Toast.makeText(
                this, "provide a location",
                Toast.LENGTH_SHORT
            ).show()
        } else if (to.isEmpty()) {
            Toast.makeText(
                this, "provide a destination",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (from.isNotEmpty() && to.isNotEmpty()) {

            getRoute(from, to)
        }

    }

    private fun getRoute(from: String, to: String) {

    }

    override fun onStart() {
        super.onStart()

    }


    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                toMap()
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    toMap()
                }
            })
        }
    }

    fun toMap() {
        intent = Intent(this@searchRoute, mapActivity::class.java)
        startActivity(intent)
    }


}
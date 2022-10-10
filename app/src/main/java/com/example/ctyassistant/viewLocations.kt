package com.example.ctyassistant

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ctyassistant.databinding.ActivityViewLocationsBinding
import com.google.firebase.database.*
import timber.log.Timber

class viewLocations : AppCompatActivity() {
    private lateinit var binding: ActivityViewLocationsBinding

    private lateinit var databaseRef: DatabaseReference
    private lateinit var locationRecycView: RecyclerView
    private lateinit var ourLocs: ArrayList<constAddPlace>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        binding.toolbarPlacesMain.setTitle("View Locations")
        binding.toolbarPlacesMain.setNavigationOnClickListener {
            onBackPressedMethod()
        }

        locationRecycView = binding.recycPlacesMain

        locationRecycView.layoutManager = LinearLayoutManager(this)
        locationRecycView.setHasFixedSize(true)

        ourLocs = arrayListOf<constAddPlace>()

        getLocations()

    }

    private fun getLocations() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Places")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {

                        val locss = userSnapshot.getValue(constAddPlace::class.java)

                        ourLocs.add(locss!!)

                    }
                    locationRecycView.adapter = recviewPlaces(ourLocs)
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Timber.tag(tag).d(error.toString())
            }

        })

    }

    fun toMain() {
        intent = Intent(this@viewLocations, AdminMain::class.java)
        startActivity(intent)
    }


    override fun onStart() {
        super.onStart()


    }

    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                //method here
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
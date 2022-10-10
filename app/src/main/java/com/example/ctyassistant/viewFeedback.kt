package com.example.ctyassistant

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ctyassistant.databinding.ActivityAddAlocationBinding
import com.example.ctyassistant.databinding.ActivityViewFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class viewFeedback : AppCompatActivity() {

    private lateinit var binding: ActivityViewFeedbackBinding

    private lateinit var databaseRef: DatabaseReference
    private lateinit var feedbackRecycView: RecyclerView
    private lateinit var userFeedb: ArrayList<constFeedback>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        setSupportActionBar(binding.toolbarFeedbMain)

        binding.toolbarFeedbMain.setTitle("View Feedback / Issues")
        binding.toolbarFeedbMain.setNavigationOnClickListener {
            onBackPressedMethod()
        }


        feedbackRecycView = binding.recycfeedb
        feedbackRecycView.layoutManager = LinearLayoutManager(this)
        feedbackRecycView.setHasFixedSize(true)

        userFeedb = arrayListOf<constFeedback>()
        getUserFeedbacks()

    }

    private fun getUserFeedbacks() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Feedback")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {

                        val feedss = userSnapshot.getValue(constFeedback::class.java)

                        userFeedb.add(feedss!!)

                    }
                    feedbackRecycView.adapter = recviewFeedback(userFeedb)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
            // Toast.makeText(this,error.toS, Toast.LENGTH_SHORT).show()
        })

    }


    fun toMain() {
        intent = Intent(this@viewFeedback, AdminMain::class.java)
        startActivity(intent)
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
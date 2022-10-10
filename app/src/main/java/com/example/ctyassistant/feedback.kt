package com.example.ctyassistant

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.ctyassistant.databinding.ActivityFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class feedback : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        setSupportActionBar(binding.feedbToolbar)

        binding.feedbToolbar.setTitle("Feedbacks / Issues")

        binding.feedbToolbar.setNavigationOnClickListener {
            onBackPressedMethod()
        }

        binding.btnFeedb.setOnClickListener {
            checkFeedback()
        }

    }

    fun checkFeedback() {

        var feedb = binding.EditTextFeedback.text.toString().trim()

        if (feedb.isEmpty()) {
            Toast.makeText(
                this, "field is empty",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (feedb.isNotEmpty()) {
            proceed(feedb)

            binding.progressFeedb.visibility = View.VISIBLE
        }
    }

    fun proceed(feedback: String) {

        val ref: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseRef: DatabaseReference = ref.getReference("Feedback")

        val k = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        val tt = k.time

        val date = sdf.format(tt)
        val userId: String? = databaseRef.push().key

        val feedb = constFeedback(feedback, userId, date)

        if (userId != null) {
            databaseRef.child(userId).setValue(feedb).addOnCompleteListener {
                if (it.isSuccessful) {

                    binding.progressFeedb.visibility = View.GONE

                    Toast.makeText(this, "sent successfully", Toast.LENGTH_SHORT).show()
                    toMap()

                } else {

                    binding.progressFeedb.visibility = View.GONE
                    Toast.makeText(this, "$it.exception", Toast.LENGTH_SHORT).show()

                    Timber.tag(tag).d(it.exception)
                }
            }.addOnFailureListener {

                binding.progressFeedb.visibility = View.GONE
                Toast.makeText(this, "Failed" + it.toString(), Toast.LENGTH_SHORT).show()

                Timber.tag(tag).d(it)
            }
        }
    }

    fun toMap() {
        intent = Intent(this@feedback, mapActivity::class.java)
        startActivity(intent)
    }

    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                //method here
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


}
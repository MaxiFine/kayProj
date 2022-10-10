package com.example.ctyassistant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.ctyassistant.databinding.ActivityAddAnAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class addAnAdmin : AppCompatActivity() {

    val tag: String = " Add an Admin"

    private lateinit var binding: ActivityAddAnAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddAnAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        binding.adminsignupToolbar.setTitle("Add An Admin")
        binding.adminsignupToolbar.setNavigationOnClickListener {
            toMain()
        }

        binding.btnCreateAccAdmin.setOnClickListener {
            checkDetails()

        }

    }

    fun checkDetails() {
        val username = binding.etUsernameAdmin.text.toString()
        val email = binding.etemailAdmin.text.toString()
        val password = binding.etpwordAdmin.text.toString()
        val confpasw = binding.etConfirmpwordAdmin.text.toString()

        if (username.isEmpty() && email.isEmpty() && password.isEmpty()
            && confpasw.isEmpty()
        ) {
            Toast.makeText(
                this, "fill in all the fields",
                Toast.LENGTH_SHORT
            ).show()
        } else if (username.isEmpty()) {
            Toast.makeText(
                this, "provide a username",
                Toast.LENGTH_SHORT
            ).show()
        } else if (email.isEmpty()) {
            Toast.makeText(
                this, "provide an email",
                Toast.LENGTH_SHORT
            ).show()
        } else if (password.isEmpty()) {
            Toast.makeText(
                this, "provide password",
                Toast.LENGTH_SHORT
            ).show()
        } else if (password.length < 8) {
            Toast.makeText(
                this, "passwords should be more than" +
                        "eight(8) characters", Toast.LENGTH_SHORT
            ).show()
        }

        if (confpasw.isEmpty()) {
            Toast.makeText(
                this, "confirm the password",
                Toast.LENGTH_SHORT
            ).show()
        } else if (!(confpasw.equals( password)) ) {
            Toast.makeText(
                this, "passwords do not match",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
            && confpasw.isNotEmpty() && confpasw.equals( password)
        ) {
            binding.progressAdminSignup.visibility = View.VISIBLE
            saveDetails(username, email, password)

            Timber.tag(tag).d("all fields have been filled")
        }
    }

    fun saveDetails(
        username: String, email: String, password: String
    ) {

        val ref: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseRef: DatabaseReference = ref.getReference("Admin")


        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                //save user details with time to realtime firebase

                binding.progressAdminSignup.visibility = View.GONE

                val k = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                val tt = k.time

                val date = sdf.format(tt)
                val userId: String? = databaseRef.push().key

                val adminDet = constAdminSignUp(username, email, password, userId, date)

                if (userId != null) {
                    databaseRef.child(userId).setValue(adminDet).addOnCompleteListener {
                        if (it.isSuccessful) {

                            binding.progressAdminSignup.visibility = View.GONE

                            Toast.makeText(
                                this, "admin added successfully",
                                Toast.LENGTH_SHORT
                            ).show()


                            Timber.tag(tag).d(" saved successfully")

                            toMain()
                        } else {
                            binding.progressAdminSignup.visibility = View.GONE
                            Toast.makeText(this, "$it.exception", Toast.LENGTH_SHORT).show()

                            Timber.tag(tag).d(it.exception)

                        }
                    }.addOnFailureListener {

                        binding.progressAdminSignup.visibility = View.GONE

                        Toast.makeText(this, "couldn't save" + it.toString(), Toast.LENGTH_SHORT)
                            .show()

                        Timber.tag(tag).d(it)
                    }
                }

            } else {

                binding.progressAdminSignup.visibility = View.GONE
                Toast.makeText(
                    this, it.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()

                Timber.tag(tag).d("an error occurred  $it.exception")
            }
        }.addOnFailureListener {

            binding.progressAdminSignup.visibility = View.GONE

            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()

            Timber.tag(tag).d(it)
        }

    }

    fun toMain() {

        intent = Intent(this@addAnAdmin, AdminMain::class.java)
        startActivity(intent)
    }

}
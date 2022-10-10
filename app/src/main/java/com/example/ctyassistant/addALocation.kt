package com.example.ctyassistant

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ctyassistant.databinding.ActivityAddAlocationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class addALocation : AppCompatActivity() {


    var imagePath: Uri? = null

    private lateinit var binding: ActivityAddAlocationBinding

    companion object {
        private val tag = "add a place"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddAlocationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        setSupportActionBar(binding.toolbarAddloc)

        binding.toolbarAddloc.setTitle("Add A Location")

        binding.toolbarAddloc.setNavigationOnClickListener {
            onBackPressedMethod()
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            {
                binding.imageAddDir.setImageURI(it)
                binding.addImageAddDir.visibility = View.GONE
                imagePath = it

            })


        binding.btnAddPlace.setOnClickListener {
            checkDetails()
        }

        binding.addImageAddDir.setOnClickListener {
            galleryImage.launch("image/*")
        }
    }

    fun checkDetails() {
        val name = binding.nameOfPlaceAddDir.text.toString()
        val tOfDesc = binding.DescripAddDir.text.toString()
        val latitude: String = binding.latAddDir.toString()
        val longitude: String = binding.lngAddDir.toString()

        if (name.isEmpty() && tOfDesc.isEmpty() && latitude.isEmpty()
            && longitude.isEmpty()
        ) {
            Toast.makeText(
                this, "please fill all the fields",
                Toast.LENGTH_SHORT
            ).show()
        } else if (name.isEmpty()) {
            Toast.makeText(
                this, "provide the name",
                Toast.LENGTH_SHORT
            ).show()
        } else if (tOfDesc.isEmpty()) {
            Toast.makeText(
                this, "provide the description",
                Toast.LENGTH_SHORT
            ).show()
        } else if (latitude.isEmpty()) {
            Toast.makeText(
                this, "provide the latitude of the place",
                Toast.LENGTH_SHORT
            ).show()
        } else if (longitude.isEmpty()) {
            Toast.makeText(
                this, "provide the longitude of the place",
                Toast.LENGTH_SHORT
            ).show()
        } else if (latitude.length != longitude.length) {
            Toast.makeText(
                this, "the length of the coordinates should be the same",
                Toast.LENGTH_SHORT
            ).show()
        }


        if (name.isNotEmpty() && tOfDesc.isNotEmpty() && latitude.isNotEmpty()
            && longitude.isNotEmpty() && latitude.length == longitude.length
        ) {
            if (imagePath != null) {

                proceedtoSave(name, tOfDesc, latitude, longitude)
            }
        }
    }


    fun proceedtoSave(
        name: String, desc: String, lat: String,
        lng: String
    ) {


        val mStorage: FirebaseStorage = FirebaseStorage.getInstance()
        val mStorageref: StorageReference = mStorage.getReference("Locations")

        val ref: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseRef: DatabaseReference = ref.getReference("Places")

        val placeId: String? = databaseRef.push().key

        if (imagePath != null) {

            binding.progressAddloc.visibility = View.VISIBLE

            val img = mStorageref.child(placeId!!)
            img.putFile(imagePath!!).addOnSuccessListener {


                //realtime database
                val k = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                val tt = k.time
                val date = sdf.format(tt)

                var latit = lat.toDouble()
                var longi = lng.toDouble()
                var imguri = imagePath.toString()

                val locationDetails = constAddPlace(
                    latit, longi, name, imguri, placeId, date, desc
                )

                databaseRef.child(placeId).setValue(locationDetails).addOnCompleteListener {
                    if (it.isSuccessful) {

                        binding.progressAddloc.visibility = View.GONE

                        Toast.makeText(
                            this@addALocation,
                            "location added successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        toMain()

                    } else {
                        binding.progressAddloc.visibility = View.GONE

                        Toast.makeText(this@addALocation, "$it.exception", Toast.LENGTH_SHORT)
                            .show()

                        Timber.tag(tag).d(it.exception)
                    }

                }.addOnFailureListener {

                    binding.progressAddloc.visibility = View.GONE
                    Toast.makeText(this, "Failed" + it.toString(), Toast.LENGTH_SHORT).show()

                    Timber.tag(tag).d(it)
                }

            }.addOnFailureListener {

                binding.progressAddloc.visibility = View.GONE

                Toast.makeText(
                    this@addALocation,
                    "couldn't upload image" + it.toString(),
                    Toast.LENGTH_SHORT
                )
                    .show()

                Timber.tag(tag).d(it)
            }
        } else {

            Toast.makeText(this@addALocation, "Please add an image", Toast.LENGTH_SHORT).show()
        }
    }

    fun toMain() {
        intent = Intent(this@addALocation, AdminMain::class.java)
        startActivity(intent)
    }

    private fun onBackPressedMethod() {
        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
                toMain()
            }
        } else {
            onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        toMain()
                    }
                })
        }

    }

}
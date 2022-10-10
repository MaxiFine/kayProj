package com.example.ctyassistant


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import timber.log.Timber


class recviewPlaces(private val locations: ArrayList<constAddPlace>) :
    RecyclerView.Adapter<recviewPlaces.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recview_locations,
            parent, false
        )

        return recviewPlaces.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = locations[position]

        holder.noplace.text = currentItem.nameOfPlace
        holder.date.text = currentItem.date
        holder.latitude.text = currentItem.lat.toString()
        holder.longitude.text = currentItem.lng.toString()
        holder.descrip.text = currentItem.typeOfInstitution.toString()

        holder.image.setImageURI(currentItem.imageUri?.toUri())

        holder.delete.setOnClickListener {

            val bd: AlertDialog.Builder = AlertDialog.Builder(holder.delete.context)

            val alertDialog: AlertDialog = bd.create()

            bd.setMessage("Are you sure you want to delete this location?")
                .setTitle("Delete A Location")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialogInterface, it ->

                    //init firebase database
                    lateinit var databaseRef: DatabaseReference
                    databaseRef = FirebaseDatabase.getInstance().getReference("Places")

                    databaseRef.child(currentItem.placeId.toString()).removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {

                                Toast.makeText(
                                    holder.delete.context, "deleted", Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    holder.delete.context, "$it.exception", Toast.LENGTH_SHORT
                                ).show()

                                Timber.tag(tag).d(it.exception)
                            }
                        }.addOnFailureListener {
                        Toast.makeText(
                            holder.delete.context, "failed" + it.toString(), Toast.LENGTH_SHORT
                        ).show()

                        Timber.tag(tag).d(it.toString())
                    }

                }.setNegativeButton("No") { dialogInterface, it ->
                    dialogInterface.dismiss()
                }.show()
        }
    }

    override fun getItemCount(): Int {

        return locations.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val delete: ImageButton = itemView.findViewById(R.id.deletePlaces)
        val image: ImageView = itemView.findViewById(R.id.imagesPlaces)

        val noplace: TextView = itemView.findViewById(R.id.namePlaces)
        val date: TextView = itemView.findViewById(R.id.timePlaces)
        val descrip: TextView = itemView.findViewById(R.id.descriptionPlaces)
        val latitude: TextView = itemView.findViewById(R.id.latPlaces)
        val longitude: TextView = itemView.findViewById(R.id.lngPlaces)

    }


}
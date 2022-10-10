package com.example.ctyassistant


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recviewFeedback(private val userFeedb: ArrayList<constFeedback>) :
    RecyclerView.Adapter<recviewFeedback.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recview_feedback,
            parent, false
        )

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userFeedb[position]

        holder.feedb.text = currentItem.feedback
        holder.time.text = currentItem.date

    }

    override fun getItemCount(): Int {

        return userFeedb.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val time: TextView = itemView.findViewById(R.id.timefeedbReceived)
        val feedb: TextView = itemView.findViewById(R.id.txtfeedbReceived)
    }

}
package com.example.parkingmobileapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class HistoryAdapter(private val history_list : ArrayList<ParkingDatabase>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {

        val historyView = LayoutInflater.from(parent.context).inflate(R.layout.history_data, parent, false)
        return ViewHolder(historyView)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {

        val database : ParkingDatabase = history_list[position] //extend to ParkingDatabase class
        holder.car_plate.text = database.car_plate //assign database value to holder
        holder.price.text = database.price.toString()
        holder.status.text = database.status

        //get child key
        val simpleDate = SimpleDateFormat("dd-MMM-yy hh:mm a")
        val currentDate = simpleDate.format(database.startTime!!.toLong() * 1000) //ntp_Time
        holder.time.text = currentDate

    }

    override fun getItemCount(): Int {
        return history_list.size
    }

    class ViewHolder(historyView : View) : RecyclerView.ViewHolder(historyView) {
        val car_plate : TextView = historyView.findViewById(R.id.carplate_data)
        val price : TextView = historyView.findViewById(R.id.price_data)
        val status: TextView = historyView.findViewById(R.id.status_data)
        val time : TextView = historyView.findViewById(R.id.history_value_date)
    }

}
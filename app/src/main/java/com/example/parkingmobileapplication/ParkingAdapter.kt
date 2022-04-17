package com.example.parkingmobileapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ParkingAdapter(private val notification_list : ArrayList<ParkingDatabase>) : RecyclerView.Adapter<ParkingAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingAdapter.ViewHolder {

        val notificationView = LayoutInflater.from(parent.context).inflate(R.layout.notification_data, parent, false)
        return ViewHolder(notificationView)
    }

    override fun onBindViewHolder(holder: ParkingAdapter.ViewHolder, position: Int) {

        val database : ParkingDatabase = notification_list[position] //extend to ParkingDatabase class
        holder.car_plate.text = database.car_plate.toString()     //assign database value to text holder
        holder.price.text = database.price.toString()

        val c: Date = Calendar.getInstance().time
        val simpleDate = SimpleDateFormat("dd-MMM-yy hh:mm a")
        val time = simpleDate.format(c)
        holder.date.text = time.toString()
    }

    override fun getItemCount(): Int {
        return notification_list.size
    }


    class ViewHolder(notificationView : View) : RecyclerView.ViewHolder(notificationView){
        val car_plate: TextView = notificationView.findViewById(R.id.txt_car_plate)
        val price: TextView = notificationView.findViewById(R.id.txt_price)
        val date: TextView = notificationView.findViewById(R.id.value_date)
    }

}
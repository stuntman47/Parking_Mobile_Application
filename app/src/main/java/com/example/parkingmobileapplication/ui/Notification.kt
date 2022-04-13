package com.example.parkingmobileapplication.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmobileapplication.ParkingAdapter
import com.example.parkingmobileapplication.ParkingDatabase
import com.example.parkingmobileapplication.databinding.FragmentNotificationBinding
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class Notification : Fragment() {
    private lateinit var binding: FragmentNotificationBinding

    private lateinit var viewnotification: RecyclerView
    private lateinit var listnotification: ArrayList<ParkingDatabase>
    private lateinit var notificationadapter: ParkingAdapter
    private lateinit var db: DatabaseReference
    //private lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewnotification = binding.notificationList //initialize notification list in adapter
        viewnotification.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        //viewnotification.setHasFixedSize(true)

        listnotification = arrayListOf<ParkingDatabase>() //initialize data array list

        getNotifications()

    }

    private fun getNotifications(){
        db = FirebaseDatabase.getInstance().getReference("epochtime1")
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
//                val car = snapshot.child("car_plate").getValue(String::class.java)
//                val price = snapshot.child("price").getValue(Integer::class.java)
//                Collections.addAll(listnotification, car!!, price!!)
                val log = snapshot.getValue(ParkingDatabase::class.java)
                listnotification.add(log!!)

                viewnotification.adapter = ParkingAdapter(listnotification) //initialize recycler view
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        //viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
//
//    }

}
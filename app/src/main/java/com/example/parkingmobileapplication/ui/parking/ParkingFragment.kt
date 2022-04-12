package com.example.parkingmobileapplication.ui.parking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.parkingmobileapplication.databinding.FragmentParkingBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DataSnapshot




class ParkingFragment : Fragment() {
    private lateinit var binding: FragmentParkingBinding
//    private lateinit var parkingViewModel: ParkingViewModel
    private var _binding: FragmentParkingBinding? = null
    private lateinit var db : DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParkingBinding.inflate(layoutInflater)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        db = FirebaseDatabase.getInstance().getReference("epochtime1") //create function pass value
//        db.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                var rootTime = snapshot.key.toString() //get epochtime1
//                //binding.valueStartTime.setText(rootTime)
//
//                var elapsedTime = snapshot.child("current_time").getValue(String::class.java) //get string value
//                //binding.valueElapsedTime.setText(elapsedTime)
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })

    }

    //clear up for memory
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun writeRFIDDatabase(){
        //get RFID ID, entry time from esp32
        //get car plate number from database, search UID and get car plate number


    }


}
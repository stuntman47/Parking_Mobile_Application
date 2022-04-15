package com.example.parkingmobileapplication.ui.parking

import android.content.Context
import android.content.Intent
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.example.parkingmobileapplication.R
import com.example.parkingmobileapplication.databinding.FragmentParkingBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DataSnapshot




class ParkingFragment : Fragment() {
    private lateinit var binding: FragmentParkingBinding
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

        var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var edit = pref.edit()
        val user = pref.getString("username", "default value")
        val phone = pref.getString("phoneNo", "default value").toString()
        if (user.equals("default value")){ //if guest, change all text to default
            binding.btTagid.setText("-123XYZ-")
            binding.valueBalance.text = "0.00"
            binding.valueStartTime.text = "00:00"
            binding.valueElapsedTime.text = "0"
            binding.valueChargeRate.text = "0.00"

        }
        else{
            db = FirebaseDatabase.getInstance().getReference("Phone Number").child(phone)
            db.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val carplate = snapshot.child("car_plate").getValue(String::class.java)
                        val balance = snapshot.child("balance").getValue(Integer::class.java)
                        binding.btTagid.setText(carplate.toString())
                        binding.valueBalance.text = balance.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        binding.btReload.setOnClickListener {
            val reloadFragment = Reload()
            val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragment_container, reloadFragment)
            transaction.commit()
        }



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
//
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
package com.example.parkingmobileapplication.ui.rfid

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.parkingmobileapplication.databinding.FragmentManagerfidBinding
import com.google.firebase.database.*


class ManageRFIDFragment : Fragment() {
    private lateinit var binding: FragmentManagerfidBinding
    private lateinit var db : DatabaseReference
    private var _binding: ManageRFIDFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManagerfidBinding.inflate(layoutInflater)
        return(binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        //var edit = pref.edit()
        val user = pref.getString("username", "default value")
        val phone = pref.getString("phoneNo", "default value").toString()

        //guest user check
        if (user.equals("default value")){
            binding.rfidUid.text = "E15049"
            binding.textCarplate.text = "1234"
        }
        else{
            //retrieve from database
            db = FirebaseDatabase.getInstance().getReference("Phone Number").child(phone)
            db.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val uid = snapshot.child("uid").getValue(String::class.java)
                    val plate = snapshot.child("car_plate").getValue(String::class.java)

                    //display rfid and car plate information
                    binding.rfidUid.text = uid
                    binding.textCarplate.text = plate
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        binding.btLinkRFID.setOnClickListener {
            val carplate = binding.valueCarplate.text.toString()
            if (carplate == ""){
                Toast.makeText(context, "Invalid car plate", Toast.LENGTH_SHORT).show()
            }
            else{
                db = FirebaseDatabase.getInstance().getReference("Phone Number").child(phone)
                db.child("car_plate").setValue(carplate)
                Toast.makeText(context, "Link successful", Toast.LENGTH_SHORT).show()
                binding.valueCarplate.text.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
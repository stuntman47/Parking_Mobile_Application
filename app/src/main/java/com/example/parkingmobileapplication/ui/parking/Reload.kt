package com.example.parkingmobileapplication.ui.parking

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.parkingmobileapplication.R
import com.example.parkingmobileapplication.databinding.FragmentReloadBinding
import com.google.firebase.database.*


class Reload : Fragment() {
    private lateinit var binding: FragmentReloadBinding
    private lateinit var db: DatabaseReference



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReloadBinding.inflate(layoutInflater)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        //var edit = pref.edit()
        //val user = pref.getString("username", "default value")
        val phone = pref.getString("phoneNo", "default value").toString()

        binding.valueRm10.setOnClickListener {
            binding.valueTopup.setText("10")
        }
        binding.valueRm20.setOnClickListener {
            binding.valueTopup.setText("20")
        }
        binding.valueRm50.setOnClickListener {
            binding.valueTopup.setText("50")
        }

        binding.btTopup.setOnClickListener {
            db = FirebaseDatabase.getInstance().getReference("Phone Number").child(phone)
            val amount = binding.valueTopup.text.toString()
            if (amount == ""){
                Toast.makeText(context, "Amount cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else if (phone == "default value"){
                Toast.makeText(context, "Account required", Toast.LENGTH_SHORT).show()
            }
            else{
                db.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val bal = snapshot.child("balance").getValue(Integer::class.java).toString()
                        db.child("balance").setValue(Integer.parseInt(amount)+Integer.parseInt(bal))
                        Toast.makeText(context, "Reload successful", Toast.LENGTH_SHORT).show()

                        val fragmentParking = ParkingFragment()
                        val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
                        transaction.replace(R.id.fragment_container, fragmentParking)
                        transaction.commit()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }
        }
    }


}
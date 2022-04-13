package com.example.parkingmobileapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.parkingmobileapplication.R
import com.example.parkingmobileapplication.UserDatabase
import com.example.parkingmobileapplication.databinding.FragmentAccountBinding
import com.example.parkingmobileapplication.databinding.FragmentReloadBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class Account : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var db : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btRegister.isEnabled = false
//        binding.btLogin.isEnabled = false

        binding.btEdit.setVisibility(View.GONE)
        //binding.btLogin.setVisibility(View.GONE)

        //guest session no user check
        binding.btRegister.setOnClickListener {
            val username = binding.valueName.text.toString()
            val phoneNo = binding.valuePhoneNo.text.toString()
            val password = binding.valuePassword.text.toString()
            val uid = binding.valueUid.text.toString()

            register(username, phoneNo, password, uid)
        }
    }

    private fun register(username: String, phoneNo: String, password: String, uid: String){
        val useraccount = UserDatabase(username,password,uid,0,0)
        db = FirebaseDatabase.getInstance().getReference("Phone Number")
        db.child(phoneNo).setValue(useraccount)
    }


}
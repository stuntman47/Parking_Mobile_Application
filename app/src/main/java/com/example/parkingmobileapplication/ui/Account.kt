package com.example.parkingmobileapplication.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.parkingmobileapplication.*
import com.example.parkingmobileapplication.R
import com.example.parkingmobileapplication.databinding.FragmentAccountBinding
import com.example.parkingmobileapplication.ui.parking.ParkingFragment
import com.example.parkingmobileapplication.ui.parking.serverUri
import com.example.parkingmobileapplication.ui.parking.subscriptionTopic
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.eclipse.paho.client.mqttv3.MqttMessage


class Account : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private var _binding: FragmentAccountBinding? = null
    private lateinit var db : DatabaseReference

    private lateinit var mqttManager: MqttManagerImpl
    private lateinit var communicator: Communicator

    var clientId = "kotlin_client_FYP"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mqttManager = MqttManagerImpl(
            requireActivity().applicationContext,
            serverUri,
            clientId,
            arrayOf(subscriptionTopic),
            IntArray(1){ 0 }
        )
        mqttManager.init()
        initMqttStatusListener()
        mqttManager.connect()

        communicator = activity as Communicator

        binding = FragmentAccountBinding.inflate(layoutInflater)
        return (binding.root)
    }

    private fun initMqttStatusListener() {
        mqttManager.mqttStatusListener = object : MqttStatusListener {
            override fun onConnectComplete(reconnect: Boolean, serverURI: String) {
                if (reconnect){
                    displayInDebugLog("Reconnected to : $serverURI")
                } else{
                    displayInDebugLog("Connected to: $serverURI")
                }
            }

            override fun onConnectFailure(exception: Throwable) {
                displayInDebugLog("Failed to connect")
            }

            override fun onConnectionLost(exception: Throwable) {
                displayInDebugLog("The Connection was lost.")
            }

            override fun onTopicSubscriptionSuccess() {
                displayInDebugLog("Subscribed!")
            }

            override fun onTopicSubscriptionError(exception: Throwable) {
                displayInDebugLog("Failed to subscribe")
            }

            override fun onMessageArrived(topic: String, message: MqttMessage) {
                displayInMessagesList(String(message.payload))

                communicator.passMQTTdata(String(message.payload))
                //mqttAction(String(message.payload))
            }


        }
    }

    private fun displayInMessagesList(message: String){
        //display timestamp
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun displayInDebugLog(message: String){
        Log.i(ParkingFragment.TAG, message)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btRegister.isEnabled = false
//        binding.btLogin.isEnabled = false
        var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var edit = pref.edit()
        val user = pref.getString("username", "default value")

        if (user.equals("default value")){
            binding.btLogout.setVisibility(View.GONE)
            binding.btRegister.setVisibility(View.VISIBLE)
            binding.btLogin.setVisibility(View.VISIBLE)
        }
        else{
            binding.btRegister.setVisibility(View.GONE)
            binding.btLogin.setVisibility(View.GONE)
            binding.btLogout.setVisibility(View.VISIBLE)

            binding.valueName.isEnabled = false
            binding.valuePhoneNo.isEnabled = false
            binding.valuePassword.isEnabled = false
            binding.valueUid.isEnabled = false

            val phone = pref.getString("phoneNo", "default value").toString()
            readData(phone)
        }

        //guest session no user check
        binding.btRegister.setOnClickListener {
            val username = binding.valueName.text.toString()
            val phoneNo = binding.valuePhoneNo.text.toString()
            val password = binding.valuePassword.text.toString()
            val uid = binding.valueUid.text.toString()

            register(username, phoneNo, password, uid)
        }

        binding.btLogin.setOnClickListener {
            val phoneNo = binding.valuePhoneNo.text.toString()
            val password = binding.valuePassword.text.toString()

            login(phoneNo,password)
        }

        binding.btLogout.setOnClickListener {
            edit.remove("username")
            edit.remove("phoneNo")
            edit.remove("car_plate")
            edit.commit()

            binding.valueName.text.clear()
            binding.valuePhoneNo.text.clear()
            binding.valuePassword.text.clear()
            binding.valueUid.text.clear()

            binding.valueName.isEnabled = true
            binding.valuePhoneNo.isEnabled = true
            binding.valuePassword.isEnabled = true
            binding.valueUid.isEnabled = true

            val fragmentParking = ParkingFragment()
            val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragment_container, fragmentParking)
            transaction.commit()
            Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun register(username: String, phoneNo: String, password: String, uid: String){
        val useraccount = UserDatabase(username,password,uid,0,"0")
        db = FirebaseDatabase.getInstance().getReference("Phone Number")

        FirebaseDatabase.getInstance().getReference("Phone Number").child(phoneNo).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (username == "" || phoneNo == "" || password == "" || uid == ""){
                    Toast.makeText(context, "Please fill up all details", Toast.LENGTH_SHORT).show()
                }
                else{
                    if(snapshot.getValue() == null){
                        db.child(phoneNo).setValue(useraccount).addOnSuccessListener {
                            binding.valueName.text.clear()
                            binding.valuePhoneNo.text.clear()
                            binding.valuePassword.text.clear()
                            binding.valueUid.text.clear()

                            Toast.makeText(context, "Register Successful", Toast.LENGTH_SHORT).show()

                        }.addOnFailureListener{
                            Toast.makeText(context, "Register failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(context, "Register failed (Incomplete details/Duplicate Phone Number)", Toast.LENGTH_LONG).show()

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun login(phoneNo: String, password: String){
        db = FirebaseDatabase.getInstance().getReference("Phone Number")

        FirebaseDatabase.getInstance().getReference("Phone Number").child(phoneNo).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){ //phone number not exist

                    val checkpassword = snapshot.child("password").getValue(String::class.java)
                    if (checkpassword.equals(password)){
                        val name = snapshot.child("username").getValue(String::class.java)
                        val carplate = snapshot.child("car_plate").getValue(String::class.java)
                        //store user session
                        var pref = activity!!.getSharedPreferences("session", Context.MODE_PRIVATE)
                        var editor = pref.edit()
                        editor.putString("username",name)
                        editor.putString("phoneNo", phoneNo)
                        editor.putString("car_plate", carplate)
                        editor.commit()

                        val fragmentParking = ParkingFragment()
                        val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
                        transaction.replace(R.id.fragment_container, fragmentParking)
                        transaction.commit()
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(context, "Invalid login", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(context, "Account not exist", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun readData(phone_num: String){
        db = FirebaseDatabase.getInstance().getReference("Phone Number").child(phone_num)
        db.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java)
                val password = snapshot.child("password").getValue(String::class.java)
                val uid = snapshot.child("uid").getValue(String::class.java)

                binding.valueName.setText(username)
                binding.textName.text = username
                binding.valuePhoneNo.setText(phone_num)
                binding.valuePassword.setText(password)
                binding.valueUid.setText(uid)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    //clear up for memory
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
package com.example.parkingmobileapplication.ui.parking

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.parkingmobileapplication.Communicator
import com.example.parkingmobileapplication.MqttManagerImpl
import com.example.parkingmobileapplication.MqttStatusListener
import com.example.parkingmobileapplication.R
import com.example.parkingmobileapplication.databinding.FragmentReloadBinding
import com.google.firebase.database.*
import org.eclipse.paho.client.mqttv3.MqttMessage


class Reload : Fragment() {
    private lateinit var binding: FragmentReloadBinding
    private lateinit var db: DatabaseReference

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

        binding = FragmentReloadBinding.inflate(layoutInflater)
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

        if (phone == "default value"){
            Toast.makeText(context, "Account Required", Toast.LENGTH_SHORT).show()
        }
        else{
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


}
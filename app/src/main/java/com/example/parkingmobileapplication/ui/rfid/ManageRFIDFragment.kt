package com.example.parkingmobileapplication.ui.rfid

import android.content.Context
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
import com.example.parkingmobileapplication.Communicator
import com.example.parkingmobileapplication.MqttManagerImpl
import com.example.parkingmobileapplication.MqttStatusListener
import com.example.parkingmobileapplication.databinding.FragmentManagerfidBinding
import com.example.parkingmobileapplication.ui.parking.ParkingFragment
import com.example.parkingmobileapplication.ui.parking.serverUri
import com.example.parkingmobileapplication.ui.parking.subscriptionTopic
import com.google.firebase.database.*
import org.eclipse.paho.client.mqttv3.MqttMessage


class ManageRFIDFragment : Fragment() {
    private lateinit var binding: FragmentManagerfidBinding
    private lateinit var db : DatabaseReference
    private var _binding: ManageRFIDFragment? = null
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

        binding = FragmentManagerfidBinding.inflate(layoutInflater)
        return(binding.root)
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
        var editor = pref.edit()
        val user = pref.getString("username", "default value")
        val phone = pref.getString("phoneNo", "default value").toString()
        //val car_plate = pref.getString("car_plate", "default value") //create key

        //guest user check
        if (user.equals("default value")){
            binding.rfidUid.text = "E15049"
            binding.textCarplate.text = "1234"
            binding.valueCarplate.isEnabled = false
            Toast.makeText(context, "Account Required", Toast.LENGTH_SHORT).show()
        }
        else{
            binding.valueCarplate.isEnabled = true
            binding.btLinkRFID.setOnClickListener {
                val carplate = binding.valueCarplate.text.toString()

                if (carplate == ""){
                    Toast.makeText(context, "Invalid car plate", Toast.LENGTH_SHORT).show()
                }
                else{
                    db = FirebaseDatabase.getInstance().getReference("Phone Number").child(phone)
                    db.child("car_plate").setValue(carplate)
                    editor.putString("car_plate", carplate)
                    editor.commit()
                    Toast.makeText(context, "Link successful", Toast.LENGTH_SHORT).show()
                    binding.valueCarplate.text.clear()
                }
            }

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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
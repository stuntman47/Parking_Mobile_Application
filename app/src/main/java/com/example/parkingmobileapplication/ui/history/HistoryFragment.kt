package com.example.parkingmobileapplication.ui.history

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmobileapplication.*
import com.example.parkingmobileapplication.databinding.FragmentHistoryBinding
import com.example.parkingmobileapplication.ui.parking.ParkingFragment
import com.example.parkingmobileapplication.ui.parking.serverUri
import com.example.parkingmobileapplication.ui.parking.subscriptionTopic
import com.google.firebase.database.*
import org.eclipse.paho.client.mqttv3.MqttMessage

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var viewhistory: RecyclerView
    private lateinit var listhistory: ArrayList<ParkingDatabase>
    private lateinit var historyadapter: HistoryAdapter
    private var _binding: FragmentHistoryBinding? = null
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

        binding = FragmentHistoryBinding.inflate(layoutInflater)
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
        val user = pref.getString("username", "default value")
        val carplate = pref.getString("car_plate", "default value").toString() //get user car plate


        viewhistory = binding.historyList //initialize notification list in adapter
        viewhistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewhistory.setHasFixedSize(true)



        listhistory = arrayListOf<ParkingDatabase>() //initialize data array list


        if (user.equals("default value")){

        }
        else{
            //Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
            db = FirebaseDatabase.getInstance().getReference("Log").child(carplate)
            db.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){ //if car plate exists
                        for(DataSnapshot in snapshot.children){ //get all timestamp values
                            //val test = DataSnapshot.child("price").getValue(Integer::class.java)
                            val log = DataSnapshot.getValue(ParkingDatabase::class.java)
                            listhistory.add(log!!)
                            //Toast.makeText(context, test.toString(), Toast.LENGTH_SHORT).show()

                            viewhistory.adapter = HistoryAdapter(listhistory) //initialize recycler view
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.parkingmobileapplication.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmobileapplication.*
import com.example.parkingmobileapplication.databinding.FragmentNotificationBinding
import com.example.parkingmobileapplication.ui.parking.ParkingFragment
import com.example.parkingmobileapplication.ui.parking.serverUri
import com.example.parkingmobileapplication.ui.parking.subscriptionTopic
import com.google.firebase.database.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import kotlin.collections.ArrayList


class Notification : Fragment() {
    private lateinit var binding: FragmentNotificationBinding

    private lateinit var viewnotification: RecyclerView
    private lateinit var listnotification: ArrayList<ParkingDatabase>
    private lateinit var notificationadapter: ParkingAdapter
    private lateinit var db: DatabaseReference
    //private lateinit var viewModel: NotificationViewModel

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

        binding = FragmentNotificationBinding.inflate(layoutInflater)

        return (binding.root)
    }

    private fun initMqttStatusListener() {
        mqttManager.mqttStatusListener = object : MqttStatusListener{
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
        Toast.makeText(context, "Please standby", Toast.LENGTH_SHORT).show()
    }

    private fun displayInDebugLog(message: String){
        Log.i(ParkingFragment.TAG, message)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val carplate = pref.getString("car_plate", "default value").toString() //get user car plate
        val time = pref.getString("startTime", "default value").toString()

//        setFragmentResultListener("requestKey") { requestKey, bundle ->
//            // We use a String here, but any type that can be put in a Bundle is supported
//            val timestamp = bundle.getString("bundleKey")
//            //Toast.makeText(context, "test: "+ timestamp.toString(), Toast.LENGTH_SHORT).show()
//            // Do something with the result

        viewnotification = binding.notificationList //initialize notification list in adapter
        viewnotification.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        //viewnotification.setHasFixedSize(true)

        listnotification = arrayListOf<ParkingDatabase>() //initialize data array list



        getNotifications(time, carplate)

    }

    private fun getNotifications(timestamp: String, carplate: String){
        db = FirebaseDatabase.getInstance().getReference("Log").child(carplate).child(timestamp)
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val log = snapshot.getValue(ParkingDatabase::class.java)
                    listnotification.add(log!!)

                    viewnotification.adapter = ParkingAdapter(listnotification) //initialize recycler view
                }

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
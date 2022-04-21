package com.example.parkingmobileapplication.ui.parking

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.parkingmobileapplication.*
import com.example.parkingmobileapplication.R
import com.example.parkingmobileapplication.databinding.FragmentParkingBinding
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import java.text.SimpleDateFormat
import java.util.*
import org.eclipse.paho.client.mqttv3.MqttMessage


const val serverUri = "tcp://broker.hivemq.com:1883"
const val subscriptionTopic = "FYP/#" //topic wild card, get sub data from root of topic


class ParkingFragment : Fragment() {
    private lateinit var binding: FragmentParkingBinding
//    private var _binding: FragmentParkingBinding? = null
    private lateinit var db : DatabaseReference
    private lateinit var db2 : DatabaseReference
    private lateinit var mqttManager: MqttManagerImpl

    var receiveMQTT: String? = null

    var clientId = "kotlin_client_FYP"

    // TAG
    companion object {
        const val TAG = "AndroidMqttClient"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //resume activity state
        var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val time = pref.getString("startTime", "default value").toString()
        val carplate = pref.getString("car_plate", "default value").toString()

        if (time != "default value"){
            //read elapsedTime
            FirebaseDatabase.getInstance().getReference("Log").child(carplate).child(time).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val elapsedTime = snapshot.child("elapsedTime").getValue(String::class.java)
                    //set value to text
                    val simpleDate = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a") //date format
                    val currentDate = simpleDate.format(time.toLong() * 1000) //ntp_Time

                    binding.valueElapsedTime.text = elapsedTime

                    binding.valueStartTime.text = currentDate
                    binding.btTagid.setBackgroundColor(Color.parseColor("#0F43A9"))
                    binding.btTagid.isEnabled = false


                    //mqttAction(receiveMQTT.toString())

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        if (arguments != null){
            receiveMQTT = arguments?.getString("ntpTime")
            //Toast.makeText(context, "Value get successful", Toast.LENGTH_SHORT).show()
            mqttAction(receiveMQTT.toString())
        }
        else{
            //Toast.makeText(context, "Value null", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()


    }

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


        binding = FragmentParkingBinding.inflate(layoutInflater)
        return (binding.root)
    }

    private fun initMqttStatusListener() {
        var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val carplate = pref.getString("car_plate", "default value")

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

                //ensure car plate is linked

                if (carplate.equals("default value")){
                    if (isAdded){ //null check
                        Toast.makeText(context, "Car Plate Link required", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    //Toast.makeText(requireContext(), "Function Run", Toast.LENGTH_SHORT).show()
                    mqttAction(String(message.payload))
                }


            }


        }
    }

    private fun displayInMessagesList(message: String){
        if (isAdded){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun displayInDebugLog(message: String){
        Log.i(TAG, message)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
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

            //test writeDatabase
            //writeRFIDDatabase(1650077006,phone,"entry")
        }

        binding.btReload.setOnClickListener {
            val reloadFragment = Reload()
            val transaction: FragmentTransaction = parentFragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragment_container, reloadFragment)
            transaction.commit()
        }


    }



//    //clear up for memory
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    private fun writeRFIDDatabase(ntp_Time: String, phone: String, status: String, car_plate: String){
        //get car plate number from database, search UID and get car plate number

        //obtain user information
        db = FirebaseDatabase.getInstance().getReference("Phone Number").child(phone)
        //initialize path
        db2 = FirebaseDatabase.getInstance().getReference("Log").child(car_plate)
        db.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val uid = snapshot.child("uid").getValue(String::class.java)
                val balance = snapshot.child("balance").getValue(Integer::class.java).toString()




                val plate = binding.btTagid.text.toString() //get car plate
                val amount = Integer.parseInt(balance) - 2 //deduct balance
                val price = Integer.parseInt(balance) - amount

                //update balance
                db.child("balance").setValue(amount)

                //write activity to database
                val parking = ParkingDatabase(uid,plate,"0", price, status, ntp_Time)
                db2.child(ntp_Time).setValue(parking) //set ntp_Time

                //save value to sharedPreferences
                var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
                var editor = pref.edit()
                editor.putString("startTime",ntp_Time)
                editor.commit()

                //set value to text
                val simpleDate = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a") //date format
                val currentDate = simpleDate.format(ntp_Time.toLong() * 1000) //ntp_Time
                binding.valueStartTime.text = currentDate

                //start at 0 minutes
                binding.valueElapsedTime.text = "0"


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        //send notification
//        val epochtime = ntp_Time //ntp_Time
//        setFragmentResult("requestKey", bundleOf("bundleKey" to epochtime))

    }

    private fun updateRFIDDatabase(ntp_Time: String, phone: String, status: String, car_plate: String){

        val startTime = binding.valueStartTime.text.toString() //get start Time
        val simpleDate = SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH) //date format
        val date = simpleDate.parse(startTime) as Date
        val output = date.time / 1000L


        //get elapsed time
        val current_time = ntp_Time.toLong()
        val elapsedTime = current_time - output
        val minutes = elapsedTime / 60



        //get amount of balance
        db = FirebaseDatabase.getInstance().getReference("Phone Number").child(phone)

        db.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val balance = snapshot.child("balance").getValue(Integer::class.java).toString()

                //update balance
                val amount = Integer.parseInt(balance) - 2 //deduct balance
                db.child("balance").setValue(amount)

                val price = Integer.parseInt(balance) - amount

                db2 = FirebaseDatabase.getInstance().getReference("Log").child(car_plate).child(output.toString())
                db2.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot2: DataSnapshot) {
                        if(snapshot2.exists())
                        {
                            val current_price = snapshot2.child("price").getValue(Integer::class.java).toString()

                            val updated_price = Integer.parseInt(current_price) + price
                            db2.child("elapsedTime").setValue(minutes.toString())
                            db2.child("price").setValue(updated_price)
                            db2.child("status").setValue(status)

                            //set value to text
                            binding.valueElapsedTime.text = minutes.toString()
                        }
                        else{
                            if (isAdded){
                                Toast.makeText(context, output.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

//        //send notification
//        val epochtime = output.toString() //initial entry time
//
//        parentFragmentManager.setFragmentResult("requestKey", bundleOf("bundleKey" to epochtime))


    }


    private fun mqttAction(data: String) {
        var pref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = pref.edit()
        //val user = pref.getString("username", "default value")
        val phone = pref.getString("phoneNo", "default value").toString()
        val car_plate = pref.getString("car_plate", "default value").toString()
        val time = pref.getString("startTime", "default value").toString()

        val timestamp = data
        val delim = ":"
        val arr = timestamp.split(delim).toTypedArray()


        //Toast.makeText(context, timestamp.toString(), Toast.LENGTH_SHORT).show()
        db = FirebaseDatabase.getInstance().getReference("Phone Number").child(phone)
        db.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val car_plate = snapshot.child("car_plate").getValue(String::class.java)
                val balance = snapshot.child("balance").getValue(Integer::class.java)
                val uid = snapshot.child("uid").getValue(String::class.java)

                if (arr[0] == "Entry"){ //parking_entry
//                            Toast.makeText(context, "Car Plate required", Toast.LENGTH_SHORT)
//                                .show()

                    if (car_plate == "0" || car_plate.equals(null)) {
                        if (isAdded){
                            Toast.makeText(context, "Car Plate required", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    else if (balance != null) { //null check
                        if (balance < 2){
                            if (isAdded){
                                Toast.makeText(context, "Balance not sufficient", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else if (arr[2] != uid){ //entry check uid
                            if (isAdded){
                                Log.i(TAG, arr[2])
                                Toast.makeText(context, "Incorrect UID", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }
                        else{
                            binding.btTagid.setBackgroundColor(Color.parseColor("#0F43A9"))
                            binding.btTagid.isEnabled = false
                            if (isAdded){
                                Toast.makeText(context, "Parking Started", Toast.LENGTH_SHORT).show()
                            }
                            writeRFIDDatabase(arr[1],phone,"entry",car_plate!!)
                        }
                    }

                }
                else if (arr[0] == "Exit"){ //parking_exit
                    if (time.equals("default value")){
                        if (isAdded) {
                            Toast.makeText(context, "Wrong Entrance", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else if (arr[2] != uid){
                        if (isAdded){
                            Toast.makeText(context, "Incorrect UID", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
                    else{
                        if (isAdded) {
                            Toast.makeText(context, "Parking End", Toast.LENGTH_SHORT).show()
                        }
                        binding.btTagid.setBackgroundColor(Color.parseColor("#47463F")) //switch back color when not active
                        binding.btTagid.isEnabled = true

                        //clear startTime from sharedPreferences
                        editor.remove("startTime")
                        editor.commit()

                        updateRFIDDatabase(arr[1],phone, "exit", car_plate!!)
                    }

                }
                else if (arr[0] == "Update"){ //parking_update
                    //update Parking
                    if (!binding.btTagid.isEnabled){

                        updateRFIDDatabase(arr[1],phone,"entry", car_plate!!)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


}
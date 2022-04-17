package com.example.parkingmobileapplication

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

interface MqttManager {
    fun init()
    fun connect()
    //fun receiveMessage(message: String, topic: String)
}

class MqttManagerImpl(
    val applicationContext: Context,
    val serverUri: String,
    val clientId: String,
    val topics: Array<String>,
    val topicQos: IntArray
) : MqttManager {
    private var mqttAndroidClient: MqttAndroidClient? = null
    var mqttStatusListener: MqttStatusListener? = null

    override fun init() {
        mqttAndroidClient = MqttAndroidClient(applicationContext, serverUri, clientId)
        mqttAndroidClient?.setCallback(object: MqttCallbackExtended{
            override fun connectionLost(cause: Throwable) {
                mqttStatusListener?.onConnectionLost(cause)
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                mqttStatusListener?.onMessageArrived(topic, message)
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                mqttStatusListener?.onConnectComplete(reconnect,serverURI)
                if(reconnect){
                    //because clean session is true, need re-subscribe
                    subscribeToTopic()
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun connect() {
        try {
            mqttAndroidClient?.connect(createConnectOptions(), null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    mqttAndroidClient?.setBufferOpts(createDisconnectedBufferOptions())
                    subscribeToTopic()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    mqttStatusListener?.onConnectFailure(exception)
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    private fun createConnectOptions(): MqttConnectOptions {
        return MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = false
        }
    }

    private fun createDisconnectedBufferOptions(): DisconnectedBufferOptions {
        return DisconnectedBufferOptions().apply {
            isBufferEnabled = true
            bufferSize = 100
            isPersistBuffer = false
            isDeleteOldestMessages = false
        }
    }

    private fun subscribeToTopic(){
        try {
            mqttAndroidClient?.subscribe(topics, topicQos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    mqttStatusListener?.onTopicSubscriptionSuccess()
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    mqttStatusListener?.onTopicSubscriptionError(exception)
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

//    override fun receiveMessage(message: String, topic: String) {
//        mqttAndroidClient?.let {
//            try {
//                val mqttMessage = MqttMessage().apply {
//                    payload = message.toByteArray()
//                }
//            } catch (e: MqttException) {
//                e.printStackTrace()
//            }
//        }
//    }

}
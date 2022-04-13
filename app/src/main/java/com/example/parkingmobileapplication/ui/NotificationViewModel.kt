package com.example.parkingmobileapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "No new notification"
    }
    val text: LiveData<String> = _text
}
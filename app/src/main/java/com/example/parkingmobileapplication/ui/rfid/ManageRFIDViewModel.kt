package com.example.parkingmobileapplication.ui.rfid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ManageRFIDViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Manage RFID Fragment"
    }
    val text: LiveData<String> = _text
}
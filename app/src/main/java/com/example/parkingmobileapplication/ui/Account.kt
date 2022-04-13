package com.example.parkingmobileapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.parkingmobileapplication.R
import com.example.parkingmobileapplication.databinding.FragmentAccountBinding
import com.example.parkingmobileapplication.databinding.FragmentReloadBinding


class Account : Fragment() {
    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return (binding.root)
    }


}
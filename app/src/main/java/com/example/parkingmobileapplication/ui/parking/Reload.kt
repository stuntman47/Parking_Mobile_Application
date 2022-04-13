package com.example.parkingmobileapplication.ui.parking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.parkingmobileapplication.databinding.FragmentReloadBinding


class Reload : Fragment() {
    private lateinit var binding: FragmentReloadBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReloadBinding.inflate(layoutInflater)
        return (binding.root)
    }


}
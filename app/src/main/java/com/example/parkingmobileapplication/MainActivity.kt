package com.example.parkingmobileapplication

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.parkingmobileapplication.databinding.ActivityMainBinding
import com.example.parkingmobileapplication.ui.history.HistoryFragment
import com.example.parkingmobileapplication.ui.parking.ParkingFragment
import com.example.parkingmobileapplication.ui.rfid.ManageRFIDFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val parkingFragment = ParkingFragment()
    private val historyFragment = HistoryFragment()
    private val rfidFragment = ManageRFIDFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        replaceFragment(parkingFragment)

        //listen for navigation activity for bottom_nav_view
        //bottom_nav_view.setOnNavigationItemSelectedListener
        binding.navView.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navigation_parking -> replaceFragment(parkingFragment)
                R.id.navigation_history -> replaceFragment(historyFragment)
                R.id.navigation_ManageRFID -> replaceFragment(rfidFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}
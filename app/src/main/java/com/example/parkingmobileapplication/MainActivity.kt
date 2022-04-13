package com.example.parkingmobileapplication

import android.graphics.Color
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
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.example.parkingmobileapplication.ui.Account
import com.example.parkingmobileapplication.ui.Notification


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val parkingFragment = ParkingFragment()
    private val historyFragment = HistoryFragment()
    private val rfidFragment = ManageRFIDFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window,false)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.title_bar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)



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

        binding.btAccount.setOnClickListener{
            val accountFragment = Account()
            val transaction: FragmentTransaction = supportFragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragment_container, accountFragment)
            transaction.commit()
        }

        binding.btNotifications.setOnClickListener{
            val notificationFragment = Notification()
            val transaction: FragmentTransaction = supportFragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragment_container, notificationFragment)
            transaction.commit()
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
package com.app.smartkantin.ui.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.smartkantin.R
import com.app.smartkantin.databinding.ActivityDashboardCustomerBinding
import com.app.smartkantin.ui.customer.fragment.HomeCustomerFragment
import com.app.smartkantin.ui.customer.fragment.MenuCustomerFragment
import com.app.smartkantin.ui.customer.fragment.OrderCustomerFragment
import com.app.smartkantin.ui.customer.fragment.ProfileCustomerFragment

class DashboardCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeCustomerFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeCustomerFragment()
                R.id.nav_menu -> MenuCustomerFragment()
                R.id.nav_order -> OrderCustomerFragment()
                R.id.nav_profile -> ProfileCustomerFragment()
                else -> HomeCustomerFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
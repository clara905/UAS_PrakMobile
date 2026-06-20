package com.app.smartkantin.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.smartkantin.R
import com.app.smartkantin.databinding.ActivityDashboardAdminBinding
import com.app.smartkantin.ui.admin.fragment.HomePenjualFragment
import com.app.smartkantin.ui.admin.fragment.MenuPenjualFragment
import com.app.smartkantin.ui.admin.fragment.OrderPenjualFragment
import com.app.smartkantin.ui.admin.fragment.ProfilePenjualFragment

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomePenjualFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomePenjualFragment()
                R.id.nav_menu -> MenuPenjualFragment()
                R.id.nav_order -> OrderPenjualFragment()
                R.id.nav_profile -> ProfilePenjualFragment()
                else -> HomePenjualFragment()
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
package com.app.smartkantin.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.smartkantin.R
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.databinding.ActivityDashboardAdminBinding
import com.app.smartkantin.ui.admin.fragment.HomePenjualFragment
import com.app.smartkantin.ui.admin.fragment.MenuPenjualFragment
import com.app.smartkantin.ui.admin.fragment.OrderPenjualFragment
import com.app.smartkantin.ui.admin.fragment.ProfilePenjualFragment
import com.app.smartkantin.utils.NotificationHelper
import com.app.smartkantin.viewmodel.OrderViewModel
import com.app.smartkantin.viewmodel.OrderViewModelFactory

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var notificationHelper: NotificationHelper
    private var lastOrderCount = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationHelper = NotificationHelper(this)
        val app = application as SmartKantinApp
        orderViewModel = ViewModelProvider(this, OrderViewModelFactory(app.database.orderDao()))[OrderViewModel::class.java]

        setupBottomNavigation()
        observeNewOrders()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomePenjualFragment())
        }
    }

    private fun observeNewOrders() {
        orderViewModel.getAllOrders().observe(this) { orders ->
            if (lastOrderCount != -1 && orders.size > lastOrderCount) {
                notificationHelper.sendNotification(
                    "Pesanan Baru!",
                    "Ada pesanan baru masuk. Segera cek dan proses!"
                )
            }
            lastOrderCount = orders.size
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
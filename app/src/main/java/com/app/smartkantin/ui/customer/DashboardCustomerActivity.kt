package com.app.smartkantin.ui.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.smartkantin.R
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.databinding.ActivityDashboardCustomerBinding
import com.app.smartkantin.ui.customer.fragment.HomeCustomerFragment
import com.app.smartkantin.ui.customer.fragment.MenuCustomerFragment
import com.app.smartkantin.ui.customer.fragment.OrderCustomerFragment
import com.app.smartkantin.ui.customer.fragment.ProfileCustomerFragment
import com.app.smartkantin.utils.NotificationHelper
import com.app.smartkantin.utils.OrderStatus
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.OrderViewModel
import com.app.smartkantin.viewmodel.OrderViewModelFactory

class DashboardCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardCustomerBinding
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var notificationHelper: NotificationHelper
    private var lastOrderStatuses = mutableMapOf<Int, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        notificationHelper = NotificationHelper(this)
        
        val app = application as SmartKantinApp
        orderViewModel = ViewModelProvider(this, OrderViewModelFactory(app.database.orderDao()))[OrderViewModel::class.java]

        setupBottomNavigation()
        observeOrderUpdates()

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeCustomerFragment())
        }
    }

    private fun observeOrderUpdates() {
        orderViewModel.getOrdersByUser(sessionManager.getUserId()).observe(this) { orders ->
            orders?.forEach { order ->
                val prevStatus = lastOrderStatuses[order.id]
                if (prevStatus != null && prevStatus != order.status) {
                    when (order.status) {
                        OrderStatus.DIPROSES -> {
                            notificationHelper.sendNotification(
                                "Pesanan Diproses",
                                "Pesanan #${order.id} sedang disiapkan penjual."
                            )
                        }
                        OrderStatus.SELESAI -> {
                            notificationHelper.sendNotification(
                                "Makanan Siap!",
                                "Pesanan #${order.id} sudah siap! Silakan ambil di kantin."
                            )
                        }
                    }
                }
                lastOrderStatuses[order.id] = order.status
            }
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
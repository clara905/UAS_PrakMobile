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
import com.app.smartkantin.utils.FirebaseConfig
import com.app.smartkantin.utils.NotificationHelper
import com.app.smartkantin.viewmodel.OrderViewModel
import com.app.smartkantin.viewmodel.OrderViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var menuViewModel: com.app.smartkantin.viewmodel.MenuViewModel
    private lateinit var promoViewModel: com.app.smartkantin.viewmodel.PromoViewModel
    private lateinit var notificationHelper: NotificationHelper
    private var lastOrderCount = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationHelper = NotificationHelper(this)
        val app = application as SmartKantinApp
        orderViewModel = ViewModelProvider(this, OrderViewModelFactory(app.database.orderDao()))[OrderViewModel::class.java]
        menuViewModel = ViewModelProvider(this, com.app.smartkantin.viewmodel.MenuViewModelFactory(com.app.smartkantin.data.repository.MenuRepository(app.database.menuDao())))[com.app.smartkantin.viewmodel.MenuViewModel::class.java]
        promoViewModel = ViewModelProvider(this, com.app.smartkantin.viewmodel.PromoViewModelFactory(com.app.smartkantin.data.repository.PromoRepository(app.database.promoDao())))[com.app.smartkantin.viewmodel.PromoViewModel::class.java]

        setupBottomNavigation()
        observeNewOrders()
        listenForNewOrdersFirebase()
        listenForMenuUpdatesFirebase()
        listenForPromoUpdatesFirebase()
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomePenjualFragment())
        }
    }

    private fun listenForMenuUpdatesFirebase() {
        val database = FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL).reference.child("menus")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    snapshot.children.forEach { child ->
                        val menu = child.getValue(com.app.smartkantin.data.entity.MenuEntity::class.java)
                        menu?.let { menuViewModel.upsertMenu(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenForPromoUpdatesFirebase() {
        val database = FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL).reference.child("promos")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    snapshot.children.forEach { child ->
                        val promo = child.getValue(com.app.smartkantin.data.entity.PromoEntity::class.java)
                        promo?.let { promoViewModel.upsertPromo(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Dengerin pesanan baru masuk dari Firebase (Online).
     * Jadi penjual dapet notif meskipun pembeli pesen pake HP lain.
     */
    private fun listenForNewOrdersFirebase() {
        val database = FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL).reference.child("orders")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Sync data Firebase ke Lokal Room
                try {
                    snapshot.children.forEach { child ->
                        val order = child.getValue(com.app.smartkantin.data.entity.OrderEntity::class.java)
                        // Pastikan ID valid (bukan 0 atau null dari Firebase)
                        if (order != null && order.id != 0) {
                            orderViewModel.upsertOrder(order)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val currentCount = snapshot.childrenCount.toInt()
                if (lastOrderCount != -1 && currentCount > lastOrderCount) {
                    notificationHelper.sendNotification(
                        "Pesanan Baru (Online)!",
                        "Ada pesanan baru masuk ke sistem Firebase."
                    )
                }
                lastOrderCount = currentCount
            }

            override fun onCancelled(error: DatabaseError) {}
        })
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
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
import com.app.smartkantin.utils.FirebaseConfig
import com.app.smartkantin.utils.NotificationHelper
import com.app.smartkantin.utils.OrderStatus
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.OrderViewModel
import com.app.smartkantin.viewmodel.OrderViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardCustomerBinding
    private lateinit var orderViewModel: OrderViewModel
    private lateinit var menuViewModel: com.app.smartkantin.viewmodel.MenuViewModel
    private lateinit var promoViewModel: com.app.smartkantin.viewmodel.PromoViewModel
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
        menuViewModel = ViewModelProvider(this, com.app.smartkantin.viewmodel.MenuViewModelFactory(com.app.smartkantin.data.repository.MenuRepository(app.database.menuDao())))[com.app.smartkantin.viewmodel.MenuViewModel::class.java]
        promoViewModel = ViewModelProvider(this, com.app.smartkantin.viewmodel.PromoViewModelFactory(com.app.smartkantin.data.repository.PromoRepository(app.database.promoDao())))[com.app.smartkantin.viewmodel.PromoViewModel::class.java]

        setupBottomNavigation()
        observeOrderUpdates()
        listenToFirebaseUpdates()
        listenForMenuUpdatesFirebase()
        listenForPromoUpdatesFirebase()

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeCustomerFragment())
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
     * Dengerin perubahan status pesanan langsung dari Firebase Realtime Database.
     * Ini yang bikin notifikasi bisa masuk lintas HP (Online).
     */
    private fun listenToFirebaseUpdates() {
        val database = FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL).reference.child("orders")
        val userId = sessionManager.getUserId()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    snapshot.children.forEach { child ->
                        val order = child.getValue(com.app.smartkantin.data.entity.OrderEntity::class.java)
                        
                        // Cek apakah ini pesanan milik user yang sedang login dan ID-nya valid
                        if (order != null && order.id != 0 && order.userId == userId) {
                            val orderId = order.id
                            val status = order.status
                            
                            // Update Room lokal dengan data terbaru dari Firebase
                            orderViewModel.upsertOrder(order)
                            
                            val prevStatus = lastOrderStatuses[orderId]
                            if (prevStatus != null && prevStatus != status) {
                                when (status) {
                                    OrderStatus.DIPROSES -> {
                                        notificationHelper.sendNotification(
                                            "Pesanan Diproses",
                                            "Pesanan #$orderId sedang disiapkan penjual."
                                        )
                                    }
                                    OrderStatus.SELESAI -> {
                                        notificationHelper.sendNotification(
                                            "Makanan Siap!",
                                            "Pesanan #$orderId sudah siap! Silakan ambil di kantin."
                                        )
                                    }
                                }
                            }
                            lastOrderStatuses[orderId] = status
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
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
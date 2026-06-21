package com.app.smartkantin

import android.app.Application
import com.app.smartkantin.data.database.AppDatabase
import com.app.smartkantin.data.entity.UserEntity
import com.app.smartkantin.utils.DefaultAccount
import com.app.smartkantin.utils.NotificationHelper
import com.app.smartkantin.utils.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SmartKantinApp : Application() {

    // Scope khusus untuk operasi database di level Application
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Database singleton, diakses dari Repository di step berikutnya
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        seedDefaultPenjual()
        seedDefaultPromo()
        observeNewOrders()
    }

    private fun seedDefaultPromo() {
        applicationScope.launch {
            val existing = database.promoDao().getPromoByCode("FRESH20")
            if (existing == null) {
                database.promoDao().insertPromo(
                    com.app.smartkantin.data.entity.PromoEntity(
                        kodePromo = "FRESH20",
                        deskripsi = "Diskon 20% untuk semua menu!",
                        persenPotongan = 20,
                        minBelanja = 0.0
                    )
                )
            }
        }
    }

    private fun observeNewOrders() {
        applicationScope.launch {
            var lastOrderCount = -1
            database.orderDao().getAllOrders().collectLatest { orders ->
                if (lastOrderCount != -1 && orders.size > lastOrderCount) {
                    val sessionManager = com.app.smartkantin.utils.SessionManager(this@SmartKantinApp)
                    if (sessionManager.getRole() == Role.PENJUAL) {
                        notificationHelper.sendNotification(
                            "Pesanan Baru Masuk!",
                            "Ada pesanan baru yang perlu Anda proses."
                        )
                    }
                }
                lastOrderCount = orders.size
            }
        }
    }

    /**
     * Membuat akun admin default agar fitur Login Admin (STEP 3)
     * bisa langsung dites tanpa perlu insert manual ke database.
     */
    private fun seedDefaultPenjual() {
        applicationScope.launch {
            val existingAdmin = database.userDao()
                .getUserByEmail(DefaultAccount.PENJUAL_EMAIL)

            if (existingAdmin == null) {
                database.userDao().insertUser(
                    UserEntity(
                        nama = "Penjual Utama",
                        email = DefaultAccount.PENJUAL_EMAIL,
                        password = DefaultAccount.PENJUAL_PASSWORD,
                        role = Role.PENJUAL,
                        namaToko = "Kantin Pusat"
                    )
                )
            }
        }
    }
}
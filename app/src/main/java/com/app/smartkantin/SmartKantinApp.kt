package com.app.smartkantin

import android.app.Application
import com.app.smartkantin.data.database.AppDatabase
import com.app.smartkantin.data.entity.UserEntity
import com.app.smartkantin.utils.DefaultAccount
import com.app.smartkantin.utils.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmartKantinApp : Application() {

    // Scope khusus untuk operasi database di level Application
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Database singleton, diakses dari Repository di step berikutnya
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        seedDefaultPenjual()
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
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
        seedDefaultAdmin()
    }

    /**
     * Membuat akun admin default agar fitur Login Admin (STEP 3)
     * bisa langsung dites tanpa perlu insert manual ke database.
     */
    private fun seedDefaultAdmin() {
        applicationScope.launch {
            val existingAdmin = database.userDao()
                .getUserByUsername(DefaultAccount.ADMIN_USERNAME)

            if (existingAdmin == null) {
                database.userDao().insertUser(
                    UserEntity(
                        nama = "Administrator",
                        username = DefaultAccount.ADMIN_USERNAME,
                        password = DefaultAccount.ADMIN_PASSWORD,
                        role = Role.ADMIN
                    )
                )
            }
        }
    }
}
package com.app.smartkantin.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.app.smartkantin.databinding.ActivitySplashBinding
import com.app.smartkantin.ui.admin.DashboardAdminActivity
import com.app.smartkantin.ui.auth.LoginActivity
import com.app.smartkantin.ui.customer.DashboardCustomerActivity
import com.app.smartkantin.utils.Role
import com.app.smartkantin.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashDelayMillis = 1500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateNext()
        }, splashDelayMillis)
    }

    private fun navigateNext() {
        val sessionManager = SessionManager(this)

        val intent = if (sessionManager.isLoggedIn()) {
            when (sessionManager.getRole()) {
                Role.PENJUAL -> Intent(this, DashboardAdminActivity::class.java)
                else -> Intent(this, DashboardCustomerActivity::class.java)
            }
        } else {
            Intent(this, LoginActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
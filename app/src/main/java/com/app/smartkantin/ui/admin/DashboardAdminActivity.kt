package com.app.smartkantin.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.smartkantin.databinding.ActivityDashboardAdminBinding
import com.app.smartkantin.ui.auth.LoginActivity
import com.app.smartkantin.utils.SessionManager

/**
 * PLACEHOLDER — akan dilengkapi fitur Kelola Menu (STEP 4)
 * dan Daftar Pesanan (STEP 9).
 */
class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        binding.tvWelcome.text = "Selamat datang, ${sessionManager.getNama()} (Admin)"

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
package com.app.smartkantin.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.smartkantin.databinding.ActivityDashboardAdminBinding
import com.app.smartkantin.ui.auth.LoginActivity
import com.app.smartkantin.utils.SessionManager

class DashboardAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardAdminBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        
        val namaToko = sessionManager.getNamaToko()
        if (namaToko != null) {
            binding.tvHeader.text = "Dashboard $namaToko"
        } else {
            binding.tvHeader.text = "Dashboard Penjual"
        }
        
        binding.tvWelcome.text = "Selamat datang,\n${sessionManager.getNama()}"

        binding.cardKelolaMenu.setOnClickListener {
            startActivity(Intent(this, KelolaMenuActivity::class.java))
        }

        binding.cardDaftarPesanan.setOnClickListener {
            Toast.makeText(this, "Tersedia di step berikutnya", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
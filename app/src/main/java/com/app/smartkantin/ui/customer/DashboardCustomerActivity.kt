package com.app.smartkantin.ui.customer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.smartkantin.databinding.ActivityDashboardCustomerBinding
import com.app.smartkantin.ui.auth.LoginActivity
import com.app.smartkantin.utils.SessionManager

/**
 * PLACEHOLDER — akan dilengkapi RecyclerView daftar menu (STEP 5),
 * detail menu (STEP 6), keranjang (STEP 7), dst.
 */
class DashboardCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardCustomerBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        binding.tvWelcome.text = "Selamat datang, ${sessionManager.getNama()}"

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
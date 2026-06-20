package com.app.smartkantin.ui.customer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.adapter.MenuCustomerAdapter
import com.app.smartkantin.data.repository.MenuRepository
import com.app.smartkantin.databinding.ActivityDashboardCustomerBinding
import com.app.smartkantin.ui.auth.LoginActivity
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.MenuViewModel
import com.app.smartkantin.viewmodel.MenuViewModelFactory

/**
 * PLACEHOLDER — akan dilengkapi detail menu (STEP 6), keranjang (STEP 7), dst.
 */
class DashboardCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardCustomerBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: MenuViewModel
    private lateinit var adapter: MenuCustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        
        val app = application as SmartKantinApp
        val repository = MenuRepository(app.database.menuDao())
        viewModel = ViewModelProvider(this, MenuViewModelFactory(repository))[MenuViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MenuCustomerAdapter { menu ->
            Toast.makeText(this, "${menu.namaMenu} ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
        }
        binding.rvMenu.apply {
            layoutManager = LinearLayoutManager(this@DashboardCustomerActivity)
            adapter = this@DashboardCustomerActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.tvWelcome.text = "Selamat datang, ${sessionManager.getNama()}"

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.allMenu.observe(this) { listMenu ->
            if (listMenu.isNullOrEmpty()) {
                binding.tvEmptyState.visibility = android.view.View.VISIBLE
                binding.rvMenu.visibility = android.view.View.GONE
            } else {
                binding.tvEmptyState.visibility = android.view.View.GONE
                binding.rvMenu.visibility = android.view.View.VISIBLE
                adapter.submitList(listMenu)
            }
        }
    }
}

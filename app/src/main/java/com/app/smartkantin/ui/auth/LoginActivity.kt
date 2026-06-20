package com.app.smartkantin.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.data.repository.AuthRepository
import com.app.smartkantin.databinding.ActivityLoginBinding
import com.app.smartkantin.ui.admin.DashboardAdminActivity
import com.app.smartkantin.ui.customer.DashboardCustomerActivity
import com.app.smartkantin.utils.Role
import com.app.smartkantin.utils.SessionManager
import com.app.smartkantin.viewmodel.AuthUiState
import com.app.smartkantin.viewmodel.AuthViewModel
import com.app.smartkantin.viewmodel.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val app = application as SmartKantinApp
        val repository = AuthRepository(app.database.userDao())
        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(repository)
        )[AuthViewModel::class.java]

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(username, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is AuthUiState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "Memproses..."
                }
                is AuthUiState.LoginSuccess -> {
                    sessionManager.saveSession(
                        userId = state.user.id,
                        nama = state.user.nama,
                        username = state.user.username,
                        role = state.user.role
                    )
                    navigateToDashboard(state.user.role)
                }
                is AuthUiState.Error -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = getString(com.app.smartkantin.R.string.login)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun navigateToDashboard(role: String) {
        val intent = if (role == Role.ADMIN) {
            Intent(this, DashboardAdminActivity::class.java)
        } else {
            Intent(this, DashboardCustomerActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
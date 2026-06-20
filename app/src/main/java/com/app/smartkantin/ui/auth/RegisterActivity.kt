package com.app.smartkantin.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.smartkantin.R
import com.app.smartkantin.SmartKantinApp
import com.app.smartkantin.data.repository.AuthRepository
import com.app.smartkantin.databinding.ActivityRegisterBinding
import com.app.smartkantin.viewmodel.AuthUiState
import com.app.smartkantin.viewmodel.AuthViewModel
import com.app.smartkantin.viewmodel.AuthViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.btnRegister.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            viewModel.register(nama, username, password, confirmPassword)
        }

        binding.tvGoToLogin.setOnClickListener {
            finish() // kembali ke LoginActivity
        }
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is AuthUiState.Loading -> {
                    binding.btnRegister.isEnabled = false
                    binding.btnRegister.text = "Memproses..."
                }
                is AuthUiState.RegisterSuccess -> {
                    Toast.makeText(
                        this,
                        "Registrasi berhasil, silakan login",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                is AuthUiState.Error -> {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = getString(R.string.register)
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }
}
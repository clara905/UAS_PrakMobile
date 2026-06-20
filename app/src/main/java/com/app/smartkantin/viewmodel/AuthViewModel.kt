package com.app.smartkantin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.smartkantin.data.entity.UserEntity
import com.app.smartkantin.data.repository.AuthRepository
import com.app.smartkantin.data.repository.RegisterResult
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class LoginSuccess(val user: UserEntity) : AuthUiState()
    object RegisterSuccess : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<AuthUiState>(AuthUiState.Idle)
    val loginState: LiveData<AuthUiState> = _loginState

    private val _registerState = MutableLiveData<AuthUiState>(AuthUiState.Idle)
    val registerState: LiveData<AuthUiState> = _registerState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = AuthUiState.Error("Email dan password wajib diisi")
            return
        }
        _loginState.value = AuthUiState.Loading
        viewModelScope.launch {
            val user = repository.login(email, password)
            _loginState.value = if (user != null) {
                AuthUiState.LoginSuccess(user)
            } else {
                AuthUiState.Error("Email atau password salah")
            }
        }
    }

    fun register(
        nama: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        namaToko: String? = null
    ) {
        when {
            nama.isBlank() || email.isBlank() || password.isBlank() -> {
                _registerState.value = AuthUiState.Error("Semua field wajib diisi")
            }
            role == com.app.smartkantin.utils.Role.PENJUAL && namaToko.isNullOrBlank() -> {
                _registerState.value = AuthUiState.Error("Nama toko wajib diisi untuk Penjual")
            }
            password.length < 6 -> {
                _registerState.value = AuthUiState.Error("Password minimal 6 karakter")
            }
            password != confirmPassword -> {
                _registerState.value = AuthUiState.Error("Konfirmasi password tidak sama")
            }
            else -> {
                _registerState.value = AuthUiState.Loading
                viewModelScope.launch {
                    when (repository.register(nama, email, password, role, namaToko)) {
                        is RegisterResult.Success -> {
                            _registerState.value = AuthUiState.RegisterSuccess
                        }
                        is RegisterResult.EmailTaken -> {
                            _registerState.value = AuthUiState.Error("Email sudah digunakan")
                        }
                    }
                }
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = AuthUiState.Idle
    }
}
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

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = AuthUiState.Error("Username dan password wajib diisi")
            return
        }
        _loginState.value = AuthUiState.Loading
        viewModelScope.launch {
            val user = repository.login(username, password)
            _loginState.value = if (user != null) {
                AuthUiState.LoginSuccess(user)
            } else {
                AuthUiState.Error("Username atau password salah")
            }
        }
    }

    fun register(nama: String, username: String, password: String, confirmPassword: String) {
        when {
            nama.isBlank() || username.isBlank() || password.isBlank() -> {
                _registerState.value = AuthUiState.Error("Semua field wajib diisi")
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
                    when (repository.register(nama, username, password)) {
                        is RegisterResult.Success -> {
                            _registerState.value = AuthUiState.RegisterSuccess
                        }
                        is RegisterResult.UsernameTaken -> {
                            _registerState.value = AuthUiState.Error("Username sudah digunakan")
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
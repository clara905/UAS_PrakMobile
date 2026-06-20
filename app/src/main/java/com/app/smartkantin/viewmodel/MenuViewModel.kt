package com.app.smartkantin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.app.smartkantin.data.entity.MenuEntity
import com.app.smartkantin.data.repository.MenuRepository
import kotlinx.coroutines.launch

sealed class MenuFormState {
    object Idle : MenuFormState()
    object Loading : MenuFormState()
    object Success : MenuFormState()
    data class Error(val message: String) : MenuFormState()
}

class MenuViewModel(private val repository: MenuRepository) : ViewModel() {

    // Reaktif: otomatis ter-update setiap ada perubahan data menu (insert/update/delete)
    val allMenu: LiveData<List<MenuEntity>> = repository.getAllMenu().asLiveData()

    private val _formState = MutableLiveData<MenuFormState>(MenuFormState.Idle)
    val formState: LiveData<MenuFormState> = _formState

    fun getMenuById(id: Int, onResult: (MenuEntity?) -> Unit) {
        viewModelScope.launch {
            onResult(repository.getMenuById(id))
        }
    }

    fun saveMenu(
        id: Int,
        namaMenu: String,
        deskripsi: String,
        hargaText: String,
        gambar: String
    ) {
        if (namaMenu.isBlank() || deskripsi.isBlank() || hargaText.isBlank()) {
            _formState.value = MenuFormState.Error("Semua field wajib diisi")
            return
        }
        val harga = hargaText.toDoubleOrNull()
        if (harga == null || harga <= 0) {
            _formState.value = MenuFormState.Error("Harga tidak valid")
            return
        }

        _formState.value = MenuFormState.Loading
        viewModelScope.launch {
            val menu = MenuEntity(
                id = id,
                namaMenu = namaMenu,
                deskripsi = deskripsi,
                harga = harga,
                gambar = gambar
            )
            if (id == 0) {
                repository.insertMenu(menu)
            } else {
                repository.updateMenu(menu)
            }
            _formState.value = MenuFormState.Success
        }
    }

    fun deleteMenu(menu: MenuEntity) {
        viewModelScope.launch {
            repository.deleteMenu(menu)
        }
    }

    fun resetFormState() {
        _formState.value = MenuFormState.Idle
    }
}